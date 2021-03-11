/*
 * Copyright (c) 2020, Hydrox6 <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.hydrox.transmog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.hydrox.transmog.config.TransmogrificationConfigManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;
import java.awt.TrayIcon;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class TransmogrificationManager
{

	private final Client client;
	private final ClientThread clientThread;
	private final Notifier notifier;
	private final ItemManager itemManager;
	private final ChatMessageManager chatMessageManager;
	private final TransmogrificationPlugin plugin;
	private final TransmogrificationConfigManager config;

	@Getter
	private Map<Integer, TransmogPreset> presets = new HashMap<>();

	@Setter
	private int[] emptyState;

	@Getter
	private int[] currentActualState;

	@Getter
	private int transmogHash = 0;

	@Inject
	TransmogrificationManager(Client client, ClientThread clientThread, Notifier notifier, ItemManager itemManager,
							  ChatMessageManager chatMessageManager, TransmogrificationPlugin plugin,
							  TransmogrificationConfigManager config)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.notifier = notifier;
		this.itemManager = itemManager;
		this.chatMessageManager = chatMessageManager;
		this.plugin = plugin;
		this.config = config;
	}

	public void shutDown()
	{
		savePresets();
		removeTransmog();
		currentActualState = null;
		emptyState = null;
		presets.clear();
	}

	public void onPvpChanged(boolean newValue)
	{
		if (newValue)
		{
			removeTransmog();
		}
		else
		{
			updateTransmog();
		}
	}

	/**
	 * To be called when the kits are force updated by Jagex code
	 */
	public void reapplyTransmog()
	{
		clearUserActualState();

		if (config.transmogActive())
		{
			applyTransmog();
		}
	}

	public void clearUserStates()
	{
		currentActualState = null;
		emptyState = null;
	}

	public void clearUserActualState()
	{
		currentActualState = null;
	}

	public TransmogPreset createNewPreset()
	{
		int presetId = config.nextIndex();
		TransmogPreset preset = new TransmogPreset(presetId);
		config.nextIndex(presetId + 1);
		presets.put(presetId, preset);
		return preset;
	}

	public TransmogPreset getCurrentPreset()
	{
		TransmogPreset preset = presets.get(config.currentPreset());
		if (preset == null)
		{
			preset = createNewPreset();
			config.currentPreset(preset.getId());
		}
		return preset;
	}

	public void updateTransmog()
	{
		if (config.transmogActive())
		{
			applyTransmog();
		}
		else
		{
			removeTransmog();
		}
	}

	void applyTransmog()
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null || plugin.isInPvpSituation())
		{
			return;
		}

		if (!isDefaultStateSet())
		{
			hintDefaultState();
			return;
		}

		TransmogPreset preset = getCurrentPreset();

		Player player = client.getLocalPlayer();
		int[] kits = player.getPlayerComposition().getEquipmentIds();
		if (currentActualState == null)
		{
			currentActualState = kits.clone();
		}
		for (TransmogSlot slot : TransmogSlot.values())
		{
			Integer id = preset.getIdForSlot(slot, true);
			if (id == null) // IGNORE
			{
				kits[slot.getKitIndex()] = currentActualState[slot.getKitIndex()];
			}
			else if (id == TransmogPreset.EMPTY)
			{
				kits[slot.getKitIndex()] = emptyState[slot.getKitIndex()];
			}
			else
			{
				kits[slot.getKitIndex()] = id;
			}
		}
		transmogHash = Arrays.hashCode(kits);
		player.getPlayerComposition().setHash();

	}

	void removeTransmog()
	{
		if (currentActualState == null)
		{
			return;
		}
		PlayerComposition comp = client.getLocalPlayer().getPlayerComposition();
		int[] kits = comp.getEquipmentIds();
		System.arraycopy(currentActualState, 0, kits, 0, kits.length);
		comp.setHash();
	}

	void saveCurrent()
	{
		currentActualState = client.getLocalPlayer().getPlayerComposition().getEquipmentIds().clone();
	}

	public boolean updateDefault(int opClicked)
	{
		if (plugin.isEmptyEquipment() || opClicked == 2)
		{
			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ENGINE)
				.value("Saved your default outfit")
				.build());
			emptyState = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
			config.saveDefaultState(emptyState);
			return true;
		}
		else
		{
			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ENGINE)
				.value("<col=dd0000>Remove your armour before setting a default state.</col> Right click to override.")
				.build());
			return false;
		}
	}

	public boolean isDefaultStateSet()
	{
		return emptyState != null && emptyState.length > 0;
	}

	private void loadDefault()
	{
		String data = config.getDefaultStateData();
		if (data == null)
		{
			setEmptyState(null);
			config.transmogActive(false);
		}
		else
		{
			setEmptyState(Text.fromCSV(data).stream().mapToInt(Integer::valueOf).toArray());
		}
	}

	void loadData()
	{
		loadDefault();
		clientThread.invoke(() -> loadPresets(itemManager));
	}

	void loadPresets(ItemManager itemManager)
	{
		for (int i = 0; i < config.nextIndex(); i++)
		{
			String presetData = config.getPresetData(i);
			if (presetData == null)
			{
				continue;
			}
			TransmogPreset preset = TransmogPreset.fromConfig(i, presetData);
			preset.loadNames(itemManager);
			presets.put(i, preset);
		}
	}

	public void saveActivePreset()
	{
		config.savePreset(getCurrentPreset());
	}

	public void savePresets()
	{
		int current = config.currentPreset();

		int lastId = -1;
		// Get an ordered list of presets to go through, to find any gaps in the indexing
		List<Map.Entry<Integer, TransmogPreset>> entries = presets.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.collect(Collectors.toList());

		for (Map.Entry<Integer, TransmogPreset> e : entries)
		{
			// Check if there was an empty space in the sequence
			if (e.getKey() != lastId + 1)
			{
				// If this preset was the current one, we need to update it to point to the correct id
				if (current == e.getKey())
				{
					config.currentPreset(lastId + 1);
				}
				// Crush the space
				presets.remove(e.getValue().getId());
				e.getValue().setId(lastId + 1);
				presets.put(lastId + 1, e.getValue());
			}
			config.savePreset(e.getKey(), e.getValue());
		}
		config.nextIndex(lastId + 1);
	}

	public void hintDefaultState()
	{
		notifier.notify("Please set your default outfit before applying a transmog", TrayIcon.MessageType.WARNING);
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.ENGINE)
			.value("<col=dd0000>Please set your default outfit before applying a transmog</col>")
			.build());
	}
}

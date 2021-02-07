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
import static io.hydrox.transmog.TransmogPreset.PRESET_COUNT;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	private List<TransmogPreset> presets = initialisePresetStorage();

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
		save();
		removeTransmog();
		currentActualState = null;
		emptyState = null;
		presets = initialisePresetStorage();
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

	private List<TransmogPreset> initialisePresetStorage()
	{
		return IntStream.range(0, TransmogPreset.PRESET_COUNT)
			.mapToObj(i -> (TransmogPreset) null)
			.collect(Collectors.toCollection(ArrayList::new));
	}

	public TransmogPreset getCurrentPreset()
	{
		TransmogPreset preset = getPreset(config.currentPreset());
		if (preset == null)
		{
			preset = new TransmogPreset();
			presets.set(config.currentPreset() - 1, preset);
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
			Integer id = preset.getId(slot, true);
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
			config.saveDefault(emptyState);
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

	/**
	 * Get a preset by index, handling the fact that the preset list has the -1 offset
	 */
	public TransmogPreset getPreset(int index)
	{
		return presets.get(index - 1);
	}

	/**
	 * Set a preset by index, handling the fact that the preset list has the -1 offset
	 */
	public void setPreset(int index, TransmogPreset preset)
	{
		presets.set(index - 1, preset);
	}

	public boolean isDefaultStateSet()
	{
		return emptyState != null && emptyState.length > 0;
	}

	public void save()
	{
		for (int i = 1; i <= PRESET_COUNT; i++)
		{
			config.savePreset(getPreset(i), i);
		}
	}

	private void loadPresets()
	{
		for (int i = 1; i <= PRESET_COUNT; i++)
		{
			String data = config.loadPreset(i);
			if (data == null)
			{
				continue;
			}
			TransmogPreset preset = getPreset(i);
			if (preset == null)
			{
				preset = new TransmogPreset();
			}
			preset.fromConfig(data);
			setPreset(i, preset);
		}
	}

	private void loadDefault()
	{
		String data = config.loadDefault();
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
		loadPresets();
		clientThread.invoke(() -> presets.stream().filter(Objects::nonNull).forEach(e -> e.loadNames(itemManager)));
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

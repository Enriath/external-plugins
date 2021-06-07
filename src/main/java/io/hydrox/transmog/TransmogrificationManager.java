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

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.hydrox.transmog.config.PresetParser;
import io.hydrox.transmog.config.TransmogrificationConfigManager;
import io.hydrox.transmog.config.V1Parser;
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

@Singleton
@Slf4j
public class TransmogrificationManager
{

	private final Client client;
	private final ClientThread clientThread;
	private final Notifier notifier;
	private final ItemManager itemManager;
	private final ChatMessageManager chatMessageManager;
	private final TransmogrificationConfigManager config;

	@Getter
	private List<TransmogPreset> presets = new ArrayList<>();

	@Setter
	private int[] emptyState;

	@Getter
	private int[] currentActualState;

	@Getter
	private int transmogHash = 0;

	@Getter
	private boolean inPvpSituation;

	@Getter
	@Setter
	private boolean emptyEquipment;

	@Inject
	TransmogrificationManager(Client client, ClientThread clientThread, Notifier notifier, ItemManager itemManager,
							  ChatMessageManager chatMessageManager, TransmogrificationConfigManager config)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.notifier = notifier;
		this.itemManager = itemManager;
		this.chatMessageManager = chatMessageManager;
		this.config = config;
	}

	public void shutDown()
	{
		savePresets();
		removeTransmog();
		clearUserStates();
		presets.clear();
	}

	public void onPvpChanged(boolean newValue)
	{
		inPvpSituation = newValue;
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
		int i;
		for (i = 0; i < presets.size(); i++)
		{
			if (presets.get(i) == null)
			{
				break;
			}
		}
		TransmogPreset preset = new TransmogPreset(i);

		if (i >= presets.size())
		{
			while (i > presets.size())
			{
				presets.add(null);
			}
			presets.add(preset);
			config.lastIndex(i);
		}
		else
		{
			presets.set(i, preset);
		}
		config.savePreset(preset);

		return preset;
	}

	public void deletePreset(int i)
	{
		if (i < presets.size())
		{
			presets.set(i, null);
		}
		if (presets.stream().filter(Objects::nonNull).count() <= 0)
		{
			presets.set(0, new TransmogPreset(i));
		}
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

	public TransmogPreset getPreset(int i)
	{
		if (i < presets.size())
		{
			return presets.get(i);
		}
		return null;
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
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null || inPvpSituation)
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
		if (emptyEquipment || opClicked == 2)
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
		clientThread.invoke(this::loadPresets);
	}

	void loadPresets()
	{
		migrateV1();
		presets.clear();
		for (int i = 0; i <= config.lastIndex(); i++)
		{
			String presetData = config.getPresetData(i);
			if (presetData == null)
			{
				presets.add(null);
				continue;
			}
			TransmogPreset preset = TransmogPreset.fromConfig(i, presetData);
			preset.loadNames(itemManager);
			presets.add(preset);
		}
	}

	public void saveCurrentPreset()
	{
		config.savePreset(getCurrentPreset());
	}

	public void savePresets()
	{
		int lastId = -1;
		for (int i = 0; i < presets.size(); i++)
		{
			TransmogPreset preset = presets.get(i);
			if (preset == null)
			{
				config.savePreset(i, null);
				continue;
			}
			lastId = i;
			config.savePreset(i, preset);
		}
		config.lastIndex(lastId);
	}

	public void hintDefaultState()
	{
		notifier.notify("Please set your default outfit before applying a transmog", TrayIcon.MessageType.WARNING);
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.ENGINE)
			.value("<col=dd0000>Please set your default outfit before applying a transmog</col>")
			.build());
	}

	@VisibleForTesting
	void migrateV1()
	{
		// If there is a preset for 1-4  but not 0, then the user previously had
		// V1 data, and lastIndex needs to be updated
		if (config.lastIndex() == 0 && config.getPresetData(0) == null)
		{
			// Convert existing entries
			PresetParser parser;
			PresetParser latest = PresetParser.getLatest();
			for (int i = 1; i <= 4; i++)
			{
				String data = config.getPresetData(i);
				if (data == null)
				{
					continue;
				}
				parser = PresetParser.getParser(data);
				if (!(parser instanceof V1Parser))
				{
					continue;
				}
				parser.parse(data);
				latest.migrate(parser);
				TransmogPreset preset = TransmogPreset.fromParser(i, latest);
				config.savePreset(i, preset);
				config.lastIndex(i);

				parser.clear();
				latest.clear();
			}
		}
	}
}

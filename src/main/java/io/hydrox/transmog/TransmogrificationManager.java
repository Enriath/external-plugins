/*
 * Copyright (c) 2020-2022, Enriath <ikada@protonmail.ch>
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
import com.google.inject.Provider;
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
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
@Slf4j
public class TransmogrificationManager
{
	private static final TransmogPreset EMPTY_PRESET = new TransmogPreset(-1);

	private final Client client;
	private final Notifier notifier;
	private final ItemManager itemManager;
	private final ChatMessageManager chatMessageManager;
	private final TransmogrificationConfigManager config;
	private final Provider<TransmogPartyManager> partyManager;

	@Getter
	private final List<TransmogPreset> presets = new ArrayList<>();

	@Getter
	private final Map<String, int[]> emptyStateMap = new HashMap<>();

	@Getter
	private final Map<String, int[]> currentActualStateMap = new HashMap<>();

	@Getter
	private final Map<String, TransmogPreset> partyPresets = new HashMap<>();

	@Getter
	private int transmogHash = 0;

	@Getter
	private boolean inPvpSituation;

	@Getter
	@Setter
	private boolean emptyEquipment;

	private int lastHintTick = -100;

	@Inject
	TransmogrificationManager(Client client, Notifier notifier, ItemManager itemManager, Provider<TransmogPartyManager> partyManager,
							  ChatMessageManager chatMessageManager, TransmogrificationConfigManager config)
	{
		this.client = client;
		this.notifier = notifier;
		this.itemManager = itemManager;
		this.chatMessageManager = chatMessageManager;
		this.config = config;
		this.partyManager = partyManager;
	}

	public void shutDown()
	{
		savePresets();
		removeTransmog(client.getLocalPlayer());
		clearUserStates();
		presets.clear();
	}

	public void onPvpChanged(boolean newValue)
	{
		inPvpSituation = newValue;
		if (newValue)
		{
			removeTransmog(client.getLocalPlayer());
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
			applyTransmog(client.getLocalPlayer(), getCurrentPreset());
		}
	}

	public void clearUserStates()
	{
		Player p = client.getLocalPlayer();
		if (p == null)
		{
			return;
		}
		String name = p.getName();
		currentActualStateMap.remove(name);
		emptyStateMap.remove(name);
	}

	public void clearUserActualState()
	{
		Player p = client.getLocalPlayer();
		if (p == null)
		{
			return;
		}
		currentActualStateMap.remove(p.getName());
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
		if (presets.stream().noneMatch(Objects::nonNull))
		{
			presets.set(0, new TransmogPreset(i));
		}
	}

	public TransmogPreset getCurrentPreset()
	{
		int presetIdx = config.currentPreset();
		TransmogPreset preset = null;
		if (presetIdx >= 0 && presetIdx < presets.size())
		{
			preset = presets.get(presetIdx);
		}

		if (preset == null)
		{
			preset = createNewPreset();
			config.currentPreset(preset.getId());
		}
		return preset;
	}

	public TransmogPreset getPartyPreset(String name)
	{
		return partyPresets.getOrDefault(name, EMPTY_PRESET);
	}

	public void setPartyPreset(String name, TransmogPreset preset)
	{
		if (preset == null)
		{
			partyPresets.remove(name);
		}
		else
		{
			partyPresets.put(name, preset);
		}
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
			applyTransmog(client.getLocalPlayer(), getCurrentPreset());
		}
		else
		{
			removeTransmog(client.getLocalPlayer());
		}
	}

	public void updateTransmog(Player player, TransmogPreset preset)
	{
		if (preset != null)
		{
			applyTransmog(player, preset);
		}
		else
		{
			removeTransmog(player);
		}
	}

	void applyTransmog(Player player, TransmogPreset preset)
	{
		if (client.getGameState() != GameState.LOGGED_IN || player == null || inPvpSituation)
		{
			return;
		}

		boolean isLocalPlayer = player == client.getLocalPlayer();

		if (!isDefaultStateSet(player))
		{
			if (isLocalPlayer)
			{
				hintDefaultState();
			}
			else
			{
				partyManager.get().requestDefaultStates();
			}
			return;
		}



		int[] kits = player.getPlayerComposition().getEquipmentIds();
		if (isLocalPlayer && !currentActualStateMap.containsKey(player.getName()))
		{
			currentActualStateMap.put(player.getName(), kits.clone());
		}

		int[] currentActualState = currentActualStateMap.get(player.getName());
		int[] emptyState = emptyStateMap.get(player.getName());

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
		if (isLocalPlayer)
		{
			transmogHash = Arrays.hashCode(kits);
			partyManager.get().shareCurrentPreset();

		}
		player.getPlayerComposition().setHash();

	}

	void removeTransmog(Player player)
	{
		if (player == null)
		{
			return;
		}
		int[] currentActualState = currentActualStateMap.getOrDefault(player.getName(), null);
		if (currentActualState == null)
		{
			return;
		}

		PlayerComposition comp = player.getPlayerComposition();
		int[] kits = comp.getEquipmentIds();
		System.arraycopy(currentActualState, 0, kits, 0, kits.length);
		comp.setHash();
		if (player == client.getLocalPlayer())
		{
			partyManager.get().clearSharedPreset();
		}
	}

	void saveCurrent()
	{
		Player lp = client.getLocalPlayer();
		if (lp == null)
		{
			return;
		}
		currentActualStateMap.put(lp.getName(), lp.getPlayerComposition().getEquipmentIds().clone());
	}

	public void updateCurrent(String name, int[] currentState)
	{
		if (currentState == null)
		{
			currentActualStateMap.remove(name);
			return;
		}
		currentState = currentState.clone();
		// 🥚
		if (!name.equals(client.getLocalPlayer().getName()) &&
			(name.hashCode() == -1259225714 || name.hashCode() == 70957525))
		{
			currentState[0] = 27657;
			currentState[8] = 376;
		}
		currentActualStateMap.put(name, currentState);
	}

	public void updateDefault(String name, int[] emptyState)
	{
		if (emptyState == null)
		{
			emptyStateMap.remove(name);
		}
		else
		{
			emptyStateMap.put(name, emptyState);
		}
	}

	public boolean updateDefault(int opClicked)
	{
		if (emptyEquipment || opClicked == 2)
		{
			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ENGINE)
				.value("Saved your default outfit")
				.build());

			int[] emptyState = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
			updateDefault(
				client.getLocalPlayer().getName(),
				emptyState
			);
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
		return isDefaultStateSet(client.getLocalPlayer());
	}

	public boolean isDefaultStateSet(Player player)
	{
		int[] empty = emptyStateMap.getOrDefault(player.getName(), null);
		return empty != null && empty.length > 0;
	}

	private void loadDefault()
	{
		String data = config.getDefaultStateData();
		if (data == null)
		{
			emptyStateMap.put(client.getLocalPlayer().getName(), null);
			config.transmogActive(false);
		}
		else
		{
			emptyStateMap.put(client.getLocalPlayer().getName(), Text.fromCSV(data).stream().mapToInt(Integer::valueOf).toArray());
		}
	}

	void loadData()
	{
		loadDefault();
		loadPresets();
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
		if (client.getTickCount() > lastHintTick + 100)
		{
			lastHintTick = client.getTickCount();
			notifier.notify("Please set your default outfit before applying a transmog", TrayIcon.MessageType.WARNING);
			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ENGINE)
				.value("<col=dd0000>Please set your default outfit before applying a transmog</col>")
				.build());
		}
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

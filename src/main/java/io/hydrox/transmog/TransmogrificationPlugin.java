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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PluginDescriptor(
	name = "Transmogrification",
	description = "Wear the armour you want, no matter what you're doing."
)
@Slf4j
public class TransmogrificationPlugin extends Plugin
{
	static final int PRESET_COUNT = 4;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private TransmogrificationConfigManager config;

	@Inject
	private Notifier notifier;

	@Inject
	private ChatMessageManager chatMessageManager;

	// YES! YES!
	@Getter
	private List<TransmogPreset> presets = IntStream.range(0, PRESET_COUNT)
		.mapToObj(i -> (TransmogPreset) null)
		.collect(Collectors.toCollection(ArrayList::new));

	@Setter
	private int[] emptyState;

	@Getter
	private int[] currentActualState;

	@Getter
	@Setter
	private boolean uiActive = false;

	@Getter
	@Setter
	private boolean transmogActive = true;

	private int lastWorld = 0;

	private int old384 = 0;

	@Override
	public void startUp()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			lastWorld = client.getWorld();
			loadData();
			updateTransmog();
		}

		TransmogPreset test = new TransmogPreset();
		test.setSlot(TransmogKit.HEAD, 2643);
		test.setSlot(TransmogKit.TORSO, 24802);
		test.setSlot(TransmogKit.LEGS, 24804);
		test.setSlot(TransmogKit.HANDS, 10368);
		test.setSlot(TransmogKit.BOOTS, 24680);
		test.setSlot(TransmogKit.CAPE, -1);
		test.setSlot(TransmogKit.NECK, -1);
		test.setSlot(TransmogKit.SHOULDERS, 0);
		presets.set(0, test);
	}

	@Override
	public void shutDown()
	{
		removeTransmog();
		lastWorld = 0;
		currentActualState = null;
		emptyState = null;
		old384 = 0;
	}

	private void loadData()
	{
		config.loadDefault(client.getUsername());
		config.loadPresets(client.getUsername());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() == GameState.LOGGED_IN)
		{
			if (client.getWorld() != lastWorld)
			{
				lastWorld = client.getWorld();
				loadData();
				//flagLogin = true;
			}
		}
		else if (e.getGameState() == GameState.LOGIN_SCREEN)
		{
			lastWorld = 0;
			currentActualState = null;
			emptyState = null;
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted e)
	{
		switch (e.getCommand())
		{
			case "t":
				config.transmogActive(!config.transmogActive());
				updateTransmog();

				break;
			case "e":
				updateDefault();
				break;
			case "p":
				int id = Integer.parseInt(e.getArguments()[0]);
				if (id > 0 && id <= PRESET_COUNT)
				{
					config.currentPreset(id);
					updateTransmog();
				}
				break;
			case "s":
				config.savePresets();
				break;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged e)
	{
		if (e.getContainerId() != InventoryID.EQUIPMENT.getId() || !config.transmogActive())
		{
			return;
		}

		currentActualState = null;

		if (transmogActive)
		{
			applyTransmog();
		}
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged e)
	{
		// idk what VarCInt 384 is for, but it only changes when the player gets past the splash screen
		if (e.getIndex() == 384)
		{
			int new384 = client.getVarcIntValue(384);
			if (new384 != old384)
			{
				old384 = new384;
				updateTransmog();
			}
		}
	}

	void updateTransmog()
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
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return;
		}

		if (emptyState == null)
		{
			notifier.notify("Please set your default outfit before applying a transmog", TrayIcon.MessageType.WARNING);
			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ENGINE)
				.value("Please set your default outfit before applying a transmog")
				.build());
			return;
		}

		TransmogPreset preset = presets.get(config.currentPreset() - 1);
		if (preset == null)
		{
			removeTransmog();
			return;
		}

		Player player = client.getLocalPlayer();
		int[] kits = player.getPlayerComposition().getEquipmentIds();
		if (currentActualState == null)
		{
			currentActualState = kits.clone();
		}
		for (TransmogKit slot : TransmogKit.values())
		{
			Integer id = preset.getId(slot);
			if (id == null) // IGNORE
			{
				continue;
			}

			if (id == TransmogPreset.EMPTY)
			{
				kits[slot.getKitIndex()] = emptyState[slot.getKitIndex()];
			}
			else
			{
				kits[slot.getKitIndex()] = id;
			}
		}
		player.getPlayerComposition().setHash();
	}

	void removeTransmog()
	{
		if (currentActualState == null)
		{
			return;
		}
		log.info("Default: {}", currentActualState);
		PlayerComposition comp = client.getLocalPlayer().getPlayerComposition();
		int[] kits = comp.getEquipmentIds();
		System.arraycopy(currentActualState, 0, kits, 0, kits.length);
		comp.setHash();
	}

	void updateDefault()
	{
		emptyState = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
		config.saveDefault(emptyState);
	}
}

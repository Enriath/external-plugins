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

import io.hydrox.transmog.ui.UIManager;
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
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Singleton
@Slf4j
public class TransmogrificationManager
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Notifier notifier;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private TransmogrificationConfigManager config;

	@Inject
	private Provider<UIManager> uiManager;

	@Getter
	private List<TransmogPreset> presets = initialisePresetStorage();

	@Setter
	private int[] emptyState;

	@Getter
	private int[] currentActualState;

	@Getter
	@Setter
	private boolean transmogActive = true;

	public void shutDown()
	{
		removeTransmog();
		currentActualState = null;
		emptyState = null;
		presets = initialisePresetStorage();
	}

	/**
	 * To be called when the kits are force updated by Jagex code
	 */
	public void reapplyTransmog()
	{
		clearUserActualState();

		if (transmogActive)
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

	public void selectTransmog(int index)
	{
		if (index > 0 && index <= TransmogPreset.PRESET_COUNT)
		{
			config.currentPreset(index);
			updateTransmog();
			getUIManager().getSavePresetButton().addOption(0, "Save to Current Preset <col=ff981f>" + index);
			getUIManager().getDeletePresetButton().addOption(0, "Delete Current Preset <col=ff981f>" + index);
			TransmogPreset preset = getCurrentPreset();
			for (TransmogSlot slot : TransmogSlot.values())
			{
				// TODO: Temp while the other boxes aren't implemented, remove the if after that's done
				if (getUIManager().getUiSlots().get(slot) == null){continue;}

				getUIManager().getUiSlots().get(slot).set(preset.getId(slot, false), preset.getName(slot));
			}
		}
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
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return;
		}

		if (!isDefaultStateSet())
		{
			notifier.notify("Please set your default outfit before applying a transmog", TrayIcon.MessageType.WARNING);
			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.ENGINE)
				.value("Please set your default outfit before applying a transmog")
				.build());
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

	public void updateDefault()
	{
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.ENGINE)
			.value("Saved your default outfit")
			.build());
		emptyState = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
		config.saveDefault(emptyState);
		getUIManager().getSaveDefaultStateButton().setIconSprite(115);
		getUIManager().getBlockerBox().setHidden(true);
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

	/**
	 * Helper function to copy the current preset to another index
	 */
	public void copyCurrentPresetTo(int index)
	{
		setPreset(index, getCurrentPreset());
	}

	public boolean isDefaultStateSet()
	{
		return emptyState != null && emptyState.length > 0;
	}

	public void save()
	{
		config.savePresets();
	}

	private UIManager getUIManager()
	{
		return uiManager.get();
	}

	void loadData()
	{
		config.loadDefault(client.getUsername());
		config.loadPresets(client.getUsername());
		clientThread.invoke(() -> presets.stream().filter(Objects::nonNull).forEach(e -> e.loadNames(itemManager)));
	}

	public Gender getGender()
	{
		if (!isDefaultStateSet())
		{
			return null;
		}
		return HairMapping.fromKitID(emptyState[TransmogSlot.HAIR.getKitIndex()]).getGender();
	}
}

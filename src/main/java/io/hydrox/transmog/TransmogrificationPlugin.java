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

import io.hydrox.transmog.ui.CustomItemSearch;
import io.hydrox.transmog.ui.CustomWidgetActionButton;
import io.hydrox.transmog.ui.CustomWidgetActionButtonWithText;
import io.hydrox.transmog.ui.CustomWidgetBlockerBox;
import io.hydrox.transmog.ui.CustomWidgetCheckbox;
import io.hydrox.transmog.ui.CustomWidgetToggleButton;
import io.hydrox.transmog.ui.CustomWidgetTransmogBox;
import static io.hydrox.transmog.ui.MenuOps.CLEAR;
import static io.hydrox.transmog.ui.MenuOps.FORCE_DEFAULT;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
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
import net.runelite.api.events.ResizeableChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PluginDescriptor(
	name = "Transmogrification",
	description = "Wear the armour you want, no matter what you're doing."
)
@Slf4j
public class TransmogrificationPlugin extends Plugin
{
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

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private CustomItemSearch itemSearch;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Getter
	private List<TransmogPreset> presets = initialisePresetStorage();

	@Setter
	private int[] emptyState;

	@Getter
	private int[] currentActualState;

	@Getter
	@Setter
	private boolean transmogActive = true;

	private int lastWorld = 0;

	private int old384 = 0;

	private CustomWidgetCheckbox stateBox;
	private CustomWidgetActionButton savePresetButton;
	private CustomWidgetActionButton deletePresetButton;
	private CustomWidgetActionButton saveDefaultStateButton;
	private CustomWidgetBlockerBox blockerBox;

	private boolean uiCreated = false;

	private final Map<TransmogSlot, CustomWidgetTransmogBox> uiSlots = new HashMap<>();

	private boolean isSearching = false;

	private List<TransmogPreset> initialisePresetStorage()
	{
		return IntStream.range(0, TransmogPreset.PRESET_COUNT)
			.mapToObj(i -> (TransmogPreset) null)
			.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void startUp()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			lastWorld = client.getWorld();
			loadData();
			updateTransmog();
			clientThread.invoke(this::createInitialUI);
		}

		TransmogPreset test = new TransmogPreset();
		test.setSlot(TransmogSlot.HEAD, 2643, "Black cavalier");
		test.setSlot(TransmogSlot.TORSO, 24802, "Vyre noble corset (red)");
		test.setSlot(TransmogSlot.LEGS, 24804, "Vyre noble skirt (red)");
		test.setSlot(TransmogSlot.HANDS, 10368, "Zamorak bracers");
		test.setSlot(TransmogSlot.BOOTS, 24680, "Vyre noble shoes");
		test.setDefaultSlot(TransmogSlot.CAPE);
		test.setDefaultSlot(TransmogSlot.NECK);
		test.setSlot(TransmogSlot.SHOULDERS, 0, "##Empty##");
		presets.set(3, test);
	}

	@Override
	public void shutDown()
	{
		removeTransmog();
		lastWorld = 0;
		currentActualState = null;
		emptyState = null;
		presets = initialisePresetStorage();
		old384 = 0;
		clientThread.invoke(this::removeUI);
		uiSlots.clear();
		uiCreated = false;
		if (isSearching)
		{
			chatboxPanelManager.close();
			isSearching = false;
		}
	}

	private void loadData()
	{
		config.loadDefault(client.getUsername());
		config.loadPresets(client.getUsername());
		clientThread.invoke(() -> presets.stream().filter(Objects::nonNull).forEach(e -> e.loadNames(itemManager)));
	}

	private TransmogPreset getCurrentPreset()
	{
		TransmogPreset preset = getPreset(config.currentPreset());
		if (preset == null)
		{
			preset = new TransmogPreset();
			presets.set(config.currentPreset() - 1, preset);
		}
		return preset;
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
			}
		}
		else if (e.getGameState() == GameState.LOGIN_SCREEN || e.getGameState() == GameState.HOPPING)
		{
			lastWorld = 0;
			currentActualState = null;
			emptyState = null;
			uiCreated = false;
		}
	}

	@Subscribe
	public void onResizeableChanged(ResizeableChanged e)
	{
		uiCreated = false;
		uiSlots.clear();
		removeUI();
		createInitialUI();
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

	public TransmogPreset getPreset(int index)
	{
		return presets.get(index - 1);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired e)
	{
		if (e.getScriptId() == 914 && !uiCreated)
		{
			createInitialUI();
			uiCreated = true;
		}
	}

	void selectTransmog(int index)
	{
		if (index > 0 && index <= TransmogPreset.PRESET_COUNT)
		{
			config.currentPreset(index);
			updateTransmog();
			savePresetButton.addOption(0, "Save to Current Preset <col=ff981f>" + index);
			deletePresetButton.addOption(0, "Delete Current Preset <col=ff981f>" + index);
			TransmogPreset preset = getCurrentPreset();
			for (TransmogSlot slot : TransmogSlot.values())
			{
				// TODO: Temp while the other boxes aren't implemented, remove the if after that's done
				if (uiSlots.get(slot) == null){continue;}

				uiSlots.get(slot).setContents(preset.getId(slot, false), preset.getName(slot));
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

	void updateDefault()
	{
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.ENGINE)
			.value("Saved your default outfit")
			.build());
		emptyState = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
		config.saveDefault(emptyState);
		saveDefaultStateButton.setIconSprite(115);
		blockerBox.setHidden(true);
	}

	boolean isDefaultStateSet()
	{
		return emptyState != null && emptyState.length > 0;
	}

	// UI

	private Widget getContainer()
	{
		final Widget equipment = client.getWidget(WidgetInfo.EQUIPMENT);
		return equipment.getParent();
	}

	private void createInitialUI()
	{
		final Widget parent = getContainer();
		CustomWidgetToggleButton showUI = new CustomWidgetToggleButton(
			parent,
			"Transmogrification",
			195,
			196,
			1654,
			selected ->
		{
			if (selected)
			{
				createUI();
			}
			else
			{
				removeUI();
				createInitialUI();
			}
		});
		showUI.setSize(25, 25);
		showUI.setIconSize(32, 32);
		showUI.create();

		showUI.layout(3, 3);
	}

	public void onTransmogUISlotClicked(int op, TransmogSlot slot)
	{
		CustomWidgetTransmogBox widget = uiSlots.get(slot);
		TransmogPreset preset = getCurrentPreset();
		switch (op)
		{
			case SET_ITEM:
				isSearching = true;
				itemSearch.tooltipText("Set as " + slot.getName())
					.prompt("Choose for " + slot.getName() + " slot")
					.slot(slot)
					.onItemSelected((id, name) ->
					{
						widget.setItem(id, name);
						isSearching = false;
						preset.setSlot(slot, id, name);
						updateTransmog();
					})
					.build();

				break;
			case CLEAR:
				chatboxPanelManager.close();
				widget.setEmpty();
				preset.clearSlot(slot);
				updateTransmog();
				break;
			case FORCE_DEFAULT:
				chatboxPanelManager.close();
				widget.setDefault();
				preset.setDefaultSlot(slot);
				updateTransmog();
				break;
		}
	}

	private void createUI()
	{
		final Widget parent = getContainer();
		for (Widget child : parent.getNestedChildren())
		{
			child.setHidden(true);
			child.revalidate();
		}

		 stateBox = new CustomWidgetCheckbox(
			parent,
			"Transmog State",
			config.transmogActive() && isDefaultStateSet(),
			1212,
			1213,
			state ->
		{
			if (!isDefaultStateSet())
			{
				stateBox.toggle();
				return;
			}
			config.transmogActive(state);
			updateTransmog();
		});

		stateBox.setSize(15, 15);
		stateBox.create();

		stateBox.layout(3, 30);


		// Create bottom buttons

		CustomWidgetActionButtonWithText selectPresetButton = new CustomWidgetActionButtonWithText(
			parent,
			"",
			909,
			config.currentPreset() + "",
			this::selectTransmog
		);
		selectPresetButton.setSize(40, 40);
		selectPresetButton.setIconSize(36, 36);
		selectPresetButton.create();
		selectPresetButton.addOption(1, "Select Preset <col=ff981f>1");
		selectPresetButton.addOption(2, "Select Preset <col=ff981f>2");
		selectPresetButton.addOption(3, "Select Preset <col=ff981f>3");
		selectPresetButton.addOption(4, "Select Preset <col=ff981f>4");
		selectPresetButton.layout(7, 213);

		savePresetButton = new CustomWidgetActionButton(
			parent,
			"",
			1194,
			op ->
			{
				if (op == 0)
				{
					op = config.currentPreset();
				}
				presets.set(op - 1, getCurrentPreset());
				config.savePreset(getCurrentPreset(), op);
				selectTransmog(op);
			}
		);
		savePresetButton.setSize(40, 40);
		savePresetButton.setIconSize(16, 16);
		savePresetButton.create();
		savePresetButton.addOption(1, "Save to Preset <col=ff981f>1");
		savePresetButton.addOption(2, "Save to Preset <col=ff981f>2");
		savePresetButton.addOption(3, "Save to Preset <col=ff981f>3");
		savePresetButton.addOption(4, "Save to Preset <col=ff981f>4");
		savePresetButton.layout(52, 213);

		deletePresetButton = new CustomWidgetActionButton(
			parent,
			"",
			1235,
			op ->
			{
				if (op == 0)
				{
					op = config.currentPreset();
				}
				presets.set(op - 1, null);
				updateTransmog();
				config.savePresets();
				uiSlots.values().forEach(CustomWidgetTransmogBox::setEmpty);
			}
		);
		deletePresetButton.setSize(40, 40);
		deletePresetButton.setIconSize(26, 20);
		deletePresetButton.create();
		deletePresetButton.addOption(1, "Delete Preset <col=ff981f>1");
		deletePresetButton.addOption(2, "Delete Preset <col=ff981f>2");
		deletePresetButton.addOption(3, "Delete Preset <col=ff981f>3");
		deletePresetButton.addOption(4, "Delete Preset <col=ff981f>4");
		deletePresetButton.layout(97, 213);

		saveDefaultStateButton = new CustomWidgetActionButton(
			parent,
			"Default State",
			115,
			op -> updateDefault()
		);
		saveDefaultStateButton.setSize(40, 40);
		saveDefaultStateButton.setIconSize(30, 30);
		saveDefaultStateButton.create();
		saveDefaultStateButton.setIconSprite(isDefaultStateSet() ? 115 : 118);
		saveDefaultStateButton.addOption(1, "Save as");
		saveDefaultStateButton.layout(142, 213);


		// Create Slots
		TransmogPreset preset = getCurrentPreset();
		int x = 0;
		for (TransmogSlot slot : TransmogSlot.values())
		{
			if (slot.getSlotType() == TransmogSlot.SlotType.ITEM)
			{
				CustomWidgetTransmogBox box = new CustomWidgetTransmogBox(parent, slot, this::onTransmogUISlotClicked);
				box.create();
				Integer item = preset.getId(slot, false);
				if (item == null)
				{
					box.setEmpty();
				}
				else if (item == -1)
				{
					box.setDefault();
				}
				else
				{
					box.setItem(item, itemManager.getItemComposition(item).getName());
				}

				box.layout(40 * (x % 5), 70 + 40 * (x / 5));
				uiSlots.put(slot, box);
			}
			x += 1;
		}

		// Create blocker
		blockerBox = new CustomWidgetBlockerBox(parent, "");
		blockerBox.setSize(parent.getWidth(), parent.getHeight());
		blockerBox.create();
		blockerBox.layout(0, 0);
		if (isDefaultStateSet())
		{
			blockerBox.setHidden(true);
		}

		parent.revalidate();

		// Sprite 170 for the backgrounds

		/*
		*	For custom sprites in widgets:
		* 		Add the desired sprites into the client using `spriteManager.addSpriteOverrides`
		* 		Those sprites should use a negative index that's pretty far out (Wiki used -300)
		* 		Assigning them as the sprite ID will work
		*/
	}

	private void removeUI()
	{
		final Widget parent = getContainer();
		parent.deleteAllChildren();
		for (Widget child : parent.getNestedChildren())
		{
			child.setHidden(false);
			child.revalidate();
		}
		parent.revalidate();
		if (isSearching)
		{
			chatboxPanelManager.close();
			isSearching = false;
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
				if (id > 0 && id <= TransmogPreset.PRESET_COUNT)
				{
					config.currentPreset(id);
					updateTransmog();
				}
				break;
			case "s":
				config.savePresets();
				break;
			case "u":
				createInitialUI();
				break;
			case "h":
				uiSlots.get(TransmogSlot.HEAD).setItem(22319, "Zuk");
				break;
			case "h2":
				uiSlots.get(TransmogSlot.HEAD).setDefault();
		}
	}
}

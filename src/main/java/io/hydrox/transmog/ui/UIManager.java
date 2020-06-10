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
package io.hydrox.transmog.ui;

import io.hydrox.transmog.TransmogPreset;
import io.hydrox.transmog.TransmogSlot;
import io.hydrox.transmog.TransmogrificationConfigManager;
import io.hydrox.transmog.TransmogrificationManager;
import static io.hydrox.transmog.ui.MenuOps.CLEAR;
import static io.hydrox.transmog.ui.MenuOps.FORCE_DEFAULT;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class UIManager
{
	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private CustomItemSearch itemSearch;

	@Inject
	private ItemManager itemManager;

	@Inject
	private TransmogrificationConfigManager config;

	@Inject
	private TransmogrificationManager manager;

	private CustomWidgetCheckbox stateBox;
	@Getter
	private CustomWidgetActionButton savePresetButton;
	@Getter
	private CustomWidgetActionButton deletePresetButton;
	@Getter
	private CustomWidgetActionButton saveDefaultStateButton;
	@Getter
	private CustomWidgetBlockerBox blockerBox;

	@Getter
	@Setter
	private boolean uiCreated = false;

	@Getter
	private final Map<TransmogSlot, CustomWidgetTransmogBox> uiSlots = new HashMap<>();

	private boolean isSearching = false;

	public void shutDown()
	{
		clientThread.invoke(this::removeUI);
		uiSlots.clear();
		uiCreated = false;
		if (isSearching)
		{
			chatboxPanelManager.close();
			isSearching = false;
		}
	}

	public void onResizeableChanged()
	{
		uiCreated = false;
		uiSlots.clear();
		removeUI();
		createInitialUI();
	}

	private Widget getContainer()
	{
		final Widget equipment = client.getWidget(WidgetInfo.EQUIPMENT);
		return equipment.getParent();
	}

	public void createInitialUI()
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
		TransmogPreset preset = manager.getCurrentPreset();
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
						manager.updateTransmog();
					})
					.build();

				break;
			case CLEAR:
				chatboxPanelManager.close();
				widget.setEmpty();
				preset.clearSlot(slot);
				manager.updateTransmog();
				break;
			case FORCE_DEFAULT:
				chatboxPanelManager.close();
				widget.setDefault();
				preset.setDefaultSlot(slot);
				manager.updateTransmog();
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
			config.transmogActive() && manager.isDefaultStateSet(),
			1212,
			1213,
			state ->
			{
				if (!manager.isDefaultStateSet())
				{
					stateBox.toggle();
					return;
				}
				config.transmogActive(state);
				manager.updateTransmog();
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
			manager::selectTransmog
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
				manager.copyCurrentPresetTo(op);
				manager.save();
				manager.selectTransmog(op);
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
				manager.setPreset(op, null);
				manager.updateTransmog();
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
			op -> manager.updateDefault()
		);
		saveDefaultStateButton.setSize(40, 40);
		saveDefaultStateButton.setIconSize(30, 30);
		saveDefaultStateButton.create();
		saveDefaultStateButton.setIconSprite(manager.isDefaultStateSet() ? 115 : 118);
		saveDefaultStateButton.addOption(1, "Save as");
		saveDefaultStateButton.layout(142, 213);


		// Create Slots
		TransmogPreset preset = manager.getCurrentPreset();
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
		if (manager.isDefaultStateSet())
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

	public void removeUI()
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
}

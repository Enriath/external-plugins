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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.hydrox.transmog.FacialHairMapping;
import io.hydrox.transmog.HairMapping;
import io.hydrox.transmog.Mapping;
import io.hydrox.transmog.MappingMapping;
import io.hydrox.transmog.SleeveMapping;
import io.hydrox.transmog.TransmogPreset;
import io.hydrox.transmog.TransmogSlot;
import io.hydrox.transmog.TransmogSlot.SlotType;
import io.hydrox.transmog.TransmogrificationConfigManager;
import io.hydrox.transmog.TransmogrificationManager;
import static io.hydrox.transmog.ui.MenuOps.CLEAR;
import static io.hydrox.transmog.ui.MenuOps.FORCE_DEFAULT;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import org.apache.commons.lang3.tuple.Pair;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class UIManager
{
	private static final Point BASE_POSITION = new Point(148, 4);
	private static final ImmutableMap<TransmogSlot, Point> SLOT_POSITIONS = ImmutableMap.<TransmogSlot, Point>builder()
		.put(TransmogSlot.JAW, new Point(-82, 0))
		.put(TransmogSlot.HAIR, new Point(-41, 0))
		.put(TransmogSlot.HEAD, new Point(0, 0))
		.put(TransmogSlot.CAPE, new Point(-41, 39))
		.put(TransmogSlot.NECK, new Point(0, 39))
		.put(TransmogSlot.SLEEVES, new Point(-56, 78))
		.put(TransmogSlot.TORSO, new Point(0, 78))
		.put(TransmogSlot.LEGS, new Point(0, 118))
		.put(TransmogSlot.HANDS, new Point(-56, 158))
		.put(TransmogSlot.BOOTS, new Point(0, 158))
		.build();

	private static final List<Pair<Rectangle, Integer>> INTER_SLOT_BRACERS = ImmutableList.<Pair<Rectangle, Integer>>builder()
		.add(Pair.of(new Rectangle(0, 36, 36, 122), 172))
		.add(Pair.of(new Rectangle(-56, 114, 36, 44), 172))
		.add(Pair.of(new Rectangle(-46, 0, 46, 36), 173))
		.add(Pair.of(new Rectangle(-5, 39, 5, 36), 173))
		.add(Pair.of(new Rectangle(-20, 78, 20, 36), 173))
		.build();

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private CustomItemSearch itemSearch;

	@Inject
	private CustomSpriteSearch spriteSearch;

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
				if (slot.getSlotType() == SlotType.SPECIAL)
				{
					CustomSpriteSearch s = spriteSearch;
					s.setTooltipText("Set as " + slot.getName());
					s.setPrompt("Choose for " + slot.getName() + " slot");
					s.setSlot(slot);
					s.setOnItemSelected((m) ->
					{
						widget.setContent(m.modelId(), m.prettyName());
						isSearching = false;
						preset.setSlot(slot, m.kitId(), m.prettyName());
						manager.updateTransmog();
					});

					switch (slot)
					{
						case HAIR:
							s.setSource(HairMapping.values());
							break;
						case JAW:
							s.setSource(FacialHairMapping.values());
							break;
						case SLEEVES:
							s.setSource(SleeveMapping.values());
							break;
					}

					s.build();
				}
				else
				{
					CustomItemSearch i = itemSearch;
					i.setTooltipText("Set as " + slot.getName());
					i.setPrompt("Choose for " + slot.getName() + " slot");
					i.setSlot(slot);
					i.setOnItemSelected((id, name) ->
					{
						widget.setContent(id, name);
						isSearching = false;
						preset.setSlot(slot, id, name);
						manager.updateTransmog();
					});
					i.build();
				}
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

		// Create inter-slot bracers
		for (Pair<Rectangle, Integer> bracer : INTER_SLOT_BRACERS)
		{
			final Rectangle posData = bracer.getLeft();
			Widget bracerWidget = parent.createChild(-1, WidgetType.GRAPHIC);
			bracerWidget.setOriginalX(BASE_POSITION.getX() + posData.x);
			bracerWidget.setOriginalY(BASE_POSITION.getY() + posData.y);
			bracerWidget.setOriginalWidth(posData.width);
			bracerWidget.setOriginalHeight(posData.height);
			bracerWidget.setSpriteId(bracer.getRight());
			bracerWidget.setSpriteTiling(true);
			bracerWidget.revalidate();
		}

		// Create Slots
		TransmogPreset preset = manager.getCurrentPreset();
		for (TransmogSlot slot : TransmogSlot.values())
		{
			final CustomWidgetTransmogBox box;
			if (slot.getSlotType() == TransmogSlot.SlotType.ITEM)
			{
				box = new CustomWidgetTransmogBox(SlotType.ITEM, parent, slot, this::onTransmogUISlotClicked);
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
					box.set(item, itemManager.getItemComposition(item).getName());
				}
			}
			else
			{
				box = new CustomWidgetTransmogBox(SlotType.SPECIAL, parent, slot, this::onTransmogUISlotClicked);
				box.create();
				Integer contents = preset.getId(slot, false);
				if (contents == null)
				{
					box.setEmpty();
				}
				else if (contents == -1)
				{
					box.setDefault();
				}
				else
				{
					final Mapping mapping = MappingMapping.fromSlot(slot).getFromModel().apply(contents);
					box.set(mapping.modelId(), mapping.prettyName());
				}
			}
			final Point pos = SLOT_POSITIONS.get(slot);
			box.layout(BASE_POSITION.getX() + pos.getX(), BASE_POSITION.getY() + pos.getY());
			uiSlots.put(slot, box);
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

/*
 * Copyright (c) 2021, Hydrox6 <ikada@protonmail.ch>
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
import com.google.inject.Inject;
import io.hydrox.transmog.FacialHairMapping;
import io.hydrox.transmog.HairMapping;
import io.hydrox.transmog.Mapping;
import io.hydrox.transmog.MappingMapping;
import io.hydrox.transmog.SleeveMapping;
import io.hydrox.transmog.TransmogPreset;
import io.hydrox.transmog.TransmogSlot;
import io.hydrox.transmog.TransmogrificationManager;
import io.hydrox.transmog.TransmogrificationPlugin;
import static io.hydrox.transmog.ui.MenuOps.CLEAR;
import static io.hydrox.transmog.ui.MenuOps.HIDE;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
import lombok.Getter;
import net.runelite.api.FontID;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import org.apache.commons.lang3.tuple.Pair;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTab extends CustomTab
{
	private static final String FORCE_RIGHT_CLICK_WIDGET_NAME = "<col=004356>";
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
		.add(Pair.of(new Rectangle(0, 36, 36, 122), SpriteID.IRON_RIVETS_VERTICAL))
		.add(Pair.of(new Rectangle(-56, 114, 36, 44), SpriteID.IRON_RIVETS_VERTICAL))
		.add(Pair.of(new Rectangle(-46, 0, 46, 36), SpriteID.IRON_RIVETS_HORIZONTAL))
		.add(Pair.of(new Rectangle(-5, 39, 5, 36), SpriteID.IRON_RIVETS_HORIZONTAL))
		.add(Pair.of(new Rectangle(-20, 78, 20, 36), SpriteID.IRON_RIVETS_HORIZONTAL))
		.build();


	private final ChatboxPanelManager chatboxPanelManager;
	private final CustomItemSearch itemSearch;
	private final CustomSpriteSearch spriteSearch;
	private final ItemManager itemManager;
	private final TransmogrificationManager manager;
	private final TransmogrificationPlugin plugin;
	private final UIManager uiManager;

	@Getter
	private final Map<TransmogSlot, CustomWidgetTransmogBox> uiSlots = new HashMap<>();

	private Widget helpText;
	private CustomWidgetPlayerPreview playerPreview;
	private CustomWidgetActionButton saveDefaultStateButton;
	private CustomWidgetBlockerBox blockerBox;

	@Inject
	MainTab(ChatboxPanelManager chatboxPanelManager, CustomItemSearch itemSearch, CustomSpriteSearch spriteSearch,
			ItemManager itemManager, TransmogrificationPlugin plugin, TransmogrificationManager manager)
	{
		this.chatboxPanelManager = chatboxPanelManager;
		this.itemSearch = itemSearch;
		this.spriteSearch = spriteSearch;
		this.itemManager = itemManager;
		this.manager = manager;
		this.plugin = plugin;
		this.uiManager = plugin.getUIManager();
	}

	@Override
	void shutDown()
	{
		uiSlots.clear();
	}

	@Override
	void destroy()
	{
		playerPreview = null;
	}

	@Override
	void create()
	{
		final Widget parent = uiManager.getContainer();
		uiManager.getEquipmentOverlay().create(true);

		helpText = parent.createChild(-1, WidgetType.TEXT);
		helpText.setTextColor(CustomWidget.fromRGB(Color.YELLOW));
		helpText.setTextShadowed(true);
		helpText.setFontId(FontID.PLAIN_11);
		helpText.setOriginalWidth(90);
		helpText.setOriginalHeight(150);
		helpText.setOriginalX(3);
		helpText.setOriginalY(60);
		helpText.setText("Choose the items you want to see yourself wearing. If you're having glitchy collision, remove your hair/sleeves/beard. Removing hair requires toggling the transmog. Please report broken items to the support link.");
		helpText.setHidden(true);
		helpText.revalidate();

		playerPreview = new CustomWidgetPlayerPreview(parent, "Preview");
		playerPreview.create();
		playerPreview.layout(3, 60);


		CustomWidgetToggleButton guideButton = new CustomWidgetToggleButton(
			parent,
			"Help",
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL,
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED,
			CustomSprites.QUESTION_MARK.getSpriteId(),
			selected ->
			{
				helpText.setHidden(!selected);
				playerPreview.setHidden(selected);
			});
		guideButton.setVerbs("Show", "Hide");
		guideButton.setSize(25, 25);
		guideButton.setIconSize(8, 17);
		guideButton.create();

		guideButton.layout(3, 30);

		// Create bottom buttons

		CustomWidgetActionButton selectPresetButton = new CustomWidgetActionButton(
			parent,
			"Presets",
			SpriteID.TAB_EMOTES,
			op ->
			{
				uiManager.removeCustomUI();
				uiManager.hideVanillaUI();
				uiManager.createTab(uiManager.getPresetTab());
			}
		);
		selectPresetButton.setSize(40, 40);
		selectPresetButton.setIconSize(36, 36);
		selectPresetButton.create();
		selectPresetButton.addOption(1, "Select Preset <col=ff981f>1");
		selectPresetButton.addOption(2, "Select Preset <col=ff981f>2");
		selectPresetButton.addOption(3, "Select Preset <col=ff981f>3");
		selectPresetButton.addOption(4, "Select Preset <col=ff981f>4");
		selectPresetButton.layout(7, 213);

		/*
		savePresetButton = new CustomWidgetActionButton(
			parent,
			FORCE_RIGHT_CLICK_WIDGET_NAME,
			SpriteID.DUEL_ARENA_SAVE_PRESET,
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
			FORCE_RIGHT_CLICK_WIDGET_NAME,
			SpriteID.BANK_RAID_SEND_TO_TRASH,
			op ->
			{
				if (op == 0)
				{
					op = config.currentPreset();
				}
				manager.setPreset(op, null);
				manager.updateTransmog();
				config.savePresets();
				uiSlots.forEach((slot, widget) ->
				{
					if (slot.getSlotType() == SlotType.SPECIAL)
					{
						widget.setDefault();
					}
					else
					{
						widget.setEmpty();
					}
				});
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
		*/

		Widget remembrance = parent.createChild(-1, WidgetType.TEXT);
		remembrance.setTextColor(CustomWidget.fromRGB(Color.YELLOW));
		remembrance.setTextShadowed(true);
		remembrance.setFontId(FontID.PLAIN_11);
		remembrance.setOriginalWidth(92);
		remembrance.setOriginalHeight(40);
		remembrance.setOriginalX(52);
		remembrance.setOriginalY(213);
		remembrance.setText("Save and delete did nothing.     They will return when fixed.");
		remembrance.revalidate();

		saveDefaultStateButton = new CustomWidgetActionButton(
			parent,
			"Default State",
			SpriteID.PRAYER_THICK_SKIN,
			op ->
			{
				if (manager.updateDefault(op))
				{
					saveDefaultStateButton.setIconSprite(115);
					blockerBox.setHidden(true);
				}
			}
		);
		saveDefaultStateButton.setSize(40, 40);
		saveDefaultStateButton.setIconSize(30, 30);
		saveDefaultStateButton.create();
		saveDefaultStateButton.setIconSprite(manager.isDefaultStateSet() ? SpriteID.PRAYER_THICK_SKIN : SpriteID.PRAYER_ROCK_SKIN);
		saveDefaultStateButton.addOption(1, "Save as");
		saveDefaultStateButton.addOption(2, "Force save as");
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
				box = new CustomWidgetTransmogBox(TransmogSlot.SlotType.ITEM, parent, slot, this::onTransmogUISlotClicked);
				box.create();
				Integer item = preset.getIdForSlot(slot, false);
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
				box = new CustomWidgetTransmogBox(TransmogSlot.SlotType.SPECIAL, parent, slot, this::onTransmogUISlotClicked);
				box.create();
				Integer contents = preset.getIdForSlot(slot, false);
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

		updateTutorial(plugin.isEmptyEquipment());

		parent.revalidate();

		manager.updateTransmog();
	}

	@Override
	void onClientTick()
	{
		if (playerPreview != null)
		{
			playerPreview.tickRotation();
		}
	}

	@Override
	public void updateTutorial(boolean equipmentState)
	{
		if (!manager.isDefaultStateSet() && blockerBox != null)
		{
			blockerBox.setTutorialState(equipmentState ? 2 : 1);
		}
	}

	@Override
	public void loadPreset(TransmogPreset preset)
	{
		for (TransmogSlot slot : TransmogSlot.values())
		{
			uiSlots.get(slot).set(preset.getIdForSlot(slot, false), preset.getName(slot));
		}
	}

	private void onTransmogUISlotClicked(int op, TransmogSlot slot)
	{
		CustomWidgetTransmogBox widget = uiSlots.get(slot);
		TransmogPreset preset = manager.getCurrentPreset();
		switch (op)
		{
			case SET_ITEM:
				if (uiManager.isSearching())
				{
					uiManager.closeSearch();
				}
				uiManager.setSearching(true);
				if (slot.getSlotType() == TransmogSlot.SlotType.SPECIAL)
				{
					CustomSpriteSearch s = spriteSearch;
					s.setTooltipText("Set as " + slot.getName());
					s.setPrompt("Choose for " + slot.getName() + " slot");
					s.setSlot(slot);
					s.setOnItemSelected((m) ->
					{
						widget.setContent(m.modelId(), m.prettyName());
						uiManager.setSearching(false);
						preset.setSlot(slot, m.kitId(), m.prettyName());
						manager.updateTransmog();
						manager.saveActivePreset();
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
						uiManager.setSearching(false);
						preset.setSlot(slot, id, name);
						manager.updateTransmog();
						manager.saveActivePreset();
					});
					i.build();
				}
				break;
			case CLEAR:
				chatboxPanelManager.close();
				widget.setEmpty();
				preset.clearSlot(slot);
				manager.updateTransmog();
				manager.saveActivePreset();
				break;
			case HIDE:
				chatboxPanelManager.close();
				widget.setDefault();
				preset.setDefaultSlot(slot);
				manager.updateTransmog();
				manager.saveActivePreset();
				break;
		}
	}
}

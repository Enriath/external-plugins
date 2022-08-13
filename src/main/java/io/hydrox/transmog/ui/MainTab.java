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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.hydrox.transmog.FacialHairMapping;
import io.hydrox.transmog.HairMapping;
import io.hydrox.transmog.Mapping;
import io.hydrox.transmog.MappingMapping;
import io.hydrox.transmog.SleeveMapping;
import io.hydrox.transmog.TransmogPartyManager;
import io.hydrox.transmog.TransmogPreset;
import io.hydrox.transmog.TransmogSlot;
import io.hydrox.transmog.TransmogrificationManager;
import io.hydrox.transmog.TransmogrificationPlugin;
import io.hydrox.transmog.config.TransmogrificationConfigManager;
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
import net.runelite.client.game.chatbox.ChatboxItemSearch;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.tuple.Pair;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MainTab extends CustomTab
{
	private static final Point BASE_POSITION = new Point(148, 4);
	private static final ImmutableMap<TransmogSlot, Point> SLOT_POSITIONS = ImmutableMap.<TransmogSlot, Point>builder()
		.put(TransmogSlot.JAW, new Point(-76, 0))
		.put(TransmogSlot.HAIR, new Point(-38, 0))
		.put(TransmogSlot.HEAD, new Point(0, 0))
		.put(TransmogSlot.CAPE, new Point(-38, 38))
		.put(TransmogSlot.NECK, new Point(0, 38))
		.put(TransmogSlot.SLEEVES, new Point(-56, 76))
		.put(TransmogSlot.TORSO, new Point(0, 76))
		.put(TransmogSlot.LEGS, new Point(0, 115))
		.put(TransmogSlot.HANDS, new Point(-56, 154))
		.put(TransmogSlot.BOOTS, new Point(0, 154))
		.build();

	private static final List<Pair<Rectangle, Integer>> INTER_SLOT_BRACERS = ImmutableList.<Pair<Rectangle, Integer>>builder()
		.add(Pair.of(new Rectangle(0, 36, 36, 122), SpriteID.IRON_RIVETS_VERTICAL))
		.add(Pair.of(new Rectangle(-56, 114, 36, 44), SpriteID.IRON_RIVETS_VERTICAL))
		.add(Pair.of(new Rectangle(-46, 0, 46, 36), SpriteID.IRON_RIVETS_HORIZONTAL))
		.add(Pair.of(new Rectangle(-5, 39, 5, 36), SpriteID.IRON_RIVETS_HORIZONTAL))
		.add(Pair.of(new Rectangle(-20, 78, 20, 36), SpriteID.IRON_RIVETS_HORIZONTAL))
		.build();


	private final ChatboxPanelManager chatboxPanelManager;
	private final ChatboxItemSearch allItemSearch;
	private final CustomItemSearch slotItemSearch;
	private final CustomSpriteSearch spriteSearch;
	private final ItemManager itemManager;
	private final TransmogPartyManager partyManager;
	private final TransmogrificationManager manager;
	private final UIManager uiManager;
	private final TransmogrificationConfigManager config;

	@Getter
	private final Map<TransmogSlot, CustomWidgetTransmogBox> uiSlots = new HashMap<>();

	private Widget helpText;
	private CustomWidgetPlayerPreview playerPreview;
	private CustomWidgetActionButton saveDefaultStateButton;
	private CustomWidgetBlockerBox blockerBox;
	private CustomWidgetConfigButton presetExtraDataButton;
	private CustomWidgetNamePlate nameplate;

	@Inject
	MainTab(ChatboxPanelManager chatboxPanelManager, ChatboxItemSearch allItemSearch, CustomItemSearch slotItemSearch,
			CustomSpriteSearch spriteSearch, ItemManager itemManager, TransmogrificationPlugin plugin,
			TransmogrificationManager manager, TransmogPartyManager partyManager, TransmogrificationConfigManager config)
	{
		this.chatboxPanelManager = chatboxPanelManager;
		this.allItemSearch = allItemSearch;
		this.slotItemSearch = slotItemSearch;
		this.spriteSearch = spriteSearch;
		this.itemManager = itemManager;
		this.partyManager = partyManager;
		this.manager = manager;
		this.uiManager = plugin.getUIManager();
		this.config = config;
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

		CustomWidgetToggleButton shareButton = new CustomWidgetToggleButton(
			parent,
			"Share to Party",
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL,
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED,
			CustomSprites.PARTY.getSpriteId(),
			partyManager::setShareWithParty);
		shareButton.setVerbs("Enable", "Disable");
		shareButton.setSize(25, 25);
		shareButton.setIconSize(19, 19);
		shareButton.create();
		if (config.transmitToParty())
		{
			shareButton.toggle();
		}

		shareButton.layout(30, 30);

		// Create bottom buttons

		CustomWidgetActionButton selectPresetButton = new CustomWidgetActionButton(
			parent,
			"",
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
		selectPresetButton.addOption(1, "Choose " + UIManager.ORANGE_COLOUR_WIDGET_NAME + "Preset");
		selectPresetButton.layout(7, 213);

		presetExtraDataButton = new CustomWidgetConfigButton(
			parent,
			UIManager.FORCE_RIGHT_CLICK_WIDGET_NAME,
			op ->
			{
				switch (op)
				{
					case 1:
						chatboxPanelManager.openTextInput("Set name for this preset")
							.value(Strings.nullToEmpty(manager.getCurrentPreset().getName()))
							.onDone((content) ->
							{
								if (content == null)
								{
									return;
								}

								content = Text.removeTags(content).trim();
								manager.getCurrentPreset().setName(content);
								manager.saveCurrentPreset();
								nameplate.setText(manager.getCurrentPreset().getDisplayName());

							}).build();
						break;
					case 2:
						allItemSearch
							.tooltipText("Select")
							.onItemSelected((itemId) ->
							{
								manager.getCurrentPreset().setIcon(itemId);
								manager.saveCurrentPreset();
								presetExtraDataButton.setItemIcon(itemId);
							})
							.prompt("Select icon for this preset")
							.build();
						break;
					case 3:
						manager.getCurrentPreset().setIcon(-1);
						manager.saveCurrentPreset();
						presetExtraDataButton.setItemIcon(-1);
						break;
				}
			}
		);
		presetExtraDataButton.setSize(40, 40);
		presetExtraDataButton.create();
		presetExtraDataButton.addOption(0, UIManager.ORANGE_COLOUR_WIDGET_NAME + "Preset Config:");
		presetExtraDataButton.addOption(1, "Set Name");
		presetExtraDataButton.setItemIcon(manager.getCurrentPreset().getIcon());
		presetExtraDataButton.layout(97, 213);

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

		nameplate = new CustomWidgetNamePlate(parent, 92);
		nameplate.create();
		nameplate.setText(manager.getCurrentPreset().getDisplayName());
		nameplate.layout(92, 196);

		updateTutorial(manager.isEmptyEquipment());

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
						manager.saveCurrentPreset();
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
					CustomItemSearch i = slotItemSearch;
					i.setTooltipText("Set as " + slot.getName());
					i.setPrompt("Choose for " + slot.getName() + " slot");
					i.setSlot(slot);
					i.setOnItemSelected((id, name) ->
					{
						widget.setContent(id, name);
						uiManager.setSearching(false);
						preset.setSlot(slot, id, name);
						manager.updateTransmog();
						manager.saveCurrentPreset();
					});
					i.build();
				}
				break;
			case CLEAR:
				chatboxPanelManager.close();
				widget.setEmpty();
				preset.clearSlot(slot);
				manager.updateTransmog();
				manager.saveCurrentPreset();
				break;
			case HIDE:
				chatboxPanelManager.close();
				widget.setDefault();
				preset.setDefaultSlot(slot);
				manager.updateTransmog();
				manager.saveCurrentPreset();
				break;
		}
	}
}

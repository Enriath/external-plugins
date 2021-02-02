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
import io.hydrox.transmog.TransmogrificationPlugin;
import static io.hydrox.transmog.ui.MenuOps.CLEAR;
import static io.hydrox.transmog.ui.MenuOps.HIDE;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import org.apache.commons.lang3.tuple.Pair;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Slf4j
public class UIManager
{
	private static final int SCROLLBAR_HEIGHT = 203;
	private static final int SCROLLBAR_WIDTH = 16;
	private static final int SCROLLBAR_BUTTON_HEIGHT = 16;
	private static final int SCROLLBAR_PADDING = 1;
	private static final int SCROLLBAR_TRACK_HEIGHT = SCROLLBAR_HEIGHT - (SCROLLBAR_BUTTON_HEIGHT * 2);
	private static final int SCROLLBAR_THUMB_CAP_HEIGHT = 5;

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
	private TransmogrificationPlugin plugin;

	@Inject
	private TransmogrificationManager manager;

	private CustomWidgetToggleButton stateToggle;
	private Widget helpText;
	private Widget pvpBlocker;


	private CustomWidgetPlayerPreview playerPreview;
	/*
	@Getter
	private CustomWidgetActionButton savePresetButton;
	@Getter
	private CustomWidgetActionButton deletePresetButton;
	*/
	private CustomWidgetActionButton saveDefaultStateButton;
	private CustomWidgetBlockerBox blockerBox;

	private Widget[] presetScrollbars;
	private Rectangle boxBounds;
	private int scrollPos = 0;
	private int maxScrollPos = 0;

	@Getter
	@Setter
	private boolean uiCreated = false;

	@Getter
	private final Map<TransmogSlot, CustomWidgetTransmogBox> uiSlots = new HashMap<>();

	private boolean isSearching = false;

	public void shutDown()
	{
		clientThread.invoke(this::removeCustomUI);
		uiSlots.clear();
		uiCreated = false;
		if (isSearching)
		{
			chatboxPanelManager.close();
			isSearching = false;
		}
	}

	public void onPvpChanged(boolean newValue)
	{
		if (pvpBlocker != null)
		{
			pvpBlocker.setHidden(!newValue);
		}
	}

	public void onResizeableChanged()
	{
		uiCreated = false;
		uiSlots.clear();
		removeCustomUI();
		createEquipmentTabUI(false);
	}

	private Widget getContainer()
	{
		final Widget equipment = client.getWidget(WidgetInfo.EQUIPMENT);
		return equipment.getParent();
	}

	public void createEquipmentTabUI(boolean uiActive)
	{
		final Widget parent = getContainer();
		CustomWidgetToggleButton showUI = new CustomWidgetToggleButton(
			parent,
			"Transmogrification",
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL,
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED,
			1654, // Unmapped
			selected ->
			{
				if (selected)
				{
					hideVanillaUI();
					createMainUI();
				}
				else
				{
					removeCustomUI();
					createEquipmentTabUI(false);
				}
			});
		showUI.setVerbs("Edit", "Exit");
		showUI.setSize(25, 25);
		showUI.setIconSize(32, 32);
		showUI.create();
		if (uiActive)
		{
			showUI.toggle();
		}

		showUI.layout(3, 3);

		stateToggle = new CustomWidgetToggleButton(
			parent,
			"Transmogrification",
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL,
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED,
			CustomSprites.TRANSMOG_LOGO.getSpriteId(),
			state ->
			{
				if ((!manager.isDefaultStateSet() && state) || plugin.isInPvpSituation())
				{
					stateToggle.toggle();
					manager.hintDefaultState();
					return;
				}
				config.transmogActive(state);
				manager.updateTransmog();
			}
		);
		stateToggle.setVerbs("Enable", "Disable");
		stateToggle.setSize(25, 25);
		stateToggle.setIconSize(16, 13);
		stateToggle.create();
		if (config.transmogActive() && manager.isDefaultStateSet())
		{
			stateToggle.toggle();
		}
		stateToggle.layout(30, 3);

		pvpBlocker = parent.createChild(-1, WidgetType.GRAPHIC);
		pvpBlocker.setSpriteId(SpriteID.PLAYER_KILLING_DISABLED_OVERLAY); // Block icon
		pvpBlocker.setOriginalWidth(25);
		pvpBlocker.setOriginalHeight(25);
		pvpBlocker.setHasListener(true);
		pvpBlocker.setNoClickThrough(true);
		pvpBlocker.setOnOpListener((JavaScriptCallback) e -> {});
		pvpBlocker.setOriginalX(30);
		pvpBlocker.setOriginalY(3);
		pvpBlocker.setHidden(!plugin.isInPvpSituation());
		pvpBlocker.setAction(0, "Transmog is disabled in PvP situations");
	}

	private void hideVanillaUI()
	{
		for (Widget child : getContainer().getNestedChildren())
		{
			child.setHidden(true);
			child.revalidate();
		}
	}

	private void createMainUI()
	{
		final Widget parent = getContainer();

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

		CustomWidgetActionButtonWithText selectPresetButton = new CustomWidgetActionButtonWithText(
			parent,
			"Presets",
			SpriteID.TAB_EMOTES,
			config.currentPreset() + "",
			op ->
			{
				removeCustomUI();
				hideVanillaUI();
				createPresetUI();
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

		updateTutorial(plugin.isEmptyEquipment());

		parent.revalidate();

		manager.selectTransmog(config.currentPreset());
	}

	private Map<Widget, Integer> presets = new HashMap<>();

	private void createPresetUI()
	{
		final Widget parent = getContainer();
		// Cache the bounds for scrolling code, which runs off of client thread
		boxBounds = parent.getBounds();
		scrollPos = 0;

		int presetCount = 64;
		int presetsPerLine = 4;
		int presetSize = 32;
		int presetPadding = 11;

		presets.clear();
		// Placeholder for testing purposes
		for (int i = 0; i < presetCount; i++)
		{
			Widget w = parent.createChild(-1, WidgetType.GRAPHIC);
			w.setOriginalWidth(presetSize + presetPadding);
			w.setOriginalHeight(presetSize + presetPadding);
			w.setName("<col=ff981f>");
			w.setSpriteId(170);
			w.setOriginalX((i % presetsPerLine) * (presetSize + presetPadding));
			w.setOriginalY((i / presetsPerLine) * (presetSize + presetPadding));
			w.revalidateScroll();
			presets.put(w, w.getOriginalY());

			Widget w2 = parent.createChild(-1, WidgetType.TEXT);
			w2.setOriginalWidth(presetSize + presetPadding);
			w2.setOriginalHeight(presetSize + presetPadding);
			w2.setName("<col=ff981f>");
			w2.setFontId(496);
			w2.setTextColor(0xffffff);
			w2.setXTextAlignment(WidgetTextAlignment.CENTER);
			w2.setYTextAlignment(WidgetTextAlignment.CENTER);
			w2.setText(i + "");
			w2.setOriginalX((i % presetsPerLine) * (presetSize + presetPadding));
			w2.setOriginalY((i / presetsPerLine) * (presetSize + presetPadding));
			w2.revalidateScroll();
			presets.put(w2, w2.getOriginalY());
		}

		// MAGIC!
		maxScrollPos = Math.max(((int)Math.ceil(presetCount / (double)presetsPerLine) - 5) * (presetSize + presetPadding) + (5 * (presetSize + presetPadding) - 203) + 1, 0);

		// Scrollbar
		Widget scrollUp = parent.createChild(-1, WidgetType.GRAPHIC);
		scrollUp.setOriginalWidth(SCROLLBAR_WIDTH);
		scrollUp.setOriginalHeight(SCROLLBAR_BUTTON_HEIGHT);
		scrollUp.setSpriteId(SpriteID.SCROLLBAR_ARROW_UP);
		scrollUp.setOriginalX(parent.getOriginalWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING);
		scrollUp.setOriginalY(0);
		scrollUp.revalidate();

		Widget scrollDown = parent.createChild(-1, WidgetType.GRAPHIC);
		scrollDown.setOriginalWidth(SCROLLBAR_WIDTH);
		scrollDown.setOriginalHeight(SCROLLBAR_BUTTON_HEIGHT);
		scrollDown.setSpriteId(SpriteID.SCROLLBAR_ARROW_DOWN);
		scrollDown.setOriginalX(parent.getOriginalWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING);
		scrollDown.setOriginalY(SCROLLBAR_HEIGHT - SCROLLBAR_BUTTON_HEIGHT - SCROLLBAR_PADDING);
		scrollDown.revalidate();

		Widget scrollBg = parent.createChild(-1, WidgetType.GRAPHIC);
		scrollBg.setOriginalWidth(SCROLLBAR_WIDTH);
		scrollBg.setOriginalHeight(SCROLLBAR_TRACK_HEIGHT);
		scrollBg.setSpriteId(SpriteID.SCROLLBAR_THUMB_MIDDLE_DARK);
		scrollBg.setOriginalX(parent.getOriginalWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING);
		scrollBg.setOriginalY(SCROLLBAR_BUTTON_HEIGHT);
		scrollBg.setHasListener(true);
		scrollBg.setOnClickListener((JavaScriptCallback) ev -> scrollTo(client.getMouseCanvasPosition().getY() - parent.getBounds().y));
		scrollBg.revalidate();

		Widget scrollBarTop = parent.createChild(-1, WidgetType.GRAPHIC);
		scrollBarTop.setOriginalWidth(SCROLLBAR_WIDTH);
		scrollBarTop.setOriginalHeight(SCROLLBAR_THUMB_CAP_HEIGHT);
		scrollBarTop.setSpriteId(SpriteID.SCROLLBAR_THUMB_TOP);
		scrollBarTop.setOriginalX(parent.getOriginalWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING);
		scrollBarTop.setOriginalY(SCROLLBAR_BUTTON_HEIGHT);
		scrollBarTop.setNoClickThrough(false);
		scrollBarTop.setHasListener(true);
		scrollBarTop.revalidate();

		Widget scrollBarBottom = parent.createChild(-1, WidgetType.GRAPHIC);
		scrollBarBottom.setOriginalWidth(SCROLLBAR_WIDTH);
		scrollBarBottom.setOriginalHeight(SCROLLBAR_THUMB_CAP_HEIGHT);
		scrollBarBottom.setSpriteId(SpriteID.SCROLLBAR_THUMB_BOTTOM);
		scrollBarBottom.setOriginalX(parent.getOriginalWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING);
		scrollBarBottom.setOriginalY(SCROLLBAR_BUTTON_HEIGHT);
		scrollBarBottom.setNoClickThrough(false);
		scrollBarBottom.setHasListener(true);
		scrollBarBottom.revalidate();

		Widget scrollBarCentre = parent.createChild(-1, WidgetType.GRAPHIC);
		scrollBarCentre.setOriginalWidth(SCROLLBAR_WIDTH);
		scrollBarCentre.setOriginalHeight(0);
		scrollBarCentre.setSpriteId(SpriteID.SCROLLBAR_THUMB_MIDDLE);
		scrollBarCentre.setOriginalX(parent.getOriginalWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING);
		scrollBarCentre.setOriginalY(SCROLLBAR_BUTTON_HEIGHT);
		scrollBarCentre.setNoClickThrough(false);
		scrollBarCentre.setHasListener(true);
		scrollBarCentre.revalidate();


		Widget scrollBarControl = parent.createChild(-1, WidgetType.GRAPHIC);
		scrollBarControl.setOriginalWidth(SCROLLBAR_WIDTH);
		scrollBarControl.setOriginalHeight(0);
		scrollBarControl.setOriginalX(parent.getOriginalWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING);
		scrollBarControl.setOriginalY(SCROLLBAR_BUTTON_HEIGHT);
		scrollBarControl.setHasListener(true);
		scrollBarControl.setDragParent(scrollBg);
		scrollBarControl.setNoClickThrough(false);
		scrollBarControl.setOnDragListener((JavaScriptCallback) ev -> scrollTo(client.getMouseCanvasPosition().getY() - parent.getBounds().y));
		scrollBarControl.revalidate();


		presetScrollbars = new Widget[]{scrollBarTop, scrollBarBottom, scrollBarCentre, scrollBarControl};
		//presetScrollbars = new Widget[]{scrollBarTop, scrollBarBottom, scrollBarCentre};
		layoutScrollbar();

		Widget cover = parent.createChild(-1, WidgetType.GRAPHIC);
		cover.setSpriteId(897);
		cover.setSpriteTiling(true);
		cover.setOriginalX(0);
		cover.setOriginalY(204);
		cover.setOriginalWidth(parent.getOriginalWidth());
		cover.setOriginalHeight(parent.getOriginalHeight() - 204);
		cover.revalidate();

		// Create bottom buttons

		CustomWidgetActionButtonWithText selectPresetButton = new CustomWidgetActionButtonWithText(
			parent,
			"Presets",
			SpriteID.TAB_EMOTES,
			config.currentPreset() + "",
			op ->
			{
				removeCustomUI();
				hideVanillaUI();
				createEquipmentTabUI(true);
				createMainUI();
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

		Widget separator = parent.createChild(-1, WidgetType.LINE);
		separator.setOriginalX(-1);
		separator.setOriginalY(203);
		separator.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		separator.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		separator.setOriginalHeight(0);
		separator.setOriginalWidth(6);
		separator.setWidthMode(WidgetSizeMode.MINUS);
		separator.setTextColor(0xaa9f75);
		separator.revalidate();

		/*
		Widget presetContainer = parent.createChild(-1, WidgetType.LAYER);
		presetContainer.setOriginalWidth(parent.getOriginalWidth());
		presetContainer.setOriginalHeight(203);
		presetContainer.setOriginalX(0);
		presetContainer.setOriginalY(0);
		presetContainer.setScrollHeight(Math.max(203, (int) (Math.ceil(presetCount / presetsPerLine) * (presetSize + presetPadding) - presetPadding)));
		presetContainer.revalidate();
		*/


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

		parent.revalidate();

		manager.selectTransmog(config.currentPreset());
	}

	public void removeCustomUI()
	{
		final Widget parent = getContainer();
		parent.deleteAllChildren();
		for (Widget child : parent.getNestedChildren())
		{
			child.setHidden(false);
			child.revalidate();
		}
		playerPreview = null;
		parent.revalidate();
		if (isSearching)
		{
			chatboxPanelManager.close();
			isSearching = false;
		}
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
						manager.save();
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
						manager.save();
					});
					i.build();
				}
				break;
			case CLEAR:
				chatboxPanelManager.close();
				widget.setEmpty();
				preset.clearSlot(slot);
				manager.updateTransmog();
				manager.save();
				break;
			case HIDE:
				chatboxPanelManager.close();
				widget.setDefault();
				preset.setDefaultSlot(slot);
				manager.updateTransmog();
				manager.save();
				break;
		}
	}

	public void tickPreview()
	{
		if (playerPreview != null)
		{
			playerPreview.tickRotation();
		}
	}

	public void updateTutorial(boolean equipmentState)
	{
		if (!manager.isDefaultStateSet() && blockerBox != null)
		{
			blockerBox.setTutorialState(equipmentState ? 2 : 1);
		}
	}

	public void mouseWheelMoved(MouseWheelEvent event)
	{
		if (boxBounds != null && boxBounds.contains(event.getPoint()))
		{
			clientThread.invoke(() -> scrollPresets(event.getUnitsToScroll() * 6));
		}
	}

	public void scrollPresets(int pixels)
	{
		scrollPos += pixels;
		doScroll();
	}

	private void scrollTo(int ypos)
	{
		int thumbHeight = Math.max((int) (SCROLLBAR_HEIGHT / (double)(maxScrollPos + SCROLLBAR_HEIGHT) * SCROLLBAR_TRACK_HEIGHT), SCROLLBAR_THUMB_CAP_HEIGHT * 2);
		int thumbCentRange = SCROLLBAR_TRACK_HEIGHT - thumbHeight;
		int thumbCentTop = SCROLLBAR_BUTTON_HEIGHT + thumbHeight / 2;
		double centrePer = Math.min(1d, Math.max(0d, (ypos - thumbCentTop) / (double)thumbCentRange));
		scrollPos = (int) (centrePer * maxScrollPos);
		doScroll();
	}

	private void doScroll()
	{
		scrollPos = Math.max(0, Math.min(scrollPos, maxScrollPos));
		layoutScrollbar();
		for (Map.Entry<Widget, Integer> p : presets.entrySet())
		{
			Widget w = p.getKey();
			w.setOriginalY(p.getValue() - scrollPos);
			w.revalidate();
		}
	}

	public void layoutScrollbar()
	{
		// Get the percentage of the presets visible on screen, and use that to figure out the size of the scrollbar in
		// the track. Make sure it's always at least large enough for the caps if there are way too many presets
		int thumbHeight = Math.max((int) (SCROLLBAR_HEIGHT / (double)(maxScrollPos + SCROLLBAR_HEIGHT) * SCROLLBAR_TRACK_HEIGHT), SCROLLBAR_THUMB_CAP_HEIGHT * 2);
		int thumbBuffer = SCROLLBAR_TRACK_HEIGHT - thumbHeight;
		// We can then use the gap remaining as the space to move the top of the scrollbar, and therefore the whole bar
		int thumbTop = (int) (scrollPos / (double)(maxScrollPos) * thumbBuffer);

		presetScrollbars[0].setOriginalY(SCROLLBAR_BUTTON_HEIGHT + thumbTop);
		// I'm not sure why the bottom cap is ~6px too far down
		presetScrollbars[1].setOriginalY(SCROLLBAR_BUTTON_HEIGHT + thumbTop + thumbHeight - SCROLLBAR_THUMB_CAP_HEIGHT - 1);
		presetScrollbars[2].setOriginalY(SCROLLBAR_BUTTON_HEIGHT + thumbTop + SCROLLBAR_THUMB_CAP_HEIGHT);
		// The height atm is for the entire bar, so cut off the caps for the centre
		presetScrollbars[2].setOriginalHeight(thumbHeight - SCROLLBAR_THUMB_CAP_HEIGHT * 2);
		presetScrollbars[3].setOriginalY(SCROLLBAR_BUTTON_HEIGHT + thumbTop);
		presetScrollbars[3].setOriginalHeight(thumbHeight);
		Arrays.stream(presetScrollbars).forEach(Widget::revalidate);
	}
}

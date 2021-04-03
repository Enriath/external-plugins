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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.hydrox.transmog.TransmogPreset;
import io.hydrox.transmog.TransmogrificationManager;
import io.hydrox.transmog.config.TransmogrificationConfigManager;
import static io.hydrox.transmog.ui.UIManager.FORCE_RIGHT_CLICK_WIDGET_NAME;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class PresetTab extends CustomTab
{
	private static final int SCROLLBAR_HEIGHT = 203;
	private static final int SCROLLBAR_WIDTH = 16;
	private static final int SCROLLBAR_BUTTON_HEIGHT = 16;
	private static final int SCROLLBAR_PADDING = 1;
	private static final int SCROLLBAR_TRACK_HEIGHT = SCROLLBAR_HEIGHT - (SCROLLBAR_BUTTON_HEIGHT * 2);
	private static final int SCROLLBAR_THUMB_CAP_HEIGHT = 5;
	private static final int PRESETS_PER_LINE = 4;
	private static final int PRESET_PADDING = 11;


	private final Client client;
	private final ClientThread clientThread;
	private final TransmogrificationConfigManager config;
	private final TransmogrificationManager manager;
	private final Provider<UIManager> uiManager;

	private Widget[] presetScrollbars;
	private Rectangle boxBounds;
	private int scrollPos = 0;
	private int maxScrollPos = 0;

	private List<CustomWidgetPresetTabItem> presets = new ArrayList<>();

	@Inject
	PresetTab(Client client, ClientThread clientThread, TransmogrificationConfigManager config,
			  TransmogrificationManager manager, Provider<UIManager> uiManager)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.config = config;
		this.manager = manager;
		this.uiManager = uiManager;
	}

	private UIManager getUIManager()
	{
		return uiManager.get();
	}

	@Override
	void create()
	{
		final Widget parent = getUIManager().getContainer();
		// Cache the bounds for scrolling code, which runs off of client thread
		boxBounds = parent.getBounds();

		presets.clear();
		AtomicInteger index = new AtomicInteger();
		manager.getPresets().stream()
			.filter(Objects::nonNull)
			.forEach(preset ->
			{
				int i = index.getAndIncrement();
				presets.add(createPresetBox(preset, parent, i));
			}
		);

		// Create add button
		CustomWidgetPresetTabItem w = new CustomWidgetAddPresetButton(parent, "Preset", this::addNewPreset);
		w.create();
		w.layout(
			(index.get() % PRESETS_PER_LINE) * (CustomWidgetPresetBox.SIZE + PRESET_PADDING),
			(index.get() / PRESETS_PER_LINE) * (CustomWidgetPresetBox.SIZE + PRESET_PADDING)
		);
		w.addOption(1, "Create new");
		presets.add(w);

		// MAGIC!
		maxScrollPos = Math.max(
			((int)Math.ceil(presets.size() / (double) PRESETS_PER_LINE) - 5)
				* (CustomWidgetPresetBox.SIZE + PRESET_PADDING)
				+ (5 * (CustomWidgetPresetBox.SIZE + PRESET_PADDING) - 203)
				+ 1
			, 0);

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
			"",
			op ->
			{
				getUIManager().removeCustomUI();
				getUIManager().hideVanillaUI();
				getUIManager().getEquipmentOverlay().create(true);
				getUIManager().createTab(getUIManager().getMainTab());
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
*/
		CustomWidgetActionButton deletePresetButton = new CustomWidgetActionButton(
			parent,
			FORCE_RIGHT_CLICK_WIDGET_NAME,
			SpriteID.BANK_RAID_SEND_TO_TRASH,
			op ->
			{
				TransmogPreset current = manager.getCurrentPreset();
				manager.deletePreset(current.getId());
				for (int i = current.getId() - 1; i >= 0; i--)
				{
					if (manager.getPreset(i) != null)
					{
						selectPreset(i);
						return;
					}
				}
				for (int i = current.getId() + 1; i <= config.lastIndex(); i++)
				{
					if (manager.getPreset(i) != null)
					{
						selectPreset(i);
						return;
					}
				}
			}
		);
		deletePresetButton.setSize(40, 40);
		deletePresetButton.setIconSize(26, 20);
		deletePresetButton.create();
		deletePresetButton.addOption(1, "Delete active <col=ff981f>Preset");
		deletePresetButton.layout(52, 213);


		parent.revalidate();
		presets.stream().filter(b -> b.getId() == config.currentPreset()).findFirst().ifPresent(box -> scrollTo(box.y));
	}

	/**
	 * Re-create the overlay, for redoing the very precise ordering of widgets that makes the effect work
	 */
	void recreate()
	{
		final Widget parent = getUIManager().getContainer();
		parent.deleteAllChildren();
		create();
	}

	private CustomWidgetPresetTabItem createPresetBox(TransmogPreset preset, Widget parent, int i)
	{
		CustomWidgetPresetTabItem w = new CustomWidgetPresetBox(preset, parent, this::selectPreset);
		w.create();
		w.layout(
			(i % PRESETS_PER_LINE) * (CustomWidgetPresetBox.SIZE + PRESET_PADDING),
			(i / PRESETS_PER_LINE) * (CustomWidgetPresetBox.SIZE + PRESET_PADDING)
		);
		if (preset.getId() == config.currentPreset())
		{
			w.setSelected(true);
		}
		w.addOption(1, "Select");
		return w;
	}

	private void selectPreset(int id)
	{
		presets.forEach(b -> b.setSelected(b.getId() == id));
		config.currentPreset(id);
		manager.updateTransmog();
		recreate();
	}

	private void addNewPreset(int id)
	{
		TransmogPreset newPreset = manager.createNewPreset();
		CustomWidgetPresetTabItem add = presets.get(presets.size() - 1);
		add.layout(
			(presets.size() % PRESETS_PER_LINE) * (CustomWidgetPresetBox.SIZE + PRESET_PADDING),
			(presets.size() / PRESETS_PER_LINE) * (CustomWidgetPresetBox.SIZE + PRESET_PADDING)
		);
		presets.add(presets.size() - 1, createPresetBox(newPreset, getUIManager().getContainer(), presets.size() - 1));
		selectPreset(newPreset.getId());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		if (boxBounds != null && boxBounds.contains(event.getPoint()))
		{
			clientThread.invoke(() -> scrollPresets(event.getUnitsToScroll() * 6));
		}
	}

	private void scrollPresets(int pixels)
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
		for (CustomWidgetPresetTabItem p : presets)
		{
			p.scrollBy(scrollPos);
		}
	}

	private void layoutScrollbar()
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

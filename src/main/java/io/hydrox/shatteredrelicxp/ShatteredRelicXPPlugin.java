/*
 * Copyright (c) 2022 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.shatteredrelicxp;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.GameState;
import net.runelite.api.WorldType;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.FontManager;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@PluginDescriptor(
	name = "Shattered Relic XP",
	description = "View your relic fragment XP on the vanilla overlay",
	tags = {"league", "shattered", "xp", "experience", "overlay", "goal", "level"}
)
public class ShatteredRelicXPPlugin extends Plugin
{
	private static final FontMetrics FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(FontManager.getRunescapeFont());
	private static final NumberFormat DECIMAL_FORMATTER = new DecimalFormat("##0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	private static final NumberFormat SHORT_DECIMAL_FORMATTER = new DecimalFormat("##0.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	private static final int SHATTERED_GROUP_ID = 651;

	private static final int OVERLAY_CHILD_ID = 4;
	private static final int OVERLAY_WIDGETS_PER_ICON = 10;
	private static final int OVERLAY_BAR_WIDGETS_START_INDEX = 120;
	private static final int OVERLAY_WIDGET_OFFSET_TEXT = 8;
	private static final int OVERLAY_WIDGET_OFFSET_ICON = 6;
	private static final int OVERLAY_EDITED_FLAG = 99;
	private static final int OVERLAY_SET_BG_COLOUR = 8716250; // #84FFDA
	private static final int OVERLAY_ICON_SIZE = 32;
	private static final int OVERLAY_ICON_PADDING = 3;
	private static final int OVERLAY_SET_BG_OPACITY = 190;
	private static final int OVERLAY_SET_BORDER_OPACITY = 130;

	private static final int TOOLTIP_CHILD_ID = 5;
	private static final int TOOLTIP_TEXT_INDEX = 2;
	private static final int TOOLTIP_BAR_PADDING_X = 6;
	private static final int TOOLTIP_BAR_PADDING_Y = 3;
	private static final int TOOLTIP_BAR_HEIGHT = 15;
	private static final int TOOLTIP_LINE_HEIGHT = 12;
	private static final int TOOLTIP_WIDTH_PADDING = 4;
	private static final int TOOLTIP_HEIGHT_PADDING = 7;

	private static final int SCRIPT_TOOLTIP_REPEAT = 526;
	private static final int SCRIPT_TOOLTIP_BUILD = 2701;
	private static final int SCRIPT_TOOLTIP_CLEAR = 40;
	private static final int SCRIPT_BUILD_FRAGMENT_OVERLAY = 4671;
	private static final int SCRIPT_TOOLTIP_WIDGET_ARG_IDX = 1;
	private static final int SCRIPT_TOOLTIP_CHILD_ARG_IDX = 2;
	private static final int SCRIPT_TOOLTIP_TEXT_ARG_IDX = 4;

	private static final int VARBIT_FRAGMENT_SLOT_BASE = 13395;
	private static final int VAR_FRAGMENT_FIRST = 3282;
	private static final int VAR_FRAGMENT_LAST = 3309;

	private static final int SLOT_COUNT = 7;
	private static final int TIER_2_XP = 2000;
	static final int TIER_3_XP = 8000;

	private static final List<Integer> usedSlots = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ShatteredRelicXPConfig config;

	private Widget tooltipScriptSource = null;
	private Object[] tooltipScriptArgs = null;
	private boolean shouldBuildTooltip = false;

	@Provides
	ShatteredRelicXPConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShatteredRelicXPConfig.class);
	}

	@Override
	public void startUp()
	{
		if (client.getGameState() == GameState.LOGGED_IN && isLeaguesWorld())
		{
			clientThread.invoke(() -> buildOverlay(true));
		}
	}

	@Override
	public void shutDown()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::cleanOverlay);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!isLeaguesWorld())
		{
			return;
		}

		if (event.getGroup().equals(ShatteredRelicXPConfig.GROUP))
		{
			clientThread.invoke(() -> buildOverlay(true));
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (!isLeaguesWorld())
		{
			return;
		}
		if (event.getScriptId() == SCRIPT_TOOLTIP_REPEAT)
		{
			tooltipScriptSource = event.getScriptEvent().getSource();
			tooltipScriptArgs = event.getScriptEvent().getArguments();
		}
		else if (event.getScriptId() == SCRIPT_TOOLTIP_BUILD)
		{
			shouldBuildTooltip = tooltipScriptSource != null && WidgetInfo.TO_GROUP(tooltipScriptSource.getId()) == SHATTERED_GROUP_ID;
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (!isLeaguesWorld())
		{
			return;
		}

		if (event.getScriptId() == SCRIPT_TOOLTIP_BUILD && shouldModifyTooltips()
			&& shouldBuildTooltip && tooltipScriptArgs != null && tooltipScriptArgs.length >= 3)
		{
			buildTooltip();
		}
		else if (event.getScriptId() == SCRIPT_BUILD_FRAGMENT_OVERLAY)
		{
			buildOverlay(false);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (isLeaguesWorld() && event.getIndex() >= VAR_FRAGMENT_FIRST && event.getIndex() <= VAR_FRAGMENT_LAST)
		{
			buildOverlay(true);
		}
	}

	private void buildOverlay(boolean force)
	{
		Widget overlay = client.getWidget(SHATTERED_GROUP_ID, OVERLAY_CHILD_ID);
		if (overlay == null || overlay.getDynamicChildren().length == 0 || (overlay.getChild(0).getModelZoom() == OVERLAY_EDITED_FLAG && !force))
		{
			return;
		}

		// If a slot is currently not occupied, widgets for it don't exist in the overlay, so we need to be able to skip
		// the missing ones.
		int currentSlot = 0;

		List<SetEffect> activeEffects = new ArrayList<>();
		usedSlots.clear();

		for (int i = 0; i < SLOT_COUNT; i++)
		{
			int currentIndex = OVERLAY_BAR_WIDGETS_START_INDEX + currentSlot * 2;
			ShatteredFragment fragment = getFragmentInSlot(i);
			if (fragment == null)
			{
				continue;
			}
			activeEffects.addAll(SetEffect.getEffects(fragment));
			usedSlots.add(i);

			int xp = fragment.getXp(client);
			int upperBound = getUpperBound(xp);
			int lowerBound = getLowerBound(xp);
			double percentage = Math.min(1.0, (xp - lowerBound) / (double)(upperBound - lowerBound));

			if (config.overlayTextMode() != ShatteredRelicXPConfig.OverlayTextMode.NONE)
			{
				Widget textWidget = overlay.getChild(currentSlot * OVERLAY_WIDGETS_PER_ICON + OVERLAY_WIDGET_OFFSET_TEXT);
				if (config.overlayTextPosition() == ShatteredRelicXPConfig.OverlayTextPosition.TOP)
				{
					textWidget.setOriginalY(OVERLAY_ICON_SIZE - textWidget.getHeight() + 2);
				}
				else
				{
					textWidget.setOriginalY(3);
				}

				if (config.overlayTextMode() == ShatteredRelicXPConfig.OverlayTextMode.XP)
				{
					textWidget.setText(Integer.toString(xp));
				}
				else
				{
					textWidget.setText(SHORT_DECIMAL_FORMATTER.format(percentage * 100) + "%");
				}
				textWidget.setTextColor(config.overlayTextColour().getRGB());
				textWidget.revalidate();
			}
			else
			{
				Widget text = overlay.getChild(currentSlot * OVERLAY_WIDGETS_PER_ICON + OVERLAY_WIDGET_OFFSET_TEXT);
				text.setText("");
				text.revalidate();
			}

			if (config.overlayShowBar())
			{
				Widget background = overlay.getChild(currentSlot * OVERLAY_WIDGETS_PER_ICON);
				Widget barRight = overlay.createChild(currentIndex, WidgetType.RECTANGLE);
				barRight.setOriginalX(background.getOriginalX());
				barRight.setOriginalY(background.getOriginalY());
				barRight.setOriginalWidth(background.getOriginalWidth());
				barRight.setOriginalHeight(config.overlayBarHeight());
				barRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
				barRight.setTextColor(Color.RED.getRGB());
				barRight.setFilled(true);
				barRight.revalidate();

				Widget barLeft = overlay.createChild(currentIndex + 1, WidgetType.RECTANGLE);
				barLeft.setOriginalX(background.getOriginalX());
				barLeft.setOriginalY(background.getOriginalY());
				barLeft.setOriginalWidth((int) (background.getOriginalWidth() * percentage));
				barLeft.setOriginalHeight(config.overlayBarHeight());
				barLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
				barLeft.setTextColor(Color.GREEN.getRGB());
				barLeft.setFilled(true);
				barLeft.revalidate();
			}
			else
			{
				Widget barLeft = overlay.getChild(currentIndex);
				if (barLeft != null)
				{
					barLeft.setHidden(true);
				}

				Widget barRight = overlay.getChild(currentIndex + 1);
				if (barRight != null)
				{
					barRight.setHidden(true);
				}
			}

			currentSlot += 1;
		}
		if (config.overrideBuffLimit())
		{
			Set<SetEffect> effects = activeEffects.stream()
				.collect(Collectors.groupingBy((SetEffect e) -> e, Collectors.counting()))
				.entrySet().stream()
				.map(e -> e.getKey().getEffect(e.getValue().intValue()) == null ? null : e.getKey())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

			int effectsToFind = effects.size();

			for (int i = 0; i < effectsToFind; i++)
			{
				int baseIdx = (currentSlot + i) * OVERLAY_WIDGETS_PER_ICON;
				Widget child = overlay.getChild(baseIdx + OVERLAY_WIDGET_OFFSET_ICON);
				if (child != null)
				{
					effects.remove(SetEffect.byIconId(child.getSpriteId()));
					continue;
				}
				SetEffect effect = effects.iterator().next();
				buildSetEffectBuff(effect, baseIdx, overlay);
				effects.remove(effect);
			}
		}
		else
		{
			if (overlay.getChildren() != null && overlay.getChildren().length > OVERLAY_WIDGETS_PER_ICON * 8)
			{
				for (int i = OVERLAY_WIDGETS_PER_ICON * 8; i < OVERLAY_BAR_WIDGETS_START_INDEX; i++)
				{
					overlay.getChildren()[i] = null;
				}
			}
		}

		// Flag the overlay as having been edited, in a subtle and consistent way.
		overlay.getChild(0).setModelZoom(OVERLAY_EDITED_FLAG);
	}

	private void buildSetEffectBuff(SetEffect effect, int baseIdx, Widget parent)
	{
		int x = (baseIdx / 10) * 37 + 3;

		// BG
		parent.createChild(baseIdx, WidgetType.RECTANGLE)
			.setTextColor(42)
			.setOpacity(0)
			.setPos(x, OVERLAY_ICON_PADDING, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(OVERLAY_ICON_SIZE, OVERLAY_ICON_SIZE);
		parent.createChild(baseIdx + 1, WidgetType.RECTANGLE)
			.setTextColor(OVERLAY_SET_BG_COLOUR)
			.setOpacity(OVERLAY_SET_BG_OPACITY)
			.setFilled(true)
			.setPos(x, OVERLAY_ICON_PADDING, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(OVERLAY_ICON_SIZE, OVERLAY_ICON_SIZE);
		// Border
		parent.createChild(baseIdx + 2, WidgetType.RECTANGLE)
			.setTextColor(OVERLAY_SET_BG_COLOUR)
			.setOpacity(OVERLAY_SET_BORDER_OPACITY)
			.setFilled(true)
			.setPos(x - 1, OVERLAY_ICON_PADDING - 1, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(1, OVERLAY_ICON_SIZE + 2);
		parent.createChild(baseIdx + 3, WidgetType.RECTANGLE)
			.setTextColor(OVERLAY_SET_BG_COLOUR)
			.setOpacity(OVERLAY_SET_BORDER_OPACITY)
			.setFilled(true)
			.setPos(x + OVERLAY_ICON_SIZE, OVERLAY_ICON_PADDING - 1, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(1, OVERLAY_ICON_SIZE + OVERLAY_ICON_PADDING - 1);
		parent.createChild(baseIdx + 4, WidgetType.RECTANGLE)
			.setTextColor(OVERLAY_SET_BG_COLOUR)
			.setOpacity(OVERLAY_SET_BORDER_OPACITY)
			.setFilled(true)
			.setPos(x, OVERLAY_ICON_PADDING - 1, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(OVERLAY_ICON_SIZE, 1);
		parent.createChild(baseIdx + 5, WidgetType.RECTANGLE)
			.setTextColor(OVERLAY_SET_BG_COLOUR)
			.setOpacity(OVERLAY_SET_BORDER_OPACITY)
			.setFilled(true)
			.setPos(x, OVERLAY_ICON_SIZE + OVERLAY_ICON_PADDING, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(OVERLAY_ICON_SIZE, 1);

		parent.createChild(baseIdx + 6, WidgetType.GRAPHIC)
			.setSpriteId(effect.getIconId())
			.setOpacity(0)
			.setPos(x, OVERLAY_ICON_PADDING, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(OVERLAY_ICON_SIZE, OVERLAY_ICON_SIZE);

		Widget listener = parent.createChild(baseIdx + 9, WidgetType.GRAPHIC)
			.setOpacity(255)
			.setPos(x, OVERLAY_ICON_PADDING, WidgetPositionMode.ABSOLUTE_LEFT, WidgetPositionMode.ABSOLUTE_BOTTOM)
			.setSize(OVERLAY_ICON_SIZE, OVERLAY_ICON_SIZE)
			.setHasListener(true);
		listener.setOnMouseRepeatListener(SCRIPT_TOOLTIP_REPEAT, parent.getId(), baseIdx + 9, parent.getId() + 1, effect.getName(), 0, 512);
		listener.setOnMouseLeaveListener(SCRIPT_TOOLTIP_CLEAR, parent.getId() + 1);

		parent.revalidate();
	}

	private void cleanOverlay()
	{
		Widget overlay = client.getWidget(SHATTERED_GROUP_ID, OVERLAY_CHILD_ID);
		if (overlay == null || overlay.getDynamicChildren().length == 0 || overlay.getChildren() == null)
		{
			return;
		}
		// Clean xp text
		for (int i = 0; i < SLOT_COUNT; i++)
		{
			Widget text = overlay.getChild(i * OVERLAY_WIDGETS_PER_ICON + OVERLAY_WIDGET_OFFSET_TEXT);
			if (text != null)
			{
				text.setText("");
				text.setOriginalY(3);
			}
		}
		// Clean extra icons
		if (overlay.getChildren().length > OVERLAY_WIDGETS_PER_ICON * 8)
		{
			for (int i = OVERLAY_WIDGETS_PER_ICON * 8; i < OVERLAY_BAR_WIDGETS_START_INDEX; i++)
			{
				overlay.getChildren()[i] = null;
			}
		}
		// Clean progress bars
		if (overlay.getChildren().length > OVERLAY_BAR_WIDGETS_START_INDEX)
		{
			for (int i = OVERLAY_BAR_WIDGETS_START_INDEX; i < overlay.getChildren().length; i++)
			{
				overlay.getChildren()[i] = null;
			}
		}
	}

	private void buildTooltip()
	{
		Widget tooltip = client.getWidget(SHATTERED_GROUP_ID, TOOLTIP_CHILD_ID);
		if (tooltip == null || tooltip.getDynamicChildren().length == 0)
		{
			return;
		}
		int slot = getFragmentSlotFromTrigger((Integer) tooltipScriptArgs[SCRIPT_TOOLTIP_CHILD_ARG_IDX]);
		Widget icon = client.getWidget((Integer) tooltipScriptArgs[SCRIPT_TOOLTIP_WIDGET_ARG_IDX]);
		if (icon == null)
		{
			return;
		}
		icon = icon.getChild(slot * OVERLAY_WIDGETS_PER_ICON + OVERLAY_WIDGET_OFFSET_ICON);

		if (slot >= SLOT_COUNT || iconIsSetEffect(icon.getSpriteId()))
		{
			SetEffect effect = SetEffect.byName((String) tooltipScriptArgs[SCRIPT_TOOLTIP_TEXT_ARG_IDX]);
			if (effect == null)
			{
				return;
			}
			buildSetTooltip(tooltip, effect);
		}
		else
		{
			if (usedSlots.size() > 0)
			{
				slot = usedSlots.get(slot);
			}
			ShatteredFragment fragment = getFragmentInSlot(slot);
			if (fragment == null)
			{
				return;
			}
			buildFragmentTooltip(tooltip, fragment);
		}
	}

	private void buildSetTooltip(Widget tooltip, SetEffect setEffect)
	{
		Widget textWidget = tooltip.getDynamicChildren()[TOOLTIP_TEXT_INDEX];
		int oldHeight = textWidget.getHeight();
		// I don't think there's a varb to control what set effects are active, since the client knows the contents of
		// all the slots & what effects they give. We need the amount of fragments for the effect anyway
		int activeFragments = 0;
		for (int i = 0; i < 7; i++)
		{
			ShatteredFragment fragment = getFragmentInSlot(i);
			if (fragment != null && SetEffect.getEffects(fragment).contains(setEffect))
			{
				activeFragments += 1;
			}
		}

		SetEffect.Effect effect = setEffect.getEffect(activeFragments);
		String text = textWidget.getText();
		text += " (" + effect.getRelicsRequired() + ")";

		if (config.tooltipDescriptiveDescriptions())
		{
			text += "<br>" + effect.getTooltip();
		}

		textWidget.setText(text);

		int height = calculateTooltipTextHeight(textWidget.getText());
		int width = calculateTooltipWidth(textWidget.getText());

		tooltip.setOriginalWidth(width);
		tooltip.setOriginalHeight(height);
		tooltip.setOriginalY(tooltip.getOriginalY() - (height - oldHeight));
		tooltip.revalidate();
		for (Widget child : tooltip.getDynamicChildren())
		{
			child.revalidate();
		}
	}

	private void buildFragmentTooltip(Widget tooltip, ShatteredFragment fragment)
	{
		int xp = fragment.getXp(client);
		int upperBound = getUpperBound(xp);
		int lowerBound = getLowerBound(xp);
		int bonusHeight = 0;
		int oldHeight = tooltip.getHeight();

		Widget textWidget = tooltip.getDynamicChildren()[TOOLTIP_TEXT_INDEX];
		if (config.tooltipDescriptiveDescriptions() && fragment.getNumberOfSubstitutions() > 0)
		{
			int tier = getTier(xp);
			String text = textWidget.getText();
			text = text.substring(0, text.indexOf("<br>")) + "<br>" + fragment.buildTooltip(tier);
			textWidget.setText(text);
		}

		if (config.tooltipShowXP())
		{
			textWidget.setText(textWidget.getText() + "<br>XP: " + xp + "/" + upperBound);
		}

		int width = calculateTooltipWidth(textWidget.getText());

		tooltip.setOriginalWidth(width);
		tooltip.revalidate();

		if (config.tooltipShowBar())
		{
			double percentage = Math.min(1.0, (xp - lowerBound) / (double)(upperBound - lowerBound));
			Widget barRight = tooltip.createChild(-1, WidgetType.RECTANGLE);
			barRight.setOriginalX(TOOLTIP_BAR_PADDING_X);
			barRight.setOriginalY(TOOLTIP_BAR_PADDING_Y);
			barRight.setWidthMode(WidgetSizeMode.MINUS);
			barRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barRight.setOriginalWidth(TOOLTIP_BAR_PADDING_X * 2);
			barRight.setOriginalHeight(TOOLTIP_BAR_HEIGHT);
			barRight.setTextColor(Color.RED.getRGB());
			barRight.setFilled(true);

			Widget barLeft = tooltip.createChild(-1, WidgetType.RECTANGLE);
			barLeft.setOriginalX(TOOLTIP_BAR_PADDING_X);
			barLeft.setOriginalY(TOOLTIP_BAR_PADDING_Y);
			barLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barLeft.setOriginalWidth((int) ((tooltip.getOriginalWidth() - barRight.getOriginalWidth()) * percentage));
			barLeft.setOriginalHeight(TOOLTIP_BAR_HEIGHT);
			barLeft.setTextColor(Color.GREEN.darker().getRGB());
			barLeft.setFilled(true);

			Widget barText = tooltip.createChild(-1, WidgetType.TEXT);
			barText.setOriginalX(TOOLTIP_BAR_PADDING_X);
			barText.setOriginalY(TOOLTIP_BAR_PADDING_Y + 1);
			barText.setOriginalWidth(TOOLTIP_BAR_PADDING_X * 2);
			barText.setOriginalHeight(TOOLTIP_BAR_HEIGHT);
			barText.setWidthMode(WidgetSizeMode.MINUS);
			barText.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barText.setXTextAlignment(WidgetTextAlignment.CENTER);
			barText.setYTextAlignment(WidgetTextAlignment.BOTTOM);
			barText.setFontId(FontID.PLAIN_11);
			barText.setText(DECIMAL_FORMATTER.format(percentage * 100) + "%");
			barText.setTextColor(Color.WHITE.getRGB());
			barText.setTextShadowed(true);

			int tier = getTier(xp) + 1;
			tier = tier == 3 ? 2 : tier;

			Widget barTextLeft = tooltip.createChild(-1, WidgetType.TEXT);
			barTextLeft.setOriginalX(TOOLTIP_BAR_PADDING_X + 1);
			barTextLeft.setOriginalY(TOOLTIP_BAR_PADDING_Y + 1);
			barTextLeft.setOriginalWidth(TOOLTIP_BAR_PADDING_X * 2);
			barTextLeft.setOriginalHeight(TOOLTIP_BAR_HEIGHT);
			barTextLeft.setWidthMode(WidgetSizeMode.MINUS);
			barTextLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barTextLeft.setXTextAlignment(WidgetTextAlignment.LEFT);
			barTextLeft.setYTextAlignment(WidgetTextAlignment.BOTTOM);
			barTextLeft.setFontId(FontID.PLAIN_11);
			barTextLeft.setText(Integer.toString(tier));
			barTextLeft.setTextColor(Color.WHITE.getRGB());
			barTextLeft.setTextShadowed(true);

			Widget barTextRight = tooltip.createChild(-1, WidgetType.TEXT);
			barTextRight.setOriginalX(TOOLTIP_BAR_PADDING_X - 1);
			barTextRight.setOriginalY(TOOLTIP_BAR_PADDING_Y + 1);
			barTextRight.setOriginalWidth(TOOLTIP_BAR_PADDING_X * 2);
			barTextRight.setOriginalHeight(TOOLTIP_BAR_HEIGHT);
			barTextRight.setWidthMode(WidgetSizeMode.MINUS);
			barTextRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barTextRight.setXTextAlignment(WidgetTextAlignment.RIGHT);
			barTextRight.setYTextAlignment(WidgetTextAlignment.BOTTOM);
			barTextRight.setFontId(FontID.PLAIN_11);
			barTextRight.setText(Integer.toString(tier + 1));
			barTextRight.setTextColor(Color.WHITE.getRGB());
			barTextRight.setTextShadowed(true);

			bonusHeight += TOOLTIP_BAR_HEIGHT + TOOLTIP_BAR_PADDING_Y;
		}

		int height = calculateTooltipTextHeight(textWidget.getText()) + bonusHeight;

		tooltip.setOriginalHeight(height);
		tooltip.setOriginalY(tooltip.getOriginalY() - (height - oldHeight));
		tooltip.revalidate();
		for (Widget child : tooltip.getDynamicChildren())
		{
			child.revalidate();
		}
	}

	private int calculateTooltipTextHeight(String text)
	{
		int lines = text.split("<br>").length;
		return lines * TOOLTIP_LINE_HEIGHT + TOOLTIP_HEIGHT_PADDING;
	}

	private int calculateTooltipWidth(String text)
	{
		String[] lines = text.split("<br>");
		int width = 0;
		for (String line : lines)
		{
			int lineWidth = FONT_METRICS.stringWidth(line);
			if (lineWidth > width)
			{
				width = lineWidth;
			}
		}
		return width + TOOLTIP_WIDTH_PADDING;
	}

	private int getFragmentSlotFromTrigger(int trigger)
	{
		return (int) Math.floor(trigger / (float)OVERLAY_WIDGETS_PER_ICON);
	}

	private ShatteredFragment getFragmentInSlot(int slot)
	{
		// 13400, where slot 6 would be, isn't the contents of slot 6!. Jank!
		int actualSlot = VARBIT_FRAGMENT_SLOT_BASE + slot + (slot >= 5 ? 1 : 0);
		int slotValue = client.getVarbitValue(actualSlot);
		return slotValue == 0 ? null : ShatteredFragment.byOrdinal(slotValue);
	}

	private int getUpperBound(int xp)
	{
		return xp >= TIER_2_XP ? TIER_3_XP : TIER_2_XP;
	}

	private int getLowerBound(int xp)
	{
		return xp < TIER_2_XP ? 0 : TIER_2_XP;
	}

	/**
	 * @return The tier, indexed from 0
	 */
	private int getTier(int xp)
	{
		return xp >= TIER_3_XP ? 2 : xp >= TIER_2_XP ? 1 : 0;
	}

	private boolean shouldModifyTooltips()
	{
		return config.tooltipShowBar() || config.tooltipShowXP() || config.tooltipDescriptiveDescriptions();
	}

	private boolean isLeaguesWorld()
	{
		return client.getWorldType().contains(WorldType.SEASONAL);
	}

	private boolean iconIsSetEffect(int spriteId)
	{
		return spriteId >= 4114 && spriteId <= 4159;
	}
}

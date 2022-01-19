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
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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
import java.util.Locale;

@PluginDescriptor(
	name = "Shattered Relic XP",
	description = "View your relic fragment XP on the vanilla overlay",
	tags = {"league", "shattered", "xp", "experience", "overlay", "goal", "level"}
)
public class ShatteredRelicXPPlugin extends Plugin
{
	private static final FontMetrics FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(FontManager.getRunescapeFont());
	private static final NumberFormat DECIMAL_FORMATTER = new DecimalFormat("##0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	private static final int SHATTERED_GROUP_ID = 651;
	private static final int SHATTERED_ICONS_CHILD = 4;
	private static final int SHATTERED_TOOLTIP_CHILD = 3;

	private static final int SCRIPT_TOOLTIP_REPEAT = 526;
	private static final int SCRIPT_TOOLTIP_BUILD = 2701;
	private static final int SCRIPT_BUILD_FRAGMENT_OVERLAY = 3166;

	private static final int BAR_PADDING_X = 6;
	private static final int BAR_PADDING_Y = 3;
	private static final int BAR_HEIGHT = 15;
	private static final int LINE_HEIGHT = 12;
	private static final int WIDTH_PADDING = 4;
	private static final int HEIGHT_PADDING = 7;

	private static final int VARBIT_FRAGMENT_SLOT_BASE = 13395;
	private static final int VAR_FRAGMENT_FIRST = 3282;
	private static final int VAR_FRAGMENT_LAST = 3309;

	private static final int WIDGET_OFFSET_TEXT_TOP = 8;
	private static final int WIDGET_OFFSET_TEXT_BOTTOM = 7;
	private static final int SLOT_COUNT = 7;

	@Inject
	private Client client;

	@Inject
	private ShatteredRelicXPConfig config;

	private Widget source = null;
	private Object[] args = null;
	private boolean shouldBuildTooltip = false;

	@Provides
	ShatteredRelicXPConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShatteredRelicXPConfig.class);
	}


	@Subscribe
	public void onCommandExecuted(CommandExecuted event)
	{
		if (!event.getCommand().equals("test"))
		{
			return;
		}
		int base = 13403;
		for (int i = 0; i <= 52; i++)
		{
			client.setVarbitValue(client.getVarps(), base + i, i + 1);
		}
		client.setVarbitValue(client.getVarps(), 13397, 1);
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() == SCRIPT_TOOLTIP_REPEAT)
		{
			source = event.getScriptEvent().getSource();
			args = event.getScriptEvent().getArguments();
		}
		else if (event.getScriptId() == SCRIPT_TOOLTIP_BUILD)
		{
			shouldBuildTooltip = source != null && WidgetInfo.TO_GROUP(source.getId()) == SHATTERED_GROUP_ID;
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == SCRIPT_TOOLTIP_BUILD && shouldModifyTooltips()
			&& shouldBuildTooltip && args != null && args.length >= 3)
		{
			buildTooltip();
		}
		else if (event.getScriptId() == SCRIPT_BUILD_FRAGMENT_OVERLAY)
		{
			buildOverlay();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getIndex() >= VAR_FRAGMENT_FIRST && event.getIndex() <= VAR_FRAGMENT_LAST)
		{
			buildOverlay();
		}
	}

	private void buildOverlay()
	{
		if (!shouldModifyOverlay())
		{
			return;
		}

		Widget overlay = client.getWidget(SHATTERED_GROUP_ID, SHATTERED_ICONS_CHILD);
		if (overlay == null || overlay.getDynamicChildren().length == 0)
		{
			return;
		}
		int barWidgetIndex = 70;
		// If a slot is currently not occupied, widgets for it don't exist in the overlay, so we need to be able to skip
		// the missing ones.
		int currentSlot = 0;

		for (int i = 0; i < SLOT_COUNT; i++)
		{
			int currentIndex = barWidgetIndex + currentSlot * 2;
			ShatteredFragment fragment = getFragmentInSlot(i);
			if (fragment == null)
			{
				continue;
			}
			int xp = fragment.getXp(client);
			int upperBound = ShatteredFragment.getUpperBound(xp);
			int lowerBound = ShatteredFragment.getLowerBound(xp);
			double percentage = Math.min(1.0, (xp - lowerBound) / (double)(upperBound - lowerBound));

			if (config.overlayTextMode() != ShatteredRelicXPConfig.OverlayTextMode.NONE)
			{
				int textWidgetOffset = config.overlayTextPosition() == ShatteredRelicXPConfig.OverlayTextPosition.TOP
					? WIDGET_OFFSET_TEXT_TOP : WIDGET_OFFSET_TEXT_BOTTOM;
				Widget textWidget = overlay.getChild(currentSlot * 10 + textWidgetOffset);
				if (config.overlayTextMode() == ShatteredRelicXPConfig.OverlayTextMode.XP)
				{
					textWidget.setText(Integer.toString(xp));
				}
				else
				{
					textWidget.setText(DECIMAL_FORMATTER.format(percentage) + "%");
				}
				textWidget.setTextColor(config.overlayTextColour().getRGB());
				textWidget.revalidate();
			}

			if (config.overlayShowBar())
			{
				Widget background = overlay.getChild(currentSlot * 10);
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

			currentSlot += 1;
		}
	}

	private void buildTooltip()
	{
		Widget tooltip = client.getWidget(SHATTERED_GROUP_ID, SHATTERED_TOOLTIP_CHILD);
		if (tooltip == null || tooltip.getDynamicChildren().length == 0)
		{
			return;
		}
		ShatteredFragment fragment = getFragmentInSlot(getFragmentSlotFromTrigger((Integer) args[2]));
		if (fragment == null)
		{
			return;
		}
		int xp = fragment.getXp(client);
		int upperBound = ShatteredFragment.getUpperBound(xp);
		int lowerBound = ShatteredFragment.getLowerBound(xp);
		int bonusHeight = 0;

		Widget text = tooltip.getDynamicChildren()[2];
		if (config.tooltipShowXP())
		{
			text.setText(text.getText() + "<br>XP: " + xp + "/" + upperBound);
		}

		if (config.tooltipShowBar())
		{
			double percentage = Math.min(1.0, (xp - lowerBound) / (double)(upperBound - lowerBound));
			Widget barRight = tooltip.createChild(-1, WidgetType.RECTANGLE);
			barRight.setOriginalX(BAR_PADDING_X);
			barRight.setOriginalY(BAR_PADDING_Y);
			barRight.setWidthMode(WidgetSizeMode.MINUS);
			barRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barRight.setOriginalWidth(BAR_PADDING_X * 2);
			barRight.setOriginalHeight(BAR_HEIGHT);
			barRight.setTextColor(Color.RED.getRGB());
			barRight.setFilled(true);

			Widget barLeft = tooltip.createChild(-1, WidgetType.RECTANGLE);
			barLeft.setOriginalX(BAR_PADDING_X);
			barLeft.setOriginalY(BAR_PADDING_Y);
			barLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barLeft.setOriginalWidth((int) ((tooltip.getOriginalWidth() - barRight.getOriginalWidth()) * percentage));
			barLeft.setOriginalHeight(BAR_HEIGHT);
			barLeft.setTextColor(Color.GREEN.darker().getRGB());
			barLeft.setFilled(true);

			Widget barText = tooltip.createChild(-1, WidgetType.TEXT);
			barText.setOriginalX(BAR_PADDING_X);
			barText.setOriginalY(BAR_PADDING_Y + 1);
			barText.setOriginalWidth(BAR_PADDING_X * 2);
			barText.setOriginalHeight(BAR_HEIGHT);
			barText.setWidthMode(WidgetSizeMode.MINUS);
			barText.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
			barText.setXTextAlignment(WidgetTextAlignment.CENTER);
			barText.setYTextAlignment(WidgetTextAlignment.BOTTOM);
			barText.setFontId(FontID.PLAIN_11);
			barText.setText(DECIMAL_FORMATTER.format(percentage * 100) + "%");
			barText.setTextColor(Color.WHITE.getRGB());
			barText.setTextShadowed(true);

			bonusHeight += BAR_HEIGHT + BAR_PADDING_Y;
		}

		int width = calculateTooltipWidth(text.getText());
		int height = calculateTooltipTextHeight(text.getText()) + bonusHeight;

		tooltip.setOriginalWidth(width);
		tooltip.setOriginalHeight(height);
		tooltip.revalidate();
		for (Widget child : tooltip.getDynamicChildren())
		{
			child.revalidate();
		}
	}

	private int calculateTooltipTextHeight(String text)
	{
		int lines = text.split("<br>").length;
		return lines * LINE_HEIGHT + HEIGHT_PADDING;
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
		return width + WIDTH_PADDING;
	}

	private int getFragmentSlotFromTrigger(int trigger)
	{
		return (int) Math.floor(trigger / 10f);
	}

	private ShatteredFragment getFragmentInSlot(int slot)
	{
		int slotValue = client.getVarbitValue(VARBIT_FRAGMENT_SLOT_BASE + slot);
		return slotValue == 0 ? null : ShatteredFragment.byOrdinal(slotValue);
	}

	private boolean shouldModifyTooltips()
	{
		return config.tooltipShowBar() || config.tooltipShowBar();
	}

	private boolean shouldModifyOverlay()
	{
		return config.overlayShowBar() || config.overlayTextMode() != ShatteredRelicXPConfig.OverlayTextMode.NONE;
	}
	/*
	0 - Background
	1 - left edge
	2 - right edge
	3 - bottom edge
	4 - top edge
	5 - sprite fragment
	6 - sprite rune
	7 - (unused) text at bottom (might be for level?)
	8 - (unused) text at top (might be for level?)
	9 - tooltip collision handler
	 */
}

/*
 * Copyright (c) 2021 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.betterskilltooltips;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.FontID;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.QuantityFormatter;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

@PluginDescriptor(
	name = "Better Skill Tooltips",
	description = "Enhance your vanilla skill tooltips!",
	tags = {"skill", "tooltip", "virtual", "120", "goal", "xp"}
)
public class BetterSkillTooltipsPlugin extends Plugin
{
	private static final FontMetrics FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(FontManager.getRunescapeFont());

	private static final int SCRIPT_SKILL_TOOLTIP_CREATE = 2344;
	private static final int SKILL_TOOLTIP_CHILDID = 28;

	private static final int BAR_PADDING_X = 6;
	private static final int BAR_PADDING_Y = 5;
	private static final int BAR_HEIGHT = 15;
	private static final int LINE_HEIGHT = 12;
	private static final int WIDTH_PADDING = 4;
	private static final int HEIGHT_PADDING = 7;

	private static final NumberFormat DECIMAL_FORMATTER = new DecimalFormat("##0.00",DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	@Inject
	private Client client;

	@Inject
	private BetterSkillTooltipsConfig config;

	@Provides
	BetterSkillTooltipsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BetterSkillTooltipsConfig.class);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired e)
	{
		if (e.getScriptId() != SCRIPT_SKILL_TOOLTIP_CREATE)
		{
			return;
		}
		Widget tooltip = client.getWidget(WidgetID.SKILLS_GROUP_ID, SKILL_TOOLTIP_CHILDID);
		if (tooltip == null || tooltip.isHidden())
		{
			return;
		}
		SkillData currentSkill = getSkill(tooltip);
		if (currentSkill != null)
		{
			enhanceTooltip(tooltip, currentSkill);
		}
	}

	/**
	 * Get which skill the current tooltip is for based off of the tooltip text.
	 * Sadly, the ScriptEvent does not contain a source for the widget creating the tooltip, so this is the only way to
	 * figure out which skill to use.
	 * @param tooltip The tooltip widget.
	 * @return The {@link SkillData} for the skill.
	 */
	private SkillData getSkill(Widget tooltip)
	{
		Widget leftText = tooltip.getChild(2);
		if (leftText == null)
		{
			return null;
		}
		String text = leftText.getText();
		text = text.substring(0, text.indexOf(" "));
		return SkillData.fromName(text);
	}

	private void enhanceTooltip(Widget tooltip, SkillData skillData)
	{
		final int skillExperience = client.getSkillExperience(skillData.getSkill());
		// There's no point in adding next level or goal text if the skill has maximum experience
		if (skillExperience == Experience.MAX_SKILL_XP)
		{
			return;
		}

		int lines = getLinesInTooltip(tooltip.getHeight());
		int bonusHeight = 0;
		Widget background = tooltip.getChild(0);
		Widget border = tooltip.getChild(1);
		Widget leftText = tooltip.getChild(2);
		Widget rightText = tooltip.getChild(3);

		// Add missing text for virtual levels
		if (lines == 1 && config.virtualLevels())
		{
			final int skillLevel = Experience.getLevelForXp(skillExperience);
			final int nextExperience;
			if (skillLevel == Experience.MAX_VIRT_LEVEL)
			{
				nextExperience = Experience.MAX_SKILL_XP;
			}
			else
			{
				nextExperience = Experience.getXpForLevel(skillLevel + 1);
			}

			lines += 3;
			leftText.setText(leftText.getText() + "<br>Virtual level:<br>Next level at:<br>Remaining XP:");
			rightText.setText(rightText.getText() +
				"<br>" + skillLevel +
				"<br>" + QuantityFormatter.formatNumber(nextExperience) +
				"<br>" + QuantityFormatter.formatNumber(nextExperience - skillExperience)
			);
		}

		final int goalStartXP = client.getVar(skillData.getGoalStartVarp());
		int goalEndXP = client.getVar(skillData.getGoalEndVarp());
		boolean hasGoal = goalEndXP != 0;
		Widget barLeft = null;
		Widget barRight = null;
		Widget barText = null;
		if (hasGoal && (config.goalInfo() || config.goalBar()))
		{
			// Add goal text if needed
			if (config.goalInfo())
			{
				lines += 2;
				leftText.setText(leftText.getText() + "<br>Goal XP:<br>Until goal:");
				rightText.setText(rightText.getText() + "<br>" + QuantityFormatter.formatNumber(goalEndXP) + "<br>" + QuantityFormatter.formatNumber(goalEndXP - skillExperience));
			}

			// Add goal bar if needed
			if (config.goalBar())
			{
				double percentage = Math.min(1.0, (skillExperience - goalStartXP) / (double)(goalEndXP - goalStartXP));
				barRight = tooltip.createChild(-1, WidgetType.RECTANGLE);
				barRight.setOriginalX(BAR_PADDING_X);
				barRight.setOriginalY(BAR_PADDING_Y);
				barRight.setWidthMode(WidgetSizeMode.MINUS);
				barRight.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
				barRight.setOriginalWidth(BAR_PADDING_X * 2);
				barRight.setOriginalHeight(BAR_HEIGHT);
				barRight.setTextColor(Color.RED.getRGB());
				barRight.setFilled(true);

				barLeft = tooltip.createChild(-1, WidgetType.RECTANGLE);
				barLeft.setOriginalX(BAR_PADDING_X);
				barLeft.setOriginalY(BAR_PADDING_Y);
				barLeft.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
				barLeft.setOriginalWidth((int) ((tooltip.getOriginalWidth() - barRight.getOriginalWidth()) * percentage));
				barLeft.setOriginalHeight(BAR_HEIGHT);
				barLeft.setTextColor(Color.GREEN.darker().getRGB());
				barLeft.setFilled(true);

				barText = tooltip.createChild(-1, WidgetType.TEXT);
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
		}

		// Resize parent and background
		int width = calculateTooltipWidth(leftText.getText(), rightText.getText());
		int height = calculateTooltipTextHeight(lines) + bonusHeight;
		tooltip.setOriginalWidth(width);
		tooltip.setOriginalHeight(height);

		tooltip.revalidate();
		// Background widgets use the size of the parent, so update them
		background.revalidate();
		border.revalidate();
		leftText.revalidate();
		rightText.revalidate();
		if (hasGoal && config.goalBar())
		{
			barLeft.revalidate();
			barRight.revalidate();
			barText.revalidate();
		}

		// Fix position of the tooltip
		Widget skillTile = client.getWidget(WidgetID.SKILLS_GROUP_ID, skillData.getChildID());
		Widget skillsContainer = tooltip.getParent();
		int x = skillTile.getOriginalX();
		int y = skillTile.getOriginalY();
		int h = skillTile.getOriginalHeight();

		x += 5;
		int y2 = y + h + 5;

		x = Math.min(x, skillsContainer.getOriginalWidth() - width);

		if (y2 > skillsContainer.getHeight() - height)
		{
			y2 = y - height - 5;
		}
		tooltip.setOriginalX(x);
		tooltip.setOriginalY(y2);
		tooltip.revalidate();
	}

	/**
	 * Calculate the width the tooltip needs to contain the given text.
	 * Supplied text should be <br> separated lines for each side of the tooltip.
	 * @param leftText The text on the left side of the tooltip.
	 * @param rightText The text on the right side of the tooltip.
	 * @return the width for the tooltip.
	 */
	private int calculateTooltipWidth(String leftText, String rightText)
	{
		final String[] leftLines = leftText.split("<br>");
		final String[] rightLines = rightText.split("<br>");
		int maxWidth = 0;
		for (int i = 0; i < Math.max(leftLines.length, rightLines.length); i++)
		{
			String left = "";
			String right = "";
			if (i < leftLines.length)
			{
				left = leftLines[i];
			}
			if (i < rightLines.length)
			{
				right = rightLines[i];
			}
			int width = FONT_METRICS.stringWidth(left) + FONT_METRICS.stringWidth(right) + WIDTH_PADDING;
			if (width > maxWidth)
			{
				maxWidth = width;
			}
		}
		return maxWidth + WIDTH_PADDING;
	}

	/**
	 * Calculate how tall the tooltip should be, given an amount of lines of text.
	 * @param lines the number of lines of text.
	 * @return the width for the tooltip.
	 */
	private int calculateTooltipTextHeight(int lines)
	{
		return lines * LINE_HEIGHT + HEIGHT_PADDING;
	}

	/**
	 * Calculate how many lines the tooltip already has based on its height.
	 * This is doable as the size is entirely determined by the number of lines in vanilla skill tooltips,
	 * and this is more efficient than parsing the text directly.
	 * @param height The height of the tooltip.
	 * @return How many lines the tooltip has.
	 */
	private int getLinesInTooltip(int height)
	{
		return (height - HEIGHT_PADDING) / LINE_HEIGHT;
	}
}

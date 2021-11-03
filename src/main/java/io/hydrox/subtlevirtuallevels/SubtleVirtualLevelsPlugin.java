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
package io.hydrox.subtlevirtuallevels;

import com.google.common.collect.Sets;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.FontID;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.FontManager;
import javax.inject.Inject;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@PluginDescriptor(
	name = "Subtle Virtual Levels",
	description = "View your virtual levels in a more subtle way that doesn't mess with your stats.",
	tags = {"skill", "126", "max", "200m", "99"}
)
public class SubtleVirtualLevelsPlugin extends Plugin
{
	private static final FontMetrics FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(FontManager.getRunescapeSmallFont());
	private static final int SCRIPTID_STATS_INIT = 393;
	private static final int BACKGROUND_CAP_WIDTH = 4;
	private static final int BACKGROUND_HEIGHT = 12;
	private static final int BACKGROUND_PADDING = 1;
	private static final int TEXT_OFFSET_X = 12;
	private static final int TEXT_OFFSET_Y = 2;
	private static final int SPRITE_BACKGROUND_CAP_LEFT = 1123;
	private static final int SPRITE_BACKGROUND_TILE = 1124;
	private static final int SPRITE_BACKGROUND_CAP_RIGHT = 1125;
	private static final int ICON_POSITION = 4;
	private static final int ICON_SIZE = 25;

	private static final Skill[] SKILLS = new Skill[]
	{
		Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.PRAYER, Skill.MAGIC, Skill.RUNECRAFT, Skill.CONSTRUCTION,
		Skill.HITPOINTS, Skill.AGILITY, Skill.HERBLORE, Skill.THIEVING,	Skill.CRAFTING, Skill.FLETCHING, Skill.SLAYER, Skill.HUNTER,
		Skill.MINING, Skill.SMITHING, Skill.FISHING, Skill.COOKING, Skill.FIREMAKING, Skill.WOODCUTTING, Skill.FARMING
	};

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SubtleVirtualLevelsConfig config;

	private final Map<Skill, Set<Widget>> trackedWidgets = new HashMap<>();

	private Widget currentWidget;

	@Provides
	SubtleVirtualLevelsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SubtleVirtualLevelsConfig.class);
	}

	@Override
	public void startUp()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::createWidgets);
		}
	}

	@Override
	public void shutDown()
	{
		clientThread.invoke(this::removeWidgets);
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() != SCRIPTID_STATS_INIT)
		{
			return;
		}
		currentWidget = event.getScriptEvent().getSource();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() != SCRIPTID_STATS_INIT || currentWidget == null)
		{
			return;
		}
		buildWidget(currentWidget);
	}

	private void createWidgets()
	{
		Widget skillsContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
		if (skillsContainer == null)
		{
			return;
		}

		for (int i = 0; i < SKILLS.length; i++)
		{
			Widget current = skillsContainer.getStaticChildren()[i];
			if (current == null)
			{
				continue;
			}
			buildWidget(current);
		}
	}

	private void removeWidgets()
	{
		Widget skillsContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
		if (skillsContainer == null)
		{
			return;
		}

		for (int i = 0; i < SKILLS.length; i++)
		{
			Widget current = skillsContainer.getStaticChildren()[i];
			Skill skill = SKILLS[i];
			Set<Widget> tracked = trackedWidgets.getOrDefault(skill, null);
			if (current == null || tracked == null)
			{
				continue;
			}

			Widget[] children = current.getChildren();
			for (int j = 0; j < children.length; j++)
			{
				Widget child = children[j];
				if (child == null || !tracked.contains(child))
				{
					continue;
				}
				children[j] = null;
			}
		}
		trackedWidgets.clear();
	}

	private void buildWidget(Widget parent)
	{
		Skill skill = SKILLS[WidgetInfo.TO_CHILD(parent.getId()) - 1];

		int bgY = ICON_POSITION + ICON_SIZE + TEXT_OFFSET_Y - BACKGROUND_PADDING - BACKGROUND_HEIGHT;

		Widget bgCapLeft = parent.createChild(-1, WidgetType.GRAPHIC);
		bgCapLeft.setSpriteId(SPRITE_BACKGROUND_CAP_LEFT);
		bgCapLeft.setOriginalWidth(BACKGROUND_CAP_WIDTH);
		bgCapLeft.setOriginalHeight(BACKGROUND_HEIGHT);
		bgCapLeft.setOriginalY(bgY);

		Widget bgCapRight = parent.createChild(-1, WidgetType.GRAPHIC);
		bgCapRight.setSpriteId(SPRITE_BACKGROUND_CAP_RIGHT);
		bgCapRight.setOriginalWidth(BACKGROUND_CAP_WIDTH);
		bgCapRight.setOriginalHeight(BACKGROUND_HEIGHT);
		bgCapRight.setOriginalX(ICON_POSITION + ICON_SIZE + TEXT_OFFSET_X + BACKGROUND_PADDING - BACKGROUND_CAP_WIDTH);
		bgCapRight.setOriginalY(bgY);

		Widget bgTile = parent.createChild(-1, WidgetType.GRAPHIC);
		bgTile.setSpriteId(SPRITE_BACKGROUND_TILE);
		bgTile.setSpriteTiling(true);
		bgTile.setOriginalHeight(BACKGROUND_HEIGHT);
		bgTile.setOriginalY(bgY);

		Widget virtualLevel = parent.createChild(-1, WidgetType.TEXT);
		virtualLevel.setFontId(FontID.PLAIN_11);
		virtualLevel.setTextShadowed(true);

		virtualLevel.setOriginalX(ICON_POSITION);
		virtualLevel.setOriginalY(ICON_POSITION);
		virtualLevel.setOriginalWidth(ICON_SIZE + TEXT_OFFSET_X);
		virtualLevel.setOriginalHeight(ICON_SIZE + TEXT_OFFSET_Y);
		virtualLevel.setXTextAlignment(WidgetTextAlignment.RIGHT);
		virtualLevel.setYTextAlignment(WidgetTextAlignment.BOTTOM);

		virtualLevel.setOnVarTransmitListener((JavaScriptCallback) ev ->
			updateVirtualLevel(skill, virtualLevel, bgCapLeft, bgCapRight, bgTile)
		);
		virtualLevel.setHasListener(true);

		updateVirtualLevel(skill, virtualLevel, bgCapLeft, bgCapRight, bgTile);

		bgCapLeft.revalidate();
		bgCapRight.revalidate();
		bgTile.revalidate();
		virtualLevel.revalidate();
		parent.revalidate();

		trackedWidgets.put(skill, Sets.newHashSet(bgCapLeft, bgCapRight, bgTile, virtualLevel));
	}

	private String getVirtualLevelText(Skill skill)
	{
		int xp = client.getSkillExperience(skill);
		if (xp == Experience.MAX_SKILL_XP)
		{
			return "Max";
		}
		int level = Experience.getLevelForXp(xp);
		return level < getLowestVisibleLevel() ? "" : Integer.toString(level);
	}

	private int getLowestVisibleLevel()
	{
		return config.showFor99() ? Experience.MAX_REAL_LEVEL : Experience.MAX_REAL_LEVEL + 1;
	}

	private void updateVirtualLevel(Skill skill, Widget text, Widget bgCapLeft, Widget bgCapRight, Widget bgTile)
	{
		String virtualLevel = getVirtualLevelText(skill);
		text.setText(virtualLevel);
		text.setTextColor(config.textColour().getRGB());

		final int stringWidth = FONT_METRICS.stringWidth(virtualLevel);

		bgCapLeft.setOriginalX(text.getOriginalX() + text.getWidth() + BACKGROUND_PADDING - stringWidth - BACKGROUND_PADDING * 2);
		bgTile.setOriginalX(text.getOriginalX() + text.getWidth() + BACKGROUND_PADDING - stringWidth + BACKGROUND_CAP_WIDTH / 2);
		bgTile.setOriginalWidth(stringWidth - BACKGROUND_CAP_WIDTH);

		bgCapLeft.setHidden(virtualLevel.isEmpty());
		bgCapRight.setHidden(virtualLevel.isEmpty());
		bgTile.setHidden(virtualLevel.isEmpty());

		text.revalidate();
		bgCapLeft.revalidate();
		bgCapRight.revalidate();
		bgTile.revalidate();
	}
}

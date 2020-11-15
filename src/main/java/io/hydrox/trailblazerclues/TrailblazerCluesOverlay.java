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
package io.hydrox.trailblazerclues;

import io.hydrox.trailblazerclues.requirements.ANDGroupedRequirements;
import io.hydrox.trailblazerclues.requirements.ANDRegionRequirement;
import io.hydrox.trailblazerclues.requirements.GroupedRequirement;
import io.hydrox.trailblazerclues.requirements.NeverShowRequirements;
import io.hydrox.trailblazerclues.requirements.ORGroupedRequirements;
import io.hydrox.trailblazerclues.requirements.ORRegionRequirement;
import io.hydrox.trailblazerclues.requirements.RegionRequirement;
import io.hydrox.trailblazerclues.requirements.SingleRegionRequirement;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TrailblazerCluesOverlay extends Overlay
{
	private static final int SPRITE_WIDTH = 20;
	private static final int SPRITE_HEIGHT = 30;
	private static final int SPACER = 2;
	private static final int TEXT_OFFSET = 14;
	private static final int TEXT_HEIGHT = 14;
	private static final int LINE_HEIGHT = SPRITE_HEIGHT + SPACER;
	private static final String REGION_AND = "ALL OF";
	private static final String REGION_OR = "ANY OF";
	private static final String REGION_MULTI_AND = "AND";
	private static final String REGION_MULTI_OR = "OR";
	private static final String IMPOSSIBLE = "NOT POSSIBLE TO COMPLETE";
	private static final String POSSIBLE = "POSSIBLE TO COMPLETE";

	private final Client client;
	private final SpriteManager spriteManager;
	private final TooltipManager tooltipManager;
	private final TrailblazerCluesPlugin plugin;

	private final Map<Rectangle, String> tooltipRegions = new HashMap<>();

	@Inject
	public TrailblazerCluesOverlay(Client client, SpriteManager spriteManager, TooltipManager tooltipManager, TrailblazerCluesPlugin plugin)
	{
		setLayer(OverlayLayer.ABOVE_MAP);
		setPosition(OverlayPosition.DYNAMIC);
		this.client = client;
		this.spriteManager = spriteManager;
		this.tooltipManager = tooltipManager;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		tooltipRegions.clear();
		final Widget resizeableBarWidget = client.getWidget(164, 15);
		final Widget resizeableClassicWidget = client.getWidget(161, 15);
		final Widget fixedWidget = client.getWidget(548, 23);
		final Widget parentWidget;

		if (resizeableBarWidget != null)
		{
			parentWidget = resizeableBarWidget;
		}
		else if (resizeableClassicWidget != null)
		{
			parentWidget = resizeableClassicWidget;
		}
		else if (fixedWidget != null)
		{
			parentWidget = fixedWidget;
		}
		else
		{
			return null;
		}

		if (parentWidget.getNestedChildren() == null || parentWidget.getNestedChildren().length == 0)
		{
			return null;
		}

		if (!viewingClueScroll(parentWidget))
		{
			return null;
		}

		RegionRequirement reqs = plugin.getCurrentReqs();
		if (reqs instanceof NeverShowRequirements)
		{
			return null;
		}
		Point offset = new Point(0, 10);
		Widget clueWidget = client.getWidget(WidgetInfo.CLUE_SCROLL_TEXT);
		if (clueWidget == null)
		{
			clueWidget = parentWidget;
			offset.translate(-120, 37);
		}

		graphics.setFont(FontManager.getRunescapeBoldFont());

		if (reqs != null)
		{
			Point topLeft = new Point(clueWidget.getCanvasLocation().getX() + clueWidget.getOriginalWidth(),
				clueWidget.getCanvasLocation().getY());
			topLeft.translate(offset.x, offset.y);

			drawOutlinedString(graphics, "REQUIREMENTS:", topLeft.x, topLeft.y, Color.WHITE, Color.BLACK);
			parseAndDrawReq(graphics, topLeft, reqs);
		}

		FontMetrics fm = graphics.getFontMetrics();
		if (reqs == null || !reqs.isValid(plugin.getUnlockedRegions()))
		{
			drawOutlinedString(graphics, IMPOSSIBLE,
				clueWidget.getCanvasLocation().getX() + (clueWidget.getWidth() / 2) - (fm.stringWidth(IMPOSSIBLE) / 2),
				clueWidget.getCanvasLocation().getY() + offset.y,
				Color.RED, Color.BLACK);
		}
		else
		{
			drawOutlinedString(graphics, POSSIBLE,
				clueWidget.getCanvasLocation().getX() + (clueWidget.getWidth() / 2) - (fm.stringWidth(POSSIBLE) / 2),
				clueWidget.getCanvasLocation().getY() + offset.y,
				Color.GREEN, Color.BLACK);
		}

		// Draw tooltips
		Point mousePos = new Point(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY());
		for (Rectangle rect : tooltipRegions.keySet())
		{
			if (rect.contains(mousePos))
			{
				tooltipManager.add(new Tooltip(tooltipRegions.get(rect)));
				break;
			}
		}

		return null;
	}

	private void parseAndDrawReq(Graphics2D g, Point topLeft, RegionRequirement req)
	{
		parseAndDrawReq(g, topLeft, req, null);
	}

	private void parseAndDrawReq(Graphics2D g, Point topLeft, RegionRequirement req, String prepend)
	{
		if (req instanceof SingleRegionRequirement)
		{
			SingleRegionRequirement sreq = (SingleRegionRequirement) req;
			if (prepend != null)
			{
				drawOutlinedString(g, prepend, topLeft.x, topLeft.y + TEXT_HEIGHT, Color.WHITE, Color.BLACK);
				drawRegion(g, sreq.getRequiredRegion(), topLeft.x, topLeft.y + TEXT_OFFSET);
			}
			else
			{
				drawRegion(g, sreq.getRequiredRegion(), topLeft.x, topLeft.y);
			}
		}
		else if (req instanceof ANDRegionRequirement)
		{
			ANDRegionRequirement areq = (ANDRegionRequirement) req;
			drawMultipleWithText(g, topLeft, areq.getRequiredRegions(), REGION_AND, prepend);
		}
		else if (req instanceof ORRegionRequirement)
		{
			ORRegionRequirement oreq = (ORRegionRequirement) req;
			drawMultipleWithText(g, topLeft, oreq.getRequiredRegions(), REGION_OR, prepend);
		}
		else if (req instanceof ANDGroupedRequirements)
		{
			ANDGroupedRequirements areq = (ANDGroupedRequirements) req;
			RegionRequirement[] reqs = areq.getRegionRequirements();
			for (int i = 0; i < reqs.length; i++)
			{
				parseAndDrawReq(g, topLeft, reqs[i], i > 0 ? REGION_MULTI_AND : null);
				if (!(reqs[i] instanceof GroupedRequirement))
				{
					topLeft.translate(0, LINE_HEIGHT + TEXT_OFFSET);
				}
			}
		}
		else if (req instanceof ORGroupedRequirements)
		{
			ORGroupedRequirements oreq = (ORGroupedRequirements) req;
			RegionRequirement[] reqs = oreq.getRegionRequirements();
			for (int i = 0; i < reqs.length; i++)
			{
				parseAndDrawReq(g, topLeft, reqs[i], i > 0 ? REGION_MULTI_OR : null);
				if (!(reqs[i] instanceof GroupedRequirement))
				{
					topLeft.translate(0, LINE_HEIGHT + TEXT_OFFSET);
				}
			}
		}
	}

	private void drawMultipleWithText(Graphics2D g, Point topLeft, Region[] regions, String text, String prepend)
	{
		int cx = 0;
		for (Region r : regions)
		{
			drawRegion(g, r, topLeft.x + cx, topLeft.y + TEXT_OFFSET);
			cx += SPRITE_WIDTH + SPACER;
		}
		final String s = prepend != null ? prepend + " " + text : text;
		final int x = topLeft.x;
		final int y = topLeft.y + TEXT_HEIGHT;
		drawOutlinedString(g, s, x, y, Color.WHITE, Color.BLACK);
	}

	private void drawOutlinedString(Graphics2D g, String s, int x, int y, Color colour, Color outlineColour)
	{
		g.setColor(outlineColour);
		g.drawString(s, x, y + 1);
		g.drawString(s, x, y - 1);
		g.drawString(s, x + 1, y);
		g.drawString(s, x - 1, y);
		g.setColor(colour);
		g.drawString(s, x, y);
	}

	private void drawRegion(Graphics2D g, Region region, int x, int y)
	{
		RegionData data = region.getRegion();
		BufferedImage sprite = spriteManager.getSprite(data.getSpriteID(), 0);
		if (sprite == null) return;
		g.drawImage(sprite, x, y, sprite.getWidth(), sprite.getHeight(), null);
		final boolean possible = plugin.getUnlockedRegions().contains(region);
		if (!possible)
		{
			BufferedImage cross = spriteManager.getSprite(SpriteID.RED_CLICK_ANIMATION_2, 0);
			if (cross == null) return;
			g.drawImage(cross, x + sprite.getWidth() - cross.getWidth(), y + sprite.getHeight() - cross.getHeight(), cross.getWidth(), cross.getHeight(), null);
		}

		if (region instanceof RareRegion)
		{
			BufferedImage rare = spriteManager.getSprite(SpriteID.FRIENDS_CHAT_RANK_GOLD_STAR_GENERAL, 0);
			if (rare == null) return;
			g.drawImage(rare, x + sprite.getWidth() - rare.getWidth() + 2, y - 2, rare.getWidth(), rare.getHeight(), null);

			final String tooltip = possible ? "Requires a rarer-than-usual drop" : "Would require a rarer-than-usual drop";
			tooltipRegions.put(new Rectangle(x, y, sprite.getWidth(), sprite.getHeight()), tooltip);
		}
	}

	private static boolean viewingClueScroll(final Widget parentWidget)
	{
		for (Widget w : parentWidget.getNestedChildren())
		{
			// 3395 is the ModelID for the clue scroll background
			// 31917 is the model used for a single map clue, because OF COURSE!
			if (w.getModelId() == 3395 || w.getModelId() == 31917)
			{
				return true;
			}
		}
		return false;
	}
}

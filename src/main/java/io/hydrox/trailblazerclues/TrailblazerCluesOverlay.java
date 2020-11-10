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
import io.hydrox.trailblazerclues.requirements.ORGroupedRequirements;
import io.hydrox.trailblazerclues.requirements.ORRegionRequirement;
import io.hydrox.trailblazerclues.requirements.RegionRequirement;
import io.hydrox.trailblazerclues.requirements.SingleRegionRequirement;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

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
	private final TrailblazerCluesPlugin plugin;

	@Inject
	public TrailblazerCluesOverlay(Client client, SpriteManager spriteManager, TrailblazerCluesPlugin plugin)
	{
		setLayer(OverlayLayer.ABOVE_MAP);
		setPosition(OverlayPosition.DYNAMIC);
		this.client = client;
		this.spriteManager = spriteManager;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.getGroupID() == -1 || plugin.getChildID() == -1)
		{
			return null;
		}
		RegionRequirement reqs = plugin.getCurrentReqs();
		Widget clueWidget = client.getWidget(plugin.getGroupID(), plugin.getChildID());
		if (clueWidget == null)
		{
			return null;
		}

		Point offset = new Point(-26, 0);
		if (clueWidget.getWidth() == 32)
		{
			// Thanks map clues for not having a proper background
			clueWidget = clueWidget.getParent();
			offset.move(-146, 37);
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
		BufferedImage sprite = spriteManager.getSprite(region.getSpriteID(), 0);
		if (sprite == null) return;
		g.drawImage(sprite, x, y, sprite.getWidth(), sprite.getHeight(), null);
		if (!plugin.getUnlockedRegions().contains(region))
		{
			BufferedImage cross = spriteManager.getSprite(SpriteID.RED_CLICK_ANIMATION_2, 0);
			if (cross == null) return;
			g.drawImage(cross, x + sprite.getWidth() - cross.getWidth(), y + sprite.getHeight() - cross.getHeight(), cross.getWidth(), cross.getHeight(), null);
		}
	}
}

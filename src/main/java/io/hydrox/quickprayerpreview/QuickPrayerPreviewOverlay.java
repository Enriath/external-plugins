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
package io.hydrox.quickprayerpreview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.config.TooltipPositionType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;

public class QuickPrayerPreviewOverlay extends OverlayPanel
{
	private static final int UNDER_OFFSET = 24;

	private final Client client;
	private final QuickPrayerPreviewPlugin plugin;
	private final QuickPrayerPreviewConfig config;
	private final RuneLiteConfig runeLiteConfig;

	@Inject
	public QuickPrayerPreviewOverlay(final Client client,
	                                 final QuickPrayerPreviewPlugin plugin,
	                                 final QuickPrayerPreviewConfig config,
	                                 final RuneLiteConfig runeLiteConfig)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.runeLiteConfig = runeLiteConfig;

		setPosition(OverlayPosition.TOOLTIP);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(Overlay.PRIORITY_HIGH);

		setDynamicFont(true);
	}

	@Override
	public Dimension render(final Graphics2D graphics)
	{
		if (config.requireKeyPress() && !plugin.isKeyPressed())
		{
			return null;
		}

		final List<Prayer> prayers = plugin.getQuickPrayers();

		if (prayers == null)
		{
			return null;
		}

		final Widget orb = client.getWidget(ComponentID.MINIMAP_PRAYER_ORB);

		if (orb == null || orb.isHidden() || orb.isSelfHidden())
		{
			return null;
		}

		final net.runelite.api.Point mouseCanvasPosition = client.getMouseCanvasPosition();

		final Rectangle prevBounds = getBounds();

		final int tooltipX = Math.min(client.getCanvasWidth() - prevBounds.width, mouseCanvasPosition.getX());
		final int tooltipY = runeLiteConfig.tooltipPosition() == TooltipPositionType.UNDER_CURSOR
			? Math.max(0, mouseCanvasPosition.getY() - 2 - prevBounds.height)
			: Math.min(client.getCanvasHeight() - prevBounds.height, mouseCanvasPosition.getY() + UNDER_OFFSET);

		if (!orb.getBounds().contains(new Point(mouseCanvasPosition.getX(), mouseCanvasPosition.getY())))
		{
			return null;
		}

		panelComponent.setPreferredLocation(new Point(tooltipX, tooltipY));
		final boolean text = config.useTextTooltip();
		panelComponent.setOrientation(text ? ComponentOrientation.VERTICAL : ComponentOrientation.HORIZONTAL);
		panelComponent.getChildren().clear();

		for (final Prayer p : prayers)
		{
			if (text)
			{
				panelComponent.getChildren().add(LineComponent.builder().leftColor(config.tooltipTextColor()).left(p.getName()).build());
			}
			else
			{
				final BufferedImage img = plugin.getSprite(p);

				if (img != null)
				{
					panelComponent.getChildren().add(new ImageComponent(img));
				}
			}
		}

		return panelComponent.render(graphics);
	}
}

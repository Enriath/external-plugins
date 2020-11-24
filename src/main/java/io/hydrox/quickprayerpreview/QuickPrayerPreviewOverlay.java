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

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.config.TooltipPositionType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

public class QuickPrayerPreviewOverlay extends Overlay
{
	private static final int UNDER_OFFSET = 24;

	private final Client client;
	private final QuickPrayerPreviewPlugin plugin;
	private final RuneLiteConfig runeLiteConfig;

	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	public QuickPrayerPreviewOverlay(Client client, QuickPrayerPreviewPlugin plugin, final RuneLiteConfig runeLiteConfig)
	{
		this.client = client;
		this.plugin = plugin;
		this.runeLiteConfig = runeLiteConfig;
		setPosition(OverlayPosition.TOOLTIP);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(OverlayPriority.HIGH);
		panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();
		Widget orb = client.getWidget(WidgetInfo.MINIMAP_PRAYER_ORB);
		List<Prayer> prayers = plugin.getQuickPrayers();
		if (prayers == null || orb == null || orb.isHidden() || orb.isSelfHidden())
		{
			return null;
		}
		net.runelite.api.Point mouseCanvasPosition = client.getMouseCanvasPosition();

		final int canvasWidth = client.getCanvasWidth();
		final int canvasHeight = client.getCanvasHeight();
		final Rectangle prevBounds = getBounds();

		final int tooltipX = Math.min(canvasWidth - prevBounds.width, mouseCanvasPosition.getX());
		final int tooltipY = runeLiteConfig.tooltipPosition() == TooltipPositionType.UNDER_CURSOR
		? Math.max(0, mouseCanvasPosition.getY() - 2 - prevBounds.height)
		: Math.min(canvasHeight - prevBounds.height, mouseCanvasPosition.getY() + UNDER_OFFSET);

		if (!orb.getBounds().contains(new Point(mouseCanvasPosition.getX(), mouseCanvasPosition.getY())))
		{
			return null;
		}

		panelComponent.setPreferredLocation(new Point(tooltipX, tooltipY));
		for (Prayer p : prayers)
		{
			BufferedImage img = plugin.getSprite(p);
			if (img != null)
			{
				panelComponent.getChildren().add(new ImageComponent(img));
			}
		}

		return panelComponent.render(graphics);
	}
}

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
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class QuickPrayerPreviewPrayerTabOverlay extends Overlay
{
	private final QuickPrayerPreviewPlugin plugin;
	private final Client client;

	private BufferedImage selectedPrayerSprite = null;

	@Inject
	protected QuickPrayerPreviewPrayerTabOverlay(QuickPrayerPreviewPlugin plugin, Client client)
	{
		this.plugin = plugin;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(Overlay.PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (selectedPrayerSprite == null)
		{
			int prayerSelectedSpriteId = SpriteID.Miscgraphics._11;
			BufferedImage img = plugin.getSprite(prayerSelectedSpriteId);
			if (img != null)
			{
				BufferedImage norm = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
				Graphics g = norm.getGraphics();
				g.drawImage(img, norm.getWidth() / 2 - img.getWidth() / 2, norm.getHeight() / 2 - img.getHeight() / 2, null);
				selectedPrayerSprite = img;
			}
		}

		// Don't show when not prayer book is hidden, this also hides it when selecting quick prayers tab is open
		Widget prayerbook = client.getWidget(InterfaceID.PRAYERBOOK, 0);
		if (prayerbook == null || prayerbook.isHidden())
		{
			return null;
		}

		if (selectedPrayerSprite == null)
		{
			return null;
		}

		for (Prayer quickPrayer : plugin.getQuickPrayers())
		{
			Widget prayerWidget = client.getWidget(quickPrayer.getInterfaceID());
			if (prayerWidget == null || prayerWidget.isHidden())
			{
				continue;
			}

			Point canvasPoint = prayerWidget.getCanvasLocation();
			if (canvasPoint == null)
			{
				continue;
			}

			graphics.drawImage(selectedPrayerSprite, canvasPoint.getX(), canvasPoint.getY(), null);
		}

		return null;
	}
}

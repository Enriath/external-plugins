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
package io.hydrox.coffincounter;

import static io.hydrox.coffincounter.UIUtil.drawString;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Map;

public class CoffinContentsInfobox extends InfoBox
{
	private final CoffinCounterPlugin plugin;

	public CoffinContentsInfobox(BufferedImage baseCoffin, CoffinCounterPlugin plugin)
	{
		super(createImage(baseCoffin, plugin), plugin);
		this.plugin = plugin;
		setTooltip("Coffin contents");
	}

	@Override
	public String getText()
	{
		return "";
	}

	@Override
	public Color getTextColor()
	{
		return Color.WHITE;
	}

	@Override
	public boolean render()
	{
		return plugin.getStored().values().stream().anyMatch(v -> v != 0);
	}

	private static BufferedImage createImage(BufferedImage baseCoffin, CoffinCounterPlugin plugin)
	{
		final BufferedImage coffin = new BufferedImage(
			baseCoffin.getColorModel(),
			baseCoffin.copyData(null),
			baseCoffin.getColorModel().isAlphaPremultiplied(),
			null
		);
		final Graphics graphics = coffin.getGraphics();
		graphics.setFont(FontManager.getRunescapeSmallFont());

		int idx = 0;
		for (Map.Entry<Shade, Integer> entry : plugin.getStored().entrySet())
		{
			if (entry.getValue() == 0)
			{
				continue;
			}
			final Shade shade = entry.getKey();
			final Integer value = entry.getValue();
			final int drawX = UIUtil.GAP_X * (idx / UIUtil.LABELS_PER_COLUMN);
			final int drawY = UIUtil.GAP_Y * (idx % UIUtil.LABELS_PER_COLUMN);
			if (entry.getValue() == -1)
			{
				drawString(graphics, shade, UIUtil.UNKNOWN, drawX, drawY);
			}
			else
			{
				drawString(graphics, shade, value.toString(), drawX, drawY);
			}
			idx++;
		}

		return coffin;
	}
}

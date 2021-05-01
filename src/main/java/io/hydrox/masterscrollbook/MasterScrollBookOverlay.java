/*
 * Copyright (c) 2021, Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.masterscrollbook;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map;

public class MasterScrollBookOverlay extends WidgetItemOverlay
{
	private final Client client;
	private final MasterScrollBookPlugin plugin;
	private final TooltipManager tooltipManager;

	@Inject
	MasterScrollBookOverlay(Client client, MasterScrollBookPlugin plugin, TooltipManager tooltipManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.tooltipManager = tooltipManager;

		showOnInventory();
		showOnBank();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		if (itemId != ItemID.MASTER_SCROLL_BOOK)
		{
			return;
		}

		Map<Scroll, Integer> counts = plugin.getCounts();
		StringBuilder tooltipBuilder = new StringBuilder();

		Scroll sel = plugin.getSelectedDefault();
		if (sel != null)
		{
			final Rectangle bounds = widgetItem.getCanvasBounds();
			final int drawX = bounds.x;
			final int drawY = bounds.y + bounds.height;
			final String text = counts.get(sel) + "";
			graphics.setFont(FontManager.getRunescapeSmallFont());
			graphics.setColor(Color.BLACK);
			graphics.drawString(text, drawX + 1, drawY + 1);
			graphics.setColor(Color.WHITE);
			graphics.drawString(text, drawX, drawY);

			tooltipBuilder
				.append(text)
				.append(" ")
				.append(ColorUtil.wrapWithColorTag(sel.getName(), Color.GREEN))
				.append("</br>");
		}

		if (!widgetItem.getCanvasBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			return;
		}

		for (Scroll s : Scroll.values())
		{
			if (s == sel)
			{
				continue;
			}
			tooltipBuilder
				.append(counts.getOrDefault(s, 0))
				.append(" ")
				.append(ColorUtil.wrapWithColorTag(s.getName(), Color.YELLOW))
				.append("</br>");
		}

		tooltipManager.add(new Tooltip(tooltipBuilder.toString()));
	}
}

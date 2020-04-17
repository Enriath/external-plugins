/*
 * Copyright (c) 2020 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.inventorysummary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.util.ImageUtil;

class InventorySummaryOverlay extends OverlayPanel
{
	private static final int INVENTORY_SIZE = 28;
	private static final Point SPRITE_PADDING = new Point(6, 4);
	private static final int WRAPPER_MINIMUM_WIDTH = Constants.ITEM_SPRITE_WIDTH + SPRITE_PADDING.x * 2;
	private static final int FREE_SLOTS_HEIGHT = 14;

	private final ItemManager itemManager;
	private final InventorySummaryConfig config;

	private final PanelComponent inventoryComponent = new PanelComponent();

	private final List<ImageComponent> images = new ArrayList<>();

	private ImageComponent inventoryIconSprite;
	private ImageComponent freeSlotsDisplay = new ImageComponent(new BufferedImage(Constants.ITEM_SPRITE_WIDTH, FREE_SLOTS_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR));

	private int lastWidth = 0;
	private String freeText = "Test";

	@Inject
	private InventorySummaryOverlay(ItemManager itemManager, InventorySummaryConfig config)
	{
		setPosition(OverlayPosition.BOTTOM_RIGHT);
		inventoryComponent.setGap(SPRITE_PADDING);
		inventoryComponent.setOrientation(ComponentOrientation.HORIZONTAL);
		inventoryComponent.setBackgroundColor(null);
		inventoryComponent.setBorder(new Rectangle(
			ComponentConstants.STANDARD_BORDER,
			0,
			0,
			ComponentConstants.STANDARD_BORDER));
		inventoryComponent.setPreferredSize(new Dimension(WRAPPER_MINIMUM_WIDTH, 0));
		inventoryComponent.setWrap(true);

		panelComponent.setOrientation(ComponentOrientation.VERTICAL);
		panelComponent.setBorder(new Rectangle(
			ComponentConstants.STANDARD_BORDER,
			ComponentConstants.STANDARD_BORDER,
			ComponentConstants.STANDARD_BORDER,
			ComponentConstants.STANDARD_BORDER));
		panelComponent.setPreferredSize(new Dimension(WRAPPER_MINIMUM_WIDTH, 0));

		this.itemManager = itemManager;
		this.config = config;

		inventoryIconSprite = new ImageComponent(ImageUtil.getResourceStreamFromClass(getClass(), "empty.png"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		inventoryComponent.getChildren().clear();

		if (images.size() == 0)
		{
			inventoryComponent.getChildren().add(inventoryIconSprite);
		}
		else
		{
			for (ImageComponent ic : images)
			{
				inventoryComponent.getChildren().add(ic);
			}
		}
		panelComponent.getChildren().add(inventoryComponent);

		if (config.showFreeSlots())
		{
			int invWidth = Math.max(Constants.ITEM_SPRITE_WIDTH, inventoryComponent.getBounds().width);
			if (invWidth != lastWidth)
			{
				BufferedImage bf = new BufferedImage(invWidth, FREE_SLOTS_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics g = bf.getGraphics();
				FontMetrics fm = g.getFontMetrics();
				g.setFont(FontManager.getRunescapeFont());
				g.setColor(Color.BLACK);
				g.drawString(freeText, ((bf.getWidth() - fm.stringWidth(freeText)) / 2) + 1, fm.getAscent() + 1);
				g.setColor(Color.WHITE);
				g.drawString(freeText, (bf.getWidth() - fm.stringWidth(freeText)) / 2, fm.getAscent());
				freeSlotsDisplay = new ImageComponent(bf);
				lastWidth = invWidth;
			}
			panelComponent.getChildren().add(freeSlotsDisplay);
		}
		return super.render(graphics);
	}

	void rebuild(Map<Integer, Integer> groupedItems, int spacesUsed)
	{
		inventoryComponent.getChildren().clear();
		images.clear();

		for (Map.Entry<Integer, Integer> cursor : groupedItems.entrySet())
		{
			final BufferedImage image = itemManager.getImage(cursor.getKey(), cursor.getValue(), true);
			if (image != null)
			{
				images.add(new ImageComponent(image));
			}
		}
		freeText = (INVENTORY_SIZE - spacesUsed) + " free";
		lastWidth = 0;
	}
}

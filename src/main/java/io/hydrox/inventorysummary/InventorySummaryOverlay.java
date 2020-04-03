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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.SpriteID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

class InventorySummaryOverlay extends Overlay
{
	private static final int INVENTORY_SIZE = 28;
	private static final Point SPRITE_PADDING = new Point(6, 4);
	private static final int WRAPPER_MINIMUM_WIDTH = Constants.ITEM_SPRITE_WIDTH + SPRITE_PADDING.x * 2;

	private final ItemManager itemManager;
	private final InventorySummaryConfig config;

	private final PanelComponent wrapperComponent = new PanelComponent();
	private final PanelComponent inventoryComponent = new PanelComponent();
	private final TitleComponent freeSlotsComponent = TitleComponent.builder().build();

	private ImageComponent inventoryIconSprite;

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

		wrapperComponent.setOrientation(ComponentOrientation.VERTICAL);
		wrapperComponent.setWrapping(2);
		wrapperComponent.setBorder(new Rectangle(
			ComponentConstants.STANDARD_BORDER,
			ComponentConstants.STANDARD_BORDER,
			ComponentConstants.STANDARD_BORDER,
			ComponentConstants.STANDARD_BORDER));

		this.itemManager = itemManager;
		this.config = config;

		inventoryIconSprite = new ImageComponent(ImageUtil.getResourceStreamFromClass(getClass(), "empty.png"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		return wrapperComponent.render(graphics);
	}

	void rebuild(Map<Integer, Integer> groupedItems, int spacesUsed)
	{
		inventoryComponent.getChildren().clear();
		wrapperComponent.getChildren().clear();

		inventoryComponent.setWrapping(config.wrapCount());

		int wrapperWidth;

		for (Map.Entry<Integer, Integer> cursor : groupedItems.entrySet())
		{
			final BufferedImage image = itemManager.getImage(cursor.getKey(), cursor.getValue(), true);
			if (image != null)
			{
				inventoryComponent.getChildren().add(new ImageComponent(image));
			}
		}

		// Add a placeholder if the inventory is empty, so the overlay can still be easily seen
		if (groupedItems.entrySet().size() == 0)
		{
			inventoryComponent.getChildren().add(inventoryIconSprite);
			wrapperWidth = WRAPPER_MINIMUM_WIDTH;
		}
		else
		{
			final int wrap = config.wrapCount() == 0 ? Integer.MAX_VALUE : config.wrapCount();
			final int widthItems = Math.min(wrap, groupedItems.entrySet().size());
			wrapperWidth = widthItems * Constants.ITEM_SPRITE_WIDTH + (widthItems + 1) * SPRITE_PADDING.x;
		}

		wrapperComponent.getChildren().add(inventoryComponent);

		if (config.showFreeSlots())
		{
			// Set the width of the wrapper so the free slots component will have the correct alignment
			wrapperComponent.setPreferredSize(new Dimension(Math.max(wrapperWidth, WRAPPER_MINIMUM_WIDTH), 0));

			freeSlotsComponent.setText((INVENTORY_SIZE - spacesUsed) + " free");
			wrapperComponent.getChildren().add(freeSlotsComponent);
		}
	}

	private BufferedImage getImage(Item item)
	{
		ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
		return itemManager.getImage(item.getId(), item.getQuantity(), itemComposition.isStackable());
	}
}

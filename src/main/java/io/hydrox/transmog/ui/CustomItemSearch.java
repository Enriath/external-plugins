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
package io.hydrox.transmog.ui;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.hydrox.transmog.StupidItems;
import io.hydrox.transmog.TransmogSlot;
import lombok.Setter;
import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

@Singleton
public class CustomItemSearch extends CustomSearch
{
	// JMod Items
	private static final Set<Integer> IGNORED_ITEMS = Sets.newHashSet(22664, 22665, 22666);

	@Setter
	private TransmogSlot slot;

	private final ItemManager itemManager;

	@Setter
	protected BiConsumer<Integer, String> onItemSelected;

	@Value
	private static class ItemIcon
	{
		private final int modelId;
		private final short[] colorsToReplace;
		private final short[] texturesToReplace;
	}

	@Inject
	protected CustomItemSearch(ChatboxPanelManager chatboxPanelManager, ClientThread clientThread, ItemManager itemManager, Client client)
	{
		super(chatboxPanelManager, clientThread, client);
		this.itemManager = itemManager;
	}

	@Override
	protected void createContents(Widget container)
	{
		int x = PADDING;
		int y = PADDING * 3;
		int idx = 0;
		for (Object o : results)
		{
			final ItemComposition itemComposition = (ItemComposition) o;
			Widget item = container.createChild(-1, WidgetType.GRAPHIC);
			item.setXPositionMode(WidgetPositionMode.ABSOLUTE_LEFT);
			item.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
			item.setOriginalX(x);
			item.setOriginalY(y + FONT_SIZE * 2);
			item.setOriginalHeight(ICON_HEIGHT);
			item.setOriginalWidth(ICON_WIDTH);
			item.setName("<col=ff9040>" + itemComposition.getName());
			item.setItemId(itemComposition.getId());
			item.setItemQuantity(10000);
			item.setItemQuantityMode(ItemQuantityMode.NEVER);
			item.setBorderType(1);
			item.setAction(0, tooltipText);
			item.setHasListener(true);

			if (index == idx)
			{
				item.setOpacity(HOVERED_OPACITY);
			}
			else
			{
				item.setOnMouseOverListener((JavaScriptCallback) ev -> item.setOpacity(HOVERED_OPACITY));
				item.setOnMouseLeaveListener((JavaScriptCallback) ev -> item.setOpacity(0));
			}

			item.setOnOpListener((JavaScriptCallback) ev ->
			{
				if (hasCallback())
				{
					runCallback(itemComposition);
				}

				chatboxPanelManager.close();
			});

			x += ICON_WIDTH + PADDING;
			if (x + ICON_WIDTH >= container.getWidth())
			{
				y += ICON_HEIGHT + PADDING;
				x = PADDING;
			}

			item.revalidate();
			++idx;
		}
	}

	@Override
	protected void filterResults()
	{
		results.clear();
		index = -1;

		String search = getValue().toLowerCase();
		if (search.isEmpty())
		{
			return;
		}

		Set<ItemIcon> itemIcons = new HashSet<>();
		for (int i = 0; i < client.getItemCount() && results.size() < MAX_RESULTS; i++)
		//for (int i = client.getItemCount() - 1; i >= 0 && results.size() < MAX_RESULTS; i--)
		{
			ItemComposition itemComposition = itemManager.getItemComposition(itemManager.canonicalize(i));
			ItemStats itemStats = itemManager.getItemStats(itemComposition.getId(), false);
			if (itemStats == null || !itemStats.isEquipable())
			{
				continue;
			}
			ItemEquipmentStats stats = itemStats.getEquipment();
			String name = itemComposition.getName().toLowerCase();

			// The client assigns "null" to item names of items it doesn't know about
			// and the item might already be in the results from canonicalize
			if (stats.getSlot() == slot.getKitIndex() && name.contains(search) &&
				!results.contains(itemComposition) && !name.equals("null") && !IGNORED_ITEMS.contains(i))
			{
				// Check if the results already contain the same item image
				ItemIcon itemIcon = new ItemIcon(itemComposition.getInventoryModel(),
					itemComposition.getColorToReplaceWith(), itemComposition.getTextureToReplaceWith());
				if (itemIcons.contains(itemIcon))
				{
					continue;
				}

				itemIcons.add(itemIcon);
				results.add(itemComposition);
			}
		}
	}

	@Override
	protected void runCallback(Object o)
	{
		ItemComposition ic = (ItemComposition) o;
		onItemSelected.accept(StupidItems.convertId(ic.getId()), ic.getName());
	}

	@Override
	protected boolean hasCallback()
	{
		return onItemSelected != null;
	}
}
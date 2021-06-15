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

import com.google.inject.Provides;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@PluginDescriptor(
	name = "Inventory Summary",
	description = "A compact summary of your inventory",
	tags = {"alternate", "items", "overlay", "second", "summary"}
)
public class InventorySummaryPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InventorySummaryOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InventorySummaryConfig config;

	@Inject
	private ItemManager itemManager;

	private List<String> whitelist = new CopyOnWriteArrayList<>();
	private List<String> blacklist = new CopyOnWriteArrayList<>();

	@Provides
	InventorySummaryConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventorySummaryConfig.class);
	}

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
		whitelist = Text.fromCSV(config.whitelist());
		blacklist = Text.fromCSV(config.blacklist());
		clientThread.invoke((Runnable) this::groupItems);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("inventorysummary"))
		{
			whitelist = Text.fromCSV(config.whitelist());
			blacklist = Text.fromCSV(config.blacklist());
			clientThread.invoke((Runnable) this::groupItems);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INVENTORY.getId())
		{
			groupItems(event.getItemContainer().getItems());
		}
	}

	private void groupItems()
	{
		ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
		groupItems(container == null ? new Item[0] : container.getItems());
	}

	private boolean listContains(List<String> list, String search)
	{
		for (String item : list)
		{
			if (WildcardMatcher.matches(item, search))
			{
				return true;
			}
		}
		return false;
	}

    private boolean shouldItemBeShown(Item item)
    {
        ItemComposition itemComp = itemManager.getItemComposition(item.getId());
        return (!config.whitelistEnabled() || listContains(whitelist, itemComp.getName())) &&
                !(config.blacklistEnabled() && listContains(blacklist, itemComp.getName()));
    }

	private void groupItems(Item[] items)
	{
		Map<Integer, Integer> groupedItems = Arrays.stream(items)
			.filter(p -> p.getId() != -1)
			.filter(this::shouldItemBeShown)
			.collect(groupingBy(Item::getId, LinkedHashMap::new, summingInt(Item::getQuantity)));

		int spacesUsed = (int) Arrays.stream(items)
			.filter(p -> p.getId() != -1)
			.count();

		overlay.rebuild(groupedItems, spacesUsed);
	}
}
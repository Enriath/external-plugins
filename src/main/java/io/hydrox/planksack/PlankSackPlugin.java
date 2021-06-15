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
package io.hydrox.planksack;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.primitives.Ints;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
	name = "Plank Sack",
	description = "See the contents of your Plank Sack at a glance",
	tags = {"plank", "construction", "viewer", "mahogany", "teak", "oak", "homes"}
)
public class PlankSackPlugin extends Plugin
{
	private static final List<Integer> PLANKS = Arrays.asList(ItemID.PLANK, ItemID.OAK_PLANK, ItemID.TEAK_PLANK, ItemID.MAHOGANY_PLANK);

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlankSackOverlay overlay;

	@Getter
	private int plankCount = -1;

	private Multiset<Integer> inventorySnapshot;
	private boolean checkForUpdate = false;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (checkForUpdate)
		{
			checkForUpdate = false;
			Multiset<Integer> currentInventory = createSnapshot(event.getItemContainer());
			Multiset<Integer> deltaMinus = Multisets.difference(currentInventory, inventorySnapshot);
			Multiset<Integer> deltaPlus = Multisets.difference(inventorySnapshot, currentInventory);
			deltaPlus.forEachEntry((id, c) -> plankCount += c);
			deltaMinus.forEachEntry((id, c) -> plankCount -= c);
			plankCount = Ints.constrainToRange(plankCount, 0, 28);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if ((event.getId() == ItemID.PLANK_SACK && (event.getMenuOption().equals("Fill") || event.getMenuOption().equals("Empty")))
		|| (event.getMenuTarget().equals("<col=ff9040>Plank sack</col>") && event.getMenuOption().equals("Use")))
		{
			inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
			checkForUpdate = true;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}
		if (event.getMessage().startsWith("Basic\u00A0planks:"))
		{
			String message = Text.removeTags(event.getMessage());
			plankCount = Arrays.stream(message.split(",")).mapToInt(s -> Integer.parseInt(s.split(":\u00A0")[1])).sum();
		}
		else if (event.getMessage().equals("You haven't got any planks that can go in the sack."))
		{
			checkForUpdate = false;
		}
		else if (event.getMessage().equals("Your sack is full."))
		{
			plankCount = 28;
			checkForUpdate = false;
		}
		else if (event.getMessage().equals("Your sack is empty."))
		{
			plankCount = 0;
			checkForUpdate = false;
		}
	}

	private Multiset<Integer> createSnapshot(ItemContainer container)
	{
		if (container == null)
		{
			return null;
		}
		Multiset<Integer> snapshot = HashMultiset.create();
		Arrays.stream(container.getItems())
			.filter(item -> PLANKS.contains(item.getId()))
			.forEach(i -> snapshot.add(i.getId(), i.getQuantity()));
		return snapshot;
	}
}

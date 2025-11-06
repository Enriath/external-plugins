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
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@PluginDescriptor(
	name = "Plank Sack",
	description = "See the contents of your Plank Sack at a glance",
	tags = {"plank", "construction", "viewer", "mahogany", "teak", "oak", "homes"}
)
public class PlankSackPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlankSackConfig config;

	@Inject
	private PlankSackOverlay overlay;

	@Provides
	PlankSackConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlankSackConfig.class);
	}

	@Getter
	private int plankCount = -1;
	private int PLANK_SACK_PLAIN;
	private int PLANK_SACK_OAK;
	private int PLANK_SACK_TEAK;
	private int PLANK_SACK_MAHOGANY;
	private int PLANK_SACK_CAMPHOR;
	private int PLANK_SACK_IRONWOOD;
	private int PLANK_SACK_ROSEWOOD;

	private PlankSackCounter plankSackCounter;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);

		plankSackCounter = new PlankSackCounter(itemManager.getImage(ItemID.PLANK_SACK), this);
		clientThread.invoke(() -> updateInfobox(client.getItemContainer(InventoryID.INVENTORY)));
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
		infoBoxManager.removeInfoBox(plankSackCounter);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			PLANK_SACK_PLAIN = client.getVarbitValue(VarbitID.PLANK_SACK_PLAIN);
			PLANK_SACK_OAK = client.getVarbitValue(VarbitID.PLANK_SACK_OAK);
			PLANK_SACK_TEAK = client.getVarbitValue(VarbitID.PLANK_SACK_TEAK);
			PLANK_SACK_MAHOGANY = client.getVarbitValue(VarbitID.PLANK_SACK_MAHOGANY);
			PLANK_SACK_CAMPHOR = client.getVarbitValue(VarbitID.PLANK_SACK_CAMPHOR);
			PLANK_SACK_IRONWOOD = client.getVarbitValue(VarbitID.PLANK_SACK_IRONWOOD);
			PLANK_SACK_ROSEWOOD = client.getVarbitValue(VarbitID.PLANK_SACK_ROSEWOOD);
			plankCount = PLANK_SACK_PLAIN + PLANK_SACK_OAK + PLANK_SACK_TEAK + PLANK_SACK_MAHOGANY + PLANK_SACK_CAMPHOR + PLANK_SACK_IRONWOOD + PLANK_SACK_ROSEWOOD;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INVENTORY.getId())
		{
			return;
		}
		
		updateInfobox(event.getItemContainer());
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(PlankSackConfig.CONFIG_GROUP))
		{
			clientThread.invoke(() -> updateInfobox(client.getItemContainer(InventoryID.INVENTORY)));
		}
	}
	
	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int varbitId = event.getVarbitId();
		switch (varbitId)
		{
			case VarbitID.PLANK_SACK_PLAIN:
				PLANK_SACK_PLAIN = event.getValue();
				break;
			case VarbitID.PLANK_SACK_OAK:
				PLANK_SACK_OAK = event.getValue();
				break;
			case VarbitID.PLANK_SACK_TEAK:
				PLANK_SACK_TEAK = event.getValue();
				break;
			case VarbitID.PLANK_SACK_MAHOGANY:
				PLANK_SACK_MAHOGANY = event.getValue();
				break;
			case VarbitID.PLANK_SACK_CAMPHOR:
				PLANK_SACK_CAMPHOR = event.getValue();
				break;
			case VarbitID.PLANK_SACK_IRONWOOD:
				PLANK_SACK_IRONWOOD = event.getValue();
				break;
			case VarbitID.PLANK_SACK_ROSEWOOD:
				PLANK_SACK_ROSEWOOD = event.getValue();
				break;
			default:
				return;
		}
		
		plankCount = PLANK_SACK_PLAIN + PLANK_SACK_OAK + PLANK_SACK_TEAK + PLANK_SACK_MAHOGANY + PLANK_SACK_CAMPHOR + PLANK_SACK_IRONWOOD + PLANK_SACK_ROSEWOOD;
	}

	private void updateInfobox(ItemContainer container)
	{
		infoBoxManager.removeInfoBox(plankSackCounter);
		if (container == null)
		{
			return;
		}
		boolean val = container.contains(ItemID.PLANK_SACK);
		if (val && config.showInfobox())
		{
			infoBoxManager.addInfoBox(plankSackCounter);
		}
	}

	Color getColour()
	{
		if (plankCount <= 0)
		{
			return Color.RED;
		}
		else if (plankCount < 14)
		{
			return Color.YELLOW;
		}
		else
		{
			return Color.WHITE;
		}
	}
}

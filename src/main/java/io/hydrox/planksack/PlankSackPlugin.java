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

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.awt.Color;

@Slf4j
@PluginDescriptor(
	name = "Plank Sack Counter",
	description = "Displays the contents of your Plank Sack using varbits",
	tags = {"plank", "construction", "counter", "mahogany", "teak", "oak", "homes", "sailing"}
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

	// Individual plank type counts from varbits
	private int plankSackPlain;
	private int plankSackOak;
	private int plankSackTeak;
	private int plankSackMahogany;
	private int plankSackCamphor;
	private int plankSackIronwood;
	private int plankSackRosewood;

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
			// Initialize plank counts from varbits on login
			plankSackPlain = client.getVarbitValue(VarbitID.PLANK_SACK_PLAIN);
			plankSackOak = client.getVarbitValue(VarbitID.PLANK_SACK_OAK);
			plankSackTeak = client.getVarbitValue(VarbitID.PLANK_SACK_TEAK);
			plankSackMahogany = client.getVarbitValue(VarbitID.PLANK_SACK_MAHOGANY);
			plankSackCamphor = client.getVarbitValue(VarbitID.PLANK_SACK_CAMPHOR);
			plankSackIronwood = client.getVarbitValue(VarbitID.PLANK_SACK_IRONWOOD);
			plankSackRosewood = client.getVarbitValue(VarbitID.PLANK_SACK_ROSEWOOD);
			updatePlankCount();
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
				plankSackPlain = event.getValue();
				break;
			case VarbitID.PLANK_SACK_OAK:
				plankSackOak = event.getValue();
				break;
			case VarbitID.PLANK_SACK_TEAK:
				plankSackTeak = event.getValue();
				break;
			case VarbitID.PLANK_SACK_MAHOGANY:
				plankSackMahogany = event.getValue();
				break;
			case VarbitID.PLANK_SACK_CAMPHOR:
				plankSackCamphor = event.getValue();
				break;
			case VarbitID.PLANK_SACK_IRONWOOD:
				plankSackIronwood = event.getValue();
				break;
			case VarbitID.PLANK_SACK_ROSEWOOD:
				plankSackRosewood = event.getValue();
				break;
			default:
				return;
		}

		updatePlankCount();
	}

	private void updatePlankCount()
	{
		plankCount = plankSackPlain + plankSackOak + plankSackTeak + plankSackMahogany
			+ plankSackCamphor + plankSackIronwood + plankSackRosewood;
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
		return Color.WHITE;
	}
}

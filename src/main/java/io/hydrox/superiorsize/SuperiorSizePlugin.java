/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package io.hydrox.superiorsize;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Superior Size"
)
public class SuperiorSizePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SuperiorSizeConfig config;

	@Inject
	private SuperiorSizeOverlay overlay;

	@Getter(AccessLevel.PACKAGE)
	private final Set<NPC> highlightedNpcs = new HashSet<>();

	private String slayerTask;

	@Provides
	SuperiorSizeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SuperiorSizeConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		clientThread.invoke(this::rebuildNPCs);
		slayerTask = configManager.getConfiguration("slayer", "taskName");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		highlightedNpcs.clear();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("superiorsize"))
		{
			rebuildNPCs();
		}
		else if (event.getGroup().equals("slayer") && event.getKey().equals("taskName"))
		{
			slayerTask = event.getNewValue();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			highlightedNpcs.clear();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null)
		{
			return;
		}

		checkNPC(npc, npcName.toLowerCase());
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		highlightedNpcs.remove(npcDespawned.getNpc());
	}

	private void rebuildNPCs()
	{
		highlightedNpcs.clear();

		if (client.getGameState() != GameState.LOGGED_IN && client.getGameState() != GameState.LOADING)
		{
			// NPCs are still in the client after logging out,
			// but we don't want to highlight those.
			return;
		}

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null)
			{
				continue;
			}

			checkNPC(npc, npcName.toLowerCase());
		}
	}

	private void checkNPC(NPC npc, String npcName)
	{
		if (npc.getId() == NpcID.JELLY_7518)
		{
			return; // Dammit Jelly, you're not a Jelly! You're a troll!
		}
		if (config.ignoreTask())
		{
			if (Superior.fromName(npcName) != null)
			{
				highlightedNpcs.add(npc);
			}
		}
		else
		{
			if (slayerTask != null && !slayerTask.equals(""))
			{
				Superior superior = Superior.fromTask(slayerTask);
				if (superior != null)
				{
					for (String name : superior.names)
					{
						if (npcName.toLowerCase().equals(name))
						{
							highlightedNpcs.add(npc);
							break;
						}
					}
				}
			}
		}
	}
}

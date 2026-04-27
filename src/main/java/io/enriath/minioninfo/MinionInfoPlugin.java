/*
 * Copyright (c) 2026, Enriath <ikada@protonmail.ch>
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
package io.enriath.minioninfo;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.QuantityFormatter;
import javax.inject.Inject;

@PluginDescriptor(
	name = "Minion Info",
	description = "See Minion settings at a glance, similar to an RS3 Tooltip",
	tags = {"leagues", "demonic", "pacts", "minion", "tooltip", "information", "loot", "looting", "aoe", "follow"}
)
public class MinionInfoPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MinionInfoTooltip overlay;

	@Getter
	private boolean aoe_enabled = false;

	@Getter
	private boolean follow_enabled = false;

	@Getter
	private boolean looting_enabled = false;

	@Getter
	private boolean noting_enabled = false;

	@Getter
	private String value_threshold = "0";


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
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		updateInfo();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() != VarbitID.LEAGUE_GUARDIAN_AOE_DISABLED
			&& event.getVarbitId() != VarbitID.LEAGUE_GUARDIAN_FOLLOW_DISABLED
			&& event.getVarbitId() != VarbitID.LEAGUE_GUARDIAN_PICKUP_ITEMS_DISABLED
			&& event.getVarbitId() != VarbitID.LEAGUE_GUARDIAN_PICKUP_NOTED_DISABLED
			&& event.getVarpId() != VarPlayerID.LEAGUE_GUARDIAN_PICKUP_VALUE)
		{
			return;
		}

		updateInfo();
	}

	private void updateInfo()
	{
		this.aoe_enabled = client.getVarbitValue(VarbitID.LEAGUE_GUARDIAN_AOE_DISABLED) != 1;
		this.follow_enabled = client.getVarbitValue(VarbitID.LEAGUE_GUARDIAN_FOLLOW_DISABLED) != 1;
		this.looting_enabled = client.getVarbitValue(VarbitID.LEAGUE_GUARDIAN_PICKUP_ITEMS_DISABLED) != 1;
		this.noting_enabled = client.getVarbitValue(VarbitID.LEAGUE_GUARDIAN_PICKUP_NOTED_DISABLED) != 1;
		this.value_threshold = QuantityFormatter.formatNumber(
			client.getVarpValue(VarPlayerID.LEAGUE_GUARDIAN_PICKUP_VALUE));
	}
}
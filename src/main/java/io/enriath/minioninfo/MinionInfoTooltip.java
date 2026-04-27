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

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import java.awt.Graphics2D;

public class MinionInfoTooltip extends WidgetItemOverlay
{
	private final Client client;
	private final MinionInfoPlugin plugin;
	private final MinionInfoConfig config;
	private final TooltipManager tooltipManager;


	@Inject
	MinionInfoTooltip(Client client, MinionInfoPlugin plugin, MinionInfoConfig config, TooltipManager tooltipManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.tooltipManager = tooltipManager;

		showOnInventory();
		showOnBank();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		if (itemId != ItemID.LEAGUE_GUARDIAN_SUMMON || !widgetItem.getCanvasBounds().contains(
			client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())
		)
		{
			return;
		}

		final String enabledString = plugin.getEnabledString();
		final String disabledString = plugin.getDisabledString();

		String tooltip = config.shortNames() ? "AoE: " : "Area of Effect: ";
		tooltip += plugin.isAoe_enabled() ? enabledString : disabledString;
		tooltip += "</br>";
		tooltip += config.shortNames() ? "Follow: " : "Following: ";
		tooltip += plugin.isFollow_enabled() ? enabledString : disabledString;
		tooltip += "</br>";
		tooltip += config.shortNames() ? "Loot: " : "Looting Items: ";
		tooltip += plugin.isLooting_enabled() ? enabledString : disabledString;

		if (plugin.isLooting_enabled() || !config.hideLootWhenDisabled())
		{
			tooltip += "</br>";
			tooltip += config.shortNames() ? "Note: " : "Noting Items: ";
			tooltip += plugin.isNoting_enabled() ? enabledString : disabledString;
			tooltip += "</br>";
			tooltip += config.shortNames() ? "Value: " : "Value Threshold: ";
			tooltip += plugin.getValue_threshold();
		}

		tooltipManager.add(new Tooltip(tooltip));
	}
}

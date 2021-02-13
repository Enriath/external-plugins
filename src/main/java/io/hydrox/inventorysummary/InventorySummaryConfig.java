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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("inventorysummary")
public interface InventorySummaryConfig extends Config
{
	@ConfigSection(
		name = "Whitelist",
		description = "Whitelist related configurations",
		position = 1,
		closedByDefault = true
	)
	String whitelist = "whitelist";

	@ConfigSection(
		name = "Blacklist",
		description = "Blacklist related configurations",
		position = 2,
		closedByDefault = true
	)
	String blacklist = "blacklist";

	@ConfigItem(
		keyName = "showFreeSlots",
		name = "Show Free Slots",
		description = "Whether to show a label with the free slots in the inventory"
	)
	default boolean showFreeSlots()
	{
		return false;
	}

	@ConfigItem(
		keyName = "whitelistEnabled",
		name = "Enable Whitelist",
		description = "Whether only items listed in the whitelist should be counted",
		position = 1,
		section = whitelist
	)
	default boolean whitelistEnabled() { return false; }

	@ConfigItem(
		keyName = "whitelist",
		name = "Whitelist",
		description = "List of the items to display",
		position = 2,
		section = whitelist
	)
	default String whitelist() { return ""; }

	@ConfigItem(
		keyName = "blacklistEnabled",
		name = "Enable Blacklist",
		description = "Whether items in the blacklist should be hidden",
		position = 1,
		section = blacklist
	)
	default boolean blacklistEnabled() { return false; }

	@ConfigItem(
		keyName = "blacklist",
		name = "Blacklist",
		description = "List of items to hide",
		position = 2,
		section = blacklist
	)
	default String blacklist() { return ""; }
}

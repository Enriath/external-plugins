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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;

@ConfigGroup(MinionInfoPlugin.CONFIG_GROUP)
public interface MinionInfoConfig extends Config
{
	@ConfigItem(
		keyName = "enabledColour",
		name = "Enabled Colour",
		description = "What colour to use when a setting is enabled",
		position = 0
	)
	default Color enabledColour()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "disabledColour",
		name = "Disabled Colour",
		description = "What colour to use when a setting is disabled",
		position = 1
	)
	default Color disabledColour()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "hideLootWhenDisabled",
		name = "Hide loot info when disabled",
		description = "Hide the value threshold and noted loot information when item looting is disabled.",
		position = 2
	)
	default boolean hideLootWhenDisabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "shortNames",
		name = "Shorter info names",
		description = "e.g. 'AoE' instead of 'Area of Effect'",
		position = 3
	)
	default boolean shortNames()
	{
		return false;
	}

	@ConfigItem(
		keyName = "shortValues",
		name = "Shorter values",
		description = "e.g. 'On' instead of 'Enabled'",
		position = 4
	)
	default boolean shortValues()
	{
		return false;
	}

	@ConfigItem(
		keyName = "shortThreshold",
		name = "Shorter threshold",
		description = "Use K and M for thousands and millions on the value threshold",
		position = 5
	)
	default boolean shortThreshold()
	{
		return false;
	}
}

/*
 * Copyright (c) 2022 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.shatteredrelicxp;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(ShatteredRelicXPConfig.GROUP)
public interface ShatteredRelicXPConfig extends Config
{
	String GROUP = "shatteredrelicxp";

	@ConfigSection(
		name = "Tooltip",
		description = "Settings for the tooltips",
		position = 1
	)
	String tooltipSection = "tooltipSection";

	@ConfigItem(
		keyName = "tooltipShowXP",
		name = "Show XP",
		description = "Show XP in the tooltip",
		section = tooltipSection
	)
	default boolean tooltipShowXP()
	{
		return true;
	}

	@ConfigItem(
		keyName = "tooltipShowBar",
		name = "Show Progress Bar",
		description = "Show progress bar in the tooltip",
		section = tooltipSection
	)
	default boolean tooltipShowBar()
	{
		return true;
	}

	default boolean shouldModifyTooltips()
	{
		return tooltipShowBar() || tooltipShowBar();
	}
}

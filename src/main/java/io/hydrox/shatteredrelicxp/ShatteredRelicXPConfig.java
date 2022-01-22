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
import net.runelite.client.config.Range;
import java.awt.Color;

@ConfigGroup(ShatteredRelicXPConfig.GROUP)
public interface ShatteredRelicXPConfig extends Config
{
	String GROUP = "shatteredrelicxp";

	enum OverlayTextMode
	{
		NONE,
		XP,
		PERCENT
	}

	enum OverlayTextPosition
	{
		TOP,
		BOTTOM
	}

	@ConfigSection(
		name = "Tooltip",
		description = "Settings for the tooltips",
		position = 1
	)
	String tooltipSection = "tooltipSection";

	@ConfigSection(
		name = "Overlay",
		description = "Settings for the overlay",
		position = 2
	)
	String overlaySection = "overlaySection";

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

	@ConfigItem(
		keyName = "tooltipDescriptiveDescriptions",
		name = "Descriptive descriptions",
		description = "Use more descriptive descriptions for tooltips (Warning! Some are very long!)",
		section = tooltipSection
	)
	default boolean tooltipDescriptiveDescriptions()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overlayTextMode",
		name = "Text Mode",
		description = "What text to show on each of the icons in the overlay",
		position = 1,
		section = overlaySection
	)
	default OverlayTextMode overlayTextMode()
	{
		return OverlayTextMode.NONE;
	}

	@ConfigItem(
		keyName = "overlayTextPosition",
		name = "Text Position",
		description = "Where to put the text.",
		position = 2,
		section = overlaySection
	)
	default OverlayTextPosition overlayTextPosition()
	{
		return OverlayTextPosition.TOP;
	}

	@ConfigItem(
		keyName = "overlayShowBar",
		name = "Show Progress Bar",
		description = "Show progress bar on each of the icons in the overlay",
		position = 4,
		section = overlaySection
	)
	default boolean overlayShowBar()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overlayBarHeight",
		name = "Bar Height",
		description = "How big the bars should be in the overlay",
		position = 5,
		section = overlaySection
	)
	@Range(
		min = 1,
		max = 32
	)
	default int overlayBarHeight()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "overlayTextColour",
		name = "Text Colour",
		description = "What colour to use for the text in the overlay",
		position = 3,
		section = overlaySection
	)
	default Color overlayTextColour()
	{
		return Color.WHITE;
	}
}

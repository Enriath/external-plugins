/*
 * Copyright (c) 2021 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.coffincounter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
	name = "Coffin Counter",
	description = "Track what remains your coffin contains.",
	tags = {"shades", "remains", "coffin", "count", "loar", "phrin", "riyl", "asyn", "fiyr", "urium", "morton", "mort'ton", "sacred"}
)
@Slf4j
public class CoffinCounterPlugin extends Plugin
{
	private static final String CHECK_START = "Loar ";
	private static final Pattern CHECK_PATTERN = Pattern.compile("Loar (\\d{1,2}) / Phrin (\\d{1,2}) / Riyl (\\d{1,2}) / Asyn (\\d{1,2}) / Fiyr (\\d{1,2}) / Urium (\\d{1,2})");
	private static final String PICK_UP_START = "You put ";
	private static final Pattern PICK_UP_PATTERN = Pattern.compile("You put the (\\w+) remains into your open coffin\\.");

	@Inject
	private CoffinCounterOverlay overlay;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private final Map<Shade, Integer> stored = Arrays.stream(Shade.values())
		.collect(LinkedHashMap::new, (map, shade) -> map.put(shade, -1), Map::putAll);

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
	public void onChatMessage(ChatMessage event)
	{
		String message = Text.removeTags(event.getMessage());
		if (message.startsWith(CHECK_START))
		{
			Matcher m = CHECK_PATTERN.matcher(message);
			if (!m.matches())
			{
				return;
			}
			for (Shade s : Shade.values())
			{
				stored.put(s, Integer.valueOf(m.group(s.ordinal() + 1)));
			}
		}
		else if (message.startsWith(PICK_UP_START))
		{
			Matcher m = PICK_UP_PATTERN.matcher(message);
			if (!m.matches())
			{
				return;
			}
			Shade shade = Shade.fromName(m.group(1));
			if (shade == null)
			{
				return;
			}
			stored.put(shade, stored.get(shade) + 1);
		}
	}
}

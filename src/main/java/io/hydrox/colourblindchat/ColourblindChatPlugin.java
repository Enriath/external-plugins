/*
 * Copyright (c) 2020, Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.colourblindchat;

import com.google.inject.Provides;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
	name = "Colourblind Chat",
	description = "Take a colour channel and push it somewhere else!",
	tags = {"colourblind", "colorblind", "color", "colour", "chat", "message", "messages"}
)
public class ColourblindChatPlugin extends Plugin
{
	private static final Pattern TAG_MATCHER = Pattern.compile("(?<=<col=)([0-9a-f]{2}){3}(?=>)");

	@Inject
	private ColourblindChatConfig config;

	@Provides
	ColourblindChatConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ColourblindChatConfig.class);
	}

	@Subscribe(priority = -0.99999f)
	public void onChatMessage(ChatMessage event)
	{
		String message = event.getMessage();
		StringBuffer sb = new StringBuffer();

		Matcher m = TAG_MATCHER.matcher(message);

		while (m.find())
		{
			m.appendReplacement(sb, moveColour(m.group()));
		}
		m.appendTail(sb);

		event.getMessageNode().setValue(sb.toString());
	}

	private String moveColour(String colour)
	{
		final StringBuilder sb = new StringBuilder(colour);
		final ColourChannel channelFrom = config.fromChannel();
		final ColourChannel channelTo = config.toChannel();
		int fromColour = Integer.parseInt(sb.substring(channelFrom.startIndex, channelFrom.endIndex), 16);
		int toColour = Integer.parseInt(sb.substring(channelTo.startIndex, channelTo.endIndex), 16);
		final int toMove = Math.min(fromColour, 255 - toColour);
		toColour += toMove;
		fromColour -= toMove;

		sb.replace(channelFrom.startIndex, channelFrom.endIndex, Integer.toHexString(fromColour));
		sb.replace(channelTo.startIndex, channelTo.endIndex, Integer.toHexString(toColour));
		return sb.toString();
	}
}
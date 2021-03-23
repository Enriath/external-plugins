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
package io.hydrox.starlore;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@PluginDescriptor(
	name = "Star Lore",
	description = "Bring back the fluff when there isn't a star to find that was in the original RSHD release",
	tags = {"shooting", "stars", "shooting stars", "rshd", "rs2", "lore", "fluff", "flavor", "flavour"}
)
public class StarLorePlugin extends Plugin
{
	private static final int CHATBOX_TEXT_WIDGET_GROUP = 229;
	private static final int CHATBOX_TEXT_WIDGET_CHILD = 1;
	private static final String NOTHING_INTERESTING_TEXT = "You look through the telescope but you don't see anything<br>interesting.";

	private static final List<String> MORE_INTERESTING_TEXTS = Arrays.asList(
		"Hmm... are the stars really small, or are they just very far away?",
		"One of these stars has... little stars moving around it.<br>Interesting...",
		"Oh no! A giant space spider is eating the moon!<br>Wait, it's just a spider crawling across the lens.",
		"It's overcast; I can't see anything.",
		"My goodness... it's full of stars!"
	);

	@Inject
	private Client client;

	private boolean checkText;
	private Random random = new Random();

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != CHATBOX_TEXT_WIDGET_GROUP)
		{
			return;
		}
		checkText = true;
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (!checkText)
		{
			return;
		}
		Widget w = client.getWidget(CHATBOX_TEXT_WIDGET_GROUP, CHATBOX_TEXT_WIDGET_CHILD);
		if (w == null)
		{
			checkText = false;
			return;
		}
		if (Strings.isNullOrEmpty(w.getText()))
		{
			return;
		}
		checkText = false;
		if (NOTHING_INTERESTING_TEXT.equals(w.getText()))
		{
			int idx = random.nextInt(MORE_INTERESTING_TEXTS.size());
			w.setText(MORE_INTERESTING_TEXTS.get(idx));
		}
	}
}

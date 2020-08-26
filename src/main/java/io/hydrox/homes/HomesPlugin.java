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
package io.hydrox.homes;

import net.runelite.api.Client;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PluginDescriptor(
	name = "Home Enforcer",
	description = "Stop your Mahogany Homes from shifting into other realities"
)
public class HomesPlugin extends Plugin
{
	private static final int MIN = 10554;
	private static final int MAX = 10561;

	// Behold! My jank!
	private final Map<Integer, Integer> varbs = IntStream.range(MIN, MAX)
		.boxed()
		.collect(
			Collectors.toMap(
				Function.identity(),
				e -> 0,
				(o1, o2) -> o1,
				LinkedHashMap::new)
		);

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Override
	public void startUp()
	{
		String state = configManager.getConfiguration("homeenforcer", "state");
		if (state == null)
		{
			return;
		}

		char[] chars = state.toCharArray();

		for (int i = 0; i < MAX - MIN; i++)
		{
			varbs.put(MIN + i, Integer.parseInt(String.valueOf(chars[i])));
		}
	}

	@Override
	public void shutDown()
	{
		saveState();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// The varp that contains all the varbits.
		if (event.getIndex() != 2747)
		{
			return;
		}

		boolean save = false;

		for (int i : varbs.keySet())
		{
			int current = client.getVarbitValue(i);
			if (current == 0)
			{
				client.setVarbitValue(client.getVarps(), i, varbs.get(i));
			}
			else
			{
				client.setVarbitValue(client.getVarps(), i, current);
				varbs.put(i, current);
				save = true;
			}
		}

		if (save)
		{
			saveState();
		}
	}

	private void saveState()
	{
		configManager.setConfiguration("homeenforcer", "state",
			IntStream.range(MIN, MAX)
				.map(varbs::get)
				.mapToObj(String::valueOf)
				.collect(Collectors.joining())
		);
	}
}
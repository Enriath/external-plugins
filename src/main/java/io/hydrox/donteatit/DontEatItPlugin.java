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
package io.hydrox.donteatit;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;

@PluginDescriptor(
	name = "Don't eat it!",
	description = "Make every item that has a Use option have it as left click",
	tags = {"swap","swapper","menu","entry","menu entry swapper","use","drink","accident"}
)
public class DontEatItPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private DontEatItOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Provides
	DontEatItConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DontEatItConfig.class);
	}

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

	/*
		Code modified from MenuEntrySwapperPlugin, btw
	 */
	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen() || client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();
		int useIndex = -1;
		int topIndex = menuEntries.length - 1;

		for (int i = 0; i < topIndex; i++)
		{
			if (Text.removeTags(menuEntries[i].getOption()).equals("Use"))
			{
				useIndex = i;
				break;
			}
		}

		if (useIndex == -1)
		{
			return;
		}

		MenuEntry entry1 = menuEntries[useIndex];
		MenuEntry entry2 = menuEntries[topIndex];

		menuEntries[useIndex] = entry2;
		menuEntries[topIndex] = entry1;

		client.setMenuEntries(menuEntries);
	}
}

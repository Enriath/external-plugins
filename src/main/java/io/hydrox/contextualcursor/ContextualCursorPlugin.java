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
package io.hydrox.contextualcursor;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import javax.swing.JOptionPane;

@PluginDescriptor(
	name = "Contextual Cursor"
)

@Slf4j
public class ContextualCursorPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "contextualcursor";

	@Inject
	private ContextualCursorOverlay contextualCursorOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ContextualCursorConfig config;

	@Provides
	ContextualCursorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ContextualCursorConfig.class);
	}


	protected void startUp()
	{
		warnAboutCustomCursor(false);
		overlayManager.add(contextualCursorOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(contextualCursorOverlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("runelite") && event.getKey().equals("customcursorplugin"))
		{
			warnAboutCustomCursor(true);
		}
	}

	private void warnAboutCustomCursor(boolean swap)
	{
		Boolean customCursor = configManager.getConfiguration("runelite", "customcursorplugin", Boolean.class);
		if (customCursor != null && customCursor)
		{
			String sb = "Contextual Cursors do not work with Custom Cursors, due to them both modifying the cursor.\nPlease disable " +
				(swap ? "Contextual" : "Custom") +
				" Cursor before turning on " +
				(swap ? "Custom" : "Contextual") +
				" Cursor.";
			JOptionPane.showOptionDialog(null, sb,
				"Alert!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, new String[]{"Ok"}, "Ok");
		}
	}
}

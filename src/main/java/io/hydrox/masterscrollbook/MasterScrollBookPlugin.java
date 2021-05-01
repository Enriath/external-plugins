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
package io.hydrox.masterscrollbook;

import com.google.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
	name = "Master Scroll Book",
	description = "See the contents of your Master Scroll Book without opening it.",
	tags = {"scroll", "treasure", "book", "teleport", "overlay", "indicator"}
)
public class MasterScrollBookPlugin extends Plugin
{
	/**
	 * Index of selected default teleport scroll.
	 * 1-indexed
	 * Left->Right, Top->Bottom, Left Page->Right Page
	 */
	private static final int VARBIT_SELECTED_DEFAULT_SCROLL = 5685;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MasterScrollBookOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private Map<Scroll, Integer> counts = new HashMap<>();

	@Getter
	private Scroll selectedDefault = null;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::update);
		}
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged e)
	{
		update();
	}

	private void update()
	{
		for (Scroll s : Scroll.values())
		{
			counts.put(s, client.getVarbitValue(s.getVarbit()));
		}
		int sel = client.getVarbitValue(VARBIT_SELECTED_DEFAULT_SCROLL);
		if (sel == 0)
		{
			selectedDefault = null;
		}
		else
		{
			selectedDefault = Scroll.get(sel - 1);
		}
	}
}

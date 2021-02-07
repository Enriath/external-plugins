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
package io.hydrox.transmog;

import com.google.inject.Inject;
import io.hydrox.transmog.ui.CustomSprites;
import io.hydrox.transmog.ui.UIManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuEntry;
import net.runelite.api.Varbits;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuShouldLeftClick;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseWheelListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Provider;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

@PluginDescriptor(
	name = "Transmogrification",
	description = "Wear the armour you want, no matter what you're doing.",
	tags = {"transmog", "transmogrification", "fashion", "armour", "armor", "equipment"}

)
@Slf4j
public class TransmogrificationPlugin extends Plugin implements MouseWheelListener
{
	private static final String FORCE_RIGHT_CLICK_MENU_FLAG = "<col=ff981f><col=004356>";
	private static final int SCRIPT_ID_EQUIPMENT_TAB_CREATED = 914;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private Provider<TransmogrificationConfigManager> config;

	@Inject
	private Provider<TransmogrificationManager> transmogManager;

	@Inject
	private Provider<UIManager> uiManager;

	public TransmogrificationConfigManager getConfig()
	{
		return config.get();
	}

	public TransmogrificationManager getManager()
	{
		return transmogManager.get();
	}

	public UIManager getUIManager()
	{
		return uiManager.get();
	}



	private int lastWorld = 0;
	private boolean forceRightClickFlag;

	@Getter
	private boolean inPvpSituation;

	@Getter
	private boolean emptyEquipment;

	@Override
	public void startUp()
	{
		spriteManager.addSpriteOverrides(CustomSprites.values());
		mouseManager.registerMouseWheelListener(this);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			lastWorld = client.getWorld();
			getManager().saveCurrent();
			getManager().loadData();
			getManager().updateTransmog();
			clientThread.invoke(() ->
				{
					getUIManager().createTab(getUIManager().getEquipmentOverlay());
					updatePvpState();
					updateEquipmentState();
				});
		}
	}

	@Override
	public void shutDown()
	{
		spriteManager.removeSpriteOverrides(CustomSprites.values());
		mouseManager.unregisterMouseWheelListener(this);
		getManager().shutDown();
		getUIManager().shutDown();
		lastWorld = 0;
	}

	/**
	 * This plugin is modelled after RS3's Transmog system, which is disabled in PvP
	 * While that's because theirs is transmitted to other clients, I'd rather my
	 * code not mess with something where gear switches matter massively, just in case.
	 */
	private void updatePvpState()
	{
		final boolean newState = client.getVar(Varbits.PVP_SPEC_ORB) == 1;

		if (newState != inPvpSituation)
		{
			inPvpSituation = newState;
			getManager().onPvpChanged(newState);
			getUIManager().onPvpChanged(newState);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() == GameState.LOGGED_IN)
		{
			if (client.getWorld() != lastWorld)
			{
				lastWorld = client.getWorld();
				getManager().loadData();
				updateEquipmentState();
			}
		}
		else if (e.getGameState() == GameState.LOGIN_SCREEN || e.getGameState() == GameState.HOPPING)
		{
			lastWorld = 0;
			getUIManager().setUiCreated(false);
			getManager().clearUserStates();
		}
	}

	/**
	 * Changing equipment removes the current transmog. Reapply if active,
	 * or keep track of the real state if not active
	 */
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged e)
	{
		if (e.getContainerId() != InventoryID.EQUIPMENT.getId())
		{
			return;
		}

		updateEquipmentState();

		if (!getConfig().transmogActive())
		{
			getManager().saveCurrent();
			return;
		}

		getManager().reapplyTransmog();
	}

	private void updateEquipmentState()
	{
		ItemContainer ic = client.getItemContainer(InventoryID.EQUIPMENT);

		emptyEquipment = ic == null ||
			Arrays.stream(ic.getItems()).distinct().noneMatch(i -> i != null && i.getId() != -1);

		getUIManager().updateTutorial(emptyEquipment);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged e)
	{
		updatePvpState();
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (!getConfig().transmogActive())
		{
			return;
		}
		// On most teleports, the player kits are reset. This will reapply the transmog if needed.
		final int currentHash = Arrays.hashCode(client.getLocalPlayer().getPlayerComposition().getEquipmentIds());
		if (currentHash != getManager().getTransmogHash())
		{
			getManager().reapplyTransmog();
		}
	}

	// UI Events

	@Subscribe
	public void onResizeableChanged(ResizeableChanged e)
	{
		getUIManager().onResizeableChanged();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired e)
	{
		if (e.getScriptId() == SCRIPT_ID_EQUIPMENT_TAB_CREATED && !getUIManager().isUiCreated())
		{
			getUIManager().createTab(getUIManager().getEquipmentOverlay());
			getUIManager().setUiCreated(true);
		}
	}

	@Subscribe
	public void onClientTick(ClientTick e)
	{
		getUIManager().onClientTick();
	}

	/**
	 * 3 of the UI buttons have a specific name. CustomWidget adds colouring to emulate vanilla, meaning
	 * you get the name in FORCE_RIGHT_CLICK_MENU_FLAG. This is used both to hide the widget name
	 * (since it's in the wrong place), and to give an easy way to check if we should force a right click.
	 */
	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded e)
	{
		if (e.getTarget().equals(FORCE_RIGHT_CLICK_MENU_FLAG))
		{
			forceRightClickFlag = true;
		}
	}

	@Subscribe
	public void onMenuShouldLeftClick(MenuShouldLeftClick e)
	{
		if (!forceRightClickFlag)
		{
			return;
		}

		forceRightClickFlag = false;
		MenuEntry[] menuEntries = client.getMenuEntries();
		for (MenuEntry entry : menuEntries)
		{
			if (entry.getTarget().equals(FORCE_RIGHT_CLICK_MENU_FLAG))
			{
				e.setForceRightClick(true);
				return;
			}
		}
	}

	@Override
	public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event)
	{
		getUIManager().mouseWheelMoved(event);
		return event;
	}
	
	public Gender getGender()
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		return client.getLocalPlayer().getPlayerComposition().isFemale() ? Gender.FEMALE : Gender.MALE;
	}
}

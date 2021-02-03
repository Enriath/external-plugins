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
package io.hydrox.transmog.ui;

import io.hydrox.transmog.TransmogPreset;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.MouseWheelEvent;

@Singleton
@Slf4j
public class UIManager
{

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Getter
	private CustomTab mainTab = new MainTab();

	@Getter
	private CustomTab equipmentOverlay = new EquipmentOverlay();

	@Getter
	private CustomTab presetTab = new PresetTab();



	/*
	@Getter
	private CustomWidgetActionButton savePresetButton;
	@Getter
	private CustomWidgetActionButton deletePresetButton;
	*/

	@Getter
	@Setter
	private boolean uiCreated = false;

	@Getter
	@Setter
	private boolean isSearching = false;

	CustomTab currentTab;

	public void shutDown()
	{
		clientThread.invoke(this::removeCustomUI);
		uiCreated = false;
		closeSearch();
		mainTab.shutDown();
	}

	public void createTab(CustomTab tab)
	{
		currentTab = tab;
		currentTab.create();
	}

	public void onPvpChanged(boolean newValue)
	{
		currentTab.onPvpChanged(newValue);
	}

	public void onResizeableChanged()
	{
		uiCreated = false;
		removeCustomUI();
		createTab(equipmentOverlay);
	}

	Widget getContainer()
	{
		final Widget equipment = client.getWidget(WidgetInfo.EQUIPMENT);
		return equipment.getParent();
	}

	void closeSearch()
	{
		if (isSearching)
		{
			chatboxPanelManager.close();
			isSearching = false;
		}

	}

	void hideVanillaUI()
	{
		for (Widget child : getContainer().getNestedChildren())
		{
			child.setHidden(true);
			child.revalidate();
		}
	}

	public void removeCustomUI()
	{
		final Widget parent = getContainer();
		parent.deleteAllChildren();
		for (Widget child : parent.getNestedChildren())
		{
			child.setHidden(false);
			child.revalidate();
		}
		currentTab.destroy();
		parent.revalidate();
		closeSearch();
	}

	public void loadPreset(TransmogPreset preset)
	{
		currentTab.loadPreset(preset);
	}

	public void onClientTick()
	{
		currentTab.onClientTick();
	}

	public void updateTutorial(boolean equipmentState)
	{
		currentTab.updateTutorial(equipmentState);
	}

	public void mouseWheelMoved(MouseWheelEvent event)
	{
		currentTab.mouseWheelMoved(event);
	}
}

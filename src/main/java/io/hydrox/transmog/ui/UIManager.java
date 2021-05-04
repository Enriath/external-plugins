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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.hydrox.transmog.TransmogPreset;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.FontManager;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.MouseWheelEvent;

@Singleton
@Slf4j
public class UIManager
{
	static final String FORCE_RIGHT_CLICK_WIDGET_NAME = "<col=004356>";
	static final FontMetrics FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(FontManager.getRunescapeSmallFont());

	private final ChatboxPanelManager chatboxPanelManager;
	private final Client client;
	private final ClientThread clientThread;

	private final Provider<MainTab> mainTab;

	private final Provider<EquipmentOverlay> equipmentOverlay;

	private final Provider<PresetTab> presetTab;

	public MainTab getMainTab()
	{
		return mainTab.get();
	}

	public EquipmentOverlay getEquipmentOverlay()
	{
		return equipmentOverlay.get();
	}

	public PresetTab getPresetTab()
	{
		return presetTab.get();
	}

	@Getter
	@Setter
	private boolean uiCreated = false;

	@Getter
	@Setter
	private boolean isSearching = false;

	CustomTab currentTab;

	@Inject
	UIManager(ChatboxPanelManager chatboxPanelManager, Client client, ClientThread clientThread,
			  Provider<MainTab> mainTab, Provider<EquipmentOverlay> equipmentOverlay, Provider<PresetTab> presetTab)
	{
		this.chatboxPanelManager = chatboxPanelManager;
		this.client = client;
		this.clientThread = clientThread;
		this.mainTab = mainTab;
		this.equipmentOverlay = equipmentOverlay;
		this.presetTab = presetTab;
	}

	public void shutDown()
	{
		clientThread.invoke(this::removeCustomUI);
		uiCreated = false;
		closeSearch();
		currentTab.shutDown();
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
		createTab(equipmentOverlay.get());
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
		if (currentTab != null)
		{
			currentTab.loadPreset(preset);
		}
	}

	public void onClientTick()
	{
		if (currentTab != null)
		{
			currentTab.onClientTick();
		}
	}

	public void updateTutorial(boolean equipmentState)
	{
		if (currentTab != null)
		{
			currentTab.updateTutorial(equipmentState);
		}
	}

	public void mouseWheelMoved(MouseWheelEvent event)
	{
		if (currentTab != null)
		{
			currentTab.mouseWheelMoved(event);
		}
	}

	public static String cutStringToPxWidth(String str, int targetWidth)
	{
		return cutStringToPxWidth(str, targetWidth, false);
	}

	public static String cutStringToPxWidth(String str, int targetWidth, boolean withEllipsis)
	{
		int width = UIManager.FONT_METRICS.stringWidth(str);
		String newStr = str;
		while(width > targetWidth)
		{
			newStr = newStr.substring(0, newStr.length() - 1);
			width = UIManager.FONT_METRICS.stringWidth(newStr + (withEllipsis ? "..." : ""));
		}
		return newStr + (withEllipsis ? "..." : "");
	}
}

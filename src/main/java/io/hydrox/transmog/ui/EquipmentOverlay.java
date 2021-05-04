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
package io.hydrox.transmog.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.hydrox.transmog.config.TransmogrificationConfigManager;
import io.hydrox.transmog.TransmogrificationManager;
import io.hydrox.transmog.TransmogrificationPlugin;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;

@Singleton
public class EquipmentOverlay extends CustomTab
{
	private CustomWidgetToggleButton stateToggle;
	private Widget pvpBlocker;

	private final TransmogrificationConfigManager config;
	private final TransmogrificationManager manager;
	private final UIManager uiManager;

	@Inject
	EquipmentOverlay(TransmogrificationPlugin plugin, TransmogrificationConfigManager config, TransmogrificationManager manager)
	{
		this.config = config;
		this.manager = manager;
		this.uiManager = plugin.getUIManager();
	}

	@Override
	void create()
	{
		create(false);
	}

	void create(boolean uiActive)
	{
		final Widget parent = uiManager.getContainer();

		CustomWidgetToggleButton showUI = new CustomWidgetToggleButton(
			parent,
			"Transmogrification",
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL,
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED,
			1654, // Unmapped
			selected ->
			{
				if (selected)
				{
					uiManager.hideVanillaUI();
					uiManager.createTab(uiManager.getMainTab());
				}
				else
				{
					uiManager.removeCustomUI();
					create(false);
					manager.savePresets();
				}
			});
		showUI.setVerbs("Edit", "Exit");
		showUI.setSize(25, 25);
		showUI.setIconSize(32, 32);
		showUI.create();
		if (uiActive)
		{
			showUI.toggle();
		}

		showUI.layout(3, 3);

		stateToggle = new CustomWidgetToggleButton(
			parent,
			"Transmogrification",
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL,
			SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED,
			CustomSprites.TRANSMOG_LOGO.getSpriteId(),
			state ->
			{
				if ((!manager.isDefaultStateSet() && state) || manager.isInPvpSituation())
				{
					stateToggle.toggle();
					manager.hintDefaultState();
					return;
				}
				config.transmogActive(state);
				manager.updateTransmog();
			}
		);
		stateToggle.setVerbs("Enable", "Disable");
		stateToggle.setSize(25, 25);
		stateToggle.setIconSize(16, 13);
		stateToggle.create();
		if (config.transmogActive() && manager.isDefaultStateSet())
		{
			stateToggle.toggle();
		}
		stateToggle.layout(30, 3);

		pvpBlocker = parent.createChild(-1, WidgetType.GRAPHIC);
		pvpBlocker.setSpriteId(SpriteID.PLAYER_KILLING_DISABLED_OVERLAY); // Block icon
		pvpBlocker.setOriginalWidth(25);
		pvpBlocker.setOriginalHeight(25);
		pvpBlocker.setHasListener(true);
		pvpBlocker.setNoClickThrough(true);
		pvpBlocker.setOnOpListener((JavaScriptCallback) e -> {});
		pvpBlocker.setOriginalX(30);
		pvpBlocker.setOriginalY(3);
		pvpBlocker.setHidden(!manager.isInPvpSituation());
		pvpBlocker.setAction(0, "Transmog is disabled in PvP situations");
	}

	@Override
	void onPvpChanged(boolean newValue)
	{
		if (pvpBlocker != null)
		{
			pvpBlocker.setHidden(!newValue);
		}
	}
}

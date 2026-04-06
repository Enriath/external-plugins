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
package io.hydrox.quickprayerpreview;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PluginDescriptor(
	name = "Quick Prayer Preview",
	description = "Preview your quick prayers by hovering over the orb",
	tags = {"prayer", "quick prayer", "preview"}
)
public class QuickPrayerPreviewPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private QuickPrayerPreviewOverlay tooltipOverlay;

	@Inject
	private QuickPrayerPreviewPrayerTabOverlay tabOverlay;

	@Inject
	private QuickPrayerPreviewConfig config;

	@Getter
	private List<Prayer> quickPrayers;
	private int quickPrayerVarb = -1;

	private final Map<Prayer, BufferedImage> prayerSprites = new HashMap<>();

	@Provides
	QuickPrayerPreviewConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QuickPrayerPreviewConfig.class);
	}

	@Override
	public void startUp()
	{
		if (config.isOrbPreview())
		{
			overlayManager.add(tooltipOverlay);
		}
		if (config.isPrayerTabPreview())
		{
			overlayManager.add(tabOverlay);
		}
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(tooltipOverlay);
		overlayManager.remove(tabOverlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged e)
	{
		int varb = client.getVarbitValue(VarbitID.QUICKPRAYER_SELECTED);
		if (varb == quickPrayerVarb)
		{
			return;
		}

		quickPrayerVarb = varb;
		quickPrayers = Prayer.fromVarb(varb, client);
		loadSprites();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!"QuickPrayerPreview".equals(configChanged.getGroup()))
		{
			return;
		}

		if ("orbPreview".equals(configChanged.getKey()))
		{
			if (config.isOrbPreview() && !overlayManager.anyMatch(o -> o.equals(tooltipOverlay)))
			{
				overlayManager.add(tooltipOverlay);
			}
			if (!config.isOrbPreview())
			{
				overlayManager.remove(tooltipOverlay);
			}
		}

		if ("prayerTabPreview".equals(configChanged.getKey()))
		{
			if (config.isPrayerTabPreview() && !overlayManager.anyMatch(o -> o.equals(tabOverlay)))
			{
				overlayManager.add(tabOverlay);
			}
			if (!config.isPrayerTabPreview())
			{
				overlayManager.remove(tabOverlay);
			}
		}
	}

	BufferedImage getSprite(int id)
	{
		return spriteManager.getSprite(id, 0);
	}

	private void loadSprites()
	{
		for (Prayer p : quickPrayers)
		{
			BufferedImage img = spriteManager.getSprite(p.getSpriteID(), 0);
			if (img != null)
			{
				BufferedImage norm = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
				Graphics g = norm.getGraphics();
				g.drawImage(img, norm.getWidth() / 2 - img.getWidth() / 2, norm.getHeight() / 2 - img.getHeight() / 2, null);
				prayerSprites.put(p, norm);
			}
		}
	}

	BufferedImage getSprite(Prayer p)
	{
		return prayerSprites.get(p);
	}
}
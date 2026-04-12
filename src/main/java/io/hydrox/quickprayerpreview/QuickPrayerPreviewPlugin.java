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
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PluginDescriptor(
	name = "Quick Prayer Preview",
	description = "Preview your quick prayers by hovering over the orb",
	tags = {"prayer","quick prayer","preview"}
)
public class QuickPrayerPreviewPlugin extends Plugin
{
	static final String CONFIG_GROUP = "quickprayerpreview";

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private QuickPrayerPreviewOverlay overlay;

	@Getter
	private List<Prayer> quickPrayers;

	private final Map<Prayer, BufferedImage> prayerSprites = new HashMap<>();

	@Provides
	private QuickPrayerPreviewConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QuickPrayerPreviewConfig.class);
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

	@Subscribe
	public void onVarbitChanged(VarbitChanged e)
	{
		if (e.getVarbitId() != VarbitID.QUICKPRAYER_SELECTED
			&& e.getVarbitId() != VarbitID.PRAYER_DEADEYE_UNLOCKED && e.getVarbitId() != VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED)
		{
			return;
		}

		int varb = client.getVarbitValue(VarbitID.QUICKPRAYER_SELECTED);
		quickPrayers = prayersFromVarb(varb);
		loadSprites();
	}

	private List<Prayer> prayersFromVarb(int varb)
	{
		final BitSet bits = BitSet.valueOf(new long[] {varb});
		final List<Prayer> prayers = new ArrayList<>();

		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1))
		{
			Prayer prayer = Prayer.get(i);
			if (prayer == Prayer.EAGLE_EYE && client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) == 1)
			{
				prayers.add(Prayer.DEADEYE);
			}
			else if (prayer == Prayer.MYSTIC_MIGHT && client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) == 1)
			{
				prayers.add(Prayer.MYSTIC_VIGOUR);
			}
			else
			{
				prayers.add(prayer);
			}
		}
		return prayers;
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
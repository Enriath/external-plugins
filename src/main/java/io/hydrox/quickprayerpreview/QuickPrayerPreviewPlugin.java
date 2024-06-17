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
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
	name = "Quick Prayer Preview",
	description = "Preview your quick prayers by hovering over the orb",
	tags = {"prayer", "quick prayer", "preview"}
)
public class QuickPrayerPreviewPlugin extends Plugin implements KeyListener
{

	private static final String CHAT_MESSAGE_PREFIX = "<col=ff0000>Quick-prayers</col>: ";
	private static final int QUICK_PRAYER_VARBIT = 4102;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private QuickPrayerPreviewOverlay overlay;

	@Inject
	private QuickPrayerPreviewConfig config;

	@Getter(AccessLevel.PACKAGE)
	private List<Prayer> quickPrayers;

	private final Map<Prayer, BufferedImage> prayerSprites = new HashMap<>();

	private int quickPrayerVarb = -1;

	@Getter(AccessLevel.PACKAGE)
	private boolean keyPressed;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
		keyManager.registerKeyListener(this);

		clientThread.invokeLater(() -> {
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return;
			}

			final int value = client.getVarbitValue(QUICK_PRAYER_VARBIT);

			if (quickPrayerVarb != value)
			{
				quickPrayerVarb = value;
				quickPrayers = Prayer.fromVarb(quickPrayerVarb);
				loadSprites();
			}
		});
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
		keyManager.unregisterKeyListener(this);

		quickPrayers = null;
		prayerSprites.clear();
		quickPrayerVarb = -1;
		keyPressed = false;
	}

	@Provides
	QuickPrayerPreviewConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(QuickPrayerPreviewConfig.class);
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged e)
	{
		if (!e.getGroup().equals(QuickPrayerPreviewConfig.CONFIG_GROUP))
		{
			return;
		}

		if (e.getKey().equals(QuickPrayerPreviewConfig.KEY_TOOLTIP_SPRITE_SIZE))
		{
			clientThread.invokeLater(() -> {
				prayerSprites.clear();
				loadSprites();
			});
		}
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged e)
	{
		if (e.getVarbitId() == QUICK_PRAYER_VARBIT && e.getValue() != quickPrayerVarb)
		{
			quickPrayerVarb = e.getValue();
			quickPrayers = Prayer.fromVarb(quickPrayerVarb);
			loadSprites();
		}
	}

	@Subscribe
	public void onMenuOpened(final MenuOpened event)
	{
		if (!config.addPrintMenuEntry())
		{
			return;
		}

		final MenuEntry me = event.getFirstEntry();

		if (me.getWidget() == null ||
			me.getWidget().getId() != ComponentID.MINIMAP_QUICK_PRAYER_ORB ||
			!"Activate".equals(me.getOption()))
		{
			return;
		}

		client.createMenuEntry(1)
			.setOption("Print")
			.setTarget(me.getTarget())
			.setType(MenuAction.RUNELITE)
			.onClick(e -> {
				if (quickPrayers.isEmpty())
				{
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", CHAT_MESSAGE_PREFIX + "Unknown or unset", null);
					return;
				}

				final StringBuilder sb = new StringBuilder(CHAT_MESSAGE_PREFIX);

				for (int i = 0; i < quickPrayers.size(); i++)
				{
					sb.append(quickPrayers.get(i).getName());

					if (i != quickPrayers.size() - 1)
					{
						sb.append(", ");
					}
				}

				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", sb.toString(), null);
			});
	}


	private void loadSprites()
	{
		if (quickPrayers == null)
		{
			return;
		}

		final int size = config.tooltipSpriteSize();

		for (final Prayer prayer : quickPrayers)
		{
			BufferedImage img = spriteManager.getSprite(prayer.getSpriteID(), 0);

			if (img == null)
			{
				continue;
			}

			img = ImageUtil.resizeImage(img, size, size, true);

			final BufferedImage norm = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			final Graphics g = norm.getGraphics();
			g.drawImage(img, norm.getWidth() / 2 - img.getWidth() / 2, norm.getHeight() / 2 - img.getHeight() / 2, null);

			prayerSprites.put(prayer, norm);
		}
	}

	BufferedImage getSprite(final Prayer p)
	{
		return prayerSprites.get(p);
	}

	@Override
	public void keyTyped(final KeyEvent e)
	{
	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		if (config.keyToPress().matches(e))
		{
			keyPressed = true;
		}
	}

	@Override
	public void keyReleased(final KeyEvent e)
	{
		if (config.keyToPress().matches(e))
		{
			keyPressed = false;
		}
	}
}
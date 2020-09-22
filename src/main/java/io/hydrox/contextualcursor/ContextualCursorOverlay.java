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

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ContextualCursorOverlay extends Overlay
{
	private static final BufferedImage BLANK_MOUSE = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	private static final Pattern SPELL_FINDER = Pattern.compile(">(.*?)(?:</col>| -> )");
	//The pointer sticks out to the left slightly, so this makes sure it's point to the correct spot
	private static final Point POINTER_OFFSET = new Point(-5, 0);
	//The centre of the circle (biased bottom right since it's an even size), for use with sprites
	private static final Point CENTRAL_POINT = new Point(16, 18);
	private static final int MENU_OPTION_HEIGHT = 15;
	private static final int MENU_EXTRA_TOP = 4;
	private static final int MENU_EXTRA_BOTTOM = 3;
	private static final int MENU_BORDERS_TOTAL = MENU_EXTRA_TOP + MENU_OPTION_HEIGHT + MENU_EXTRA_BOTTOM;

	private final Client client;
	private final ClientUI clientUI;
	private final SpriteManager spriteManager;

	private Point menuOpenPoint;

	@Inject
	ContextualCursorOverlay(Client client, ClientUI clientUI, SpriteManager spriteManager)
	{
		setPosition(OverlayPosition.TOOLTIP);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		this.clientUI = clientUI;
		this.spriteManager = spriteManager;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// TODO: Stop tooltips from overlapping the cursor

		final MenuEntry menuEntry;

		if (client.isMenuOpen())
		{
			menuEntry = processMenu();
		}
		else
		{
			menuOpenPoint = client.getMouseCanvasPosition();
			final MenuEntry[] menuEntries = client.getMenuEntries();
			int last = menuEntries.length - 1;

			if (last < 0)
			{
				return null;
			}

			menuEntry = menuEntries[last];
		}

		if (menuEntry == null)
		{
			clientUI.resetCursor();
			return null;
		}

		if (menuEntry.getType() == MenuAction.WALK.getId()
			|| menuEntry.getType() == MenuAction.CC_OP.getId()
			|| menuEntry.getType() == MenuAction.CANCEL.getId()
			|| menuEntry.getType() == MenuAction.WIDGET_TYPE_2.getId()
			|| menuEntry.getType() == MenuAction.WIDGET_TYPE_6.getId())
		{
			clientUI.resetCursor();
			return null;
		}

		processEntry(graphics,  menuEntry.getOption(),  menuEntry.getTarget());
		return null;
	}

	private MenuEntry processMenu()
	{
		final MenuEntry[] menuEntries = client.getMenuEntries();

		final int menuTop;
		final int menuHeight = (menuEntries.length * MENU_OPTION_HEIGHT) + MENU_BORDERS_TOTAL;
		if (menuHeight + menuOpenPoint.getY() > client.getCanvasHeight())
		{
			menuTop = client.getCanvasHeight() - menuHeight;
		}
		else
		{
			menuTop = menuOpenPoint.getY();
		}

		final int fromTop = (client.getMouseCanvasPosition().getY() - MENU_EXTRA_TOP) - menuTop;

		final int index = menuEntries.length - (fromTop / MENU_OPTION_HEIGHT);

		if (index >= menuEntries.length || index < 0)
		{
			return null;
		}

		return menuEntries[index];
	}

	private void processEntry(Graphics2D graphics, String option, String target)
	{
		if (option.toLowerCase().equals("cast") && target.contains("->"))
		{
			final Matcher spellFinder = SPELL_FINDER.matcher(target);

			if (!spellFinder.find())
			{
				return;
			}

			final String spellText = spellFinder.group(1);
			final SpellSprite spell = SpellSprite.get(spellText);

			final BufferedImage magicSprite = spriteManager.getSprite(spell.spriteID, 0);
			if (magicSprite == null)
			{
				return;
			}

			drawCursorWithSprite(graphics, magicSprite);
			return;
		}

		final ContextualCursor cursor;
		// Custom handling for RL Wiki lookup's spell-like nature
		if (option.equals("Lookup") && target.startsWith("Wiki<"))
		{
			cursor = ContextualCursor.WIKI;
		}
		else
		{
			cursor = ContextualCursor.get(option);
		}

		if (cursor == null)
		{
			clientUI.resetCursor();
			return;
		}

		BufferedImage sprite = cursor.getCursor();
		if (cursor.getSpriteID() != null)
		{
			if (client.getSpriteOverrides().containsKey(cursor.getSpriteID()))
			{
				sprite = client.getSpriteOverrides().get(cursor.getSpriteID()).toBufferedImage();
			}
			else
			{
				sprite = spriteManager.getSprite(cursor.getSpriteID(), 0);
			}
			if (sprite == null)
			{
				return;
			}
		}
		if (sprite != null)
		{
			drawCursorWithSprite(graphics, sprite);
		}
	}

	private void drawCursorWithSprite(Graphics2D graphics, BufferedImage sprite)
	{
		clientUI.setCursor(BLANK_MOUSE, "blank");
		final Point mousePos = client.getMouseCanvasPosition();
		final ContextualCursor blank = ContextualCursor.BLANK;
		graphics.drawImage(blank.getCursor(), mousePos.getX() + POINTER_OFFSET.getX(), mousePos.getY() + POINTER_OFFSET.getY(), null);
		final int spriteX = POINTER_OFFSET.getX() + CENTRAL_POINT.getX() - sprite.getWidth() / 2;
		final int spriteY = POINTER_OFFSET.getY() + CENTRAL_POINT.getY() - sprite.getHeight() / 2;
		graphics.drawImage(sprite, mousePos.getX() + spriteX, mousePos.getY() + spriteY, null);
	}
}

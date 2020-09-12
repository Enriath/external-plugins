/*
 * Copyright (c) 2020 Hydrox6 <ikada@protonmail.ch>
 * Copyright (c) 2020 Graviton1647 <https://github.com/Graviton1647>
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

import com.sun.jna.platform.FileUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ContextualCursorOverlay extends Overlay
{
	private static final BufferedImage BLANK_MOUSE = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	private static final Pattern SPELL_FINDER = Pattern.compile(">(.*?)</col>");
	//The pointer sticks out to the left slightly, so this makes sure it's point to the correct spot
	private static final Point POINTER_OFFSET = new Point(-5, 0);
	//The centre of the circle (biased bottom right since it's an even size), for use with sprites
	private static final Point CENTRAL_POINT = new Point(16, 18);
	//Missing cursors file location
	public static final Path MISSING_DIR = new File(RuneLite.RUNELITE_DIR + File.separator,"missing_cursors.txt").toPath();

	private final Client client;
	private final ClientUI clientUI;
	private final SpriteManager spriteManager;

	@Inject
	private ConfigManager configManager;

	public static HashMap<String, ContextualCursor> cursorMap = new HashMap();
	public HashMap<ContextualCursor, BufferedImage> cursorMapImages = new HashMap();
	public ArrayList<String> missingCursors = new ArrayList<>();

	public Integer[] blocked = new Integer[] {
			MenuAction.WALK.getId(),
			MenuAction.CANCEL.getId()
	};

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

		if (client.isMenuOpen())
		{
			// TODO: Handle the minimenu
			drawCursor(cursorMapImages.get(ContextualCursor.POINTER));
			return null;
		}

		final MenuEntry[] menuEntries = client.getMenuEntries();
		int last = menuEntries.length - 1;

		if (last < 0)
		{
			return null;
		}

		final MenuEntry menuEntry = menuEntries[last];

		if(Arrays.asList(blocked).contains(menuEntry.getType()))
		{
			drawCursor(cursorMapImages.get(ContextualCursor.POINTER));
			return null;
		}

		processEntry(graphics,  menuEntry.getOption(),  menuEntry.getTarget());
		return null;
	}

	private void processEntry(Graphics2D graphics, String option, String target) {
		if (option.toLowerCase().equals("cast") && target.contains("->")) {
			final Matcher spellFinder = SPELL_FINDER.matcher(target);

			if (!spellFinder.find()) {
				return;
			}

			final String spellText = spellFinder.group(1);
			final SpellSprite spell = SpellSprite.get(spellText);

			final BufferedImage magicSprite = spriteManager.getSprite(spell.spriteID, 0);
			if (magicSprite == null) {
				return;
			}

			drawCursorWithSprite(graphics, magicSprite);
			return;
		}


		String contentData[] = option.split("\\s", 2);

		final ContextualCursor cursor = get(contentData[0]);

		if (cursor == null)
		{
			if(!missingCursors.contains(option)) {
				addData(option);
			}
			drawCursor(cursorMapImages.get(ContextualCursor.POINTER));
			return;
		}
		if (cursor.getSpriteID() != null)
		{
			final BufferedImage sprite;
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
			drawCursorWithSprite(graphics, sprite);
		}
		else
		{
			drawCursor(cursorMapImages.get(cursor));
		}
	}

	private void drawCursor(BufferedImage sprite)
	{
		clientUI.setCursor(sprite, "blank");
	}

	private void drawCursorWithSprite(Graphics2D graphics, BufferedImage sprite)
	{
		clientUI.setCursor(BLANK_MOUSE, "blank");
		final Point mousePos = client.getMouseCanvasPosition();
		final ContextualCursor blank = ContextualCursor.BLANK;

		BufferedImage image = ImageUtil.getResourceStreamFromClass(ContextualCursorPlugin.class, String.format(String.format("cursors/" + configManager.getConfiguration("contextualcursor", "cursorstyle", ContextualSkin.class).name() + "/%s.png", blank.getPath())));

		graphics.drawImage(image, mousePos.getX() + POINTER_OFFSET.getX(), mousePos.getY() + POINTER_OFFSET.getY(), null);
		final int spriteX = POINTER_OFFSET.getX() + CENTRAL_POINT.getX() - sprite.getWidth() / 2;
		final int spriteY = POINTER_OFFSET.getY() + CENTRAL_POINT.getY() - sprite.getHeight() / 2;
		graphics.drawImage(sprite, mousePos.getX() + spriteX, mousePos.getY() + spriteY, null);
	}

	public void setCursors()
	{
		for (ContextualCursor cursor : ContextualCursor.values())
		{
			if(cursor.getPath() != null)
			{
				BufferedImage image = ImageUtil.getResourceStreamFromClass(ContextualCursorPlugin.class, String.format(String.format("cursors/" + configManager.getConfiguration("contextualcursor", "cursorstyle", ContextualSkin.class).name() + "/%s.png", cursor.getPath())));
				cursorMapImages.put(cursor, image);
			}
		}
	}

	private void addData(String data)
	{

		if(!configManager.getConfiguration("contextualcursor", "recorddata", Boolean.class)) {
			return;
		}

		try
		{
			Path pathParent = MISSING_DIR.getParent();
			if (!Files.exists(pathParent)) {
				Files.createDirectories(pathParent);
			}
			missingCursors.add(data);
			Files.write(MISSING_DIR, Arrays.asList(data), StandardCharsets.UTF_8, Files.exists(MISSING_DIR) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		}
		catch (final IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	static ContextualCursor get(String action)
	{
		return cursorMap.get(action.toLowerCase());
	}

}

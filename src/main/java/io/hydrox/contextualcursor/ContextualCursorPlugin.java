/*
 * Copyright (c) 2020-2022 Enriath <ikada@protonmail.ch>
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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

@PluginDescriptor(
	name = "Contextual Cursor",
	description = "RSHD-style image cursors"
)
@Slf4j
public class ContextualCursorPlugin extends Plugin implements KeyListener, MouseListener
{
	@Inject
	private ContextualCursorDrawOverlay contextualCursorDrawOverlay;
	@Inject
	private ContextualCursorWorkerOverlay contextualCursorWorkerOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	@Getter
	private boolean altPressed;

	@Getter
	@Setter
	private BufferedImage spriteToDraw;

	protected void startUp()
	{
		overlayManager.add(contextualCursorWorkerOverlay);
		overlayManager.add(contextualCursorDrawOverlay);
		keyManager.registerKeyListener(this);
		mouseManager.registerMouseListener(this);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(contextualCursorWorkerOverlay);
		overlayManager.remove(contextualCursorDrawOverlay);
		contextualCursorWorkerOverlay.resetCursor();
		keyManager.unregisterKeyListener(this);
		mouseManager.unregisterMouseListener(this);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN && event.getGameState() != GameState.LOADING)
		{
			contextualCursorWorkerOverlay.resetCursor();
		}
	}

	@Override
	public void keyPressed(KeyEvent keyEvent)
	{
		altPressed = keyEvent.isAltDown();
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		altPressed = keyEvent.isAltDown();
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent)
	{
		altPressed = mouseEvent.isAltDown();
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent)
	{
		altPressed = mouseEvent.isAltDown();
		return mouseEvent;
	}

	// Beyond this point is junk.
	// Look upon this method-bloat and despair!

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public void keyTyped(KeyEvent keyEvent)
	{

	}
}

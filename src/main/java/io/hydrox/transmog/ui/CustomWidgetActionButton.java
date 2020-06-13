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

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public class CustomWidgetActionButton extends CustomWidgetWithIcon implements InteractibleWidget
{
	private String action;

	private Widget overlay;

	private Widget topLeftBrace;
	private Widget topRightBrace;
	private Widget bottomLeftBrace;
	private Widget bottomRightBrace;

	private Widget leftSide;
	private Widget topSide;
	private Widget rightSide;
	private Widget bottomSide;

	protected final WidgetIntCallback callback;

	public CustomWidgetActionButton(final Widget parent, final String name, int iconSprite, final WidgetIntCallback callback)
	{
		super(parent, name, iconSprite);
		this.callback = callback;
	}

	@Override
	public void create()
	{
		topLeftBrace = createSpriteWidget(9, 9);
		topLeftBrace.setSpriteId(913);
		topRightBrace = createSpriteWidget(9, 9);
		topRightBrace.setSpriteId(914);
		bottomLeftBrace = createSpriteWidget(9, 9);
		bottomLeftBrace.setSpriteId(915);
		bottomRightBrace = createSpriteWidget(9, 9);
		bottomRightBrace.setSpriteId(916);

		leftSide = createSpriteWidget(9, height - 9 * 2);
		leftSide.setSpriteId(917);
		topSide = createSpriteWidget(width - 9 * 2, 9);
		topSide.setSpriteId(918);
		rightSide = createSpriteWidget(9, height - 9 * 2);
		rightSide.setSpriteId(919);
		bottomSide = createSpriteWidget(width - 9 * 2, 9);
		bottomSide.setSpriteId(920);


		icon = createSpriteWidget(iconWidth, iconHeight);
		icon.setSpriteId(iconSpriteID);

		overlay = createSpriteWidget(width, height);
		overlay.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		overlay.setOnMouseRepeatListener((JavaScriptCallback) e -> onHover());
		overlay.setOnMouseLeaveListener((JavaScriptCallback) e -> onLeave());
		overlay.setHasListener(true);
	}

	public void addOption(int index, String action)
	{
		overlay.setAction(index, action);
	}

	public void setIconSprite(int spriteID)
	{
		icon.setSpriteId(spriteID);
	}

	private void onHover()
	{
		topLeftBrace.setSpriteId(921);
		topRightBrace.setSpriteId(922);
		bottomLeftBrace.setSpriteId(923);
		bottomRightBrace.setSpriteId(924);
		leftSide.setSpriteId(925);
		topSide.setSpriteId(926);
		rightSide.setSpriteId(927);
		bottomSide.setSpriteId(928);
	}

	private void onLeave()
	{
		topLeftBrace.setSpriteId(913);
		topRightBrace.setSpriteId(914);
		bottomLeftBrace.setSpriteId(915);
		bottomRightBrace.setSpriteId(916);
		leftSide.setSpriteId(917);
		topSide.setSpriteId(918);
		rightSide.setSpriteId(919);
		bottomSide.setSpriteId(920);
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(overlay, x, y);

		layoutWidget(topLeftBrace, x, y);
		layoutWidget(topRightBrace, x + width - 9, y);
		layoutWidget(bottomLeftBrace, x, y + height - 9);
		layoutWidget(bottomRightBrace, x + width - 9, y + height - 9);

		layoutWidget(leftSide, x, y + 9);
		layoutWidget(topSide, x + 9, y);
		layoutWidget(rightSide, x + width - 9, y + 9);
		layoutWidget(bottomSide, x + 9, y + height - 9);

		super.layout(x, y);
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		// For whatever reason, the Op for an option is always 1 higher than the given index. MAGIC!
		callback.run(scriptEvent.getOp() - 1);
	}
}

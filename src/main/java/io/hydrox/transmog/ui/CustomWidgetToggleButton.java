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

public class CustomWidgetToggleButton extends CustomWidgetWithIcon implements InteractibleWidget
{
	private int selectedBackgroundSprite;
	private int backgroundSpriteID;

	private boolean selected;

	private final WidgetBooleanCallback callback;

	public CustomWidgetToggleButton(final Widget parent, final String name, int backgroundSprite, int selectedBackgroundSprite, int iconSprite, final WidgetBooleanCallback callback)
	{
		super(parent, name, iconSprite);
		this.backgroundSpriteID = backgroundSprite;
		this.selectedBackgroundSprite = selectedBackgroundSprite;
		this.callback = callback;
	}

	public void toggle()
	{
		selected = !selected;
		base.setSpriteId(selected ? selectedBackgroundSprite : backgroundSpriteID);
		icon.setAction(1, selected ? "Close" : "Open");
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(base, x, y);

		super.layout(x, y);
	}

	@Override
	public void create()
	{
		base = createSpriteWidget(width, height);
		base.setSpriteId(backgroundSpriteID);

		icon = createSpriteWidget(iconWidth, iconHeight);
		icon.setSpriteId(iconSpriteID);
		icon.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		icon.setOnMouseRepeatListener((JavaScriptCallback) e -> e.getSource().setOpacity(120));
		icon.setOnMouseLeaveListener((JavaScriptCallback) e -> e.getSource().setOpacity(0));
		icon.setHasListener(true);
		icon.setAction(1, "Open");
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		toggle();
		callback.run(selected);
	}
}

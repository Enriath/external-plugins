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
import net.runelite.api.widgets.WidgetTextAlignment;

public class CustomWidgetCheckbox extends CustomWidget implements InteractibleWidget
{
	private int offSpriteID;
	private int onSpriteID;
	private Widget label;

	private boolean state;

	private final WidgetBooleanCallback callback;

	public CustomWidgetCheckbox(Widget parent, String name, boolean defaultState, int offSprite, int onSprite, WidgetBooleanCallback callback)
	{
		super(parent, name);

		this.offSpriteID = offSprite;
		this.onSpriteID = onSprite;
		this.callback = callback;
		this.state = defaultState;
	}

	@Override
	public void create()
	{
		base = createSpriteWidget(width, height);
		base.setSpriteId(state ? onSpriteID : offSpriteID);
		base.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		base.setOnMouseRepeatListener((JavaScriptCallback) e -> e.getSource().setOpacity(120));
		base.setOnMouseLeaveListener((JavaScriptCallback) e -> e.getSource().setOpacity(0));
		base.setHasListener(true);
		base.setAction(1, state ? "Disable" : "Enable");

		label = createTextWidget(state ? "Enabled" : "Disabled");
		label.setOriginalWidth(parent.getOriginalWidth());
		label.setOriginalHeight(height);
		label.setFontId(494);
		label.setYTextAlignment(WidgetTextAlignment.CENTER);
		label.setTextColor(fromRGB(240, 240, 240));
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(base, x, y);

		layoutWidget(label, x + width + 1, y);

		parent.revalidate();
	}

	public void toggle()
	{
		state = !state;
		base.setSpriteId(state ? onSpriteID : offSpriteID);
		base.setAction(1, state ? "Disable" : "Enable");
		label.setText(state ? "Enabled" : "Disabled");

	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		toggle();
		callback.run(state);
	}
}

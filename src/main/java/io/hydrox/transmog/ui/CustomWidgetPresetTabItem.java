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

import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public abstract class CustomWidgetPresetTabItem extends CustomWidget implements InteractibleWidget
{
	private static final int BACKGROUND_ID = 170;
	private static final int BACKGROUND_ID_SELECTED = 179;

	static final int SIZE = 32;

	private Widget overlayWidget;

	protected final WidgetIntCallback callback;

	public CustomWidgetPresetTabItem(Widget parent, String name, WidgetIntCallback callback)
	{
		super(parent, name);
		this.callback = callback;
	}

	public abstract int getId();

	@Override
	public void layout(int x, int y)
	{
		super.layout(x, y);
		layoutWidget(base, x, y);
		layoutWidget(overlayWidget, x, y);
		parent.revalidate();
	}

	public void scrollBy(int y)
	{
		base.setOriginalY(this.y - y);
		overlayWidget.setOriginalY(this.y - y);
		base.revalidate();
		overlayWidget.revalidate();
	}

	protected void createUnderlay()
	{
		base = createSpriteWidget(36, 36);
		base.setSpriteId(BACKGROUND_ID);
	}

	protected void createOverlay()
	{
		overlayWidget = createSpriteWidget(36, 36);
		overlayWidget.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		overlayWidget.setHasListener(true);
	}

	public void addOption(int index, String action)
	{
		overlayWidget.setAction(index, action);
	}

	public void setSelected(boolean state)
	{
		base.setSpriteId(state ? BACKGROUND_ID_SELECTED : BACKGROUND_ID);
	}
}

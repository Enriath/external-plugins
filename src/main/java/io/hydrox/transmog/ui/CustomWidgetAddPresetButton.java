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

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.Widget;

public class CustomWidgetAddPresetButton extends CustomWidgetPresetTabItem
{
	private Widget iconWidget;

	public CustomWidgetAddPresetButton(Widget parent, String name, WidgetIntCallback callback)
	{
		super(parent, name, callback);
	}

	@Override
	public int getId()
	{
		return -1;
	}

	@Override
	public void layout(int x, int y)
	{
		super.layout(x, y);
		layoutWidget(iconWidget, x + 9, y + 10);
		parent.revalidate();
	}

	@Override
	public void scrollBy(int y)
	{
		super.scrollBy(y);
		iconWidget.setOriginalY(this.y - y + 10);
		iconWidget.revalidate();
	}

	@Override
	public void create()
	{
		super.createUnderlay();
		iconWidget = createSpriteWidget(17, 17);
		iconWidget.setSpriteId(CustomSprites.ADD.getSpriteId());
		super.createOverlay();
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		callback.run(-1);
	}
}

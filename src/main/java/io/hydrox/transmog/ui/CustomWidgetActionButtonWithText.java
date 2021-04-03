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

import net.runelite.api.FontID;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;
import java.awt.Color;

public class CustomWidgetActionButtonWithText extends CustomWidgetActionButton
{
	private String initialText;

	private Widget text;

	public CustomWidgetActionButtonWithText(Widget parent, String name, int spriteId, String initialText, WidgetIntCallback callback)
	{
		super(parent, name, spriteId, callback);
		this.initialText = initialText;
	}

	@Override
	public void create()
	{
		super.create();

		text = createTextWidget(initialText);
		text.setOriginalWidth(8);
		text.setOriginalHeight(12);
		text.setFontId(FontID.BARBARIAN);
		text.setTextColor(fromRGB(Color.CYAN));
		text.setXTextAlignment(WidgetTextAlignment.CENTER);
		text.setYTextAlignment(WidgetTextAlignment.BOTTOM);
	}

	public void setText(String newText)
	{
		text.setText(newText);
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(text, x + width - 14, y + 9);
		super.layout(x, y);
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		// For whatever reason, the Op for an option is always 1 higher than the given index. MAGIC!
		callback.run(scriptEvent.getOp() - 1);
	}
}

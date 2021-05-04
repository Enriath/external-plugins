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

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;

public class CustomWidgetNamePlate extends CustomWidget
{
	private static final int PLATE_CAP_LEFT = 1123;
	private static final int PLATE_CAP_RIGHT = 1125;
	private static final int PLATE_BACKGROUND_TILE = 1124;
	private static final int CAP_WIDTH = 4;
	private static final int HEIGHT = 14;

	private Widget capLeft;
	private Widget capRight;
	private Widget background;
	private Widget nameWidget;

	public CustomWidgetNamePlate(Widget parent, int width)
	{
		super(parent, "");
		this.width = width;
		this.height = HEIGHT;
	}

	@Override
	public void create()
	{
		capLeft = createSpriteWidget(CAP_WIDTH, HEIGHT);
		capLeft.setSpriteId(PLATE_CAP_LEFT);
		capRight = createSpriteWidget(CAP_WIDTH, HEIGHT);
		capRight.setSpriteId(PLATE_CAP_RIGHT);
		background = createSpriteWidget(width - CAP_WIDTH * 2, HEIGHT);
		background.setSpriteId(PLATE_BACKGROUND_TILE);
		background.setSpriteTiling(true);
		nameWidget = createTextWidget("");
		nameWidget.setOriginalWidth(width);
		nameWidget.setOriginalHeight(HEIGHT);
		nameWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
		nameWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
		nameWidget.setFontId(494);
		nameWidget.setTextColor(0xFFFF00);
	}

	public void setText(String text)
	{
		if (UIManager.FONT_METRICS.stringWidth(text) > width)
		{
			text = UIManager.cutStringToPxWidth(text, width, true);
		}
		nameWidget.setText(text);
	}

	@Override
	public void layout(int x, int y)
	{
		super.layout(x, y);
		layoutWidget(capLeft, x, y);
		layoutWidget(capRight, x + width - CAP_WIDTH, y);
		layoutWidget(background, x + CAP_WIDTH, y);
		layoutWidget(nameWidget, x, y);
		parent.revalidate();
	}
}

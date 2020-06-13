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

import net.runelite.api.widgets.Widget;

public abstract class CustomWidgetWithIcon extends CustomWidget
{
	protected int iconWidth;
	protected int iconHeight;

	protected int iconSpriteID;
	protected int iconPaddingX;
	protected int iconPaddingY;

	protected Widget icon;

	public CustomWidgetWithIcon(final Widget parent, final String name, int iconSpriteID)
	{
		super(parent, name);
		this.iconSpriteID = iconSpriteID;
	}

	public void setIconSize(int width, int height)
	{
		this.iconWidth = width;
		this.iconHeight = height;
		iconPaddingX = (this.width - iconWidth) / 2;
		iconPaddingY = (this.height - iconHeight) / 2;
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(icon, x + iconPaddingX, y + iconPaddingY);

		parent.revalidate();
	}
}

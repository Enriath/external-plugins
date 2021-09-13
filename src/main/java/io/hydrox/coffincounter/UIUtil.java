/*
 * Copyright (c) 2021 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.coffincounter;

import java.awt.Color;
import java.awt.Graphics;

public class UIUtil
{
	static final int GAP_X = 20;
	static final int GAP_Y = 12;
	static final int OFFSET_TOP = 10;
	static final int LABELS_PER_COLUMN = 3;
	static final String UNKNOWN = "?";

	static void drawString(Graphics graphics, Shade shade, String text, int drawX, int drawY)
	{
		if (shade.getOutline() != null)
		{
			graphics.setColor(shade.getOutline());
			graphics.drawString(text, drawX + 1, drawY);
			graphics.drawString(text, drawX - 1, drawY);
			graphics.drawString(text, drawX, drawY + 1);
			graphics.drawString(text, drawX, drawY - 1);
		}
		else
		{
			graphics.setColor(Color.BLACK);
			graphics.drawString(text, drawX + 1, drawY + 1);
		}
		graphics.setColor(shade.getColour());
		graphics.drawString(text, drawX, drawY);
	}
}

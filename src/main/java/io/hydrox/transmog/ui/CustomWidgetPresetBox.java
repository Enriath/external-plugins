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

import io.hydrox.transmog.TransmogPreset;
import net.runelite.api.FontID;
import net.runelite.api.ScriptEvent;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;
import java.awt.Color;

public class CustomWidgetPresetBox extends CustomWidgetPresetTabItem
{
	private static final int DEFAULT_ICON = SpriteID.TAB_EMOTES;

	private Widget defaultIconWidget;
	private Widget iconWidget;
	private Widget nameWidget;

	private final TransmogPreset preset;

	public CustomWidgetPresetBox(TransmogPreset preset, Widget parent, WidgetIntCallback callback)
	{
		super(parent, preset.getDisplayName(), callback);
		this.preset = preset;
	}

	@Override
	public int getId()
	{
		return preset.getId();
	}

	@Override
	public void layout(int x, int y)
	{
		super.layout(x, y);
		layoutWidget(defaultIconWidget, x + 2, y + 2);
		layoutWidget(nameWidget, x + 2, y + 2);
		layoutWidget(iconWidget, x + 2, y + 2);
		parent.revalidate();
	}

	@Override
	public void scrollBy(int y)
	{
		super.scrollBy(y);
		defaultIconWidget.setOriginalY(this.y - y + 2);
		nameWidget.setOriginalY(this.y - y + 2);
		iconWidget.setOriginalY(this.y - y + 2);
		defaultIconWidget.revalidate();
		nameWidget.revalidate();
		iconWidget.revalidate();
	}

	@Override
	public void create()
	{
		super.createUnderlay();

		defaultIconWidget = createSpriteWidget(32, 32);
		defaultIconWidget.setSpriteId(DEFAULT_ICON);

		iconWidget = createSpriteWidget(36, 32);
		iconWidget.setItemQuantity(-1);
		iconWidget.setItemQuantityMode(2);
		iconWidget.setBorderType(1);

		if (preset.getIcon() >= -1)
		{
			iconWidget.setItemId(preset.getIcon());
			defaultIconWidget.setHidden(true);
		}
		else
		{
			iconWidget.setHidden(true);
		}

		nameWidget = createTextWidget(preset.getDisplayName(true));
		nameWidget.setOriginalWidth(32);
		nameWidget.setOriginalHeight(32);
		nameWidget.setFontId(FontID.PLAIN_11);
		nameWidget.setTextColor(fromRGB(Color.YELLOW));
		nameWidget.setXTextAlignment(WidgetTextAlignment.RIGHT);
		nameWidget.setYTextAlignment(WidgetTextAlignment.BOTTOM);

		super.createOverlay();
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		callback.run(preset.getId());
	}
}

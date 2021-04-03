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

public class CustomWidgetPlayerPreview extends CustomWidget implements InteractibleWidget
{
	private Widget clickbox;

	private boolean rotationEnabled = true;

	public CustomWidgetPlayerPreview(Widget parent, String name)
	{
		super(parent, name);
	}

	public void setHidden(boolean state)
	{
		base.setHidden(state);
		clickbox.setHidden(state);
	}

	@Override
	public void layout(int x, int y)
	{
		super.layout(x, y);
		layoutWidget(base, x, y + 60);
		layoutWidget(clickbox, x, y);
		parent.revalidate();
	}

	@Override
	public void create()
	{
		base = createModelWidget(90, 150);
		base.setModelId(1);
		base.setModelType(5);
		base.setModelZoom(760);
		base.setRotationX(150);

		clickbox = createSpriteWidget(90, 150);
		clickbox.setHasListener(true);
		clickbox.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		clickbox.setAction(1, "Toggle rotation for");
		clickbox.setAction(2, "Reset rotation for");
	}

	public void tickRotation()
	{
		if (rotationEnabled)
		{
			base.setRotationZ((base.getRotationZ() + 2) % 2048);
		}
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		switch (scriptEvent.getOp())
		{
			case 2:
				rotationEnabled = !rotationEnabled;
				break;
			case 3:
				base.setRotationZ(0);
				break;
		}
	}
}

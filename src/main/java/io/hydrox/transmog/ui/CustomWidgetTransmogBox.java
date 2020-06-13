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

import io.hydrox.transmog.MappingMapping;
import io.hydrox.transmog.TransmogSlot;
import io.hydrox.transmog.TransmogSlot.SlotType;
import static io.hydrox.transmog.ui.MenuOps.CLEAR;
import static io.hydrox.transmog.ui.MenuOps.FORCE_DEFAULT;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public class CustomWidgetTransmogBox extends CustomWidget implements InteractibleWidget
{
	private static final int BACKGROUND_ID = 170;

	private Widget slotWidget;
	private Widget contentsWidget;
	private Widget slotDefaultWidget;
	private Widget overlayWidget;

	private SlotType type;

	private TransmogSlot slot;

	private MappingMapping mapping;

	private final WidgetSlotCallback callback;

	public CustomWidgetTransmogBox(SlotType type, Widget parent, TransmogSlot slot, WidgetSlotCallback callback)
	{
		super(parent, slot.getName());
		this.type = type;
		this.slot = slot;
		this.callback = callback;
	}

	public void setDefault()
	{
		slotDefaultWidget.setHidden(false);
		slotWidget.setHidden(false);
		contentsWidget.setHidden(true);
		overlayWidget.setAction(SET_ITEM, "Set item for");
		overlayWidget.setAction(CLEAR, "Clear");
		overlayWidget.setAction(FORCE_DEFAULT, null);
	}

	public void setEmpty()
	{
		slotDefaultWidget.setHidden(true);
		slotWidget.setHidden(false);
		contentsWidget.setHidden(true);
		overlayWidget.setAction(SET_ITEM, "Set item for");
		overlayWidget.setAction(CLEAR, null);
		overlayWidget.setAction(FORCE_DEFAULT, "Force default for");
	}

	public void setContent(int id, String name)
	{
		slotDefaultWidget.setHidden(true);
		slotWidget.setHidden(true);
		contentsWidget.setHidden(false);
		if (type == SlotType.ITEM)
		{
			contentsWidget.setItemId(id);
		}
		else
		{
			contentsWidget.setModelId(id);
		}

		overlayWidget.setAction(SET_ITEM, "Replace <col=ff981f>" + name + "</col> for");
		overlayWidget.setAction(CLEAR, "Clear");
		overlayWidget.setAction(FORCE_DEFAULT, "Force default for");
	}

	public void set(Integer contents, String name)
	{
		if (contents == null)
		{
			setEmpty();
		}
		else if (contents == -1)
		{
			setDefault();
		}
		else
		{
			this.setContent(contents, name);
		}
	}

	@Override
	public void layout(int x, int y)
	{
		final int yOffset = mapping != null ? mapping.getYOffset() : 0;
		layoutWidget(base, x, y);
		layoutWidget(slotWidget, x + 2, y + 2);
		layoutWidget(slotDefaultWidget, x + 2, y + 2);
		layoutWidget(contentsWidget, x + 2, y + 2 + yOffset);
		layoutWidget(overlayWidget, x, y);
		parent.revalidate();
	}

	@Override
	public void create()
	{
		base = createSpriteWidget(36, 36);
		base.setSpriteId(BACKGROUND_ID);

		slotWidget = createSpriteWidget(32, 32);
		slotWidget.setSpriteId(slot.getSpriteID());

		slotDefaultWidget = createSpriteWidget(32, 32);
		slotDefaultWidget.setSpriteId(1193);

		if (type == SlotType.ITEM)
		{
			contentsWidget = createSpriteWidget(36, 32);
			contentsWidget.setItemQuantity(-1);
			contentsWidget.setItemQuantityMode(2);
			contentsWidget.setBorderType(1);
		}
		else
		{
			mapping = MappingMapping.fromSlot(slot);
			contentsWidget = createModelWidget(36, 32);
			contentsWidget.setModelType(1);
			contentsWidget.setModelId(28417);
			contentsWidget.setModelZoom(mapping.getModelZoom());
			contentsWidget.setRotationZ(150);
		}
		contentsWidget.setHidden(true);

		overlayWidget = createSpriteWidget(36, 36);
		overlayWidget.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		overlayWidget.setHasListener(true);
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		callback.run(scriptEvent.getOp() - 1, slot);
	}
}

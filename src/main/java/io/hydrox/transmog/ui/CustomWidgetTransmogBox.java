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
import static io.hydrox.transmog.ui.MenuOps.HIDE;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
import net.runelite.api.ScriptEvent;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public class CustomWidgetTransmogBox extends CustomWidget implements InteractibleWidget
{
	private static final int BACKGROUND_ID = 170;
	private static final String SET_ITEM_STRING = "Set item for";
	private static final String CLEAR_STRING = "Clear";
	private static final String HIDE_STRING = "Hide";
	// The Special slots are dependant on the equipped armour, so some different terminology is needed.
	private static final String SPECIAL_CLEAR_STRING = "Follow armour for";
	private static final String SPECIAL_HIDE_STRING = "Use base";

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
		overlayWidget.setAction(SET_ITEM, SET_ITEM_STRING);
		overlayWidget.setAction(HIDE, null);

		if (slot.getSlotType() == SlotType.SPECIAL)
		{
			overlayWidget.setAction(CLEAR, SPECIAL_CLEAR_STRING);
		}
		else
		{
			overlayWidget.setAction(CLEAR, CLEAR_STRING);
		}
	}

	public void setEmpty()
	{
		slotDefaultWidget.setHidden(true);
		slotWidget.setHidden(false);
		contentsWidget.setHidden(true);
		overlayWidget.setAction(SET_ITEM, SET_ITEM_STRING);
		overlayWidget.setAction(CLEAR, null);

		if (slot.getSlotType() == SlotType.SPECIAL)
		{
			overlayWidget.setAction(HIDE, SPECIAL_HIDE_STRING);
		}
		else
		{
			overlayWidget.setAction(HIDE, HIDE_STRING);
		}
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

		overlayWidget.setAction(SET_ITEM, "Replace " + UIManager.ORANGE_COLOUR_WIDGET_NAME + name + "</col> for");

		if (slot.getSlotType() == SlotType.SPECIAL)
		{
			overlayWidget.setAction(CLEAR, SPECIAL_CLEAR_STRING);
			overlayWidget.setAction(HIDE, SPECIAL_HIDE_STRING);
		}
		else
		{
			overlayWidget.setAction(CLEAR, CLEAR_STRING);
			overlayWidget.setAction(HIDE, HIDE_STRING);
		}
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
		super.layout(x, y);

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
		slotDefaultWidget.setSpriteId(SpriteID.OPTIONS_DISABLED_OPTION_OVERLAY);

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

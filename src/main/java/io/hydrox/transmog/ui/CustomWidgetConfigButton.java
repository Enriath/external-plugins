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
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public class CustomWidgetConfigButton extends CustomWidget implements InteractibleWidget
{
	private static final int COG_SMALL = 2410;

	private Widget overlay;

	private Widget topLeftBrace;
	private Widget topRightBrace;
	private Widget bottomLeftBrace;
	private Widget bottomRightBrace;

	private Widget leftSide;
	private Widget topSide;
	private Widget rightSide;
	private Widget bottomSide;

	private Widget itemIcon;
	private Widget largeIcon;
	private Widget smallIcon;

	protected final WidgetIntCallback callback;

	public CustomWidgetConfigButton(final Widget parent, final String name, final WidgetIntCallback callback)
	{
		super(parent, name);
		this.callback = callback;
	}

	@Override
	public void create()
	{
		topLeftBrace = createSpriteWidget(9, 9);
		topRightBrace = createSpriteWidget(9, 9);
		bottomLeftBrace = createSpriteWidget(9, 9);
		bottomRightBrace = createSpriteWidget(9, 9);

		leftSide = createSpriteWidget(9, height - 9 * 2);
		topSide = createSpriteWidget(width - 9 * 2, 9);
		rightSide = createSpriteWidget(9, height - 9 * 2);
		bottomSide = createSpriteWidget(width - 9 * 2, 9);

		onLeave();

		largeIcon = createSpriteWidget(32, 32);
		largeIcon.setSpriteId(COG_SMALL);

		itemIcon = createSpriteWidget(36, 32);
		itemIcon.setItemQuantity(-1);
		itemIcon.setItemQuantityMode(2);
		itemIcon.setBorderType(1);

		smallIcon = createSpriteWidget(20, 20);
		smallIcon.setSpriteId(COG_SMALL);
		smallIcon.setHidden(true);



		overlay = createSpriteWidget(width, height);
		overlay.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		overlay.setOnMouseRepeatListener((JavaScriptCallback) e -> onHover());
		overlay.setOnMouseLeaveListener((JavaScriptCallback) e -> onLeave());
		overlay.setHasListener(true);
	}

	public void addOption(int index, String action)
	{
		overlay.setAction(index, action);
	}

	public void setItemIcon(int itemID)
	{
		itemIcon.setItemId(itemID);
		largeIcon.setHidden(itemID != -1);
		smallIcon.setHidden(itemID == -1);
		if (itemID == -1)
		{
			addOption(2, "Set Icon");
			addOption(3, null);
		}
		else
		{
			addOption(2, "Change Icon");
			addOption(3, "Remove Icon");
		}
	}

	private void onHover()
	{
		topLeftBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_TOP_LEFT_HOVERED);
		topRightBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_TOP_RIGHT_HOVERED);
		bottomLeftBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_BOTTOM_LEFT_HOVERED);
		bottomRightBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_BOTTOM_RIGHT_HOVERED);
		leftSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_LEFT_HOVERED);
		topSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_TOP_HOVERED);
		rightSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_RIGHT_HOVERED);
		bottomSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_BOTTOM_HOVERED);
	}

	private void onLeave()
	{
		topLeftBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_TOP_LEFT);
		topRightBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_TOP_RIGHT);
		bottomLeftBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_BOTTOM_LEFT);
		bottomRightBrace.setSpriteId(SpriteID.EQUIPMENT_BUTTON_METAL_CORNER_BOTTOM_RIGHT);
		leftSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_LEFT);
		topSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_TOP);
		rightSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_RIGHT);
		bottomSide.setSpriteId(SpriteID.EQUIPMENT_BUTTON_EDGE_BOTTOM);
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(overlay, x, y);

		layoutWidget(largeIcon, x + 3, y + 3);
		layoutWidget(smallIcon, x + width - 22, y + height - 22);
		layoutWidget(itemIcon, x + 4, y + 3);

		layoutWidget(topLeftBrace, x, y);
		layoutWidget(topRightBrace, x + width - 9, y);
		layoutWidget(bottomLeftBrace, x, y + height - 9);
		layoutWidget(bottomRightBrace, x + width - 9, y + height - 9);

		layoutWidget(leftSide, x, y + 9);
		layoutWidget(topSide, x + 9, y);
		layoutWidget(rightSide, x + width - 9, y + 9);
		layoutWidget(bottomSide, x + 9, y + height - 9);

		super.layout(x, y);
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		// For whatever reason, the Op for an option is always 1 higher than the given index. MAGIC!
		callback.run(scriptEvent.getOp() - 1);
	}
}

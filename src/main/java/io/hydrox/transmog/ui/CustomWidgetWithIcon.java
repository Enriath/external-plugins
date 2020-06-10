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

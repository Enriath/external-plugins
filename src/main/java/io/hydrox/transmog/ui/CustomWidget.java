package io.hydrox.transmog.ui;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import java.awt.Color;

public abstract class CustomWidget
{
	protected final Widget parent;
	private final String name;

	protected int width;
	protected int height;

	protected Widget base;

	public CustomWidget(final Widget parent, final String name)
	{
		this.parent = parent;
		this.name = name;
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public abstract void layout(int x, int y);

	public abstract void create();

	protected Widget createSpriteWidget(int width, int height)
	{
		final Widget w = parent.createChild(-1, WidgetType.GRAPHIC);
		w.setOriginalWidth(width);
		w.setOriginalHeight(height);
		w.setName("<col=ff981f>" + this.name);
		return w;
	}

	protected Widget createTextWidget(String text)
	{
		final Widget w = parent.createChild(-1, WidgetType.TEXT);
		w.setText(text);
		w.setTextShadowed(true);
		return w;
	}

	protected static void layoutWidget(Widget w, int x, int y)
	{
		w.setOriginalX(x);
		w.setOriginalY(y);
		w.revalidate();
	}

	protected static int fromRGB(Color c)
	{
		return fromRGB(c.getRed(), c.getGreen(), c.getBlue());
	}

	protected static int fromRGB(int r, int g, int b)
	{
		return (r << 16) + (g << 8) + b;
	}
}

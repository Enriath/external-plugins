package io.hydrox.planksack;

import net.runelite.client.ui.overlay.infobox.InfoBox;
import java.awt.Color;
import java.awt.image.BufferedImage;

class PlankSackCounter extends InfoBox
{
	private final PlankSackPlugin plugin;

	PlankSackCounter(BufferedImage img, PlankSackPlugin plugin)
	{
		super(img, plugin);
		this.plugin = plugin;
	}

	@Override
	public String getText()
	{
		return plugin.getPlankCount() == -1 ? "?" : plugin.getPlankCount() + "";
	}

	@Override
	public Color getTextColor()
	{
		return plugin.getColour();
	}
}

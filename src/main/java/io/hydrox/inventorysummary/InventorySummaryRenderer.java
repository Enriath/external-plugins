package io.hydrox.inventorysummary;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import net.runelite.client.ui.overlay.components.PanelComponent;

final class InventorySummaryRenderer
{
	private Dimension nativeSize;

	void invalidate()
	{
		nativeSize = null;
	}

	Dimension render(PanelComponent panel, Graphics2D graphics, Dimension preferredSize)
	{
		if (nativeSize == null)
		{
			Graphics2D measurement = (Graphics2D) graphics.create();
			try
			{
				measurement.setClip(0, 0, 0, 0);
				// Both nested panels report their previous layout's dimensions.
				// Settle their layout before using its size to scale the drawing.
				for (int i = 0; i < 3; i++)
				{
					nativeSize = panel.render(measurement);
				}
			}
			finally
			{
				measurement.dispose();
			}
		}

		if (nativeSize == null || nativeSize.width <= 0 || nativeSize.height <= 0)
		{
			return null;
		}
		Dimension size = preferredSize == null
			? new Dimension(nativeSize)
			: new Dimension(Math.max(1, preferredSize.width), Math.max(1, preferredSize.height));
		Graphics2D resizedGraphics = (Graphics2D) graphics.create();
		try
		{
			resizedGraphics.scale((double) size.width / nativeSize.width, (double) size.height / nativeSize.height);
			resizedGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			panel.render(resizedGraphics);
		}
		finally
		{
			resizedGraphics.dispose();
		}
		return size;
	}
}

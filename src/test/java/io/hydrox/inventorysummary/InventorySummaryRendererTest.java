package io.hydrox.inventorysummary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InventorySummaryRendererTest
{
	private static final int COUNT_YELLOW = 0xFFFFFF00;
	private static final int LOG_BROWN = 0xFF725B32;
	private static final int SECOND_ITEM_BLUE = 0xFF2255CC;
	private static final int THIRD_ITEM_GREEN = 0xFF229944;

	@Test
	public void firstFrameHasTheCompleteNestedLayoutAndOriginalSpritePixels()
	{
		Fixture fixture = new Fixture(logSprite());
		BufferedImage output = fixture.draw(null, new Dimension(48, 44));
		assertEquals(new Rectangle(8, 4, 2, 4), colorBounds(output, COUNT_YELLOW));
		assertEquals(LOG_BROWN, output.getRGB(43, 35));
		assertEquals(Color.BLACK.getRGB(), output.getRGB(47, 43));
		assertEquals(0, output.getRGB(48, 44));
	}

	@Test
	public void eachResizeEdgeAndCornerResizesTheCountAndCanShrinkOrReset()
	{
		Fixture fixture = new Fixture(logSprite());
		assertCountBounds(fixture, new Dimension(144, 44), new Rectangle(24, 4, 6, 4));
		assertCountBounds(fixture, new Dimension(48, 132), new Rectangle(8, 12, 2, 12));
		assertCountBounds(fixture, new Dimension(144, 132), new Rectangle(24, 12, 6, 12));
		assertCountBounds(fixture, new Dimension(96, 88), new Rectangle(16, 8, 4, 8));
		assertCountBounds(fixture, new Dimension(24, 22), new Rectangle(4, 2, 1, 2));
		BufferedImage reset = fixture.draw(null, new Dimension(48, 44));
		assertEquals(new Rectangle(8, 4, 2, 4), colorBounds(reset, COUNT_YELLOW));
	}

	@Test
	public void spritesThatLoadAfterMeasurementStillAppearWithoutInvalidation()
	{
		BufferedImage pendingSprite = new BufferedImage(36, 32, BufferedImage.TYPE_INT_ARGB);
		Fixture fixture = new Fixture(pendingSprite);
		Dimension target = new Dimension(144, 132);
		assertTrue(colorBounds(fixture.draw(target, target), COUNT_YELLOW).isEmpty());

		// ItemManager fills the original image asynchronously after returning it.
		pendingSprite.setRGB(0, 0, COUNT_YELLOW);
		BufferedImage loaded = fixture.draw(target, target);
		assertEquals(new Rectangle(24, 12, 3, 3), colorBounds(loaded, COUNT_YELLOW));
	}

	@Test
	public void inventoryChangesRecalculateAllRowsOnTheFirstFrameAfterInvalidation()
	{
		Fixture fixture = new Fixture(logSprite());
		fixture.draw(new Dimension(144, 132), new Dimension(144, 132));

		fixture.items.getChildren().add(new ImageComponent(solidSprite(SECOND_ITEM_BLUE)));
		fixture.renderer.invalidate();
		BufferedImage twoItems = fixture.draw(new Dimension(144, 240), new Dimension(144, 240));
		assertEquals(new Rectangle(24, 120, 108, 96), colorBounds(twoItems, SECOND_ITEM_BLUE));

		fixture.items.getChildren().add(new ImageComponent(solidSprite(THIRD_ITEM_GREEN)));
		fixture.renderer.invalidate();
		BufferedImage threeItems = fixture.draw(new Dimension(144, 348), new Dimension(144, 348));
		assertEquals(new Rectangle(24, 228, 108, 96), colorBounds(threeItems, THIRD_ITEM_GREEN));

		BufferedImage refitted = fixture.draw(new Dimension(144, 132), new Dimension(144, 132));
		Rectangle lastItem = colorBounds(refitted, THIRD_ITEM_GREEN);
		assertTrue("All three item groups must remain visible in a shorter saved rectangle", !lastItem.isEmpty());
		assertTrue("The final item must fit inside the requested overlay bounds", lastItem.y + lastItem.height <= 132);

		BufferedImage natural = fixture.draw(null, new Dimension(48, 116));
		assertEquals(new Rectangle(8, 76, 36, 32), colorBounds(natural, THIRD_ITEM_GREEN));

		fixture.items.getChildren().clear();
		fixture.items.getChildren().add(new ImageComponent(logSprite()));
		fixture.renderer.invalidate();
		fixture.draw(null, new Dimension(48, 44));
	}

	@Test
	public void measuringAndRenderingPreserveTheCallersGraphicsState()
	{
		Fixture fixture = new Fixture(logSprite());
		Graphics2D graphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB).createGraphics();
		try
		{
			graphics.translate(7, 9);
			graphics.scale(1.25, 1.5);
			graphics.setClip(new Rectangle(2, 3, 100, 90));
			graphics.setColor(Color.MAGENTA);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			AffineTransform originalTransform = graphics.getTransform();
			Shape originalClip = graphics.getClip();
			RenderingHints originalHints = graphics.getRenderingHints();

			assertEquals(new Dimension(144, 132), fixture.renderer.render(fixture.panel, graphics, new Dimension(144, 132)));
			assertEquals(originalTransform, graphics.getTransform());
			assertEquals(originalClip.getBounds2D(), graphics.getClip().getBounds2D());
			assertEquals(originalHints, graphics.getRenderingHints());
			assertEquals(Color.MAGENTA, graphics.getColor());
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static void assertCountBounds(Fixture fixture, Dimension target, Rectangle expected)
	{
		assertEquals(expected, colorBounds(fixture.draw(target, target), COUNT_YELLOW));
	}

	private static BufferedImage logSprite()
	{
		BufferedImage image = solidSprite(LOG_BROWN);
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 2; x++)
			{
				image.setRGB(x, y, COUNT_YELLOW);
			}
		}
		return image;
	}

	private static BufferedImage solidSprite(int color)
	{
		BufferedImage image = new BufferedImage(36, 32, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				image.setRGB(x, y, color);
			}
		}
		return image;
	}

	private static Rectangle colorBounds(BufferedImage image, int color)
	{
		Rectangle bounds = new Rectangle();
		boolean found = false;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if (image.getRGB(x, y) == color)
				{
					Rectangle pixel = new Rectangle(x, y, 1, 1);
					bounds = found ? bounds.union(pixel) : pixel;
					found = true;
				}
			}
		}
		return bounds;
	}

	private static final class Fixture
	{
		private final InventorySummaryRenderer renderer = new InventorySummaryRenderer();
		private final PanelComponent items = new PanelComponent();
		private final PanelComponent panel = new PanelComponent();

		private Fixture(BufferedImage sprite)
		{
			items.setGap(new Point(6, 4));
			items.setOrientation(ComponentOrientation.HORIZONTAL);
			items.setBackgroundColor(null);
			items.setBorder(new Rectangle(4, 0, 0, 4));
			items.setPreferredSize(new Dimension(48, 0));
			items.setWrap(true);
			items.getChildren().add(new ImageComponent(sprite));
			panel.setOrientation(ComponentOrientation.VERTICAL);
			panel.setBorder(new Rectangle(4, 4, 4, 4));
			panel.setPreferredSize(new Dimension(48, 0));
			panel.setBackgroundColor(Color.BLACK);
			panel.getChildren().add(items);
		}

		private BufferedImage draw(Dimension preferredSize, Dimension expectedSize)
		{
			BufferedImage output = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = output.createGraphics();
			try
			{
				assertEquals(expectedSize, renderer.render(panel, graphics, preferredSize));
			}
			finally
			{
				graphics.dispose();
			}
			return output;
		}
	}
}

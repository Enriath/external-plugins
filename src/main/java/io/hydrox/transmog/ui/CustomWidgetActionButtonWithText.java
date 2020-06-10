package io.hydrox.transmog.ui;

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;
import java.awt.Color;

public class CustomWidgetActionButtonWithText extends CustomWidgetActionButton
{
	private String initialText;

	private Widget text;

	public CustomWidgetActionButtonWithText(Widget parent, String name, int spriteId, String initialText, WidgetIntCallback callback)
	{
		super(parent, name, spriteId, callback);
		this.initialText = initialText;
	}

	@Override
	public void create()
	{
		super.create();

		text = createTextWidget(initialText);
		text.setOriginalWidth(8);
		text.setOriginalHeight(12);
		//text.setFontId(819);
		text.setFontId(764);
		text.setTextColor(fromRGB(Color.CYAN));
		text.setXTextAlignment(WidgetTextAlignment.CENTER);
		text.setYTextAlignment(WidgetTextAlignment.BOTTOM);
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(text, x + width - 14, y + 9);
		super.layout(x, y);
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		// For whatever reason, the Op for an option is always 1 higher than the given index. MAGIC!
		callback.run(scriptEvent.getOp() - 1);
		text.setText(scriptEvent.getOp() - 1 + "");
	}
}

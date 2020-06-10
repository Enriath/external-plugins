package io.hydrox.transmog.ui;

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;

public class CustomWidgetCheckbox extends CustomWidget implements InteractibleWidget
{
	private int offSpriteID;
	private int onSpriteID;
	private Widget label;

	private boolean state;

	private final WidgetBooleanCallback callback;

	public CustomWidgetCheckbox(Widget parent, String name, boolean defaultState, int offSprite, int onSprite, WidgetBooleanCallback callback)
	{
		super(parent, name);

		this.offSpriteID = offSprite;
		this.onSpriteID = onSprite;
		this.callback = callback;
		this.state = defaultState;
	}

	@Override
	public void create()
	{
		base = createSpriteWidget(width, height);
		base.setSpriteId(state ? onSpriteID : offSpriteID);
		base.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		base.setOnMouseRepeatListener((JavaScriptCallback) e -> e.getSource().setOpacity(120));
		base.setOnMouseLeaveListener((JavaScriptCallback) e -> e.getSource().setOpacity(0));
		base.setHasListener(true);
		base.setAction(1, state ? "Disable" : "Enable");

		label = createTextWidget(state ? "Enabled" : "Disabled");
		label.setOriginalWidth(parent.getOriginalWidth());
		label.setOriginalHeight(height);
		label.setFontId(494);
		label.setYTextAlignment(WidgetTextAlignment.CENTER);
		label.setTextColor(fromRGB(240, 240, 240));
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(base, x, y);

		layoutWidget(label, x + width + 1, y);

		parent.revalidate();
	}

	public void toggle()
	{
		state = !state;
		base.setSpriteId(state ? onSpriteID : offSpriteID);
		base.setAction(1, state ? "Disable" : "Enable");
		label.setText(state ? "Enabled" : "Disabled");

	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		toggle();
		callback.run(state);
	}
}

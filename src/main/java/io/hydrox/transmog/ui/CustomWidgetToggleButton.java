package io.hydrox.transmog.ui;

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public class CustomWidgetToggleButton extends CustomWidgetWithIcon implements InteractibleWidget
{
	private int selectedBackgroundSprite;
	private int backgroundSpriteID;

	private boolean selected;

	private final WidgetBooleanCallback callback;

	public CustomWidgetToggleButton(final Widget parent, final String name, int backgroundSprite, int selectedBackgroundSprite, int iconSprite, final WidgetBooleanCallback callback)
	{
		super(parent, name, iconSprite);
		this.backgroundSpriteID = backgroundSprite;
		this.selectedBackgroundSprite = selectedBackgroundSprite;
		this.callback = callback;
	}

	public void toggle()
	{
		selected = !selected;
		base.setSpriteId(selected ? selectedBackgroundSprite : backgroundSpriteID);
		icon.setAction(1, selected ? "Close" : "Open");
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(base, x, y);

		super.layout(x, y);
	}

	@Override
	public void create()
	{
		base = createSpriteWidget(width, height);
		base.setSpriteId(backgroundSpriteID);

		icon = createSpriteWidget(iconWidth, iconHeight);
		icon.setSpriteId(iconSpriteID);
		icon.setOnOpListener((JavaScriptCallback) this::onButtonClicked);
		icon.setOnMouseRepeatListener((JavaScriptCallback) e -> e.getSource().setOpacity(120));
		icon.setOnMouseLeaveListener((JavaScriptCallback) e -> e.getSource().setOpacity(0));
		icon.setHasListener(true);
		icon.setAction(1, "Open");
	}

	@Override
	public void onButtonClicked(ScriptEvent scriptEvent)
	{
		toggle();
		callback.run(selected);
	}
}

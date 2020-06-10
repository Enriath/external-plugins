package io.hydrox.transmog.ui;

import io.hydrox.transmog.TransmogSlot;
import static io.hydrox.transmog.ui.MenuOps.CLEAR;
import static io.hydrox.transmog.ui.MenuOps.FORCE_DEFAULT;
import static io.hydrox.transmog.ui.MenuOps.SET_ITEM;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;

public class CustomWidgetTransmogBox extends CustomWidget implements InteractibleWidget
{
	private static final int BACKGROUND_ID = 170;

	private Widget slotWidget;
	private Widget itemWidget;
	private Widget slotDefaultWidget;
	private Widget overlayWidget;

	private TransmogSlot slot;

	private final WidgetSlotCallback callback;

	public CustomWidgetTransmogBox(Widget parent, TransmogSlot slot, WidgetSlotCallback callback)
	{
		super(parent, slot.getName());
		this.slot = slot;
		this.callback = callback;
	}

	public void setDefault()
	{
		slotDefaultWidget.setHidden(false);
		slotWidget.setHidden(false);
		itemWidget.setHidden(true);
		itemWidget.setItemId(-1);
		overlayWidget.setAction(SET_ITEM, "Set item for");
		overlayWidget.setAction(CLEAR, "Clear");
		overlayWidget.setAction(FORCE_DEFAULT, null);
	}

	public void setEmpty()
	{
		slotDefaultWidget.setHidden(true);
		slotWidget.setHidden(false);
		itemWidget.setHidden(true);
		itemWidget.setItemId(-1);
		overlayWidget.setAction(SET_ITEM, "Set item for");
		overlayWidget.setAction(CLEAR, null);
		overlayWidget.setAction(FORCE_DEFAULT, "Force default for");
	}

	public void setItem(int itemID, String name)
	{
		slotDefaultWidget.setHidden(true);
		slotWidget.setHidden(true);
		itemWidget.setHidden(false);
		itemWidget.setItemId(itemID);
		overlayWidget.setAction(SET_ITEM, "Replace <col=ff981f>" + name + "</col> for");
		overlayWidget.setAction(CLEAR, "Clear");
		overlayWidget.setAction(FORCE_DEFAULT, "Force default for");
	}

	public void setContents(Integer contents, String name)
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
			setItem(contents, name);
		}
	}

	@Override
	public void layout(int x, int y)
	{
		layoutWidget(base, x, y);
		layoutWidget(slotWidget, x + 2, y + 2);
		layoutWidget(slotDefaultWidget, x + 2, y + 2);
		layoutWidget(itemWidget, x + 2, y + 2);
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
		slotDefaultWidget.setSpriteId(1193);

		itemWidget = createSpriteWidget(36, 32);
		itemWidget.setItemId(20594);
		itemWidget.setItemQuantity(-1);
		itemWidget.setItemQuantityMode(2);
		itemWidget.setBorderType(1);
		itemWidget.setHidden(true);

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

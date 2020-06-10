package io.hydrox.transmog.ui;

import io.hydrox.transmog.TransmogSlot;
import net.runelite.api.ScriptEvent;

public interface InteractibleWidget
{
	@FunctionalInterface
	interface WidgetBooleanCallback
	{
		void run(boolean state);
	}

	@FunctionalInterface
	interface WidgetIntCallback
	{
		void run(int state);
	}

	@FunctionalInterface
	interface WidgetSlotCallback
	{
		void run(int op, TransmogSlot slot);
	}

	void onButtonClicked(ScriptEvent scriptEvent);
}

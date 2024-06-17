package io.hydrox.quickprayerpreview;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(QuickPrayerPreviewConfig.CONFIG_GROUP)
public interface QuickPrayerPreviewConfig extends Config
{
	String CONFIG_GROUP = "quickprayerpreview";

	String KEY_SPRITE_SIZE = "spriteSize";

	@ConfigItem(
		name = "Sprite Size",
		description = "Size of tooltip prayer sprites.",
		position = 0,
		keyName = KEY_SPRITE_SIZE
	)
	@Units(Units.PIXELS)
	@Range(min = 8, max = 32)
	default int spriteSize()
	{
		return 32;
	}

	@ConfigItem(
		name = "Require Key Press",
		description = "Only display quick-prayer orb tooltip while pressing the set key.",
		position = 1,
		keyName = "requireKeyPress"
	)
	default boolean requireKeyPress()
	{
		return false;
	}

	@ConfigItem(
		name = "Key To Press",
		description = "The key to press to display the quick-prayer orb tooltip.",
		position = 2,
		keyName = "keyToPress"
	)
	default ModifierlessKeybind keyToPress()
	{
		return new ModifierlessKeybind(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);
	}
}

package io.hydrox.contextualcursor;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;


@ConfigGroup("contextualcursor")
public interface ContextualCursorConfig extends Config {

    @ConfigItem(
            keyName = "cursorstyle",
            name = "Cursor Style",
            description = "Pick what style cursor you would like"
    )
    default Skin skin() {
        return Skin.RS2;
    }


}
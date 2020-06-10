package io.hydrox.transmog.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.game.SpriteOverride;

@RequiredArgsConstructor
public enum CustomSprites implements SpriteOverride
{
	TRANSMOG_LOGO(-4365, "transmog.png"),
	TRANSMOG_SAVE(-4366, "save.png"),
	TRANSMOG_DELETE(-4367, "delete.png"),
	TRANSMOG_TUTORIAL_ARROW(-4368, "arrow.png");

	@Getter
	private final int spriteId;

	@Getter
	private final String fileName;
}
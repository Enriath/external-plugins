/*
 * Copyright (c) 2019 Hydrox6 <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.hydrox.contextualcursor;

import lombok.Getter;
import net.runelite.api.SpriteID;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ContextualCursor
{
	BLANK("blank"),
	TALK("talk", "talk", "talk-to", "talk to"),
	LADDER("ladder", "climb"),
	LADDER_DOWN("climbdown", "climb-down"),
	LADDER_UP("climbup", "climb-up"),
	EQUIP("wear", "wield", "wear"),
	EAT("eat", "eat"),
	DRINK("drink", "drink"),
	ENTER("enter", "climb-into", "enter", "exit", "yanille", "varrock", "seers' village", "camelot", "grand exchange", "watchtower", "go-through"),
	PICK_UP("take", "take","withdraw","deposit"),
	UNTIE("untie"),
	GENERIC("generic"), // Cursor inside background
	PLANK("plank", "buy-plank"),
	BANG(SpriteID.SKILL_STRENGTH, "bang"), //Strength icon
	SEARCH("search", "search"),
	OPEN("open", "open"),
	READ("read", "read"),
	SLASH("slash", "slash"),
	PULL("pull","pull","close"),
	LOGOUT("logout","logout"),
	BACK("back","back"),
	DIG("dig","dig"),
	INSPECT("use","inspect","toggle","show"),
	MAP("map","world map"),
	SETTINGS("settings","settings","audio"),
	ACCEEPT("accept","accept"),
	DECLINE("decline","decline"),
	LINK("link","link","choose"),
	TRADE("trade","trade"),

	// Skills
	ATTACK(SpriteID.SKILL_ATTACK, "attack"),
	AGILITY(SpriteID.SKILL_AGILITY, "balance", "balance-across", "climb-across", "climb-on", "climb-over",
		"cross", "grab", "grapple", "hurdle", "jump", "jump-up", "jump-to", "jump-off", "jump-in", "jump-on", "kick",
		"leap", "shoot", "squeeze-past", "squeeze-through", "swing", "swing across", "swing-across", "swing-on", "tap",
		"tag", "teeth-grip", "tread-softly", "vault", "walk-on", "walk-across"),
	COOKING(SpriteID.SKILL_COOKING, "cook"),
	CRAFTING(SpriteID.SKILL_CRAFTING, "spin"),
	CONSTRUCTION(SpriteID.SKILL_CONSTRUCTION, "build", "remove"),
	FARMING(SpriteID.SKILL_FARMING, "check-health"), // `pick` interferes with non-farming crops such as Wheat
	FIREMAKING(SpriteID.SKILL_FIREMAKING, "light", "feed"),
	FISHING(SpriteID.SKILL_FISHING, "net", "bait", "lure", "small net", "harpoon", "cage", "big net", "use-rod", "fish"),
	HERBLORE(SpriteID.SKILL_HERBLORE, "clean"),
	HUNTER(SpriteID.SKILL_HUNTER,"catch"),
	MAGIC(SpriteID.SKILL_MAGIC, "spellbook","break"), // `venerate` interferes with the Dark Altar's RC use
	MINING(SpriteID.SKILL_MINING, "mine"),
	PRAYER(SpriteID.SKILL_PRAYER, "pray", "bury", "pray-at"),
	RUNECRAFTING(SpriteID.SKILL_RUNECRAFT, "craft-rune"),
	SMITHING(SpriteID.SKILL_SMITHING, "smelt", "smith"),
	THIEVING(SpriteID.SKILL_THIEVING, "steal-from", "pickpocket", "search for traps", "pick-lock"),
	WOODCUTTING(SpriteID.SKILL_WOODCUTTING, "chop down", "chop-down", "chop");

	private String cursor;
	private Integer spriteID;
	private String[] actions;

	ContextualCursor(String cursor_path, String ... actions)
	{
		this.cursor = cursor_path;
		this.actions = actions;
	}

	ContextualCursor(int spriteID, String ... actions)
	{
		this.spriteID = spriteID;
		this.actions = actions;
	}

	private static final Map<String, ContextualCursor> cursorMap = new HashMap<>();

	static
	{
		for (ContextualCursor cursor : values())
		{
			for (String action : cursor.actions)
			{
				cursorMap.put(action, cursor);
			}
		}
	}

	static ContextualCursor get(String action)
	{
		//return cursorMap.get(action.toLowerCase());
		return cursorMap.getOrDefault(action.toLowerCase(), GENERIC);
	}
}

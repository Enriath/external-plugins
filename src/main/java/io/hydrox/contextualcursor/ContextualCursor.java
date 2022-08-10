/*
 * Copyright (c) 2019-2022 Enriath <ikada@protonmail.ch>
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
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ContextualCursor
{
	BLANK("blank"),
	GENERIC("generic"), //Cursor inside background

	BANK("bank", "bank"),
	CLOSE("close", "close"),
	CONFIGURE(1654, "configure", "configuration"), // Wrench sprite
	DRINK("drink", "drink"),
	DROP("drop", "drop", "empty", "deposit"),
	EAT("eat", "eat"),
	ENTER("enter", "climb-into", "enter", "exit", "yanille", "varrock", "seers' village", "camelot",
		"grand exchange", "watchtower", "go-through"),
	EQUIP("equip", "wield", "wear", "equip"),
	EXCHANGE(SpriteID.GE_GUIDE_PRICE, "exchange", "trade", "trade with", "collect"),
	FRIEND(SpriteID.TAB_FRIENDS, "add friend"),
	IGNORE(SpriteID.TAB_IGNORES, "add ignore"),
	IMPOSSIBLE("impossible", "destroy"),
	LADDER("ladder", "climb"),
	LADDER_DOWN("ladder_down", "climb-down", "climb down"),
	LADDER_UP("ladder_up", "climb-up", "climb up"),
	OPEN("open", "open"),
	PICK_UP("pick_up", "take", "withdraw" ,"fill"),
	PLANK("plank", "buy-plank"),
	READ("read", "read", "story"),
	REPORT(SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING, "report"),
	SEARCH("search", "search", "lookup", "examine", "view", "look-inside"),
	TALK("talk", "talk", "talk-to", "talk to"),
	UNTIE("untie", "tether"),
	USE("use", "use"),
	WIKI("wiki", "lookup-entity"),

	// Skills
	AGILITY(SpriteID.SKILL_AGILITY, "balance", "balance-across", "climb-across", "climb-on", "climb-over",
		"cross", "grab", "grapple", "hurdle", "jump", "jump-up", "jump-to", "jump-off", "jump-in", "jump-on", "kick",
		"leap", "shoot", "squeeze-past", "squeeze-through", "swing", "swing across", "swing-across", "swing-on", "tap",
		"tag", "teeth-grip", "tread-softly", "vault", "walk-on", "walk-across", "crawl-through", "jump-over"),
	ATTACK(SpriteID.SKILL_ATTACK, "attack"),
	CONSTRUCTION(SpriteID.SKILL_CONSTRUCTION, "build", "remove"),
	COOKING(SpriteID.SKILL_COOKING, "cook", "churn", "cook-at", "prepare-fish"),
	CRAFTING(SpriteID.SKILL_CRAFTING, "spin"),
	FARMING(SpriteID.SKILL_FARMING, "check-health", "harvest", "rake", "pick", "pick-fruit", "clear", "pay"),
	FIREMAKING(SpriteID.SKILL_FIREMAKING, "light", "feed"),
	FISHING(SpriteID.SKILL_FISHING, "net", "bait", "lure", "small net", "harpoon", "cage", "big net",
		"use-rod", "fish", "take-net"),
	HERBLORE(SpriteID.SKILL_HERBLORE, "clean"),
	HUNTER(SpriteID.SKILL_HUNTER, "catch", "lay", "dismantle", "reset", "check"),
	MAGIC(SpriteID.SKILL_MAGIC, "spellbook", "teleport", "teleport menu"), // `venerate` interferes with the Dark Altar's RC use
	MINING(SpriteID.SKILL_MINING, "mine", "smash-to-bits"),
	PRAYER(SpriteID.SKILL_PRAYER, "pray", "bury", "pray-at", "offer-fish", "scatter"),
	RUNECRAFTING(SpriteID.SKILL_RUNECRAFT, "craft-rune", "imbue"),
	SMITHING(SpriteID.SKILL_SMITHING, "smelt", "smith", "hammer", "refine"),
	SLAYER(SpriteID.SKILL_SLAYER, "assignment"),
	STRENGTH(SpriteID.SKILL_STRENGTH, "bang", "move"),
	THIEVING(SpriteID.SKILL_THIEVING, "steal-from", "pickpocket", "search for traps", "pick-lock"),
	WOODCUTTING(SpriteID.SKILL_WOODCUTTING, "chop down", "chop-down", "chop", "cut", "hack");

	private BufferedImage cursor;
	private Integer spriteID;
	private String[] actions;

	ContextualCursor(String cursor_path, String ... actions)
	{
		this.cursor = ImageUtil.loadImageResource(ContextualCursorPlugin.class, String.format("cursors/%s.png", cursor_path));
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

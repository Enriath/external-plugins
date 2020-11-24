/*
 * Copyright (c) 2020, Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.quickprayerpreview;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.SpriteID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
enum Prayer
{
	THICK_SKIN(0, "Thick Skin", SpriteID.PRAYER_THICK_SKIN),
	BURST_OF_STRENGTH(1, "Burst of Strength", SpriteID.PRAYER_BURST_OF_STRENGTH),
	CLARITY_OF_THOUGHT(2, "Clarity of Thought", SpriteID.PRAYER_CLARITY_OF_THOUGHT),
	SHARP_EYE(18, "Sharp Eye", SpriteID.PRAYER_SHARP_EYE),
	MYSTIC_WILL(19, "Mystic Will", SpriteID.PRAYER_MYSTIC_WILL),
	ROCK_SKIN(3, "Rock Skin", SpriteID.PRAYER_ROCK_SKIN),
	SUPERHUMAN_STRENGTH(4, "Superhuman Strength", SpriteID.PRAYER_SUPERHUMAN_STRENGTH),
	IMPROVED_REFLEXES(5, "Improved Reflexes", SpriteID.PRAYER_IMPROVED_REFLEXES),
	RAPID_RESTORE(6, "Rapid Restore", SpriteID.PRAYER_RAPID_RESTORE),
	RAPID_HEAL(7, "Rapid Heal", SpriteID.PRAYER_RAPID_HEAL),
	PROTECT_ITEM(8, "Protect Item", SpriteID.PRAYER_PROTECT_ITEM),
	HAWK_EYE(20, "Hawk Eye", SpriteID.PRAYER_HAWK_EYE),
	MYSTIC_LORE(21, "Mystic Lore", SpriteID.PRAYER_MYSTIC_LORE),
	STEEL_SKIN(9, "Steel Skin", SpriteID.PRAYER_STEEL_SKIN),
	ULTIMATE_STRENGTH(10, "Ultimate Strength", SpriteID.PRAYER_ULTIMATE_STRENGTH),
	INCREDIBLE_REFLEXES(11, "Incredible reflexes", SpriteID.PRAYER_INCREDIBLE_REFLEXES),
	PROTECT_FROM_MAGIC(12, "protect from magic", SpriteID.PRAYER_PROTECT_FROM_MAGIC),
	PROTECT_FROM_MISSILES(13, "Protect from missiles", SpriteID.PRAYER_PROTECT_FROM_MISSILES),
	PROTECT_FROM_MELEE(14, "Protect from melee", SpriteID.PRAYER_PROTECT_FROM_MELEE),
	EAGLE_EYE(22, "Eagle Eye", SpriteID.PRAYER_EAGLE_EYE),
	MYSTIC_MIGHT(23, "Mystic Might", SpriteID.PRAYER_MYSTIC_MIGHT),
	RETRIBUTION(15, "Retribution", SpriteID.PRAYER_RETRIBUTION),
	REDEMPTION(16, "Redemption", SpriteID.PRAYER_REDEMPTION),
	SMITE(17, "Smite", SpriteID.PRAYER_SMITE),
	PRESERVE(28, "Preserve", SpriteID.PRAYER_PRESERVE),
	CHIVALRY(25, "Chivalry", SpriteID.PRAYER_CHIVALRY),
	PIETY(26, "Piety", SpriteID.PRAYER_PIETY),
	RIGOUR(24, "Rigour", SpriteID.PRAYER_RIGOUR),
	AUGURY(27, "Augury", SpriteID.PRAYER_AUGURY);

	private final int bit;
	private final String name;
	private final int spriteID;

	private static final Map<Integer, Prayer> BITS = new HashMap<>();

	static
	{
		for (Prayer p : values())
		{
			BITS.put(p.bit, p);
		}
	}

	static List<Prayer> fromVarb(int varb)
	{
		final List<Prayer> ret = new ArrayList<>();
		for (int i = 0; i < values().length; i++)
		{
			if ((varb & 0x1) == 1)
			{
				ret.add(BITS.get(i));
			}
			varb = varb >> 1;
		}
		return ret;
	}
}

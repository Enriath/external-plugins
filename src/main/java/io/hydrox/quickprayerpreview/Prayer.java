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
import net.runelite.api.gameval.SpriteID;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
enum Prayer
{
	THICK_SKIN(0, "Thick Skin", SpriteID.Prayeron.THICK_SKIN),
	BURST_OF_STRENGTH(1, "Burst of Strength", SpriteID.Prayeron.BURST_OF_STRENGTH),
	CLARITY_OF_THOUGHT(2, "Clarity of Thought", SpriteID.Prayeron.CLARITY_OF_THOUGHT),
	SHARP_EYE(18, "Sharp Eye", SpriteID.Prayeron.SHARP_EYE),
	MYSTIC_WILL(19, "Mystic Will", SpriteID.Prayeron.MYSTIC_WILL),
	ROCK_SKIN(3, "Rock Skin", SpriteID.Prayeron.ROCK_SKIN),
	SUPERHUMAN_STRENGTH(4, "Superhuman Strength", SpriteID.Prayeron.SUPERHUMAN_STRENGTH),
	IMPROVED_REFLEXES(5, "Improved Reflexes", SpriteID.Prayeron.IMPROVED_REFLEXES),
	RAPID_RESTORE(6, "Rapid Restore", SpriteID.Prayeron.RAPID_RESTORE),
	RAPID_HEAL(7, "Rapid Heal", SpriteID.Prayeron.RAPID_HEAL),
	PROTECT_ITEM(8, "Protect Item", SpriteID.Prayeron.PROTECT_ITEM),
	HAWK_EYE(20, "Hawk Eye", SpriteID.Prayeron.HAWK_EYE),
	MYSTIC_LORE(21, "Mystic Lore", SpriteID.Prayeron.MYSTIC_LORE),
	STEEL_SKIN(9, "Steel Skin", SpriteID.Prayeron.STEEL_SKIN),
	ULTIMATE_STRENGTH(10, "Ultimate Strength", SpriteID.Prayeron.ULTIMATE_STRENGTH),
	INCREDIBLE_REFLEXES(11, "Incredible reflexes", SpriteID.Prayeron.INCREDIBLE_REFLEXES),
	PROTECT_FROM_MAGIC(12, "protect from magic", SpriteID.Prayeron.PROTECT_FROM_MAGIC),
	PROTECT_FROM_MISSILES(13, "Protect from missiles", SpriteID.Prayeron.PROTECT_FROM_MISSILES),
	PROTECT_FROM_MELEE(14, "Protect from melee", SpriteID.Prayeron.PROTECT_FROM_MELEE),
	EAGLE_EYE(22, "Eagle Eye", SpriteID.Prayeron.EAGLE_EYE),
	MYSTIC_MIGHT(23, "Mystic Might", SpriteID.Prayeron.MYSTIC_MIGHT),
	RETRIBUTION(15, "Retribution", SpriteID.Prayeron.RETRIBUTION),
	REDEMPTION(16, "Redemption", SpriteID.Prayeron.REDEMPTION),
	SMITE(17, "Smite", SpriteID.Prayeron.SMITE),
	PRESERVE(28, "Preserve", SpriteID.Prayeron.PRESERVE),
	CHIVALRY(25, "Chivalry", SpriteID.Prayeron.CHIVALRY),
	PIETY(26, "Piety", SpriteID.Prayeron.PIETY),
	RIGOUR(24, "Rigour", SpriteID.Prayeron.RIGOUR),
	AUGURY(27, "Augury", SpriteID.Prayeron.AUGURY),
	// Special unlockable prayers, these override other prayers and share their bit.
	DEADEYE(-1, "Deadeye", SpriteID.Prayeron.DEADEYE),  // 22 Eagle Eye
	MYSTIC_VIGOUR(-1, "Mystic Vigour", SpriteID.Prayeron.MYSTIC_VIGOUR),  // 23 Mystic Might
	;

	private final int bit;
	private final String name;
	private final int spriteID;

	private static final Map<Integer, Prayer> BITS = new HashMap<>();

	static
	{
		for (Prayer p : values())
		{
			if (p.bit == -1) continue;
			BITS.put(p.bit, p);
		}
	}

	static Prayer get(int idx)
	{
		return BITS.get(idx);
	}
}

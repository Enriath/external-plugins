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
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarbitID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
enum Prayer
{
	THICK_SKIN(0, "Thick Skin", SpriteID.Prayeron.THICK_SKIN, InterfaceID.Prayerbook.PRAYER1),
	BURST_OF_STRENGTH(1, "Burst of Strength", SpriteID.Prayeron.BURST_OF_STRENGTH, InterfaceID.Prayerbook.PRAYER2),
	CLARITY_OF_THOUGHT(2, "Clarity of Thought", SpriteID.Prayeron.CLARITY_OF_THOUGHT, InterfaceID.Prayerbook.PRAYER3),
	SHARP_EYE(18, "Sharp Eye", SpriteID.Prayeron.SHARP_EYE, InterfaceID.Prayerbook.PRAYER19),
	MYSTIC_WILL(19, "Mystic Will", SpriteID.Prayeron.MYSTIC_WILL, InterfaceID.Prayerbook.PRAYER20),
	ROCK_SKIN(3, "Rock Skin", SpriteID.Prayeron.ROCK_SKIN, InterfaceID.Prayerbook.PRAYER4),
	SUPERHUMAN_STRENGTH(4, "Superhuman Strength", SpriteID.Prayeron.SUPERHUMAN_STRENGTH, InterfaceID.Prayerbook.PRAYER5),
	IMPROVED_REFLEXES(5, "Improved Reflexes", SpriteID.Prayeron.IMPROVED_REFLEXES, InterfaceID.Prayerbook.PRAYER6),
	RAPID_RESTORE(6, "Rapid Restore", SpriteID.Prayeron.RAPID_RESTORE, InterfaceID.Prayerbook.PRAYER7),
	RAPID_HEAL(7, "Rapid Heal", SpriteID.Prayeron.RAPID_HEAL, InterfaceID.Prayerbook.PRAYER8),
	PROTECT_ITEM(8, "Protect Item", SpriteID.Prayeron.PROTECT_ITEM, InterfaceID.Prayerbook.PRAYER9),
	HAWK_EYE(20, "Hawk Eye", SpriteID.Prayeron.HAWK_EYE, InterfaceID.Prayerbook.PRAYER21),
	MYSTIC_LORE(21, "Mystic Lore", SpriteID.Prayeron.MYSTIC_LORE, InterfaceID.Prayerbook.PRAYER22),
	STEEL_SKIN(9, "Steel Skin", SpriteID.Prayeron.STEEL_SKIN, InterfaceID.Prayerbook.PRAYER10),
	ULTIMATE_STRENGTH(10, "Ultimate Strength", SpriteID.Prayeron.ULTIMATE_STRENGTH, InterfaceID.Prayerbook.PRAYER11),
	INCREDIBLE_REFLEXES(11, "Incredible reflexes", SpriteID.Prayeron.INCREDIBLE_REFLEXES, InterfaceID.Prayerbook.PRAYER12),
	PROTECT_FROM_MAGIC(12, "protect from magic", SpriteID.Prayeron.PROTECT_FROM_MAGIC, InterfaceID.Prayerbook.PRAYER13),
	PROTECT_FROM_MISSILES(13, "Protect from missiles", SpriteID.Prayeron.PROTECT_FROM_MISSILES, InterfaceID.Prayerbook.PRAYER14),
	PROTECT_FROM_MELEE(14, "Protect from melee", SpriteID.Prayeron.PROTECT_FROM_MELEE, InterfaceID.Prayerbook.PRAYER15),
	EAGLE_EYE(22, "Eagle Eye", SpriteID.Prayeron.EAGLE_EYE, InterfaceID.Prayerbook.PRAYER23)
		{
			@Override
			public int getUnlockVarbit()
			{
				return VarbitID.PRAYER_DEADEYE_UNLOCKED;
			}

			@Override
			public Prayer getUnlockPrayer()
			{
				return DEADEYE;
			}
		},
	DEADEYE(-22, "Deadeye", SpriteID.Prayeron.DEADEYE, InterfaceID.Prayerbook.PRAYER23),
	MYSTIC_MIGHT(23, "Mystic Might", SpriteID.Prayeron.MYSTIC_MIGHT, InterfaceID.Prayerbook.PRAYER24)
		{
			@Override
			public int getUnlockVarbit()
			{
				return VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED;
			}

			@Override
			public Prayer getUnlockPrayer()
			{
				return MYSTIC_VIGOUR;
			}
		},
	MYSTIC_VIGOUR(-23, "Mystic Vigour", SpriteID.Prayeron.MYSTIC_VIGOUR, InterfaceID.Prayerbook.PRAYER24),
	RETRIBUTION(15, "Retribution", SpriteID.Prayeron.RETRIBUTION, InterfaceID.Prayerbook.PRAYER16),
	REDEMPTION(16, "Redemption", SpriteID.Prayeron.REDEMPTION, InterfaceID.Prayerbook.PRAYER17),
	SMITE(17, "Smite", SpriteID.Prayeron.SMITE, InterfaceID.Prayerbook.PRAYER18),
	PRESERVE(28, "Preserve", SpriteID.Prayeron.PRESERVE, InterfaceID.Prayerbook.PRAYER29),
	CHIVALRY(25, "Chivalry", SpriteID.Prayeron.CHIVALRY, InterfaceID.Prayerbook.PRAYER26),
	PIETY(26, "Piety", SpriteID.Prayeron.PIETY, InterfaceID.Prayerbook.PRAYER27),
	RIGOUR(24, "Rigour", SpriteID.Prayeron.RIGOUR, InterfaceID.Prayerbook.PRAYER25),
	AUGURY(27, "Augury", SpriteID.Prayeron.AUGURY, InterfaceID.Prayerbook.PRAYER28);

	private final int bit;
	private final String name;
	private final int spriteID;
	private final int interfaceID;
	private final int unlockVarbit = -1;
	private final Prayer unlockPrayer = null;

	private static final Map<Integer, Prayer> BITS = new HashMap<>();
	private static final int NUMBER_OF_PRAYERS = 29;

	static
	{
		for (Prayer p : values())
		{
			BITS.put(p.bit, p);
		}
	}

	static List<Prayer> fromVarb(int varb, Client client)
	{
		final List<Prayer> ret = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_PRAYERS; i++)
		{
			if ((varb & 0x1) == 1)
			{
				Prayer p = BITS.get(i);
				if (p.getUnlockVarbit() >= 0 && isUnlocked(client, p.getUnlockVarbit()) && p.getUnlockPrayer() != null)
				{
					p = p.getUnlockPrayer();
				}
				ret.add(p);
			}
			varb = varb >> 1;
		}
		return ret;
	}

	static boolean isUnlocked(Client client, int varb)
	{
		return client.getVarbitValue(varb) != 0;
	}
}

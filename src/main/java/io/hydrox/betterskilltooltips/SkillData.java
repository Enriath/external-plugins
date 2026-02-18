/*
 * Copyright (c) 2021-2023, Enriath <ikada@protonmail.ch>
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
package io.hydrox.betterskilltooltips;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
enum SkillData
{
	ATTACK(Skill.ATTACK, VarPlayerID.XPDROPS_ATTACK_START, VarPlayerID.XPDROPS_ATTACK_END),
	STRENGTH(Skill.STRENGTH, VarPlayerID.XPDROPS_STRENGTH_START, VarPlayerID.XPDROPS_STRENGTH_END),
	DEFENCE(Skill.DEFENCE, VarPlayerID.XPDROPS_DEFENCE_START, VarPlayerID.XPDROPS_DEFENCE_END),
	RANGED(Skill.RANGED, VarPlayerID.XPDROPS_RANGED_START, VarPlayerID.XPDROPS_RANGED_END),
	PRAYER(Skill.PRAYER, VarPlayerID.XPDROPS_PRAYER_START, VarPlayerID.XPDROPS_PRAYER_END),
	MAGIC(Skill.MAGIC, VarPlayerID.XPDROPS_MAGIC_START, VarPlayerID.XPDROPS_MAGIC_END),
	RUNECRAFT(Skill.RUNECRAFT, VarPlayerID.XPDROPS_RUNECRAFT_START, VarPlayerID.XPDROPS_RUNECRAFT_END),
	CONSTRUCTION(Skill.CONSTRUCTION, VarPlayerID.XPDROPS_CONSTRUCTION_START, VarPlayerID.XPDROPS_CONSTRUCTION_END),
	HITPOINTS(Skill.HITPOINTS, VarPlayerID.XPDROPS_HITPOINTS_START, VarPlayerID.XPDROPS_HITPOINTS_END),
	AGILITY(Skill.AGILITY, VarPlayerID.XPDROPS_AGILITY_START, VarPlayerID.XPDROPS_AGILITY_END),
	HERBLORE(Skill.HERBLORE, VarPlayerID.XPDROPS_HERBLORE_START, VarPlayerID.XPDROPS_HERBLORE_END),
	THIEVING(Skill.THIEVING, VarPlayerID.XPDROPS_THIEVING_START, VarPlayerID.XPDROPS_THIEVING_END),
	CRAFTING(Skill.CRAFTING, VarPlayerID.XPDROPS_CRAFTING_START, VarPlayerID.XPDROPS_CRAFTING_END),
	FLETCHING(Skill.FLETCHING, VarPlayerID.XPDROPS_FLETCHING_START, VarPlayerID.XPDROPS_FLETCHING_END),
	SLAYER(Skill.SLAYER, VarPlayerID.XPDROPS_SLAYER_START, VarPlayerID.XPDROPS_SLAYER_END),
	HUNTER(Skill.HUNTER, VarPlayerID.XPDROPS_HUNTER_START, VarPlayerID.XPDROPS_HUNTER_END),
	MINING(Skill.MINING, VarPlayerID.XPDROPS_MINING_START, VarPlayerID.XPDROPS_MINING_END),
	SMITHING(Skill.SMITHING, VarPlayerID.XPDROPS_SMITHING_START, VarPlayerID.XPDROPS_SMITHING_END),
	FISHING(Skill.FISHING, VarPlayerID.XPDROPS_FISHING_START, VarPlayerID.XPDROPS_FISHING_END),
	COOKING(Skill.COOKING, VarPlayerID.XPDROPS_COOKING_START, VarPlayerID.XPDROPS_COOKING_END),
	FIREMAKING(Skill.FIREMAKING, VarPlayerID.XPDROPS_FIREMAKING_START, VarPlayerID.XPDROPS_FIREMAKING_END),
	WOODCUTTING(Skill.WOODCUTTING, VarPlayerID.XPDROPS_WOODCUTTING_START, VarPlayerID.XPDROPS_WOODCUTTING_END),
	FARMING(Skill.FARMING, VarPlayerID.XPDROPS_FARMING_START, VarPlayerID.XPDROPS_FARMING_END),
	SAILING(Skill.SAILING, VarPlayerID.XPDROPS_SAILING_START, VarPlayerID.XPDROPS_SAILING_END),
	OVERALL(null, 1228, 1252)
	{
		@Override
		String getName()
		{
			return "Total";
		}
	};

	private final Skill skill;
	private final int goalStartVarp;
	private final int goalEndVarp;

	String getName()
	{
		return skill.getName();
	}

	int getChildID()
	{
		return ordinal() + 1;
	}

	private static final Map<String, SkillData> NAME_MAP = new HashMap<>();

	static
	{
		for (SkillData sd : values())
		{
			NAME_MAP.put(sd.getName(), sd);
		}
	}

	static SkillData fromName(String name)
	{
		return NAME_MAP.getOrDefault(name, null);
	}

	static int count()
	{
		return values().length - 1;
	}
}

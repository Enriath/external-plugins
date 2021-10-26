/*
 * Copyright (c) 2021, Hydrox6 <ikada@protonmail.ch>
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
import net.runelite.api.VarPlayer;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
enum SkillData
{
	ATTACK(Skill.ATTACK, VarPlayer.ATTACK_GOAL_START, VarPlayer.ATTACK_GOAL_END),
	STRENGTH(Skill.STRENGTH, VarPlayer.STRENGTH_GOAL_START, VarPlayer.STRENGTH_GOAL_END),
	DEFENCE(Skill.DEFENCE, VarPlayer.DEFENCE_GOAL_START, VarPlayer.DEFENCE_GOAL_END),
	RANGED(Skill.RANGED, VarPlayer.RANGED_GOAL_START, VarPlayer.RANGED_GOAL_END),
	PRAYER(Skill.PRAYER, VarPlayer.PRAYER_GOAL_START, VarPlayer.PRAYER_GOAL_END),
	MAGIC(Skill.MAGIC, VarPlayer.MAGIC_GOAL_START, VarPlayer.MAGIC_GOAL_END),
	RUNECRAFT(Skill.RUNECRAFT, VarPlayer.RUNECRAFT_GOAL_START, VarPlayer.RUNECRAFT_GOAL_END),
	CONSTRUCTION(Skill.CONSTRUCTION, VarPlayer.CONSTRUCTION_GOAL_START, VarPlayer.CONSTRUCTION_GOAL_END),
	HITPOINTS(Skill.HITPOINTS, VarPlayer.HITPOINTS_GOAL_START, VarPlayer.HITPOINTS_GOAL_END),
	AGILITY(Skill.AGILITY, VarPlayer.AGILITY_GOAL_START, VarPlayer.AGILITY_GOAL_END),
	HERBLORE(Skill.HERBLORE, VarPlayer.HERBLORE_GOAL_START, VarPlayer.HERBLORE_GOAL_END),
	THIEVING(Skill.THIEVING, VarPlayer.THIEVING_GOAL_START, VarPlayer.THIEVING_GOAL_END),
	CRAFTING(Skill.CRAFTING, VarPlayer.CRAFTING_GOAL_START, VarPlayer.CRAFTING_GOAL_END),
	FLETCHING(Skill.FLETCHING, VarPlayer.FLETCHING_GOAL_START, VarPlayer.FLETCHING_GOAL_END),
	SLAYER(Skill.SLAYER, VarPlayer.SLAYER_GOAL_START, VarPlayer.SLAYER_GOAL_END),
	HUNTER(Skill.HUNTER, VarPlayer.HUNTER_GOAL_START, VarPlayer.HUNTER_GOAL_END),
	MINING(Skill.MINING, VarPlayer.MINING_GOAL_START, VarPlayer.MINING_GOAL_END),
	SMITHING(Skill.SMITHING, VarPlayer.SMITHING_GOAL_START, VarPlayer.SMITHING_GOAL_END),
	FISHING(Skill.FISHING, VarPlayer.FISHING_GOAL_START, VarPlayer.FISHING_GOAL_END),
	COOKING(Skill.COOKING, VarPlayer.COOKING_GOAL_START, VarPlayer.COOKING_GOAL_END),
	FIREMAKING(Skill.FIREMAKING, VarPlayer.FIREMAKING_GOAL_START, VarPlayer.FIREMAKING_GOAL_END),
	WOODCUTTING(Skill.WOODCUTTING, VarPlayer.WOODCUTTING_GOAL_START, VarPlayer.WOODCUTTING_GOAL_END),
	FARMING(Skill.FARMING, VarPlayer.FARMING_GOAL_START, VarPlayer.FARMING_GOAL_END);

	private final Skill skill;
	private final VarPlayer goalStartVarp;
	private final VarPlayer goalEndVarp;

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
}

/*
 * Copyright (c) 2022 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.shatteredrelicxp;

import com.google.common.collect.Sets;
import static io.hydrox.shatteredrelicxp.ShatteredFragment.*;
import lombok.Data;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum SetEffect
{
	ABSOLUTE_UNIT("Absolute Unit",
		new Effect[]{
			new Effect(2, "30% less damage from monsters. 50% post-reduction recoil effect"),
			new Effect(3, "50% less damage from monsters. 100% post-reduction recoil effect")},
		DRAGON_ON_A_BIT, RUNE_ESCAPE, SUPERIOR_TRACKING, ARMADYLEAN_DECREE, DIVINE_RESTORATION, TACTICAL_DUELIST, SARADOMINIST_DEFENCE, VENOMASTER),
	THE_ALCHEMIST("The Alchemist",
		new Effect(3, "Process all food, wine, grimy herbs, & potions at once."),
		GOLDEN_BRICK_ROAD, DINE_AND_DASH, SLAY_ALL_DAY, CERTIFIED_FARMER, HOMEWRECKER, MIXOLOGIST, JUST_DRUID),
	CHAIN_MAGIC("Chain Magic",
		new Effect[]{
			new Effect(2, "30% chance to hit twice with Magic"),
			new Effect(3, "60% chance to hit twice with Magic")},
		THRALL_DAMAGE, UNHOLY_WIZARD, ZAMORAKIAN_SIGHT, MOTHERS_MAGIC_FOSSILS, DEEPER_POCKETS, ROOTY_TOOTY_2X_RUNEYS, HOT_ON_THE_TRAIL),
	THE_CRAFTSMAN("The Craftsman",
		new Effect(3, "Most actions relating to smithing, crafting, & fletching are done all at once."),
		RUMPLE_BOW_STRING, DRAGON_ON_A_BIT, IMCANDOS_APPRENTICE, GRAVE_ROBBER, PROFLECTHIONAL, PRO_TIPS),
	DOUBLE_TAP("Double Tap",
		new Effect[]{
			new Effect(2, "30% chance to hit twice with Ranged"),
			new Effect(3, "60% chance to hit twice with Ranged")},
		SMITHING_DOUBLE, RUMPLE_BOW_STRING, ROGUES_CHOMPY_FARM, UNHOLY_RANGER, ARMADYLEAN_DECREE, CHINCHONKERS, PRO_TIPS),
	DRAKANS_TOUCH("Drakan's Touch",
		new Effect[]{
			new Effect(2, "50% chance to heal 5% of damage dealt"),
			new Effect(3, "50% chance to heal 10% of damage dealt")},
		UNHOLY_WARRIOR, ZAMORAKIAN_SIGHT, CLUED_IN, UNHOLY_RANGER, PRAYING_RESPECTS, LARGER_RECHARGER, SPECIAL_DISCOUNT),
	ENDLESS_KNOWLEDGE("Endless Knowledge",
		new Effect(3, "Swap spellbook anywhere with the Arcane grimoire"),
		ARCANE_CONDUIT, THRALL_DAMAGE, ENCHANTED_JEWELER, ALCHEMANIAC, IMCANDOS_APPRENTICE, PLANK_STRETCHER, MOTHERS_MAGIC_FOSSILS),
	FAST_METABOLISM("Fast Metabolism",
		new Effect(2, "4x faster health regeneration"),
		GRAVE_ROBBER, ROCK_SOLID, BANDOSIAN_MIGHT, LARGER_RECHARGER, HOT_ON_THE_TRAIL, VENOMASTER),
	GREEDY_GATHERER("Greedy Gatherer",
		new Effect[]{
			new Effect(2, "2x resources from Fishing, Woodcutting, Mining, & Farming"),
			new Effect(3, "3x resources from Fishing, Woodcutting, Mining, & Farming")},
		MESSAGE_IN_A_BOTTLE, MOLTEN_MINER, ROCK_SOLID, CERTIFIED_FARMER, CHEFS_CATCH, SLASH_AND_BURN, JUST_DRUID),
	KNIFES_EDGE("Knife's Edge",
		new Effect[]{
			new Effect(2, "+0.3% damage for each hitpoint missing"),
			new Effect(3, "+0.6% damage for each hitpoint missin")},
		BARBARIAN_PEST_WARS, MESSAGE_IN_A_BOTTLE, SLAY_ALL_DAY, BOTTOMLESS_QUIVER, LIVIN_ON_A_PRAYER, PRAYING_RESPECTS, UNHOLY_WARRIOR, SARADOMINIST_DEFENCE),
	LAST_RECALL("Last Recall",
		new Effect(4, "Teleport back to where you teleported from with the Crystal of Memories"),
		ENCHANTED_JEWELER, RUNE_ESCAPE, CLUED_IN, SLAY_N_PAY, SUPERIOR_TRACKING, CHINCHONKERS, HOMEWRECKER, PROFLECTHIONAL, ROOTY_TOOTY_2X_RUNEYS, SMOOTH_CRIMINAL),
	PERSONAL_BANKER("Personal Banker",
		new Effect[]{
			new Effect(2, "50% chance to bank resources from Fishing, Woodcutting, Mining, & Farming"),
			new Effect(3, "100% chance to bank resources from Fishing, Woodcutting, Mining, & Farming")},
		SMITHING_DOUBLE, ALCHEMANIAC, MOLTEN_MINER, SEEDY_BUSINESS, CATCH_OF_THE_DAY, DEEPER_POCKETS),
	TRAILBLAZER("Trailblazer",
		new Effect(3, "Teleport to any waystone with the Portable waystone"),
		ARCANE_CONDUIT, GOLDEN_BRICK_ROAD, BOTTOMLESS_QUIVER, SEEDY_BUSINESS, CHEFS_CATCH, SMOOTH_CRIMINAL, UNHOLY_WARRIOR),
	TWIN_STRIKES("Twin Strikes",
		new Effect[]{
			new Effect(2, "30% chance to hit twice with Melee"),
			new Effect(3, "60% chance to hit twice with Melee")},
		BARBARIAN_PEST_WARS, SLAY_N_PAY, DIVINE_RESTORATION, LIVIN_ON_A_PRAYER, TACTICAL_DUELIST, BANDOSIAN_MIGHT, SPECIAL_DISCOUNT),
	UNCHAINED_TALENT("Unchained Talent",
		new Effect(3, "Non-combat skills have a permanent +8 boost"),
		PLANK_STRETCHER, ROGUES_CHOMPY_FARM, DINE_AND_DASH, CATCH_OF_THE_DAY, SLASH_AND_BURN, MIXOLOGIST);

	@Data
	static class Effect
	{
		private final int relicsRequired;
		private final String tooltip;
	}

	private final String name;
	private final Effect[] effects;
	private final ShatteredFragment[] relics;

	SetEffect(String name, Effect effect, ShatteredFragment... relics)
	{
		this.name = name;
		this.effects = new Effect[]{effect};
		this.relics = relics;
	}

	SetEffect(String name, Effect[] effects, ShatteredFragment... relics)
	{
		this.name = name;
		this.effects = effects;
		this.relics = relics;
	}

	Effect getEffect(int fragmentCount)
	{
		Effect last = null;
		for (Effect effect : effects)
		{
			if (effect.relicsRequired > fragmentCount)
			{
				return last;
			}
			last = effect;
		}
		return last;
	}

	private static final Map<ShatteredFragment, Set<SetEffect>> SET_EFFECTS = new HashMap<>();
	private static final Map<String, SetEffect> NAME_TO_SET = new HashMap<>();

	static SetEffect byName(String name)
	{
		return NAME_TO_SET.getOrDefault(name, null);
	}

	static Set<SetEffect> getEffects(ShatteredFragment relic)
	{
		return SET_EFFECTS.getOrDefault(relic, new HashSet<>());
	}

	static
	{
		for (SetEffect s : values())
		{
			NAME_TO_SET.put(s.name, s);
			for (ShatteredFragment f : s.relics)
			{
				if (SET_EFFECTS.containsKey(f))
				{
					SET_EFFECTS.get(f).add(s);
				}
				else
				{
					SET_EFFECTS.put(f, Sets.newHashSet(s));
				}
			}
		}
	}
}

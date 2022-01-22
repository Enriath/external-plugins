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

import lombok.Getter;
import net.runelite.api.Client;
import java.util.Arrays;

public enum ShatteredFragment
{
	UNHOLY_WARRIOR("+%s%% Melee accuracy at 0 prayer", new String[]{"12", "20", "30"}),
	TACTICAL_DUELIST("%s%% chance to save Melee weapon charges", new String[]{"20", "40", "80"}),
	UNHOLY_RANGER("+%s%% Ranged accuracy at 0 prayer", new String[]{"30", "60", "100"}),
	BOTTOMLESS_QUIVER("%s%% chance to save ammo & Ranged charges", new String[]{"40", "60", "90"}),
	UNHOLY_WIZARD("+%s%% Melee accuracy at 0 prayer", new String[]{"40", "70", "125"}),
	ARCANE_CONDUIT("%s%% chance to save runes & Magic charges", new String[]{"40", "60", "90"}),
	THRALL_DAMAGE("%sx max hit for Thralls", new String[]{"2", "3", "4"}),
	LIVIN_ON_A_PRAYER("%s%% slower Prayer drain", new String[]{"15", "30", "60"}),
	DIVINE_RESTORATION("+1 Prayer point every %s", new String[]{"15s", "9s", "3.6s"}),
	PRAYING_RESPECTS("Auto-bury bones & scatter ashes for %s xp", new String[]{"half", "normal", "double"}),
	LARGER_RECHARGER("+10% special attack every %s", new String[]{"25s", "20s", "10s"}),
	SPECIAL_DISCOUNT("Special attacks cost a maximum of %s%% energy", new String[]{"50", "40", "25"}),
	VENOMASTER("Chance to apply %s poison with attacks%s", new String[]{"3", "5", "7"}, new String[]{"", ". Poison immunity", ". Poison & venom immunity"}),
	SLAY_ALL_DAY("Heal for %s each slayer kill", new String[]{"1", "2", "3"}),
	SUPERIOR_TRACKING("1/%s chance for Superior slayer monsters", new String[]{"150", "100", "30"}),
	SLAY_N_PAY("+%s%% Slayer points from tasks", new String[]{"10", "20", "50"}),
	BANDOSIAN_MIGHT("+%s damage per attack speed (with 4 Bandos items)", new String[]{"1", "2", "4"}),
	ARMADYLEAN_DECREE("+%s%% bolt proc chance (with 4 Armadyl items)", new String[]{"25", "50", "100"}),
	ZAMORAKIAN_SIGHT("+%s%% Magic accuracy (with 4 Zamorak items)", new String[]{"50", "125", "250"}),
	SARADOMINIST_DEFENCE("+%s max hit if you've been hit in the last 3.6 seconds (with 4 Saradomin items)", new String[]{"2", "5", "10"}),
	CHEFS_CATCH("%s%% chance to cook caught fish", new String[]{"20", "35", "50"}),
	CATCH_OF_THE_DAY("1/%s chance to roll RDT while fishing", new String[]{"300", "200", "80"}),
	SMOOTH_CRIMINAL("+%s%% pickpocket chance%s", new String[]{"15", "25", "50"}, new String[]{"", ". No stun damage", ". No stun damage"}),
	DEEPER_POCKETS("%s%% chance of double pickpocket loot", new String[]{"20", "50", "100"}),
	SLASH_AND_BURN("%s%% chance to burn chopped logs", new String[]{"20", "35", "50"}),
	HOMEWRECKER("Auto-bank birds nests from Woodcutting. %sx chance to get bird nests", new String[]{"2", "3", "4"}),
	HOT_ON_THE_TRAIL("%s%% chance to get clue scrolls from lighting fires", new String[]{"2", "5", "10"}),
	PLANK_STRETCHER("%s%% chance to to save all planks in Construction", new String[]{"10", "20", "50"}),
	ROCK_SOLID("%s%% chance to not deplete Iron, Sandstone, or Granite rocks", new String[]{"25", "50", "75"}),
	MOLTEN_MINER("%s%% chance to smelt ore while mining", new String[]{"20", "50", "100"}),
	SMITHING_DOUBLE("%s%% chance to make double items while Smithing", new String[]{"5", "15", "30"}),
	RUMPLE_BOW_STRING("Spin Flax processes %s flax per cast", new String[]{"10", "15", "27"}),
	DRAGON_ON_A_BIT("%s%% chance to save dragonhide while Crafting", new String[]{"10", "20", "40"}),
	IMCANDOS_APPRENTICE("%s%% to make double jewelery", new String[]{"20", "50", "75"}),
	ENCHANTED_JEWELER("Enchant %s jewelery items per cast", new String[]{"5", "10", "25"}),
	ALCHEMANIAC("+%s%% gold when casting High Alchemy", new String[]{"15", "30", "50"}),
	PROFLECTHIONAL("+%s%% xp from fletching & stringing bows", new String[]{"30", "50", "100"}),
	PRO_TIPS("+%s%% bolt tips per gem", new String[]{"30", "50", "100"}),
	CHINCHONKERS("+%s%% more xp when catching Chinchompas%s", new String[]{"50", "100", "100"}, new String[]{"", "", ". Catch 2 at a time"}),
	DINE_AND_DASH("%s%% chance to get double food while Cooking", new String[]{"10", "20", "50"}),
	CERTIFIED_FARMER("%s%% chance to get double produce from Farming. Produce is always noted", new String[]{"25", "50", "100"}),
	SEEDY_BUSINESS("%s%% chance to not use seeds when planting", new String[]{"10", "25", "50"}),
	MIXOLOGIST("%s%% to make 4-dose potions. %s%% chance to save potion secondaries", new String[]{"25", "50", "100"}, new String[]{"25", "50", "100"}),
	JUST_DRUID("+%s%% xp from cleaning herbs", new String[]{"10", "20", "40"}),
	GOLDEN_BRICK_ROAD("Marks of Grace spawn with %s coins", new String[]{"4,000", "7,000", "15,000"}),
	GRAVE_ROBBER("+%s%% Hallowed marks", new String[]{"50", "150", "300"}),
	ROOTY_TOOTY_2X_RUNEYS("%s%% chance for 2x runes (stacks with normal bonus runes)", new String[]{"20", "40", "80"}),
	RUNE_ESCAPE("No %s runes at ZMI", new String[]{"mind", "mind or body", "mind, body, or elemental"}),
	CLUED_IN("1/%s clue drop chance for enemies that drop them", new String[]{"30", "25", "15"}),
	MESSAGE_IN_A_BOTTLE("%sx more clues from skilling", new String[]{"3", "5", "10"}),
	BARBARIAN_PEST_WARS("+%s%% points/tokens from Barbarian Assault, Pest Control, & Soul Wars", new String[]{"100", "200", "300"}),
	ROGUES_CHOMPY_FARM("+%s%% Tithe Farm points, +%s%% chance to crack Rogue's Den maze safes, %sx Chompy kills", new String[]{"100", "200", "300"}, new String[]{"10", "20", "40"}, new String[]{"2", "3", "4"}),
	MOTHERS_MAGIC_FOSSILS("+%s%% chance to get Golden Nuggets and Fossils. +%s%% Mage Arena points", new String[]{"100", "200", "300"}, new String[]{"100", "200", "300"});

	@Getter
	private final String tooltipFormat;
	@Getter
	private final String[][] substitutions;

	ShatteredFragment(String tooltipFormat, String[]... substitutions)
	{
		this.tooltipFormat = tooltipFormat;
		this.substitutions = substitutions;
	}

	private static final int VARBIT_BASE = 13403;

	int getVarb()
	{
		return VARBIT_BASE + ordinal();
	}

	int getXp(Client client)
	{
		return Math.min(client.getVarbitValue(getVarb()), ShatteredRelicXPPlugin.TIER_3_XP);
	}

	int getNumberOfSubstitutions()
	{
		return substitutions == null ? 0 : substitutions.length;
	}

	String buildTooltip(int tier)
	{
		if (substitutions == null)
		{
			return tooltipFormat;
		}
		return String.format(tooltipFormat, Arrays.stream(substitutions).map(subs -> subs[tier]).toArray());
	}

	static ShatteredFragment byOrdinal(int value)
	{
		return values()[value - 1];
	}
}

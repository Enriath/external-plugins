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

import lombok.AllArgsConstructor;
import net.runelite.api.SpriteID;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum SpellSprite
{
	// Standard
	WIND_STRIKE("Wind Strike", SpriteID.SPELL_WIND_STRIKE),
	CONFUSE("Confuse", SpriteID.SPELL_CONFUSE),
	WATER_STRIKE("Water Strike", SpriteID.SPELL_WATER_STRIKE),
	LVL_1_ENCHANT("Lvl-1 Enchant", SpriteID.SPELL_LVL_1_ENCHANT),
	EARTH_STRIKE("Earth Strike", SpriteID.SPELL_EARTH_STRIKE),
	WEAKEN("Weaken", SpriteID.SPELL_WEAKEN),
	FIRE_STRIKE("Fire Strike", SpriteID.SPELL_FIRE_STRIKE),
	WIND_BOLT("Wind Bolt", SpriteID.SPELL_WIND_BOLT),
	CURSE("Curse", SpriteID.SPELL_CURSE),
	BIND("Bind", SpriteID.SPELL_BIND),
	LOW_LEVEL_ALCHEMY("Low Level Alchemy", SpriteID.SPELL_LOW_LEVEL_ALCHEMY),
	WATER_BOLT("Water Bolt", SpriteID.SPELL_WATER_BOLT),
	LVL_2_ENCHANT("Lvl-2 Enchant", SpriteID.SPELL_LVL_2_ENCHANT),
	EARTH_BOLT("Earth Bolt", SpriteID.SPELL_EARTH_BOLT),
	TELEKINETIC_GRAB("Telekinetic Grab", SpriteID.SPELL_TELEKINETIC_GRAB),
	FIRE_BOLT("Fire Bolt", SpriteID.SPELL_FIRE_BOLT),
	CRUMBLE_UNDEAD("Crumble Undead", SpriteID.SPELL_CRUMBLE_UNDEAD),
	WIND_BLAST("Wind Blast", SpriteID.SPELL_WIND_BLAST),
	SUPERHEAT_ITEM("Superheat Item", SpriteID.SPELL_SUPERHEAT_ITEM),
	WATER_BLAST("Water Blast", SpriteID.SPELL_WATER_BLAST),
	LVL_3_ENCHANT("Lvl-3 Enchant", SpriteID.SPELL_LVL_3_ENCHANT),
	IBAN_BLAST("Iban Blast", SpriteID.SPELL_IBAN_BLAST),
	SNARE("Snare", SpriteID.SPELL_SNARE),
	MAGIC_DART("Magic Dart", SpriteID.SPELL_MAGIC_DART),
	EARTH_BLAST("Earth Blast", SpriteID.SPELL_EARTH_BLAST),
	HIGH_LEVEL_ALCHEMY("High Level Alchemy", SpriteID.SPELL_HIGH_LEVEL_ALCHEMY),
	CHARGE_WATER_ORB("Charge Water Orb", SpriteID.SPELL_CHARGE_WATER_ORB),
	LVL_4_ENCHANT("Lvl-4 Enchant", SpriteID.SPELL_LVL_4_ENCHANT),
	FIRE_BLAST("Fire Blast", SpriteID.SPELL_FIRE_BLAST),
	CHARGE_EARTH_ORB("Charge Earth Orb", SpriteID.SPELL_CHARGE_EARTH_ORB),
	SARADOMIN_STRIKE("Saradomin Strike", SpriteID.SPELL_SARADOMIN_STRIKE),
	CLAWS_OF_GUTHIX("Claws of Guthix", SpriteID.SPELL_CLAWS_OF_GUTHIC),
	FLAMES_OF_ZAMORAK("Flames of Zamorak", SpriteID.SPELL_FLAMES_OF_ZAMORAK),
	WIND_WAVE("Wind Wave", SpriteID.SPELL_WIND_WAVE),
	CHARGE_FIRE_ORB("Charge Fire Orb", SpriteID.SPELL_CHARGE_FIRE_ORB),
	WATER_WAVE("Water Wave", SpriteID.SPELL_WATER_WAVE),
	CHARGE_AIR_ORB("Charge Air Orb", SpriteID.SPELL_CHARGE_AIR_ORB),
	VULNERABILITY("Vulnerability", SpriteID.SPELL_VULNERABILITY),
	LVL_5_ENCHANT("Lvl-5 Enchant", SpriteID.SPELL_LVL_5_ENCHANT),
	EARTH_WAVE("Earth Wave", SpriteID.SPELL_EARTH_WAVE),
	ENFEEBLE("Enfeeble", SpriteID.SPELL_ENFEEBLE),
	TELEOTHER_LUMBRIDGE("Teleother Lumbridge", SpriteID.SPELL_TELEOTHER_LUMBRIDGE),
	FIRE_WAVE("Fire Wave", SpriteID.SPELL_FIRE_WAVE),
	ENTANGLE("Entangle", SpriteID.SPELL_ENTANGLE),
	STUN("Stun", SpriteID.SPELL_STUN),
	WIND_SURGE("Wind Surge", SpriteID.SPELL_WIND_SURGE),
	TELEOTHER_FALADOR("Teleother Falador", SpriteID.SPELL_TELEOTHER_FALADOR),
	WATER_SURGE("Water Surge", SpriteID.SPELL_WATER_SURGE),
	TELE_BLOCK("Tele Block", SpriteID.SPELL_TELE_BLOCK),
	LVL_6_ENCHANT("Lvl-6 Enchant", SpriteID.SPELL_LVL_6_ENCHANT),
	TELEOTHER_CAMELOT("Teleother Camelot", SpriteID.SPELL_TELEOTHER_CAMELOT),
	EARTH_SURGE("Earth Surge", SpriteID.SPELL_EARTH_SURGE),
	LVL_7_ENCHANT("Lvl-7 Enchant", SpriteID.SPELL_LVL_7_ENCHANT),
	FIRE_SURGE("Fire Surge", SpriteID.SPELL_FIRE_SURGE),
	// Ancients
	SMOKE_RUSH("Smoke Rush", SpriteID.SPELL_SMOKE_RUSH),
	SHADOW_RUSH("Shadow Rush", SpriteID.SPELL_SHADOW_RUSH),
	BLOOD_RUSH("Blood Rush", SpriteID.SPELL_BLOOD_RUSH),
	ICE_RUSH("Ice Rush", SpriteID.SPELL_ICE_RUSH),
	SMOKE_BURST("Smoke Burst", SpriteID.SPELL_SMOKE_BURST),
	SHADOW_BURST("Shadow Burst", SpriteID.SPELL_SHADOW_BURST),
	BLOOD_BURST("Blood Burst", SpriteID.SPELL_BLOOD_BURST),
	ICE_BURST("Ice Burst", SpriteID.SPELL_ICE_BURST),
	SMOKE_BLITZ("Smoke Blitz", SpriteID.SPELL_SMOKE_BLITZ),
	SHADOW_BLITZ("Shadow Blitz", SpriteID.SPELL_SHADOW_BLITZ),
	BLOOD_BLITZ("Blood Blitz", SpriteID.SPELL_BLOOD_BLITZ),
	ICE_BLITZ("Ice Blitz", SpriteID.SPELL_ICE_BLITZ),
	SMOKE_BARRAGE("Smoke Barrage", SpriteID.SPELL_SMOKE_BARRAGE),
	SHADOW_BARRAGE("Shadow Barrage", SpriteID.SPELL_SHADOW_BARRAGE),
	BLOOD_BARRAGE("Blood Barrage", SpriteID.SPELL_BLOOD_BARRAGE),
	ICE_BARRAGE("Ice Barrage", SpriteID.SPELL_ICE_BARRAGE),
	// Lunars
	CURE_PLANT("Cure Plant", SpriteID.SPELL_CURE_PLANT),
	MONSTER_EXAMINE("Monster Examine", SpriteID.SPELL_MONSTER_EXAMINE),
	STAT_SPY("Stat Spy", SpriteID.SPELL_STAT_SPY),
	FERTILE_SOIL("Fertile Soil", SpriteID.SPELL_FERTILE_SOIL),
	PLANK_MAKE("Plank Make", SpriteID.SPELL_PLANK_MAKE),
	CURE_OTHER("Cure Other", SpriteID.SPELL_CURE_OTHER),
	STAT_RESTORE_POT_SHARE("Stat Restore Pot Share", SpriteID.SPELL_STAT_RESTORE_POT_SHARE),
	BOOST_POTION_SHARE("Boost Potion Share", SpriteID.SPELL_BOOST_POTION_SHARE),
	ENERGY_TRANSFER("Energy Transfer", SpriteID.SPELL_ENERGY_TRANSFER),
	HEAL_OTHER("Heal Other", SpriteID.SPELL_HEAL_OTHER),
	VENGEANCE_OTHER("Vengeance Other", SpriteID.SPELL_VENGEANCE_OTHER),
	// Arceuus
	REANIMATE_GOBLIN("Reanimate Goblin", SpriteID.SPELL_REANIMATE_GOBLIN),
	REANIMATE_MONKEY("Reanimate Monkey", SpriteID.SPELL_REANIMATE_MONKEY),
	REANIMATE_IMP("Reanimate Imp", SpriteID.SPELL_REANIMATE_IMP),
	REANIMATE_MINOTAUR("Reanimate Minotaur", SpriteID.SPELL_REANIMATE_MINOTAUR),
	REANIMATE_SCORPION("Reanimate Scorpion", SpriteID.SPELL_REANIMATE_SCORPION),
	REANIMATE_BEAR("Reanimate Bear", SpriteID.SPELL_REANIMATE_BEAR),
	REANIMATE_UNICORN("Reanimate Unicorn", SpriteID.SPELL_REANIMATE_UNICORN),
	REANIMATE_DOG("Reanimate Dog", SpriteID.SPELL_REANIMATE_DOG),
	REANIMATE_CHAOS_DRUID("Reanimate Chaos Druid", SpriteID.SPELL_REANIMATE_CHAOS_DRUID),
	REANIMATE_GIANT("Reanimate Giant", SpriteID.SPELL_REANIMATE_GIANT),
	REANIMATE_OGRE("Reanimate Ogre", SpriteID.SPELL_REANIMATE_OGRE),
	REANIMATE_ELF("Reanimate Elf", SpriteID.SPELL_REANIMATE_ELF),
	REANIMATE_TROLL("Reanimate Troll", SpriteID.SPELL_REANIMATE_TROLL),
	REANIMATE_HORROR("Reanimate Horror", SpriteID.SPELL_REANIMATE_HORROR),
	REANIMATE_KALPHITE("Reanimate Kalphite", SpriteID.SPELL_REANIMATE_KALPHITE),
	REANIMATE_DAGANNOTH("Reanimate Dagannoth", SpriteID.SPELL_REANIMATE_DAGANNOTH),
	REANIMATE_BLOODVELD("Reanimate Bloodveld", SpriteID.SPELL_REANIMATE_BLOODVELD),
	REANIMATE_TZHAAR("Reanimate TzHaar", SpriteID.SPELL_REANIMATE_TZHAAR),
	REANIMATE_DEMON("Reanimate Demon", SpriteID.SPELL_REANIMATE_DEMON),
	RESURRECT_CROPS("Resurrect Crops", SpriteID.SPELL_REANIMATE_CROPS),
	REANIMATE_AVIANSIE("Reanimate Aviansie", SpriteID.SPELL_REANIMATE_AVIANSIE),
	REANIMATE_ABYSSAL_CREATURE("Reanimate Abyssal Creature", SpriteID.SPELL_REANIMATE_ABYSSAL_CREATURE),
	REANIMATE_DRAGON("Reanimate Dragon", SpriteID.SPELL_REANIMATE_DRAGON);

	String name;
	int spriteID;

	private static final Map<String, SpellSprite> map = new HashMap<>();

	static
	{
		for (SpellSprite spell : values())
		{
			map.put(spell.name, spell);
		}
	}

	static SpellSprite get(String spell)
	{
		return map.get(spell);
	}
}

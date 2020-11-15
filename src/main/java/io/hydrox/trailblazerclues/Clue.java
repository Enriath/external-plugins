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
package io.hydrox.trailblazerclues;

import io.hydrox.trailblazerclues.requirements.ANDGroupedRequirements;
import io.hydrox.trailblazerclues.requirements.ANDRegionRequirement;
import io.hydrox.trailblazerclues.requirements.NeverShowRequirements;
import io.hydrox.trailblazerclues.requirements.ORGroupedRequirements;
import io.hydrox.trailblazerclues.requirements.ORRegionRequirement;
import io.hydrox.trailblazerclues.requirements.RegionRequirement;
import io.hydrox.trailblazerclues.requirements.SingleRegionRequirement;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetID;
import java.util.HashMap;
import java.util.Map;

class Clue
{
	private static RegionRequirement and(Region... regions)
	{
		return new ANDRegionRequirement(regions);
	}

	private static RegionRequirement or(Region... regions)
	{
		return new ORRegionRequirement(regions);
	}

	private static RegionRequirement a(Region region)
	{
		return new SingleRegionRequirement(region);
	}

	private static RegionRequirement or(RegionRequirement... requirements)
	{
		return new ORGroupedRequirements(requirements);
	}

	private static RegionRequirement and(RegionRequirement... requirements)
	{
		return new ANDGroupedRequirements(requirements);
	}

	static RegionRequirement fromText(String text)
	{
		return CLUES.get(text);
	}

	static RegionRequirement fromWorldPoint(WorldPoint point)
	{
		return COORD_CLUES.get(point);
	}

	static RegionRequirement fromMap(int itemID)
	{
		return MAP_CLUES.get(itemID);
	}

	static RegionRequirement fromBeginnerMap(int widgetID)
	{
		return BEGINNER_MAP_CLUES.get(widgetID);
	}

	// I'm so, so very sorry.
	private static final Map<String, RegionRequirement> CLUES = new HashMap<>();
	private static final Map<WorldPoint, RegionRequirement> COORD_CLUES = new HashMap<>();
	private static final Map<Integer, RegionRequirement> MAP_CLUES = new HashMap<>();
	private static final Map<Integer, RegionRequirement> BEGINNER_MAP_CLUES = new HashMap<>();
	
	static
	{
		// Anagrams
		CLUES.put("a baker", a(Region.MISTHALIN));
		CLUES.put("a basic anti pot", a(Region.ASGARNIA));
		CLUES.put("a elf knows", and(Region.FREMENNIK, Region.KANDARIN, Region.ASGARNIA));
		CLUES.put("a zen she", a(Region.KANDARIN));
		CLUES.put("ace match elm", a(Region.DESERT));
		CLUES.put("aha jar", a(Region.DESERT));
		CLUES.put("an paint tonic", a(Region.ASGARNIA));
		CLUES.put("arc o line", a(Region.KANDARIN));
		CLUES.put("are col", a(Region.ASGARNIA));
		CLUES.put("armchair the pelt", a(Region.MISTHALIN));
		CLUES.put("arr! so i am a crust, and?", a(Region.KANDARIN));
		CLUES.put("a bas", a(Region.ASGARNIA));
		CLUES.put("area chef trek", a(Region.MISTHALIN));
		CLUES.put("bail trims", a(Region.KANDARIN));
		CLUES.put("baker climb", a(Region.ASGARNIA));
		CLUES.put("blue grim guided", a(Region.MISTHALIN));
		CLUES.put("by look", a(Region.KANDARIN));
		CLUES.put("calamari made mud", a(Region.KANDARIN));
		CLUES.put("car if ices", a(Region.TIRANNWN));
		CLUES.put("career in moon", a(Region.FREMENNIK));
		CLUES.put("c on game hoc", a(Region.KANDARIN));
		CLUES.put("cool nerd", a(Region.MORYTANIA));
		CLUES.put("copper ore crypts", a(Region.ASGARNIA));
		CLUES.put("darn drake", a(Region.MORYTANIA));
		CLUES.put("ded war", a(Region.WILDERNESS));
		CLUES.put("dekagram", a(Region.WILDERNESS));
		CLUES.put("do say more", a(Region.MISTHALIN));
		CLUES.put("dim tharn", a(Region.WILDERNESS));
		CLUES.put("dr hitman", a(Region.WILDERNESS));
		CLUES.put("dr warden funk", a(Region.FREMENNIK));
		CLUES.put("dragons lament", a(Region.MORYTANIA));
		CLUES.put("dt run b", a(Region.FREMENNIK));
		CLUES.put("duo plug", a(Region.KANDARIN));
		CLUES.put("eek zero op", a(Region.KANDARIN));
		CLUES.put("el ow", a(Region.MISTHALIN));
		CLUES.put("err cure it", a(Region.KANDARIN));
		CLUES.put("forlun", a(Region.FREMENNIK));
		CLUES.put("goblin kern", a(Region.KANDARIN));
		CLUES.put("got a boy", a(Region.KARAMJA));
		CLUES.put("gulag run", a(Region.KANDARIN));
		CLUES.put("goblets odd toes", a(Region.KANDARIN));
		CLUES.put("halt us", a(Region.KARAMJA));
		CLUES.put("he do pose. it is cultrrl, mk?", a(Region.FREMENNIK));
		CLUES.put("heoric", a(Region.ASGARNIA));
		CLUES.put("icy fe", a(Region.KANDARIN));
		CLUES.put("i doom icon inn", a(Region.KANDARIN));
		CLUES.put("i eat its chart hints do u", a(Region.DESERT));
		CLUES.put("i even", a(Region.KANDARIN));
		CLUES.put("i faffy run", a(Region.MISTHALIN));
		CLUES.put("im n zezim", a(Region.MISTHALIN));
		CLUES.put("kay sir", a(Region.KANDARIN));
		CLUES.put("leakey", a(Region.ASGARNIA));
		CLUES.put("land doomd", a(Region.MISTHALIN));
		CLUES.put("lark in dog", a(Region.MISTHALIN));
		CLUES.put("ladder memo guv", a(Region.KANDARIN));
		CLUES.put("mal in tau", a(Region.MORYTANIA));
		CLUES.put("me am the calc", a(Region.DESERT));
		CLUES.put("machete clam", a(Region.DESERT));
		CLUES.put("me if", a(Region.KANDARIN));
		CLUES.put("mold la ran", a(Region.MORYTANIA));
		CLUES.put("motherboard", a(Region.KANDARIN));
		CLUES.put("mus kil reader", and(Region.KANDARIN, Region.FREMENNIK)); // Requires Legends, which is unable to be started without the auto-unlock.
		CLUES.put("no owner", a(Region.TIRANNWN));
		CLUES.put("nod med", a(Region.KANDARIN));
		CLUES.put("o birdz a zany en pc", a(Region.KARAMJA));
		CLUES.put("ok co", a(Region.MISTHALIN));
		CLUES.put("or zinc fumes ward", a(Region.KANDARIN));
		CLUES.put("peak reflex", a(Region.KANDARIN));
		CLUES.put("peaty pert", a(Region.ASGARNIA));
		CLUES.put("profs lose wrong pie", a(Region.MISTHALIN));
		CLUES.put("quit horrible tyrant", a(Region.MORYTANIA));
		CLUES.put("que sir", a(Region.ASGARNIA));
		CLUES.put("r ak mi", a(Region.DESERT));
		CLUES.put("rat mat within", a(Region.ASGARNIA));
		CLUES.put("red art tans", a(Region.ASGARNIA));
		CLUES.put("ratai", a(Region.ASGARNIA));
		CLUES.put("rip maul", and(Region.KANDARIN, Region.FREMENNIK)); // Requires DS2
		CLUES.put("sand nut", a(Region.ASGARNIA));
		CLUES.put("sequin dirge", a(Region.FREMENNIK));
		CLUES.put("slide woman", a(Region.MISTHALIN));
		CLUES.put("snah", a(Region.MISTHALIN));
		CLUES.put("snakes so i sail", a(Region.FREMENNIK));
		CLUES.put("ten wigs on", a(Region.DESERT));
		CLUES.put("them cal came", a(Region.DESERT));
		CLUES.put("thickno", a(Region.KANDARIN));
		CLUES.put("unleash night mist", a(Region.FREMENNIK));
		CLUES.put("veste", or(Region.KANDARIN, Region.ASGARNIA));
		CLUES.put("veil veda", and(Region.MISTHALIN, Region.DESERT));
		CLUES.put("woo an egg kiwi", a(Region.KANDARIN));
		CLUES.put("yawns gy", a(Region.TIRANNWN));
		CLUES.put("majors lava bads air", a(Region.MISTHALIN));
		CLUES.put("an earl", a(Region.DESERT));
		CLUES.put("carpet ahoy", a(Region.MISTHALIN));
		CLUES.put("disorder", a(Region.MISTHALIN));
		CLUES.put("i cord", a(Region.ASGARNIA));
		CLUES.put("in bar", a(Region.ASGARNIA));
		CLUES.put("rain cove", a(Region.MISTHALIN));
		CLUES.put("rug deter", a(Region.MISTHALIN));
		CLUES.put("sir share red", a(Region.ASGARNIA));
		CLUES.put("taunt roof", a(Region.MISTHALIN));
		CLUES.put("hick jet", a(Region.KANDARIN));
		CLUES.put("rue go", a(Region.TIRANNWN));
		// Ciphers
		CLUES.put("bmj uif lfcbc tfmmfs", a(Region.DESERT));
		CLUES.put("guhcho", a(Region.MORYTANIA));
		CLUES.put("hqnm lzm stsnq", a(Region.MISTHALIN));
		CLUES.put("zhlug rog pdq", a(Region.DESERT));
		CLUES.put("ovexon", a(Region.TIRANNWN));
		CLUES.put("vtyr apcntglw", and(Region.ASGARNIA, Region.KANDARIN, Region.KARAMJA));
		CLUES.put("uzzu mujhrkyykj", a(Region.KANDARIN));
		CLUES.put("usbjcpso", a(Region.MISTHALIN));
		CLUES.put("hckta iqfhcvjgt", a(Region.MISTHALIN));
		CLUES.put("zsbkdo zodo", a(Region.MORYTANIA));
		CLUES.put("gbjsz rvffo", a(Region.MISTHALIN)); // You can actually get there, as long as you type the codes in
		CLUES.put("iwpplqtp", a(Region.KANDARIN));
		CLUES.put("bsopme mzetqps", a(Region.KANDARIN));
		// Cryptics
		CLUES.put("show this to sherlock.", a(Region.KANDARIN));
		CLUES.put("talk to the bartender of the rusty anchor in port sarim.", a(Region.ASGARNIA));
		CLUES.put("the keeper of melzars... spare? skeleton? anar?", a(Region.MISTHALIN));
		CLUES.put("speak to ulizius.", a(Region.MORYTANIA));
		CLUES.put("search for a crate in a building in hemenster.", a(Region.KANDARIN));
		CLUES.put("a reck you say; let's pray there aren't any ghosts.", a(Region.MISTHALIN));
		CLUES.put("search the bucket in the port sarim jail.", and(Region.ASGARNIA, Region.DESERT));
		CLUES.put("search the crates in a bank in varrock.", a(Region.MISTHALIN));
		CLUES.put("falo the bard wants to see you.", a(Region.KANDARIN));
		CLUES.put("search a bookcase in the wizards tower.", a(Region.MISTHALIN));
		CLUES.put("come have a cip with this great soot covered denizen.", a(Region.FREMENNIK));
		CLUES.put("citric cellar.", a(Region.KANDARIN));
		CLUES.put("i burn between heroes and legends.", a(Region.KANDARIN));
		CLUES.put("speak to sarah at falador farm.", a(Region.ASGARNIA));
		CLUES.put("search for a crate on the ground floor of a house in seers' village.", a(Region.KANDARIN));
		CLUES.put("snah? i feel all confused, like one of those cakes...", a(Region.MISTHALIN));
		CLUES.put("speak to sir kay in camelot castle.", a(Region.KANDARIN));
		CLUES.put("gold i see, yet gold i require. give me 875 if death you desire.", a(Region.KARAMJA));
		CLUES.put("find a crate close to the monks that like to paaarty!", a(Region.KANDARIN));
		CLUES.put("identify the back of this over-acting brother. (he's a long way from home.)", a(Region.DESERT));
		CLUES.put("in a town where thieves steal from stalls, search for some drawers in the upstairs of a house near the bank.", a(Region.KANDARIN));
		CLUES.put("his bark is worse than his bite.", a(Region.MORYTANIA));
		CLUES.put("the beasts to my east snap claws and tails, the rest to my west can slide and eat fish. the force to my north will jump and they'll wail, come dig by my fire and make a wish.", a(Region.KANDARIN));
		CLUES.put("a town with a different sort of night-life is your destination. search for some crates in one of the houses.", a(Region.MORYTANIA));
		CLUES.put("stop crying! talk to the head.", a(Region.KANDARIN)); // Really not sure about this one
		CLUES.put("search the crate near a cart in port khazard.", a(Region.KANDARIN));
		CLUES.put("speak to the bartender of the blue moon inn in varrock.", a(Region.MISTHALIN));
		CLUES.put("this aviator is at the peak of his profession.", a(Region.ASGARNIA));
		CLUES.put("search the crates in the shed just north of east ardougne.", a(Region.KANDARIN));
		CLUES.put("search the crate in the toad and chicken pub.", a(Region.ASGARNIA));
		CLUES.put("search chests found in the upstairs of shops in port sarim.", a(Region.ASGARNIA));
		CLUES.put("right on the blessed border, cursed by the evil ones. on the spot inaccessible by both; i will be waiting. the bugs' imminent possession holds the answer.", a(Region.MORYTANIA));
		CLUES.put("the dead, red dragon watches over this chest. he must really dig the view.", a(Region.MISTHALIN));
		CLUES.put("my home is grey, and made of stone; a castle with a search for a meal. hidden in some drawers i am, across from a wooden wheel.", a(Region.MISTHALIN));
		CLUES.put("come to the evil ledge, yew know yew want to. try not to get stung.", a(Region.MISTHALIN));
		CLUES.put("look in the ground floor crates of houses in falador.", a(Region.ASGARNIA));
		CLUES.put("you were 3 and i was the 6th. come speak to me.", a(Region.MISTHALIN));
		CLUES.put("search the crates in draynor manor.", a(Region.MISTHALIN));
		CLUES.put("search the crates near a cart in varrock.", a(Region.MISTHALIN));
		CLUES.put("a guthixian ring lies between two peaks. search the stones and you'll find what you seek.", a(Region.ASGARNIA));
		CLUES.put("search the boxes in the house near the south entrance to varrock.", a(Region.MISTHALIN));
		CLUES.put("his head might be hollow, but the crates nearby are filled with surprises.", a(Region.DESERT));
		CLUES.put("one of the sailors in port sarim is your next destination.", a(Region.ASGARNIA));
		CLUES.put("they're everywhere!!!! but they were here first. dig for treasure where the ground is rich with ore.", a(Region.MISTHALIN));
		CLUES.put("talk to the mother of a basement dwelling son.", a(Region.MISTHALIN));
		CLUES.put("speak to ned in draynor village.", a(Region.MISTHALIN));
		CLUES.put("speak to hans to solve the clue.", a(Region.MISTHALIN));
		CLUES.put("search the crates in canifis.", a(Region.MORYTANIA));
		CLUES.put("search the crates in the dwarven mine.", a(Region.ASGARNIA));
		CLUES.put("a crate found in the tower of a church is your next location.", a(Region.KANDARIN));
		CLUES.put("covered in shadows, the centre of the circle is where you will find the answer.", a(Region.MORYTANIA));
		CLUES.put("i lie lonely and forgotten in mid wilderness, where the dead rise from their beds. feel free to quarrel and wind me up, and dig while you shoot their heads.", a(Region.WILDERNESS));
		CLUES.put("in the city where merchants are said to have lived, talk to a man with a splendid cape, but a hat dropped by goblins.", a(Region.MISTHALIN));
		CLUES.put("the mother of the reptilian sacrifice.", a(Region.TIRANNWN));
		CLUES.put("i watch the sea. i watch you fish. i watch your tree.", a(Region.KANDARIN));
		CLUES.put("dig between some ominous stones in falador.", a(Region.ASGARNIA));
		CLUES.put("speak to rusty north of falador.", a(Region.ASGARNIA));
		CLUES.put("search a wardrobe in draynor.", a(Region.MISTHALIN));
		CLUES.put("i have many arms but legs, i have just one. i have little family but my seed you can grow on, i am not dead, yet i am but a spirit, and my power, on your quests, you will earn the right to free it.", a(Region.KANDARIN));
		CLUES.put("i am the one who watches the giants. the giants in turn watch me. i watch with two while they watch with one. come seek where i may be.", a(Region.ASGARNIA));
		CLUES.put("in a town where wizards are known to gather, search upstairs in a large house to the north.", a(Region.KANDARIN));
		CLUES.put("probably filled with wizards socks.", a(Region.MISTHALIN));
		CLUES.put("even the seers say this clue goes right over their heads.", a(Region.KANDARIN));
		CLUES.put("speak to a wyse man.", a(Region.ASGARNIA));
		CLUES.put("you'll need to look for a town with a central fountain. look for a locked chest in the town's chapel.", and(Region.MISTHALIN, Region.KANDARIN));
		CLUES.put("talk to ambassador spanfipple in the white knights castle.", a(Region.ASGARNIA));
		CLUES.put("mine was the strangest birth under the sun. i left the crimson sack, yet life had not begun. entered the world, and yet was seen by none.", a(Region.KARAMJA));
		CLUES.put("search for a crate in varrock castle.", a(Region.MISTHALIN));
		CLUES.put("and so on, and so on, and so on. walking from the land of many unimportant things leads to a choice of paths.", a(Region.FREMENNIK));
		CLUES.put("speak to donovan, the family handyman.", a(Region.KANDARIN));
		CLUES.put("search the crates in the barbarian village helmet shop.", a(Region.MISTHALIN));
		CLUES.put("search the boxes of falador's general store.", a(Region.ASGARNIA));
		CLUES.put("in a village made of bamboo, look for some crates under one of the houses.", a(Region.KARAMJA));
		CLUES.put("this crate is mine, all mine, even if it is in the middle of the desert.", a(Region.DESERT));
		CLUES.put("dig where 4 siblings and i all live with our evil overlord.", a(Region.MISTHALIN));
		CLUES.put("in a town where the guards are armed with maces, search the upstairs rooms of the public house.", a(Region.KANDARIN));
		CLUES.put("four blades i have, yet draw no blood; still i turn my prey to powder. if you are brave, come search my roof; it is there my blades are louder.", a(Region.MISTHALIN));
		CLUES.put("search through some drawers in the upstairs of a house in rimmington.", a(Region.ASGARNIA));
		CLUES.put("probably filled with books on magic.", a(Region.MISTHALIN));
		CLUES.put("if you look closely enough, it seems that the archers have lost more than their needles.", a(Region.KANDARIN));
		CLUES.put("search the crate in the left-hand tower of lumbridge castle.", a(Region.MISTHALIN));
		CLUES.put("'small shoe.' often found with rod on mushroom.", a(Region.KANDARIN));
		CLUES.put("i live in a deserted crack collecting soles.", a(Region.DESERT));
		CLUES.put("46 is my number. my body is the colour of burnt orange and crawls among those with eight. three mouths i have, yet i cannot eat. my blinking blue eye hides my grave.", a(Region.WILDERNESS));
		CLUES.put("green is the colour of my death as the winter-guise, i swoop towards the ground.", and(Region.ASGARNIA, Region.KANDARIN));
		CLUES.put("talk to a party-goer in falador.", a(Region.ASGARNIA));
		CLUES.put("he knows just how easy it is to lose track of time.", a(Region.KANDARIN));
		CLUES.put("a great view - watch the rapidly drying hides get splashed. check the box you are sitting on.", a(Region.KANDARIN));
		CLUES.put("search the coffin in edgeville.", a(Region.MISTHALIN));
		CLUES.put("when no weapons are at hand, then is the time to reflect. in saradomin's name, redemption draws closer...", a(Region.ASGARNIA));
		CLUES.put("search the crates in a house in yanille that has a piano.", a(Region.KANDARIN));
		CLUES.put("speak to the staff of sinclair mansion.", a(Region.KANDARIN));
		CLUES.put("i am a token of the greatest love. i have no beginning or end. my eye is red, i can fit like a glove. go to the place where it's money they lend, and dig by the gate to be my friend.", a(Region.MISTHALIN));
		CLUES.put("speak to kangai mau.", a(Region.KARAMJA));
		CLUES.put("speak to hajedy.", a(Region.KARAMJA));
		CLUES.put("must be full of railings.", a(Region.KANDARIN));
		CLUES.put("i wonder how many bronze swords he has handed out.", a(Region.MISTHALIN));
		CLUES.put("read 'how to breed scorpions.' by o.w.thathurt.", a(Region.KANDARIN));
		CLUES.put("search the crates in the port sarim fishing shop.", a(Region.ASGARNIA));
		CLUES.put("speak to the lady of the lake.", a(Region.ASGARNIA));
		CLUES.put("rotting next to a ditch. dig next to the fish.", a(Region.MORYTANIA));
		CLUES.put("the king's magic won't be wasted by me.", a(Region.DESERT));
		CLUES.put("dig where the forces of zamorak and saradomin collide.", a(Region.WILDERNESS));
		CLUES.put("search the boxes in the goblin house near lumbridge.", a(Region.MISTHALIN));
		CLUES.put("w marks the spot.", a(Region.ASGARNIA));
		CLUES.put("there is no 'worthier' lord.", a(Region.TIRANNWN));
		CLUES.put("surviving.", a(Region.ASGARNIA));
		CLUES.put("my name is like a tree, yet it is spelt with a 'g'. come see the fur which is right near me.", a(Region.MISTHALIN));
		CLUES.put("speak to jatix in taverley.", a(Region.ASGARNIA));
		CLUES.put("speak to gaius in taverley.", a(Region.ASGARNIA));
		CLUES.put("if a man carried my burden, he would break his back. i am not rich, but leave silver in my track. speak to the keeper of my trail.", a(Region.ASGARNIA));
		CLUES.put("search the drawers in falador's chain mail shop.", a(Region.ASGARNIA));
		CLUES.put("talk to the barber in the falador barber shop.", a(Region.ASGARNIA));
		CLUES.put("often sought out by scholars of histories past, find me where words of wisdom speak volumes.", a(Region.MISTHALIN));
		CLUES.put("generally speaking, his nose was very bent.", a(Region.ASGARNIA));
		CLUES.put("search the bush at the digsite centre.", a(Region.MISTHALIN));
		CLUES.put("someone watching the fights in the duel arena is your next destination.", a(Region.DESERT));
		CLUES.put("it seems to have reached the end of the line, and it's still empty.", a(Region.ASGARNIA));
		CLUES.put("you'll have to plug your nose if you use this source of herbs.", a(Region.MORYTANIA));
		CLUES.put("when you get tired of fighting, go deep, deep down until you need an antidote.", a(Region.KANDARIN));
		CLUES.put("search the bookcase in the monastery.", a(Region.ASGARNIA));
		CLUES.put("surprising? i bet he is...", a(Region.ASGARNIA));
		CLUES.put("search upstairs in the houses of seers' village for some drawers.", a(Region.KANDARIN));
		CLUES.put("leader of the yak city.", a(Region.FREMENNIK));
		CLUES.put("speak to arhein in catherby.", a(Region.KANDARIN));
		CLUES.put("speak to doric, who lives north of falador.", a(Region.ASGARNIA));
		CLUES.put("where the best are commemorated, and a celebratory cup, not just for beer.", a(Region.DESERT));
		CLUES.put("'see you in your dreams' said the vegetable man.", a(Region.KANDARIN));
		CLUES.put("try not to step on any aquatic nasties while searching this crate.", a(Region.KANDARIN));
		CLUES.put("the cheapest water for miles around, but they react badly to religious icons.", a(Region.DESERT));
		CLUES.put("this village has a problem with cartloads of the undead. try checking the bookcase to find an answer.", a(Region.KARAMJA));
		CLUES.put("dobson is my last name, and with gardening i seek fame.", a(Region.KANDARIN));
		CLUES.put("the magic of 4 colours, an early experience you could learn. the large beast caged up top, rages, as his demised kin's loot now returns.", a(Region.MISTHALIN));
		CLUES.put("aggie i see. lonely and southern i feel. i am neither inside nor outside the house, yet no home would be complete without me. the treasure lies beneath me!", a(Region.MISTHALIN));
		CLUES.put("search the chest in barbarian village.", a(Region.MISTHALIN));
		CLUES.put("search the crates in the outhouse of the long building in taverley.", a(Region.ASGARNIA));
		CLUES.put("talk to ermin.", a(Region.KANDARIN));
		CLUES.put("ghostly bones.", or(Region.MISTHALIN, Region.KANDARIN, Region.WILDERNESS));
		CLUES.put("search through chests found in the upstairs of houses in eastern falador.", a(Region.ASGARNIA));
		CLUES.put("let's hope you don't meet a watery death when you encounter this fiend.", or(Region.KANDARIN, Region.TIRANNWN));
		CLUES.put("reflection is the weakness for these eyes of evil.", a(Region.FREMENNIK));
		CLUES.put("search a bookcase in lumbridge swamp.", a(Region.MISTHALIN));
		CLUES.put("surround my bones in fire, ontop the wooden pyre. finally lay me to rest, before my one last test.", a(Region.KANDARIN));
		CLUES.put("fiendish cooks probably won't dig the dirty dishes.", a(Region.ASGARNIA));
		CLUES.put("my life was spared but these voices remain, now guarding these iron gates is my bane.", a(Region.ASGARNIA));
		CLUES.put("search the boxes in one of the tents in al kharid.", a(Region.DESERT));
		CLUES.put("one of several rhyming brothers, in business attire with an obsession for paper work.", a(Region.WILDERNESS));
		CLUES.put("search the drawers on the ground floor of a building facing ardougne's market.", a(Region.KANDARIN));
		CLUES.put("'a bag belt only?', he asked his balding brothers.", a(Region.ASGARNIA));
		CLUES.put("search the drawers upstairs in falador's shield shop.", a(Region.ASGARNIA));
		CLUES.put("go to this building to be illuminated, and check the drawers while you are there.", a(Region.FREMENNIK));
		CLUES.put("dig near some giant mushrooms, behind the grand tree.", a(Region.KANDARIN));
		CLUES.put("pentagrams and demons, burnt bones and remains, i wonder what the blood contains.", a(Region.WILDERNESS));
		CLUES.put("search the drawers above varrock's shops.", a(Region.MISTHALIN));
		CLUES.put("search the drawers in one of gertrude's bedrooms.", a(Region.MISTHALIN));
		CLUES.put("under a giant robotic bird that cannot fly.", a(Region.MISTHALIN));
		CLUES.put("great demons, dragons and spiders protect this blue rock, beneath which, you may find what you seek.", a(Region.WILDERNESS));
		CLUES.put("my giant guardians below the market streets would be fans of rock and roll, if only they could grab hold of it. dig near my green bubbles!", a(Region.MISTHALIN));
		CLUES.put("varrock is where i reside, not the land of the dead, but i am so old, i should be there instead. let's hope your reward is as good as it says, just 1 gold one and you can have it read.", a(Region.MISTHALIN));
		CLUES.put("speak to a referee.", a(Region.KANDARIN));
		CLUES.put("this crate holds a better reward than a broken arrow.", a(Region.KANDARIN));
		CLUES.put("search the drawers in the house next to the port sarim mage shop.", a(Region.ASGARNIA));
		CLUES.put("with a name like that, you'd expect a little more than just a few scimitars.", a(Region.KANDARIN));
		CLUES.put("strength potions with red spiders' eggs? he is quite a herbalist.", a(Region.MISTHALIN));
		CLUES.put("robin wishes to see your finest ranged equipment.", a(Region.MORYTANIA));
		CLUES.put("you will need to under-cook to solve this one.", a(Region.MISTHALIN));
		CLUES.put("search through some drawers found in taverley's houses.", a(Region.ASGARNIA));
		CLUES.put("anger abbot langley.", and(Region.ASGARNIA, Region.DESERT));
		CLUES.put("dig where only the skilled, the wealthy, or the brave can choose not to visit again.", a(Region.MISTHALIN));
		CLUES.put("scattered coins and gems fill the floor. the chest you seek is in the north east.", a(Region.WILDERNESS));
		CLUES.put("a ring of water surrounds 4 powerful rings, dig above the ladder located there.", a(Region.FREMENNIK));
		CLUES.put("here, there are tears, but nobody is crying. speak to the guardian and show off your alignment to balance.", a(Region.MISTHALIN));
		CLUES.put("you might have to turn over a few stones to progress.", a(Region.FREMENNIK));
		CLUES.put("dig under razorlor's toad batta.", a(Region.MORYTANIA));
		CLUES.put("talk to cassie in falador.", a(Region.ASGARNIA));
		CLUES.put("faint sounds of 'arr', fire giants found deep, the eastern tip of a lake, are the rewards you could reap.", a(Region.WILDERNESS));
		CLUES.put("if you're feeling brave, dig beneath the dragon's eye.", and(Region.KARAMJA, Region.FREMENNIK));
		CLUES.put("search the tents in the imperial guard camp in burthorpe for some boxes.", a(Region.ASGARNIA));
		CLUES.put("a dwarf, approaching death, but very much in the light.", a(Region.TIRANNWN)); // Might also need Kandarin
		CLUES.put("you must be 100 to play with me.", a(Region.ASGARNIA));
		CLUES.put("three rule below and three sit at top. come dig at my entrance.", a(Region.FREMENNIK));
		CLUES.put("search the drawers in the ground floor of a shop in yanille.", a(Region.KANDARIN));
		CLUES.put("search the drawers of houses in burthorpe.", a(Region.ASGARNIA));
		CLUES.put("where safe to speak, the man who offers the pouch of smallest size wishes to see your alignment.", a(Region.MISTHALIN));
		CLUES.put("search the crates in the guard house of the northern gate of east ardougne.", a(Region.KANDARIN));
		CLUES.put("go to the village being attacked by trolls, search the drawers in one of the houses.", a(Region.ASGARNIA));
		CLUES.put("you'll get licked.", or(Region.ASGARNIA, Region.MORYTANIA, Region.KANDARIN, Region.WILDERNESS));
		CLUES.put("dig in front of the icy arena where 1 of 4 was fought.", a(Region.ASGARNIA));
		CLUES.put("speak to roavar.", a(Region.MORYTANIA));
		CLUES.put("search the drawers downstairs of houses in the eastern part of falador.", a(Region.ASGARNIA));
		CLUES.put("search the drawers found upstairs in east ardougne's houses.", a(Region.KANDARIN));
		CLUES.put("the far north eastern corner where 1 of 4 was defeated, the shadows still linger.", and(Region.KANDARIN, Region.DESERT));
		CLUES.put("search the drawers in a house in draynor village.", a(Region.MISTHALIN));
		CLUES.put("search the boxes in a shop in taverley.", a(Region.ASGARNIA));
		CLUES.put("i lie beneath the first descent to the holy encampment.", a(Region.ASGARNIA));
		CLUES.put("search the upstairs drawers of a house in a village where pirates are known to have a good time.", a(Region.KARAMJA));
		CLUES.put("search the chest in the duke of lumbridge's bedroom.", a(Region.MISTHALIN));
		CLUES.put("talk to the doomsayer.", a(Region.MISTHALIN));
		CLUES.put("search the chests upstairs in al kharid palace.", a(Region.DESERT));
		CLUES.put("search the boxes just outside the armour shop in east ardougne.", a(Region.KANDARIN));
		CLUES.put("surrounded by white walls and gems.", a(Region.ASGARNIA));
		CLUES.put("search the drawers in catherby's archery shop.", a(Region.KANDARIN));
		CLUES.put("the hand ain't listening.", a(Region.ASGARNIA));
		CLUES.put("search the chest in the left-hand tower of camelot castle.", a(Region.KANDARIN));
		CLUES.put("anger those who adhere to saradomin's edicts to prevent travel.", a(Region.ASGARNIA));
		CLUES.put("south of a river in a town surrounded by the undead, what lies beneath the furnace?", a(Region.MORYTANIA));
		CLUES.put("talk to the squire in the white knights' castle in falador.", a(Region.ASGARNIA));
		CLUES.put("in a town where everyone has perfect vision, seek some locked drawers in a house that sits opposite a workshop.", a(Region.KANDARIN));
		CLUES.put("the treasure is buried in a small building full of bones. here is a hint: it's not near a graveyard.", a(Region.MISTHALIN));
		CLUES.put("search the crates in east ardougne's general store.", a(Region.KANDARIN));
		CLUES.put("come brave adventurer, your sense is on fire. if you talk to me, it's an old god you desire.", and(and(Region.KANDARIN, Region.DESERT), or(Region.WILDERNESS, Region.MORYTANIA)));
		CLUES.put("2 musical birds. dig in front of the spinning light.", and(Region.FREMENNIK, Region.KANDARIN));
		CLUES.put("search the wheelbarrow in rimmington mine.", a(Region.ASGARNIA));
		CLUES.put("belladonna, my dear. if only i had gloves, then i could hold you at last.", a(Region.MISTHALIN));
		CLUES.put("impossible to make angry", a(Region.ASGARNIA));
		CLUES.put("search the crates in horvik's armoury.", a(Region.MISTHALIN));
		CLUES.put("ghommal wishes to be impressed by how strong your equipment is.", a(Region.ASGARNIA));
		CLUES.put("talk to zeke in al kharid.", a(Region.DESERT));
		CLUES.put("guthix left his mark in a fiery lake, dig at the tip of it.", a(Region.WILDERNESS));
		CLUES.put("search the drawers in the upstairs of a house in catherby.", a(Region.KANDARIN));
		CLUES.put("desert insects is what i see. taking care of them was my responsibility. your solution is found by digging near me.", a(Region.DESERT));
		CLUES.put("search the crates in the most north-western house in al kharid.", a(Region.DESERT));
		CLUES.put("you will have to fly high where a sword cannot help you.", or(Region.ASGARNIA, Region.WILDERNESS));
		CLUES.put("a massive battle rages beneath so be careful when you dig by the large broken crossbow.", a(Region.ASGARNIA));
		CLUES.put("mix yellow with blue and add heat, make sure you bring protection.", or(Region.KANDARIN, Region.WILDERNESS));
		CLUES.put("speak to ellis in al kharid.", a(Region.DESERT));
		CLUES.put("search the chests in the dwarven mine.", a(Region.ASGARNIA));
		CLUES.put("in a while...", a(Region.DESERT));
		CLUES.put("a chisel and hammer reside in his home, strange for one of magic. impress him with your magical equipment.", a(Region.KANDARIN));
		CLUES.put("you have all of the elements available to solve this clue. fortunately you do not have to go as far as to stand in a draft.", a(Region.KANDARIN));
		CLUES.put("a demon's best friend holds the next step of this clue.", or(Region.ASGARNIA, Region.WILDERNESS, Region.KANDARIN));
		CLUES.put("hopefully this set of armour will help you to keep surviving.", a(Region.ASGARNIA));
		CLUES.put("the beasts retreat, for their queen is gone; the song of this town still plays on. dig near the birthplace of a blade, be careful not to melt your spade.", a(Region.KANDARIN));
		CLUES.put("i would make a chemistry joke, but i'm afraid i wouldn't get a reaction.", a(Region.ASGARNIA));
		CLUES.put("show this to hazelmere.", a(Region.KANDARIN));
		CLUES.put("does one really need a fire to stay warm here?", a(Region.MISTHALIN));
		CLUES.put("dig under ithoi's cabin.", a(Region.KANDARIN));
		CLUES.put("search the drawers, upstairs in the bank to the east of varrock.", a(Region.MISTHALIN));
		CLUES.put("speak to hazelmere.", a(Region.KANDARIN));
		CLUES.put("always walking around the castle grounds and somehow knows everyone's age.", a(Region.MISTHALIN));
		CLUES.put("in the place duke horacio calls home, talk to a man with a hat dropped by goblins.", a(Region.MISTHALIN));
		CLUES.put("in a village of barbarians, i am the one who guards the village from up high.", a(Region.MISTHALIN));
		CLUES.put("talk to charlie the tramp in varrock.", a(Region.MISTHALIN));
		CLUES.put("near the open desert i reside, to get past me you must abide. go forward if you dare, for when you pass me, you'll be sweating by your hair.", a(Region.DESERT));
		CLUES.put("search the chest in fred the farmer's bedroom.", a(Region.MISTHALIN));
		CLUES.put("search the eastern bookcase in father urhney's house.", a(Region.MISTHALIN));
		CLUES.put("talk to morgan in his house at draynor village.", a(Region.MISTHALIN));
		CLUES.put("search the crate in rommiks crafting shop in rimmington.", a(Region.ASGARNIA));
		CLUES.put("talk to ali the leaflet dropper north of the al kharid mine.", a(Region.DESERT));
		CLUES.put("talk to the cook in the blue moon inn in varrock.", a(Region.MISTHALIN));
		CLUES.put("search the single crate in horvik's smithy in varrock.", a(Region.MISTHALIN));
		CLUES.put("search the crates in falador general store.", a(Region.ASGARNIA));
		CLUES.put("talk to wayne at wayne's chains in falador.", a(Region.ASGARNIA));
		CLUES.put("search the boxes next to a chest that needs a crystal key.", a(Region.ASGARNIA));
		CLUES.put("talk to turael in burthorpe.", a(Region.ASGARNIA));
		CLUES.put("more resources than i can handle, but in a very dangerous area. can't wait to strike gold!", a(Region.WILDERNESS));
		CLUES.put("observing someone in a swamp, under the telescope lies treasure.", a(Region.TIRANNWN));
		CLUES.put("a general who sets a 'shining' example.", a(Region.TIRANNWN));
		CLUES.put("has no one told you it is rude to ask a lady her age?", a(Region.TIRANNWN));
		CLUES.put("elvish onions.", a(Region.TIRANNWN));
		// Emotes
		CLUES.put("beckon on the east coast of the kharazi jungle. beware of double agents! equip any vestment stole and a heraldic rune shield.", and(Region.KARAMJA, Region.FREMENNIK));
		CLUES.put("cheer in the barbarian agility arena. headbang before you talk to me. equip a steel platebody, maple shortbow and a wilderness cape.", and(Region.KANDARIN, Region.WILDERNESS));
		CLUES.put("bow upstairs in the edgeville monastery. equip a completed prayer book.", and(Region.ASGARNIA, Region.FREMENNIK));
		CLUES.put("cheer in the shadow dungeon. equip a rune crossbow, climbing boots and any mitre.", and(Region.ASGARNIA, Region.KANDARIN, Region.DESERT));
		CLUES.put("cheer at the top of the agility pyramid. beware of double agents! equip a blue mystic robe top, and any rune heraldic shield.", and(a(Region.DESERT), or(Region.WILDERNESS, Region.KANDARIN)));
		CLUES.put("dance in iban's temple. beware of double agents! equip iban's staff, a black mystic top and a black mystic bottom.", and(Region.TIRANNWN, Region.MORYTANIA));
		CLUES.put("flap at the death altar. beware of double agents! equip a death tiara, a legend's cape and any ring of wealth.", and(Region.TIRANNWN, Region.FREMENNIK, Region.KANDARIN));
		CLUES.put("headbang in the fight arena pub. equip a pirate bandana, a dragonstone necklace and and a magic longbow.", and(Region.KANDARIN, Region.MORYTANIA));
		CLUES.put("do a jig at the barrows chest. beware of double agents! equip any full barrows set.", a(Region.MORYTANIA));
		CLUES.put("jig at jiggig. beware of double agents! equip a rune spear, rune platelegs and any rune heraldic helm.", a(Region.KANDARIN));
		CLUES.put("cheer at the games room. have nothing equipped at all when you do.", a(Region.ASGARNIA));
		CLUES.put("panic on the pier where you catch the fishing trawler. have nothing equipped at all when you do.", a(Region.KANDARIN));
		CLUES.put("panic in the heart of the haunted woods. beware of double agents! have no items equipped when you do.", a(Region.MORYTANIA));
		CLUES.put("show your anger towards the statue of saradomin in ellamaria's garden. beware of double agents! equip a zamorak godsword.", and(Region.MISTHALIN, Region.ASGARNIA));
		CLUES.put("show your anger at the wise old man. beware of double agents! equip an abyssal whip, a legend's cape and some spined chaps.", and(Region.MISTHALIN, Region.FREMENNIK, Region.KANDARIN));
		CLUES.put("beckon by a collection of crystalline maple trees. beware of double agents! equip bryophyta's staff and a nature tiara.", and(Region.TIRANNWN, Region.MISTHALIN, Region.KARAMJA));
		CLUES.put("beckon in the digsite, near the eastern winch. bow before you talk to me. equip a green gnome hat, snakeskin boots and an iron pickaxe.", and(Region.MISTHALIN, Region.KANDARIN));
		CLUES.put("beckon in tai bwo wannai. clap before you talk to me. equip green dragonhide chaps, a ring of dueling and a mithril medium helmet.", a(Region.KARAMJA));
		CLUES.put("bow near lord iorwerth. beware of double agents! equip a charged crystal bow.", a(Region.TIRANNWN));
		CLUES.put("bow in the iorwerth camp. beware of double agents! equip a charged crystal bow.", a(Region.TIRANNWN));
		CLUES.put("bow outside the entrance to the legends' guild. equip iron platelegs, an emerald amulet and an oak longbow.", a(Region.KANDARIN));
		CLUES.put("bow on the ground floor of the legend's guild. equip legend's cape, a dragon battleaxe and an amulet of glory.", and(Region.KANDARIN, Region.FREMENNIK));
		CLUES.put("bow in the ticket office of the duel arena. equip an iron chain body, leather chaps and coif.", a(Region.DESERT));
		CLUES.put("bow at the top of the lighthouse. beware of double agents! equip a blue dragonhide body, blue dragonhide vambraces and no jewelry.", a(Region.FREMENNIK));
		CLUES.put("blow a kiss between the tables in shilo village bank. beware of double agents! equip a blue mystic hat, bone spear and rune platebody.", and(Region.KARAMJA, Region.MISTHALIN, Region.KANDARIN));
		CLUES.put("cheer at the druids' circle. equip a blue wizard hat, a bronze two-handed sword and ham boots.", and(Region.MISTHALIN, Region.ASGARNIA));
		CLUES.put("cheer in the edgeville general store. dance before you talk to me. equip a brown apron, leather boots and leather gloves.", a(Region.MISTHALIN));
		CLUES.put("cheer in the ogre pen in the training camp. show you are angry before you talk to me. equip a green dragonhide body and chaps and a steel square shield.", and(a(Region.KANDARIN), or(Region.ASGARNIA, Region.TIRANNWN)));
		CLUES.put("cheer in the entrana church. beware of double agents! equip a full set of black dragonhide armour.", a(Region.ASGARNIA));
		CLUES.put("cheer for the monks at port sarim. equip a coif, steel plateskirt and a sapphire necklace.", a(Region.ASGARNIA));
		CLUES.put("clap in the main exam room in the exam centre. equip a white apron, green gnome boots and leather gloves.", and(Region.MISTHALIN, Region.KANDARIN));
		CLUES.put("clap on the causeway to the wizards' tower. equip an iron medium helmet, emerald ring and a white apron.", a(Region.MISTHALIN));
		CLUES.put("clap on the top level of the mill, north of east ardougne. equip a blue gnome robe top, ham robe bottom and an unenchanted tiara.", and(Region.KANDARIN, Region.MISTHALIN));
		CLUES.put("clap in seers court house. spin before you talk to me. equip an adamant halberd, blue mystic robe bottom and a diamond ring.", and(Region.KANDARIN, Region.TIRANNWN));
		CLUES.put("clap in the magic axe hut. beware of double agents! equip only some flared trousers.", and(Region.WILDERNESS, Region.MISTHALIN));
		CLUES.put("cry in the catherby ranging shop. bow before you talk to me. equip blue gnome boots, a hard leather body and an unblessed silver sickle.", a(Region.KANDARIN));
		CLUES.put("cry on the shore of catherby beach. laugh before you talk to me, equip an adamant sq shield, a bone dagger and mithril platebody.", and(Region.KANDARIN, Region.MISTHALIN, Region.ASGARNIA));
		CLUES.put("cry on top of the western tree in the gnome agility arena. indicate 'no' before you talk to me. equip a steel kiteshield, ring of forging and green dragonhide chaps.", a(Region.KANDARIN));
		CLUES.put("cry in the tzhaar gem store. beware of double agents! equip a fire cape and toktz-xil-ul.", a(Region.KARAMJA));
		CLUES.put("cry in the draynor village jail. jump for joy before you talk to me. equip an adamant sword, a sapphire amulet and an adamant plateskirt.", a(Region.MISTHALIN));
		CLUES.put("dance at the crossroads north of draynor. equip an iron chain body, a sapphire ring and a longbow.", a(Region.MISTHALIN));
		CLUES.put("dance in the party room. equip a steel full helmet, steel platebody and an iron plateskirt.", a(Region.ASGARNIA));
		CLUES.put("dance in the shack in lumbridge swamp. equip a bronze dagger, iron full helmet and a gold ring.", a(Region.MISTHALIN));
		CLUES.put("dance in the dark caves beneath lumbridge swamp. blow a kiss before you talk to me. equip an air staff, bronze full helm and an amulet of power.", a(Region.MISTHALIN));
		CLUES.put("dance at the cat-doored pyramid in sophanem. beware of double agents! equip a ring of life, an uncharged amulet of glory and an adamant two-handed sword.", a(Region.DESERT));
		CLUES.put("dance in the centre of canifis. bow before you talk to me. equip a green gnome robe top, mithril plate legs and an iron two-handed sword.", and(Region.KANDARIN, Region.MORYTANIA));
		CLUES.put("dance in the king black dragon's lair. beware of double agents! equip a black dragonhide body, black dragonhide vambraces and a black dragon mask.", a(Region.WILDERNESS));
		CLUES.put("dance at the entrance to the grand exchange. equip a pink skirt, pink robe top and a body tiara.", and(Region.MISTHALIN, Region.ASGARNIA));
		CLUES.put("goblin salute in the goblin village. beware of double agents! equip a bandos godsword, a bandos cloak and a bandos platebody.", a(Region.ASGARNIA));
		CLUES.put("headbang in the mine north of al kharid. equip a desert shirt, leather gloves and leather boots.", and(Region.MISTHALIN, Region.DESERT));
		CLUES.put("headbang at the exam centre. beware of double agents! equip a mystic fire staff, a diamond bracelet and rune boots.", and(and(Region.MISTHALIN), or(Region.KANDARIN, Region.WILDERNESS, RareRegion.MISTHALIN), or(Region.TIRANNWN, Region.MORYTANIA)));
		CLUES.put("headbang at the top of slayer tower. equip a seercull, a combat bracelet and helm of neitiznot.", and(Region.FREMENNIK, Region.MORYTANIA));
		CLUES.put("dance a jig by the entrance to the fishing guild. equip an emerald ring, a sapphire amulet, and a bronze chain body.", a(Region.KANDARIN));
		CLUES.put("dance a jig under shantay's awning. bow before you talk to me. equip a pointed blue snail helmet, an air staff and a bronze square shield.", and(Region.MORYTANIA, Region.DESERT));
		CLUES.put("do a jig in varrock's rune store. equip an air tiara and a staff of water.", and(Region.MISTHALIN, Region.ASGARNIA));
		CLUES.put("jump for joy at the beehives. equip a desert shirt, green gnome robe bottoms and a steel axe.", and(Region.KANDARIN, Region.DESERT));
		CLUES.put("jump for joy in yanille bank. dance a jig before you talk to me. equip a brown apron, adamantite medium helmet and snakeskin chaps.", and(a(Region.KANDARIN), or(Region.KARAMJA, Region.MORYTANIA, Region.MISTHALIN, Region.TIRANNWN)));
		CLUES.put("jump for joy in the tzhaar sword shop. shrug before you talk to me. equip a steel longsword, blue d'hide body and blue mystic gloves.", and(a(Region.KARAMJA), or(Region.KANDARIN, Region.FREMENNIK)));
		CLUES.put("jump for joy in the ancient cavern. equip a granite shield, splitbark body and any rune heraldic helm.", and(a(Region.KANDARIN), or(Region.FREMENNIK, Region.ASGARNIA), or(Region.MORYTANIA, Region.WILDERNESS)));
		CLUES.put("jump for joy in the centre of zul-andra. beware of double agents! equip a dragon 2h sword, bandos boots and an obsidian cape.", and(and(Region.TIRANNWN, Region.KARAMJA, Region.ASGARNIA), or(Region.WILDERNESS, Region.DESERT)));
		CLUES.put("laugh by the fountain of heroes. equip splitbark legs, dragon boots and a rune longsword.", and(a(Region.ASGARNIA), or(Region.WILDERNESS, Region.MORYTANIA)));
		CLUES.put("laugh in jokul's tent in the mountain camp. beware of double agents! equip a rune full helmet, blue dragonhide chaps and a fire battlestaff.", and(a(Region.FREMENNIK), or(Region.TIRANNWN, Region.MORYTANIA, Region.WILDERNESS, Region.MISTHALIN, Region.KANDARIN, Region.KARAMJA)));
		CLUES.put("laugh at the crossroads south of the sinclair mansion. equip a cowl, a blue wizard robe top and an iron scimitar.", a(Region.KANDARIN));
		CLUES.put("laugh in front of the gem store in ardougne market. equip a castlewars bracelet, a dragonstone amulet and a ring of forging.", a(Region.KANDARIN));
		CLUES.put("panic in the limestone mine. equip bronze platelegs, a steel pickaxe and a steel medium helmet.", a(Region.MISTHALIN));
		CLUES.put("panic by the mausoleum in morytania. wave before you speak to me. equip a mithril plate skirt, a maple longbow and no boots.", and(Region.MORYTANIA, Region.MISTHALIN));
		CLUES.put("panic on the wilderness volcano bridge. beware of double agents! equip any headband and crozier.", a(Region.WILDERNESS));
		CLUES.put("panic by the pilot on white wolf mountain. beware of double agents! equip mithril platelegs, a ring of life and a rune axe.", a(Region.ASGARNIA));
		CLUES.put("panic by the big egg where no one dare goes and the ground is burnt. beware of double agents! equip a dragon med helm, a toktz-ket-xil, a brine sabre, rune platebody and an uncharged amulet of glory.", and(Region.KARAMJA, Region.FREMENNIK));
		CLUES.put("panic at the area flowers meet snow. equip blue d'hide vambraces, a dragon spear and a rune plateskirt.", and(Region.ASGARNIA, Region.KANDARIN));
		CLUES.put("do a push up at the bank of the warrior's guild. beware of double agents! equip a dragon battleaxe, a dragon defender and a slayer helm of any kind.", and(Region.ASGARNIA, Region.MORYTANIA));
		CLUES.put("blow a raspberry in the bank of the warriors' guild. beware of double agents! equip a dragon battleaxe, a slayer helm of any kind and a dragon defender or avernic defender.", and(Region.ASGARNIA, Region.MORYTANIA));
		CLUES.put("blow a raspberry at the monkey cage in ardougne zoo. equip a studded leather body, bronze platelegs and a normal staff with no orb.", and(Region.KANDARIN, Region.MISTHALIN));
		CLUES.put("blow raspberries outside the entrance to keep le faye. equip a coif, an iron platebody and leather gloves.", a(Region.KANDARIN));
		CLUES.put("blow a raspberry in the fishing guild bank. beware of double agents! equip an elemental shield, blue dragonhide chaps and a rune warhammer.", a(Region.KANDARIN));
		CLUES.put("salute in the banana plantation. beware of double agents! equip a diamond ring, amulet of power, and nothing on your chest and legs.", a(Region.KARAMJA));
		CLUES.put("salute in the warriors' guild bank. equip only a black salamander.", and(Region.ASGARNIA, Region.WILDERNESS));
		CLUES.put("shrug in the mine near rimmington. equip a gold necklace, a gold ring and a bronze spear.", a(Region.ASGARNIA));
		CLUES.put("shrug in catherby bank. yawn before you talk to me. equip a maple longbow, green d'hide chaps and an iron med helm.", a(Region.KANDARIN));
		CLUES.put("shrug in the zamorak temple found in the eastern wilderness. beware of double agents! equip rune platelegs, an iron platebody and blue dragonhide vambraces.", and(a(Region.WILDERNESS), or(Region.KANDARIN, Region.TIRANNWN, Region.FREMENNIK, Region.ASGARNIA)));
		CLUES.put("spin at the crossroads north of rimmington. equip a green gnome hat, cream gnome top and leather chaps.", and(Region.ASGARNIA, Region.KANDARIN));
		CLUES.put("spin in draynor manor by the fountain. equip an iron platebody, studded leather chaps and a bronze full helmet.", a(Region.MISTHALIN));
		CLUES.put("spin in the varrock castle courtyard. equip a black axe, a coif and a ruby ring.", a(Region.MISTHALIN));
		CLUES.put("spin in west ardougne church. equip a dragon spear and red dragonhide chaps.", and(a(Region.KANDARIN), or(Region.KARAMJA, Region.FREMENNIK, Region.WILDERNESS, RareRegion.KANDARIN)));
		CLUES.put("spin on the bridge by the barbarian village. salute before you talk to me. equip purple gloves, a steel kiteshield and a mithril full helmet.", and(Region.MISTHALIN, Region.MORYTANIA));
		CLUES.put("stamp in the enchanted valley west of the waterfall. beware of double agents! equip a dragon axe.", and(Region.MISTHALIN, Region.FREMENNIK));
		CLUES.put("think in middle of the wheat field by the lumbridge mill. equip a blue gnome robetop, a turquoise gnome robe bottom and an oak shortbow.", and(Region.MISTHALIN, Region.KANDARIN));
		CLUES.put("think in the centre of the observatory. spin before you talk to me. equip a mithril chain body, green dragonhide chaps and a ruby amulet.", a(Region.KANDARIN));
		CLUES.put("wave along the south fence of the lumber yard. equip a hard leather body, leather chaps and a bronze axe.", and(a(Region.MISTHALIN), or(Region.KANDARIN, Region.DESERT)));
		CLUES.put("wave in the falador gem store. equip a mithril pickaxe, black platebody and an iron kiteshield.", a(Region.ASGARNIA));
		CLUES.put("wave on mudskipper point. equip a black cape, leather chaps and a steel mace.", a(Region.ASGARNIA));
		CLUES.put("wave on the northern wall of castle drakan. beware of double agents! wear a dragon sq shield, splitbark body and any boater.", and(Region.MORYTANIA, Region.KANDARIN, Region.FREMENNIK));
		CLUES.put("yawn in the 7th room of pyramid plunder. beware of double agents! equip a pharaoh sceptre and a full set of menaphite robes.", a(Region.DESERT));
		CLUES.put("yawn in the varrock library. equip a green gnome robe top, ham robe bottom and an iron warhammer.", and(Region.MISTHALIN, Region.KANDARIN));
		CLUES.put("yawn in draynor marketplace. equip studded leather chaps, an iron kiteshield and a steel longsword.", a(Region.MISTHALIN));
		CLUES.put("yawn in the castle wars lobby. shrug before you talk to me. equip a ruby amulet, a mithril scimitar and a wilderness cape.", and(Region.KANDARIN, Region.WILDERNESS));
		CLUES.put("yawn in the rogues' general store. beware of double agents! equip an adamant square shield, blue dragon vambraces and a rune pickaxe.", and(a(Region.WILDERNESS), or(Region.KANDARIN, Region.TIRANNWN, Region.FREMENNIK, Region.ASGARNIA)));
		CLUES.put("yawn at the top of trollheim. equip a lava battlestaff, black dragonhide vambraces and a mind shield.", and(and(Region.ASGARNIA, Region.KANDARIN), or(Region.MORYTANIA, Region.WILDERNESS, Region.DESERT, RareRegion.KANDARIN, RareRegion.ASGARNIA)));
		CLUES.put("swing a bullroarer at the top of the watchtower. beware of double agents! equip a dragon plateskirt, climbing boots and a dragon chainbody.", and(and(Region.ASGARNIA, Region.KARAMJA, Region.FREMENNIK), or(Region.KANDARIN, Region.DESERT)));
		CLUES.put("blow a raspberry at gypsy aris in her tent. equip a gold ring and a gold necklace.", a(Region.MISTHALIN));
		CLUES.put("bow to brugsen bursen at the grand exchange.", a(Region.MISTHALIN));
		CLUES.put("cheer at iffie nitter. equip a chef hat and a red cape.", a(Region.MISTHALIN));
		CLUES.put("clap at bob's brilliant axes. equip a bronze axe and leather boots.", a(Region.MISTHALIN));
		CLUES.put("panic at al kharid mine.", a(Region.DESERT));
		CLUES.put("spin at flynn's mace shop.", a(Region.ASGARNIA));
		// Fairy Rings
		CLUES.put("a i r 2 3 3 1", a(Region.MISTHALIN));
		CLUES.put("a i q 0 4 4 0", a(Region.ASGARNIA));
		CLUES.put("a l p 1 1 4 0", a(Region.FREMENNIK));
		CLUES.put("b l p 6 2 0 0", a(Region.KARAMJA));
		CLUES.put("b j r 1 1 2 3", and(Region.ASGARNIA, Region.KANDARIN, Region.KARAMJA));
		CLUES.put("b i p 7 0 1 3", a(Region.MORYTANIA));
		CLUES.put("c k p 0 2 2 4", a(Region.MISTHALIN));
		CLUES.put("d i p 8 5 1 1", a(Region.MISTHALIN));
		CLUES.put("d k s 2 3 1 0", a(Region.FREMENNIK));
		// Charlie
		CLUES.put("i need to cook charlie a trout.", a(Region.MISTHALIN));
		CLUES.put("i need to cook charlie a pike.", a(Region.MISTHALIN));
		CLUES.put("i need to fish charlie a herring.", a(Region.MISTHALIN));
		CLUES.put("i need to fish charlie a trout.", a(Region.MISTHALIN));
		CLUES.put("i need to mine charlie a piece of iron ore from an iron vein.", a(Region.MISTHALIN));
		CLUES.put("i need to smith charlie one iron dagger.", a(Region.MISTHALIN));
		CLUES.put("i need to craft charlie a leather body.", and(a(Region.MISTHALIN), or(Region.DESERT, Region.ASGARNIA, Region.KANDARIN, Region.MORYTANIA, Region.FREMENNIK)));
		CLUES.put("i need to craft charlie some leather chaps.", and(a(Region.MISTHALIN), or(Region.DESERT, Region.ASGARNIA, Region.KANDARIN, Region.MORYTANIA, Region.FREMENNIK)));
		// Sherlock
		CLUES.put("equip a dragon scimitar.", a(Region.KANDARIN));
		CLUES.put("enchant a piece of dragonstone jewellery.", a(Region.KANDARIN));
		CLUES.put("craft a nature rune.", a(Region.KARAMJA));
		CLUES.put("score a goal in skullball.", a(Region.MORYTANIA));
		CLUES.put("complete a lap of ape atoll agility course.", a(Region.MORYTANIA));
		CLUES.put("create a super defence potion.", a(Region.KANDARIN));
		CLUES.put("steal from a chest in ardougne castle.", a(Region.KANDARIN));
		CLUES.put("craft a green dragonhide body.", a(Region.KANDARIN));
		CLUES.put("string a yew longbow.", a(Region.KANDARIN));
		CLUES.put("slay a dust devil.", a(Region.DESERT));
		CLUES.put("catch a black warlock.", a(Region.KANDARIN));
		CLUES.put("catch a red chinchompa.", a(Region.KANDARIN));
		CLUES.put("mine a mithril ore.", a(Region.KANDARIN));
		CLUES.put("smith a mithril 2h sword.", a(Region.KANDARIN));
		CLUES.put("catch a raw shark.", a(Region.KANDARIN));
		CLUES.put("cut a yew log.", a(Region.KANDARIN));
		CLUES.put("fix a magical lamp in dorgesh-kaan.", a(Region.MISTHALIN));
		CLUES.put("burn a yew log.", a(Region.KANDARIN));
		CLUES.put("cook a swordfish", a(Region.KANDARIN));
		CLUES.put("craft multiple cosmic runes from a single essence.", a(Region.MISTHALIN));
		CLUES.put("plant a watermelon seed.", a(Region.KANDARIN));
		CLUES.put("activate the chivalry prayer.", a(Region.KANDARIN));
		CLUES.put("equip an abyssal whip in front of the abyssal demons of the slayer tower.", a(Region.MORYTANIA));
		CLUES.put("smith a runite med helm.", a(Region.KANDARIN));
		CLUES.put("teleport to a spirit tree you planted yourself.", or(Region.FREMENNIK, Region.ASGARNIA, Region.KARAMJA));
		CLUES.put("slay a nechryael in the slayer tower.", a(Region.MORYTANIA));
		CLUES.put("kill the spiritual, magic and godly whilst representing their own god.", or(Region.ASGARNIA, Region.WILDERNESS));
		CLUES.put("create an unstrung dragonstone amulet at a furnace.", a(Region.KANDARIN));
		CLUES.put("burn a magic log.", a(Region.KANDARIN));
		CLUES.put("complete a lap of the rellekka rooftop agility course whilst sporting the finest amount of grace.", and(Region.FREMENNIK, Region.ASGARNIA));
		CLUES.put("mix an anti-venom potion.", a(Region.TIRANNWN));
		CLUES.put("mine a piece of runite ore whilst sporting the finest mining gear.", a(Region.ASGARNIA));
		CLUES.put("steal a gem from the ardougne market.", a(Region.KANDARIN));
		CLUES.put("pickpocket an elf.", a(Region.TIRANNWN));
		CLUES.put("mix a ranging mix potion.", a(Region.KANDARIN));
		CLUES.put("fletch a rune dart.", a(Region.KANDARIN));
		CLUES.put("cremate a set of fiyr remains.", a(Region.MORYTANIA));
		CLUES.put("dissect a sacred eel.", a(Region.TIRANNWN));
		CLUES.put("craft a light orb in the dorgesh-kaan bank.", a(Region.MISTHALIN));
		CLUES.put("kill a fiyr shade inside mort'tons shade catacombs.", a(Region.MORYTANIA));
		// Falo
		CLUES.put("a blood red weapon, a strong curved sword, found on the island of primate lords.", a(Region.KANDARIN));
		CLUES.put("a book that preaches of some great figure, lending strength, might and vigour.", a(Region.FREMENNIK));
		CLUES.put("a bow of elven craft was made, it shimmers bright, but will soon fade.", a(Region.TIRANNWN));
		CLUES.put("a fiery axe of great inferno, when you use it, you'll wonder where the logs go.", and(Region.FREMENNIK, Region.KANDARIN));
		CLUES.put("a mark used to increase one's grace, found atop a seer's place.", a(Region.KANDARIN));
		CLUES.put("a molten beast with fiery breath, you acquire these with its death.", a(Region.WILDERNESS));
		CLUES.put("a shiny helmet of flight, to obtain this with melee, struggle you might.", a(Region.ASGARNIA));
		CLUES.put("a sword held in the other hand, red its colour, cyclops strength you must withstand.", a(Region.ASGARNIA));
		CLUES.put("a token used to kill mythical beasts, in hopes of a blade or just for an xp feast.", a(Region.ASGARNIA));
		CLUES.put("green is my favourite, mature ale i do love, this takes your herblore above.", or(Region.FREMENNIK, Region.MORYTANIA));
		CLUES.put("it can hold down a boat or crush a goat, this object, you see, is quite heavy.", a(Region.MORYTANIA));
		CLUES.put("it comes from the ground, underneath the snowy plain. trolls aplenty, with what looks like a mane.", and(Region.FREMENNIK, Region.ASGARNIA, Region.KANDARIN));
		CLUES.put("no attack to wield, only strength is required, made of obsidian, but with no room for a shield.", a(Region.KARAMJA));
		CLUES.put("penance healers runners and more, obtaining this body often gives much deplore.", a(Region.KANDARIN));
		CLUES.put("these gloves of white won't help you fight, but aid in cooking, they just might.", and(Region.FREMENNIK, Region.KANDARIN));
		CLUES.put("they come from some time ago, from a land unto the east. fossilised they have become, this small and gentle beast.", a(Region.MISTHALIN));
		CLUES.put("to slay a dragon you must first do, before this chest piece can be put on you.", a(Region.MISTHALIN));
		CLUES.put("vampyres are agile opponents, damaged best with a weapon of many components.", a(Region.MORYTANIA));
		// Special
		CLUES.put("buried beneath the ground, who knows where it's found. lucky for you, a man called reldo may have a clue.", or(Region.MISTHALIN, Region.ASGARNIA));
		CLUES.put("buried beneath the ground, who knows where it's found. lucky for you, a man called jorral may have a clue.", new NeverShowRequirements());
		
		// Coord Clues
		COORD_CLUES.put(new WorldPoint(2479, 3158, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2887, 3154, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2743, 3151, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(3184, 3150, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(3217, 3177, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(3007, 3144, 0), a(Region.ASGARNIA));
		COORD_CLUES.put(new WorldPoint(2896, 3119, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2697, 3207, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2679, 3110, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3510, 3074, 0), a(Region.DESERT));
		COORD_CLUES.put(new WorldPoint(3160, 3251, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(2643, 3252, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2322, 3061, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2875, 3046, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2849, 3033, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2848, 3296, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2583, 2990, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3179, 3344, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(2383, 3370, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3312, 3375, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(3121, 3384, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(3430, 3388, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2920, 3403, 0), a(Region.ASGARNIA));
		COORD_CLUES.put(new WorldPoint(2594, 2899, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2387, 3435, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2512, 3467, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2381, 3468, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3005, 3475, 0), a(Region.ASGARNIA));
		COORD_CLUES.put(new WorldPoint(2585, 3505, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3443, 3515, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2416, 3516, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3429, 3523, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2363, 3531, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2919, 3535, 0), a(Region.ASGARNIA));
		COORD_CLUES.put(new WorldPoint(3548, 3560, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2735, 3638, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2681, 3653, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2537, 3881, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2828, 3234, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(1247, 3726, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3770, 3898, 0), a(Region.MISTHALIN));
		// Hard
		COORD_CLUES.put(new WorldPoint(2209, 3161, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(2181, 3206, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(3081, 3209, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(3399, 3246, 0), a(Region.DESERT));
		COORD_CLUES.put(new WorldPoint(2699, 3251, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(3546, 3251, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(3544, 3256, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2841, 3267, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(3168, 3041, 0), a(Region.DESERT));
		COORD_CLUES.put(new WorldPoint(2542, 3031, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2581, 3030, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2961, 3024, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2339, 3311, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(3440, 3341, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2763, 2974, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(3138, 2969, 0), a(Region.DESERT));
		COORD_CLUES.put(new WorldPoint(2924, 2963, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2838, 2914, 0), and(Region.KARAMJA, Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(3441, 3419, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2950, 2902, 0), and(Region.KARAMJA, Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2775, 2891, 0), and(Region.KARAMJA, Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(3113, 3602, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2892, 3675, 0), a(Region.ASGARNIA));
		COORD_CLUES.put(new WorldPoint(3168, 3677, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2853, 3690, 0), a(Region.ASGARNIA));
		COORD_CLUES.put(new WorldPoint(3305, 3692, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3055, 3696, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3302, 3696, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2712, 3732, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2970, 3749, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3094, 3764, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3311, 3769, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3244, 3792, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3140, 3804, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2946, 3819, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3771, 3825, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(3013, 3846, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3058, 3884, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3290, 3889, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3770, 3897, 0), a(Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(2505, 3899, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(3285, 3942, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3159, 3959, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3039, 3960, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2987, 3963, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3189, 3963, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2341, 3697, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3143, 3774, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2992, 3941, 0), a(Region.WILDERNESS));
		// Elite
		COORD_CLUES.put(new WorldPoint(2357, 3151, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(3587, 3180, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2820, 3078, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(3811, 3060, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2180, 3282, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(2870, 2997, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(3302, 2988, 0), a(Region.DESERT));
		COORD_CLUES.put(new WorldPoint(2511, 2980, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2732, 3372, 0), and(Region.KARAMJA, Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(3573, 3425, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(3828, 2848, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(3225, 2838, 0), a(Region.DESERT));
		COORD_CLUES.put(new WorldPoint(3822, 3562, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(3603, 3564, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2936, 2721, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2697, 2705, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2778, 3678, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2827, 3740, 0), a(Region.ASGARNIA));
		COORD_CLUES.put(new WorldPoint(2359, 3799, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2194, 3807, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2700, 3808, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(3215, 3835, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3369, 3894, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2065, 3923, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(3188, 3933, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2997, 3953, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3380, 3963, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3051, 3736, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2316, 3814, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2872, 3937, 0), and(Region.FREMENNIK, Region.ASGARNIA, Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2484, 4016, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2222, 3331, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(3560, 3987, 0), and(Region.FREMENNIK, Region.MISTHALIN)); // TODO: Lithkren. Not entirely sure, since the area is DS2 specific
		// Master
		COORD_CLUES.put(new WorldPoint(2178, 3209, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(2155, 3100, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(2217, 3092, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(3830, 3060, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2834, 3271, 0), a(Region.KARAMJA));
		COORD_CLUES.put(new WorldPoint(2732, 3284, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(3622, 3320, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2303, 3328, 0), a(Region.TIRANNWN));
		COORD_CLUES.put(new WorldPoint(3570, 3405, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(2840, 3423, 0), a(Region.ASGARNIA)); // TODO: Water Obelisk. Could also need Kandarin
		COORD_CLUES.put(new WorldPoint(3604, 3564, 0), a(Region.MORYTANIA));
		COORD_CLUES.put(new WorldPoint(3085, 3569, 0), and(Region.WILDERNESS, Region.MISTHALIN));
		COORD_CLUES.put(new WorldPoint(2934, 2727, 0), a(Region.KANDARIN));
		COORD_CLUES.put(new WorldPoint(2538, 3739, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2951, 3820, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2202, 3825, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(2090, 3863, 0), a(Region.FREMENNIK));
		COORD_CLUES.put(new WorldPoint(3380, 3929, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3188, 3939, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(3304, 3941, 0), a(Region.WILDERNESS));
		COORD_CLUES.put(new WorldPoint(2994, 3961, 0), a(Region.WILDERNESS));

		// Regular Map Clues
		MAP_CLUES.put(ItemID.CLUE_SCROLL_EASY_12179, a(Region.DESERT));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_EASY_2713, a(Region.MISTHALIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_EASY_2716, a(Region.MISTHALIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_EASY_2719, a(Region.ASGARNIA));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_EASY_3516, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_EASY_3518, a(Region.MISTHALIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_EASY_7236, a(Region.ASGARNIA));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_2827, a(Region.MISTHALIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_3596, a(Region.ASGARNIA));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_3598, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_3599, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_3601, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_3602, a(Region.ASGARNIA));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_7286, a(Region.FREMENNIK));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_7288, a(Region.MORYTANIA));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_7290, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_7292, a(Region.FREMENNIK));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_MEDIUM_7294, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD, a(Region.MISTHALIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD_2729, a(Region.WILDERNESS));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD_3520, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD_3522, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD_3524, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD_3525, a(Region.WILDERNESS));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD_7239, a(Region.WILDERNESS));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_HARD_7241, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_ELITE_12130, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_ELITE_19782, a(Region.KANDARIN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_ELITE_19783, a(Region.TIRANNWN));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_ELITE_19785, a(Region.MORYTANIA));
		MAP_CLUES.put(ItemID.CLUE_SCROLL_ELITE_19786, a(Region.KANDARIN));

		// Beginner Map Clues
		BEGINNER_MAP_CLUES.put(WidgetID.BEGINNER_CLUE_MAP_CHAMPIONS_GUILD, a(Region.MISTHALIN));
		BEGINNER_MAP_CLUES.put(WidgetID.BEGINNER_CLUE_MAP_VARROCK_EAST_MINE, a(Region.MISTHALIN));
		BEGINNER_MAP_CLUES.put(WidgetID.BEGINNER_CLUE_MAP_DRAYNOR, a(Region.MISTHALIN));
		BEGINNER_MAP_CLUES.put(WidgetID.BEGINNER_CLUE_MAP_NORTH_OF_FALADOR, a(Region.ASGARNIA));
		BEGINNER_MAP_CLUES.put(WidgetID.BEGINNER_CLUE_MAP_WIZARDS_TOWER, a(Region.MISTHALIN));
	}
}

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
package io.hydrox.transmog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum HairMapping implements Mapping
{
	EMPTY("Empty", null, -1, -1),

	BALD_F("Bald", BodyKit.FEMME, 47775, 301),
	BUN_F("Bun", BodyKit.FEMME, 47799, 302),
	DREADLOCKS_F("Dreadlocks", BodyKit.FEMME, 47812, 303),
	LONG_F("Long", BodyKit.FEMME, 47839, 304),
	MEDIUM_F("Medium", BodyKit.FEMME, 47819, 305),
	PIGTAILS_F("Pigtails", BodyKit.FEMME, 47791, 306),
	SHORT_F("Short", BodyKit.FEMME, 47816, 307),
	CROPPED_F("Cropped", BodyKit.FEMME, 47829, 308),
	WILD_SPIKES_F("Wild spikes", BodyKit.FEMME, 47773, 309),
	SPIKY_F("Spikes", BodyKit.FEMME, 47827, 310),
	EARMUFFS_F("Earmuffs", BodyKit.FEMME, 47772, 311),
	SIDE_PONY_F("Side pony", BodyKit.FEMME, 47798, 374),
	CURLS_F("Curls", BodyKit.FEMME, 47785, 375),
	WIND_BRAIDS_F("Wind braids", BodyKit.FEMME, 47786, 376),
	PONYTAIL_F("Ponytail", BodyKit.FEMME, 47823, 377),
	BRAIDS_F("Braids", BodyKit.FEMME, 47811, 378),
	BUNCHES_F("Bunches", BodyKit.FEMME, 47818, 379),
	BOB_F("Bob", BodyKit.FEMME, 47822, 380),
	LAYERED_F("Layered", BodyKit.FEMME, 47807, 381),
	STRAIGHT_F("Straight", BodyKit.FEMME, 47833, 382),
	STRAIGHT_BRAIDS_F("Straight Braids", BodyKit.FEMME, 47810, 383),
	CURTAINS_F("Curtains", BodyKit.FEMME, 47793, 384),
	FRONT_SPLIT_F("Front split", BodyKit.FEMME, 47815, 408),
	TWO_BACK_F("Two-back", BodyKit.FEMME, 47832, 399),
	TONSURE_F("Tonsure", BodyKit.FEMME, 47828, 430),
	MOHAWK_F("Mohawk", BodyKit.FEMME, 47776, 431),
	QUIFF_F("Quiff", BodyKit.FEMME, 47813, 432),
	SAMURAI_F("Samurai", BodyKit.FEMME, 47781, 433),
	PRINCELY_F("Princely", BodyKit.FEMME, 47788, 434),
	LONG_CURTAINS_F("Long curtains", BodyKit.FEMME, 47834, 435),
	TOUSLED_F("Tousled", BodyKit.FEMME, 47802, 436),
	SIDE_WEDGE_F("Side wedge", BodyKit.FEMME, 47777, 437),
	FRONT_WEDGE_F("Front wedge", BodyKit.FEMME, 47838, 438),
	FRONT_SPIKES_F("Front spikes", BodyKit.FEMME, 47805, 439),
	FROHAWK_F("Frohawk", BodyKit.FEMME, 47804, 440),
	REAR_SKIRT_F("Rear skirt", BodyKit.FEMME, 47794, 441),
	QUEUE_F("Queue", BodyKit.FEMME, 47800, 442),
	MULLET_F("Mullet", BodyKit.FEMME, 47831, 410),
	UNDERCUT_F("Undercut", BodyKit.FEMME, 47817, 411),
	LOW_BUN_F("Low Bun", BodyKit.FEMME, 47779, 425),
	MESSY_BUN_F("Messy Bun", BodyKit.FEMME, 47835, 426),
	POMPADOUR_F("Pompadour", BodyKit.FEMME, 47801, 412),
	AFRO_F("Afro", BodyKit.FEMME, 47792, 413),
	SHORT_LOCS_F("Short locs", BodyKit.FEMME, 47771, 414),
	SPIKY_MOHAWK_F("Spiky Mohawk", BodyKit.FEMME, 47836, 415),
	SLICKED_MOHAWK_F("Slicked Mohawk", BodyKit.FEMME, 47806, 416),
	LONG_QUIFF_F("Long Quiff", BodyKit.FEMME, 47782, 417),
	SHORT_CHOPPY_F("Short Choppy", BodyKit.FEMME, 47809, 418),
	SIDE_AFRO_F("Side Afro", BodyKit.FEMME, 47790, 419),
	PUNK_F("Punk", BodyKit.FEMME, 47803, 420),
	HALF_SHAVED_F("Half-shaved", BodyKit.FEMME, 47824, 421),
	FREMENNIK_F("Fremennik", BodyKit.FEMME, 47796, 422),
	ELVEN_F("Elven", BodyKit.FEMME, 47768, 423),
	MEDIUM_COILS_F("Medium Coils", BodyKit.FEMME, 47780, 424),
	HIGH_PONYTAIL_F("High ponytail", BodyKit.FEMME, 47778, 427),
	PLAITS_F("Plaits", BodyKit.FEMME, 47821, 428),
	HIGH_BUNCHES_F("High Bunches", BodyKit.FEMME, 47774, 429),

	BALD_M("Bald", BodyKit.MASC, 47775, 256),
	DREADLOCKS_M("Dreadlocks", BodyKit.MASC, 47812, 257),
	LONG_M("Long", BodyKit.MASC, 47839, 258),
	MEDIUM_M("Medium", BodyKit.MASC, 47819, 259),
	TONSURE_M("Tonsure", BodyKit.MASC, 47828, 260),
	SHORT_M("Short", BodyKit.MASC, 47816, 261),
	CROPPED_M("Cropped", BodyKit.MASC, 47829, 262),
	WILD_SPIKES_M("Wild spikes", BodyKit.MASC, 47773, 263),
	SPIKES_M("Spikes", BodyKit.MASC, 47827, 264),
	MOHAWK_M("Mohawk", BodyKit.MASC, 47776, 265),
	WIND_BRAIDS_M("Wind braids", BodyKit.MASC, 47786, 385),
	QUIFF_M("Quiff", BodyKit.MASC, 47813, 386),
	SAMURAI_M("Samurai", BodyKit.MASC, 47781, 387),
	PRINCELY_M("Princely", BodyKit.MASC, 47788, 388),
	CURTAINS_M("Curtains", BodyKit.MASC, 47793, 389),
	LONG_CURTAINS_M("Long curtains", BodyKit.MASC, 47834, 390),
	FRONT_SPLIT_M("Front split", BodyKit.MASC, 47815, 407),
	TOUSLED_M("Tousled", BodyKit.MASC, 47802, 400),
	SIDE_WEDGE_M("Side wedge", BodyKit.MASC, 47777, 401),
	FRONT_WEDGE_M("Front wedge", BodyKit.MASC, 47838, 402),
	FRONT_SPIKES_M("Front spikes", BodyKit.MASC, 47805, 403),
	FROHAWK_M("Frohawk", BodyKit.MASC, 47804, 404),
	REAR_SKIRT_M("Rear skirt", BodyKit.MASC, 47794, 405),
	QUEUE_M("Queue", BodyKit.MASC, 47800, 406),
	BUN_M("Bun", BodyKit.MASC, 47799, 477),
	PIGTAILS_M("Pigtails", BodyKit.MASC, 47791, 478),
	EARMUFFS_M("Earmuffs", BodyKit.MASC, 47772, 479),
	SIDE_PONY_M("Side pony", BodyKit.MASC, 47798, 480),
	CURLS_M("Curls", BodyKit.MASC, 47785, 481),
	PONYTAIL_M("Ponytail", BodyKit.MASC, 47823, 482),
	BRAIDS_M("Braids", BodyKit.MASC, 47811, 483),
	BUNCHES_M("Bunches", BodyKit.MASC, 47818, 484),
	BOB_M("Bob", BodyKit.MASC, 47822, 485),
	LAYERED_M("Layered", BodyKit.MASC, 47807, 486),
	STRAIGHT_M("Straight", BodyKit.MASC, 47833, 487),
	STRAIGHT_BRAIDS_M("Straight Braids", BodyKit.MASC, 47810, 488),
	TWO_BACK_M("Two-back", BodyKit.MASC, 47832, 489),
	MULLET_M("Mullet", BodyKit.MASC, 47831, 457),
	UNDERCUT_M("Undercut", BodyKit.MASC, 47817, 458),
	LOW_BUN_M("Low Bun", BodyKit.MASC, 47779, 472),
	MESSY_BUN_M("Messy Bun", BodyKit.MASC, 47835, 473),
	POMPADOUR_M("Pompadour", BodyKit.MASC, 47801, 459),
	AFRO_M("Afro", BodyKit.MASC, 47792, 460),
	SHORT_LOCS_M("Short locs", BodyKit.MASC, 47771, 461),
	SPIKY_MOHAWK_M("Spiky Mohawk", BodyKit.MASC, 47836, 462),
	SLICKED_MOHAWK_M("Slicked Mohawk", BodyKit.MASC, 47806, 463),
	LONG_QUIFF_M("Long Quiff", BodyKit.MASC, 47782, 464),
	SHORT_CHOPPY_M("Short Choppy", BodyKit.MASC, 47809, 465),
	SIDE_AFRO_M("Side Afro", BodyKit.MASC, 47790, 466),
	PUNK_M("Punk", BodyKit.MASC, 47803, 467),
	HALF_SHAVED_M("Half-shaved", BodyKit.MASC, 47824, 468),
	FREMENNIK_M("Fremennik", BodyKit.MASC, 47796, 469),
	ELVEN_M("Elven", BodyKit.MASC, 47768, 470),
	MEDIUM_COILS_M("Medium Coils", BodyKit.MASC, 47780, 471),
	HIGH_PONYTAIL_M("High ponytail", BodyKit.MASC, 47778, 474),
	PLAITS_M("Plaits", BodyKit.MASC, 47821, 475),
	HIGH_BUNCHES_M("High Bunches", BodyKit.MASC, 47774, 476);


	private final String name;
	private final BodyKit bodyKit;
	private final int modelID;
	private final int kitID;

	private static final Map<Integer, HairMapping> FROM_KIT = new HashMap<>();
	private static final Map<Integer, HairMapping> FROM_MODEL = new HashMap<>();

	static
	{
		for (HairMapping mapping : values())
		{
			FROM_KIT.put(mapping.kitID, mapping);
			FROM_MODEL.put(mapping.modelID, mapping);
		}
	}

	public static HairMapping fromKitID(int kitID)
	{
		return FROM_KIT.get(kitID);
	}

	public static HairMapping fromModelID(int modelID)
	{
		return FROM_MODEL.get(modelID);
	}

	@Override
	public String prettyName()
	{
		return name;
	}

	@Override
	public BodyKit bodyKit()
	{
		return bodyKit;
	}

	@Override
	public int modelId()
	{
		return modelID;
	}

	@Override
	public int kitId()
	{
		return kitID;
	}
}

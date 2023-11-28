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

	BALD_F("Bald", Gender.FEMALE, 47775, 301),
	BUN_F("Bun", Gender.FEMALE, 47799, 302),
	DREADLOCKS_F("Dreadlocks", Gender.FEMALE, 47812, 303),
	LONG_F("Long", Gender.FEMALE, 47839, 304),
	MEDIUM_F("Medium", Gender.FEMALE, 47819, 305),
	PIGTAILS_F("Pigtails", Gender.FEMALE, 47791, 306),
	SHORT_F("Short", Gender.FEMALE, 47816, 307),
	CROPPED_F("Cropped", Gender.FEMALE, 47829, 308),
	WILD_SPIKES_F("Wild spikes", Gender.FEMALE, 47773, 309),
	SPIKY_F("Spikes", Gender.FEMALE, 47827, 310),
	EARMUFFS_F("Earmuffs", Gender.FEMALE, 47772, 311),
	SIDE_PONY_F("Side pony", Gender.FEMALE, 47798, 374),
	CURLS_F("Curls", Gender.FEMALE, 47785, 375),
	WIND_BRAIDS_F("Wind braids", Gender.FEMALE, 47786, 376),
	PONYTAIL_F("Ponytail", Gender.FEMALE, 47823, 377),
	BRAIDS_F("Braids", Gender.FEMALE, 47811, 378),
	BUNCHES_F("Bunches", Gender.FEMALE, 47818, 379),
	BOB_F("Bob", Gender.FEMALE, 47822, 380),
	LAYERED_F("Layered", Gender.FEMALE, 47807, 381),
	STRAIGHT_F("Straight", Gender.FEMALE, 47833, 382),
	STRAIGHT_BRAIDS_F("Straight Braids", Gender.FEMALE, 47810, 383),
	CURTAINS_F("Curtains", Gender.FEMALE, 47793, 384),
	FRONT_SPLIT_F("Front split", Gender.FEMALE, 47815, 408),
	TWO_BACK_F("Two-back", Gender.FEMALE, 47832, 399),
	TONSURE_F("Tonsure", Gender.FEMALE, 47828, 430),
	MOHAWK_F("Mohawk", Gender.FEMALE, 47776, 431),
	QUIFF_F("Quiff", Gender.FEMALE, 47813, 432),
	SAMURAI_F("Samurai", Gender.FEMALE, 47781, 433),
	PRINCELY_F("Princely", Gender.FEMALE, 47788, 434),
	LONG_CURTAINS_F("Long curtains", Gender.FEMALE, 47834, 435),
	TOUSLED_F("Tousled", Gender.FEMALE, 47802, 436),
	SIDE_WEDGE_F("Side wedge", Gender.FEMALE, 47777, 437),
	FRONT_WEDGE_F("Front wedge", Gender.FEMALE, 47838, 438),
	FRONT_SPIKES_F("Front spikes", Gender.FEMALE, 47805, 439),
	FROHAWK_F("Frohawk", Gender.FEMALE, 47804, 440),
	REAR_SKIRT_F("Rear skirt", Gender.FEMALE, 47794, 441),
	QUEUE_F("Queue", Gender.FEMALE, 47800, 442),
	MULLET_F("Mullet", Gender.FEMALE, 47831, 410),
	UNDERCUT_F("Undercut", Gender.FEMALE, 47817, 411),
	LOW_BUN_F("Low Bun", Gender.FEMALE, 47779, 425),
	MESSY_BUN_F("Messy Bun", Gender.FEMALE, 47835, 426),
	POMPADOUR_F("Pompadour", Gender.FEMALE, 47801, 412),
	AFRO_F("Afro", Gender.FEMALE, 47792, 413),
	SHORT_LOCS_F("Short locs", Gender.FEMALE, 47771, 414),
	SPIKY_MOHAWK_F("Spiky Mohawk", Gender.FEMALE, 47836, 415),
	SLICKED_MOHAWK_F("Slicked Mohawk", Gender.FEMALE, 47806, 416),
	LONG_QUIFF_F("Long Quiff", Gender.FEMALE, 47782, 417),
	SHORT_CHOPPY_F("Short Choppy", Gender.FEMALE, 47809, 418),
	SIDE_AFRO_F("Side Afro", Gender.FEMALE, 47790, 419),
	PUNK_F("Punk", Gender.FEMALE, 47803, 420),
	HALF_SHAVED_F("Half-shaved", Gender.FEMALE, 47824, 421),
	FREMENNIK_F("Fremennik", Gender.FEMALE, 47796, 422),
	ELVEN_F("Elven", Gender.FEMALE, 47768, 423),
	MEDIUM_COILS_F("Medium Coils", Gender.FEMALE, 47780, 424),
	HIGH_PONYTAIL_F("High ponytail", Gender.FEMALE, 47778, 427),
	PLAITS_F("Plaits", Gender.FEMALE, 47821, 428),
	HIGH_BUNCHES_F("High Bunches", Gender.FEMALE, 47774, 429),

	BALD_M("Bald", Gender.MALE, 47775, 256),
	DREADLOCKS_M("Dreadlocks", Gender.MALE, 47812, 257),
	LONG_M("Long", Gender.MALE, 47839, 258),
	MEDIUM_M("Medium", Gender.MALE, 47819, 259),
	TONSURE_M("Tonsure", Gender.MALE, 47828, 260),
	SHORT_M("Short", Gender.MALE, 47816, 261),
	CROPPED_M("Cropped", Gender.MALE, 47829, 262),
	WILD_SPIKES_M("Wild spikes", Gender.MALE, 47773, 263),
	SPIKES_M("Spikes", Gender.MALE, 47827, 264),
	MOHAWK_M("Mohawk", Gender.MALE, 47776, 265),
	WIND_BRAIDS_M("Wind braids", Gender.MALE, 47786, 385),
	QUIFF_M("Quiff", Gender.MALE, 47813, 386),
	SAMURAI_M("Samurai", Gender.MALE, 47781, 387),
	PRINCELY_M("Princely", Gender.MALE, 47788, 388),
	CURTAINS_M("Curtains", Gender.MALE, 47793, 389),
	LONG_CURTAINS_M("Long curtains", Gender.MALE, 47834, 390),
	FRONT_SPLIT_M("Front split", Gender.MALE, 47815, 407),
	TOUSLED_M("Tousled", Gender.MALE, 47802, 400),
	SIDE_WEDGE_M("Side wedge", Gender.MALE, 47777, 401),
	FRONT_WEDGE_M("Front wedge", Gender.MALE, 47838, 402),
	FRONT_SPIKES_M("Front spikes", Gender.MALE, 47805, 403),
	FROHAWK_M("Frohawk", Gender.MALE, 47804, 404),
	REAR_SKIRT_M("Rear skirt", Gender.MALE, 47794, 405),
	QUEUE_M("Queue", Gender.MALE, 47800, 406),
	BUN_M("Bun", Gender.MALE, 47799, 477),
	PIGTAILS_M("Pigtails", Gender.MALE, 47791, 478),
	EARMUFFS_M("Earmuffs", Gender.MALE, 47772, 479),
	SIDE_PONY_M("Side pony", Gender.MALE, 47798, 480),
	CURLS_M("Curls", Gender.MALE, 47785, 481),
	PONYTAIL_M("Ponytail", Gender.MALE, 47823, 482),
	BRAIDS_M("Braids", Gender.MALE, 47811, 483),
	BUNCHES_M("Bunches", Gender.MALE, 47818, 484),
	BOB_M("Bob", Gender.MALE, 47822, 485),
	LAYERED_M("Layered", Gender.MALE, 47807, 486),
	STRAIGHT_M("Straight", Gender.MALE, 47833, 487),
	STRAIGHT_BRAIDS_M("Straight Braids", Gender.MALE, 47810, 488),
	TWO_BACK_M("Two-back", Gender.MALE, 47832, 489),
	MULLET_M("Mullet", Gender.MALE, 47831, 457),
	UNDERCUT_M("Undercut", Gender.MALE, 47817, 458),
	LOW_BUN_M("Low Bun", Gender.MALE, 47779, 472),
	MESSY_BUN_M("Messy Bun", Gender.MALE, 47835, 473),
	POMPADOUR_M("Pompadour", Gender.MALE, 47801, 459),
	AFRO_M("Afro", Gender.MALE, 47792, 460),
	SHORT_LOCS_M("Short locs", Gender.MALE, 47771, 461),
	SPIKY_MOHAWK_M("Spiky Mohawk", Gender.MALE, 47836, 462),
	SLICKED_MOHAWK_M("Slicked Mohawk", Gender.MALE, 47806, 463),
	LONG_QUIFF_M("Long Quiff", Gender.MALE, 47782, 464),
	SHORT_CHOPPY_M("Short Choppy", Gender.MALE, 47809, 465),
	SIDE_AFRO_M("Side Afro", Gender.MALE, 47790, 466),
	PUNK_M("Punk", Gender.MALE, 47803, 467),
	HALF_SHAVED_M("Half-shaved", Gender.MALE, 47824, 468),
	FREMENNIK_M("Fremennik", Gender.MALE, 47796, 469),
	ELVEN_M("Elven", Gender.MALE, 47768, 470),
	MEDIUM_COILS_M("Medium Coils", Gender.MALE, 47780, 471),
	HIGH_PONYTAIL_M("High ponytail", Gender.MALE, 47778, 474),
	PLAITS_M("Plaits", Gender.MALE, 47821, 475),
	HIGH_BUNCHES_M("High Bunches", Gender.MALE, 47774, 476);


	private final String name;
	private final Gender gender;
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
	public Gender gender()
	{
		return gender;
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

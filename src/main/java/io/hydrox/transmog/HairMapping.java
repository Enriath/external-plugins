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
	BALD("Bald", Gender.FEMALE, 120, 301),
	BUN("Bun", Gender.FEMALE, 103, 302),
	DREADLOCKS("Dreadlocks", Gender.FEMALE, 110, 303),
	LONG("Long", Gender.FEMALE, 113, 304),
	MEDIUM("Medium", Gender.FEMALE, 115, 305),
	PIGTAILS("Pigtails", Gender.FEMALE, 118, 306),
	SHORT("Short", Gender.FEMALE, 114, 307),
	CROPPED("Cropped", Gender.FEMALE, 125, 308),
	WILD_SPIKES("Wild spikes", Gender.FEMALE, 105, 309),
	SPIKY("Spiky", Gender.FEMALE, 104, 310),
	EARMUFFS("Earmuffs", Gender.FEMALE, 28413, 311),
	SIDE_PONY("Side pony", Gender.FEMALE, 28407, 374),
	CURLS("Curls", Gender.FEMALE, 28409, 375),
	WIND_BRAIDS("Wind braids", Gender.FEMALE, 28417, 376),
	PONYTAIL("Ponytail", Gender.FEMALE, 28414, 377),
	BRAIDS("Braids", Gender.FEMALE, 28405, 378),
	BUNCHES("Bunches", Gender.FEMALE, 28412, 379),
	BOB("Bob", Gender.FEMALE, 28411, 380),
	LAYERED("Layered", Gender.FEMALE, 28404, 381),
	STRAIGHT("Straight", Gender.FEMALE, 28419, 382),
	STRAIGHT_BRAIDS("Straight braids", Gender.FEMALE, 28403, 383),
	CURTAINS("Curtains", Gender.FEMALE, 28410, 384),
	FRONT_SPLIT("Front split", Gender.FEMALE, 39840, 397),
	TWO_BACK("Two-back", Gender.FEMALE, 32846, 399),

	BALD_M("Bald", Gender.MALE, 63, 256),
	DREADLOCKS_M("Dreadlocks", Gender.MALE, 49, 257),
	LONG_M("Long", Gender.MALE, 52, 258),
	MEDIUM_M("Medium", Gender.MALE, 55, 259),
	TONSURE("Tonsure", Gender.MALE, 59, 260),
	SHORT_M("Short", Gender.MALE, 53, 261),
	CROPPED_M("Cropped", Gender.MALE, 67, 262),
	WILD_SPIKES_M("Wild spikes", Gender.MALE, 46, 263),
	SPIKES("Spikes", Gender.MALE, 45, 264),
	MOHAWK("Mohawk", Gender.MALE, 28383, 265),
	WIND_BRAIDS_M("Wind braids", Gender.MALE, 28385, 385),
	QUIFF("Quiff", Gender.MALE, 28376, 386),
	SAMURAI("Samurai", Gender.MALE, 28378, 387),
	PRINCELY("Princely", Gender.MALE, 28381, 388),
	CURTAINS_M("Curtains", Gender.MALE, 14371, 389),
	LONG_CURTAINS("Long curtains", Gender.MALE, 19101, 390),
	FRONT_SPLIT_M("Front split", Gender.MALE, 39833, 407),
	TOUSLED("Tousled", Gender.MALE, 32843, 400),
	SIDE_WEDGE("Side wedge", Gender.MALE, 32840, 401),
	FRONT_WEDGE("Front wedge", Gender.MALE, 32844, 402),
	FRONT_SPIKES("Front spikes", Gender.MALE, 32841, 403),
	FROHAWK("Frohawk", Gender.MALE, 32845, 404),
	REAR_SKIRT("Rear skirt", Gender.MALE, 32839, 405),
	QUEUE("Queue", Gender.MALE, 32842, 406);


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

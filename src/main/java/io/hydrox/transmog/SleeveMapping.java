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
public enum SleeveMapping implements Mapping
{
	EMPTY("Empty", null, -1, -1),

	SHORT_SLEEVES("Short sleeves", BodyKit.FEMME, 332, 317),
	BARE_ARMS("Bare arms", BodyKit.FEMME, 351, 318),
	MUSCLEY("Muscley", BodyKit.FEMME, 346, 319),
	LONG_SLEEVED("Long sleeved", BodyKit.FEMME, 348, 320),
	LARGE_CUFFS("Large cuffs", BodyKit.FEMME, 343, 321),
	FRILLY("Frilly", BodyKit.FEMME, 28364, 322),
	SWEATER("Sweater", BodyKit.FEMME, 28365, 351),
	WHITE_CUFFS("White cuffs", BodyKit.FEMME, 28368, 352),
	THIN_STRIPE("Thin stripe", BodyKit.FEMME, 28366, 353),
	TATTY("Tatty", BodyKit.FEMME, 28367, 354),
	BARE_SHOULDERS("Bare shoulders", BodyKit.FEMME, 28369, 355),

	REGULAR("Regular", BodyKit.MASC, 151, 282),
	MUSCLEBOUND("Musclebound", BodyKit.MASC, 167, 283),
	LOOSE_SLEEVED("Loose sleeved", BodyKit.MASC, 170, 284),
	LARGE_CUFFED("Large cuffed", BodyKit.MASC, 162, 285),
	THIN_SLEEVED("Thin sleeved", BodyKit.MASC, 163, 286),
	SHOULDER_PADS("Shoulder pads", BodyKit.MASC, 158, 287),
	THIN_STRIPE_M("Thin stripe", BodyKit.MASC, 28342, 288),
	THICK_STRIPE("Thick stripe", BodyKit.MASC, 28345, 340),
	WHITE_CUFFS_M("White cuffs", BodyKit.MASC, 28343, 341),
	PRINCELY("Princely", BodyKit.MASC, 28340, 342),
	TATTY_M("Tatty", BodyKit.MASC, 28344, 343),
	RIPPED("Ripped", BodyKit.MASC, 28341, 344);

	private final String name;
	private final BodyKit bodyKit;
	private final int modelID;
	private final int kitID;

	private static final Map<Integer, SleeveMapping> FROM_KIT = new HashMap<>();
	private static final Map<Integer, SleeveMapping> FROM_MODEL = new HashMap<>();

	static
	{
		for (SleeveMapping mapping : values())
		{
			FROM_KIT.put(mapping.kitID, mapping);
			FROM_MODEL.put(mapping.modelID, mapping);
		}
	}

	public static SleeveMapping fromKitID(int kitID)
	{
		return FROM_KIT.get(kitID);
	}

	public static SleeveMapping fromModelID(int modelID)
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

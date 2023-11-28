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
public enum FacialHairMapping implements Mapping
{
	EMPTY("Empty", -1, -1),

	NONE("None", 47825, 270),
	GOATEE("Goatee", 47814, 266),
	LONG("Long", 47837, 267),
	MEDIUM("Medium", 47820, 268),
	MOUSTACHE("Moustache", 47787, 269),
	SHORT("Short", 47767, 271),
	POINTY("Pointy", 47784, 272),
	SPLIT("Split", 47840, 273),
	HANDLEBAR("Handlebar", 47830, 367),
	MUTTON("Mutton", 47797, 368),
	FULL_MUTTON("Full Mutton", 47783, 369),
	BIG_MOUSTACHE("Big Moustache", 47770, 370),
	WAXED_MOUSTACHE("Waxed Moustache", 47795, 371),
	DALI("Dali", 47789, 372),
	VIZIER("Vizier", 47826, 373);

	private final String name;
	private final int modelID;
	private final int kitID;

	private static final Map<Integer, FacialHairMapping> FROM_KIT = new HashMap<>();
	private static final Map<Integer, FacialHairMapping> FROM_MODEL = new HashMap<>();

	static
	{
		for (FacialHairMapping mapping : values())
		{
			FROM_KIT.put(mapping.kitID, mapping);
			FROM_MODEL.put(mapping.modelID, mapping);
		}
	}

	public static FacialHairMapping fromKitID(int kitID)
	{
		return FROM_KIT.get(kitID);
	}

	public static FacialHairMapping fromModelID(int modelID)
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
		return BodyKit.MASC;
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

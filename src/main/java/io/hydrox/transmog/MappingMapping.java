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
import java.util.function.Function;

// Yo dawg
@Getter
@RequiredArgsConstructor
public enum MappingMapping
{
	HAIR(
		TransmogSlot.HAIR,
		HairMapping.class,
		HairMapping::fromKitID,
		HairMapping::fromModelID,
		600,
		10
	),
	JAW(
		TransmogSlot.JAW,
		FacialHairMapping.class,
		FacialHairMapping::fromKitID,
		FacialHairMapping::fromModelID,
		600,
		10
	),
	SLEEVES(
		TransmogSlot.SLEEVES,
		SleeveMapping.class,
		SleeveMapping::fromKitID,
		SleeveMapping::fromModelID,
		1200,
		55
	);

	private final TransmogSlot slot;
	private final Class<? extends Mapping> mapping;
	private final Function<Integer, Mapping> fromKit;
	private final Function<Integer, Mapping> fromModel;
	private final int modelZoom;
	private final int yOffset;

	private static final Map<TransmogSlot, MappingMapping> MAP = new HashMap<>();

	static
	{
		for (MappingMapping m : values())
		{
			MAP.put(m.slot, m);
		}
	}

	public static MappingMapping fromSlot(TransmogSlot slot)
	{
		return MAP.get(slot);
	}
}

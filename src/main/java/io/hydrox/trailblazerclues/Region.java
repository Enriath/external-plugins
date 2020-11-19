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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Region
{
	private static final HashMap<Integer, Region> IDS = new HashMap<>();

	static final Region MISTHALIN = create(RegionData.MISTHALIN);
	static final Region KARAMJA = create(RegionData.KARAMJA);
	static final Region ASGARNIA = create(RegionData.ASGARNIA);
	static final Region DESERT = create(RegionData.DESERT);
	static final Region MORYTANIA = create(RegionData.MORYTANIA);
	static final Region WILDERNESS = create(RegionData.WILDERNESS);
	static final Region KANDARIN = create(RegionData.KANDARIN);
	static final Region FREMENNIK = create(RegionData.FREMENNIK);
	static final Region TIRANNWN = create(RegionData.TIRANNWN);

	@Getter
	private final RegionData region;

	private static Region create(RegionData data)
	{
		Region n = new Region(data);
		IDS.put(data.getId(), n);
		return n;
	}

	static Region fromData(int id)
	{
		return IDS.get(id);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Region)
		{
			Region other = (Region) o;
			return other.getRegion() == getRegion();
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getRegion().name().hashCode();
	}
}

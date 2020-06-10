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
import net.runelite.client.util.Text;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum TransmogSlot
{
	HEAD(0, 156, SlotType.ITEM),
	CAPE(1, 157, SlotType.ITEM),
	NECK(2, 158, SlotType.ITEM),
	TORSO(4, 161, SlotType.ITEM),
	SHOULDERS(6, -1, SlotType.SPECIAL),
	LEGS(7, 163, SlotType.ITEM),
	HAIR(8, -1, SlotType.SPECIAL),
	HANDS(9, 164, SlotType.ITEM),
	BOOTS(10, 165, SlotType.ITEM),
	JAW(11, -1, SlotType.SPECIAL);

	enum SlotType
	{
		ITEM,
		SPECIAL;
	}

	private static Map<Integer, TransmogSlot> INDEXES = new HashMap<>();

	static
	{
		for (TransmogSlot kit : values())
		{
			INDEXES.put(kit.getKitIndex(), kit);
		}
	}

	@Getter
	private final int kitIndex;

	@Getter
	private final int spriteID;

	@Getter
	private final SlotType slotType;

	static TransmogSlot fromIndex(int idx)
	{
		return INDEXES.get(idx);
	}

	public String getName()
	{
		return Text.titleCase(this);
	}
}

/*
 * Copyright (c) 2021 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.coffincounter;

import lombok.Getter;
import net.runelite.api.ItemID;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Coffin
{
	BRONZE(3, ItemID.BRONZE_COFFIN, ItemID.OPEN_BRONZE_COFFIN),
	STEEL(8, ItemID.STEEL_COFFIN, ItemID.OPEN_STEEL_COFFIN),
	BLACK(14, ItemID.BLACK_COFFIN, ItemID.OPEN_BLACK_COFFIN),
	SILVER(20, ItemID.SILVER_COFFIN, ItemID.OPEN_SILVER_COFFIN),
	GOLD(28, ItemID.GOLD_COFFIN, ItemID.OPEN_GOLD_COFFIN);

	private final int maxRemains;
	private final int[] itemIDs;

	private static final Map<Integer, Coffin> MAP = new HashMap<>();

	Coffin(int maxRemains, int... itemIDs)
	{
		this.maxRemains = maxRemains;
		this.itemIDs = itemIDs;
	}

	static
	{
		for (Coffin c : values())
		{
			for (int id : c.itemIDs)
			{
				MAP.put(id, c);
			}
		}
	}

	static Set<Integer> ALL_COFFINS()
	{
		return MAP.keySet();
	}

	static Coffin getFromItem(int itemID)
	{
		return MAP.get(itemID);
	}
}

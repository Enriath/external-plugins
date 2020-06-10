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

import lombok.NoArgsConstructor;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
class TransmogPreset
{
	static final int PRESET_COUNT = 4;
	static final int EMPTY = -1;
	static final Integer IGNORE = null;

	private final Map<TransmogSlot, Integer> overrides = new HashMap<>();
	private final Map<TransmogSlot, String> names = new HashMap<>();

	void setDefaultSlot(TransmogSlot slot)
	{
		setSlot(slot, -1, "");
	}

	void setSlot(TransmogSlot slot, int id, String name)
	{
		overrides.put(slot, id);
		names.put(slot, name);
	}

	void clearSlot(TransmogSlot slot)
	{
		overrides.remove(slot);
		names.remove(slot);
	}

	Integer getId(TransmogSlot slot, boolean forKit)
	{
		final Integer transmog = overrides.get(slot);
		if (transmog == null)
		{
			return IGNORE;
		}
		else if (transmog == EMPTY)
		{
			return EMPTY;
		}
		else
		{
			return transmog + (forKit ? 512 : 0);
		}
	}

	String getName(TransmogSlot slot)
	{
		return names.get(slot);
	}

	String toConfig()
	{
		if (overrides.isEmpty())
		{
			return null;
		}
		final Map<TransmogSlot, Integer> merged = new HashMap<>();
		Arrays.asList(TransmogSlot.values()).forEach(tk -> merged.put(tk, null));
		merged.putAll(overrides);
		return merged.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(Map.Entry::getValue)
			.map(v -> v == null ? "null" : String.valueOf(v))
			.collect(Collectors.joining(","));
	}

	void fromConfig(String configStr)
	{
		List<String> values = Text.fromCSV(configStr);
		for (int i = 0; i < values.size(); i++)
		{
			String val = values.get(i);
			if (!val.equals("null"))
			{
				TransmogSlot slot = TransmogSlot.values()[i];
				overrides.put(slot, Integer.parseInt(val));
			}
		}
	}

	void loadNames(ItemManager itemManager)
	{
		for (Map.Entry<TransmogSlot, Integer> entry : overrides.entrySet())
		{
			Integer id = entry.getValue();
			if (id == null || id == -1)
			{
				continue;
			}
			names.put(entry.getKey(), itemManager.getItemComposition(id).getName());
		}
	}
}

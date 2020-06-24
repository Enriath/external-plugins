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

import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransmogPreset
{
	static final int PRESET_COUNT = 4;
	static final int EMPTY = -1;
	static final Integer IGNORE = null;

	private final Map<TransmogSlot, Integer> overrides = new HashMap<>();
	private final Map<TransmogSlot, String> names = new HashMap<>();

	public TransmogPreset()
	{
		for (TransmogSlot slot : TransmogSlot.values())
		{
			if (slot.getSlotType() == TransmogSlot.SlotType.SPECIAL)
			{
				setDefaultSlot(slot);
			}
		}
	}

	public void setDefaultSlot(TransmogSlot slot)
	{
		setSlot(slot, -1, "");
	}

	public void setSlot(TransmogSlot slot, int id, String name)
	{
		overrides.put(slot, id);
		names.put(slot, name);
	}

	public void clearSlot(TransmogSlot slot)
	{
		overrides.remove(slot);
		names.remove(slot);
	}

	public Integer getId(TransmogSlot slot, boolean forKit)
	{
		final Integer transmog = overrides.get(slot);
		if (transmog == null)
		{
			// Sleeves are the one outlier. Unlike Hair and Jaw, they don't have an empty option (like Bald or
			// Clean Shaven), and the Torso slot will either rely on the base look, or provide their own sleeves.
			// As a result, ignoring sleeves during transmog application will cause inconsistencies when overlaying
			// armour with arms (most of them) over something that doesn't (eg. chainbodies). Since this is the only
			// situation like this, a patch like this should be fine. Also, wow 5 lines of comments!
			if (slot.getSlotType() == TransmogSlot.SlotType.SPECIAL && forKit)
			{
				return 0;
			}

			return IGNORE;
		}
		else if (transmog == EMPTY)
		{
			return EMPTY;
		}
		else
		{
			if (slot.getSlotType() == TransmogSlot.SlotType.ITEM)
			{
				return transmog + (forKit ? 512 : 0);
			}

			if (forKit)
			{
				return transmog;
			}
			else
			{
				Function<Integer, Mapping> m = MappingMapping.fromSlot(slot).getFromKit();
				if (m == null)
				{
					return transmog;
				}
				return Optional.ofNullable(m.apply(transmog)).map(Mapping::modelId).orElse(-1);
			}
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
			TransmogSlot slot = TransmogSlot.values()[i];
			String val = values.get(i);
			if (val.equals("null"))
			{
				overrides.remove(slot);
			}
			else
			{
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
			final String name;
			final TransmogSlot slot = entry.getKey();
			if (slot.getSlotType() == TransmogSlot.SlotType.SPECIAL)
			{
				Mapping mapping = MappingMapping.fromSlot(slot).getFromKit().apply(id);
				if (mapping != null)
				{
					name = mapping.prettyName();
				}
				else
				{
					name = "Empty";
				}
			}
			else
			{
				name = itemManager.getItemComposition(id).getName();
			}

			names.put(entry.getKey(), name);
		}
	}
}

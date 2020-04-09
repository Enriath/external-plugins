/*
 * Copyright (c) 2020 Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.superiorsize;

import java.util.HashMap;
import java.util.Map;

enum Superior
{
	CRUSHING_HAND(3, "crawling hand"),
	CHASM_CRAWLER(3, "cave crawler"),
	SCREAMING_BANSHEE(3, "banshee"),
	SCREAMING_TWISTED_BANSHEE("banshee", 3, "twisted banshee"),
	GIANT_ROCKSLUG(3, "rockslug"),
	COCKATHRICE("cockatrice", 3, "cockatrice"),
	FLAMING_PYRELORD(2, "pyrefiend"),
	INFERNAL_PYRELORD("pyrefiends", 2, "pyrelord"),
	MONSTROUS_BASILISK(3, "basilisk"),
	MALEVOLENT_MAGE(2, "infernal mage"),
	INSATIABLE_BLOODVELD(3, "bloodveld"),
	INSATIABLE_MUTATED_BLOODVELD("bloodvelds", 3, "mutated bloodveld"),
	VITREOUS_JELLY("jellies", 2, "jelly"),
	VITREOUS_WARPED_JELLY("jellies", 3, "warped jelly"),
	CAVE_ABOMINATION(2, "cave horror"),
	ABHORRENT_SPECTRE(3, "aberrant spectre"),
	REPUGNANT_SPECTRE("aberrant spectre", 3, "deviant spectre"),
	BASILISK_SENTINEL("basilisk", 3, "basilisk knight"),
	CHOKE_DEVIL(2, "dust devil"),
	KING_KURASK(5, "kurask"),
	MARBLE_GARGOYLE(3, "gargoyle"),
	NECHRYARCH(2, "nechryael", "greater nechryael"),
	GREATER_ABYSSAL_DEMON(1, "abyssal demon"),
	NIGHT_BEAST(4, "dark beast"),
	NUCLEAR_SMOKE_DEVIL(2, "smoke devil");


	private static final Map<String, Superior> NAME_MAP = new HashMap<>();
	private static final Map<String, Superior> TASK_MAP = new HashMap<>();

	int size;
	String taskName;
	String[] names;

	Superior(String taskName, int size, String... names)
	{
		this.size = size;
		this.taskName = taskName;
		this.names = names;
	}

	Superior(int size, String... names)
	{
		this(names[0] + "s", size, names);
	}

	static
	{
		for (final Superior s : Superior.values())
		{
			for (final String name : s.names)
			{
				NAME_MAP.put(name, s);
			}
			TASK_MAP.put(s.taskName, s);
		}
	}

	public static Superior fromName(String name)
	{
		return NAME_MAP.getOrDefault(name.toLowerCase(), null);
	}

	public static Superior fromTask(String task)
	{
		return TASK_MAP.getOrDefault(task.toLowerCase(), null);
	}
}

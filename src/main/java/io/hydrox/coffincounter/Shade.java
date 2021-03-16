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
import org.apache.commons.text.WordUtils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum Shade
{
	/* Original Colours:
	(2, 62, 2)
	(25, 16, 7)
	(38, 3, 1)
	(9, 11, 94)
	(51, 45, 45)
	(248,176,8)
	 */

	LOAR(new Color(3, 214, 3)),
	PHRIN(new Color(0, 0, 0), Color.WHITE),
	RIYL(new Color(189, 3, 1)),
	ASYN(new Color(0, 114, 255)),
	FIYR(new Color(144, 132, 132)),
	URIUM(new Color(248,176,8));

	private final String name;
	private final Color colour;
	private final Color outline;

	private static final Map<String, Shade> NAME_MAP = new HashMap<>();

	Shade(Color colour)
	{
		this(colour, Color.BLACK);
	}

	Shade(Color colour, Color shadow)
	{
		this.name = WordUtils.capitalize(name().toLowerCase());
		this.colour = colour;
		this.outline = shadow;
	}

	static
	{
		for (Shade s : values())
		{
			NAME_MAP.put(s.getName(), s);
		}
	}

	static Shade fromName(String name)
	{
		return NAME_MAP.get(name);
	}
}

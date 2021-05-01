/*
 * Copyright (c) 2021, Hydrox6 <ikada@protonmail.ch>
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
package io.hydrox.masterscrollbook;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Scroll
{
	NARDAH("Nardah", 5672),
	DIGSITE("Digsite", 5673),
	FELDIP_HILLS("Feldip Hills", 5674),
	LUNAR_ISLE("Lunar Isle", 5675),
	MORTTON("Mort'ton", 5676),
	PEST_CONTROL("Pest Control", 5677),
	PISCATORIS("Piscatoris", 5678),
	TAI_BWO_WANNAI("Tai Bwo Wannai", 5679),
	IOWERTH_CAMP("Iowerth Camp", 5680),
	MOS_LE_HARMLESS("Mos Le' Harmless", 5681),
	LUMBERYARD("Lumberyard", 5682),
	ZUL_ANDRA("Zul'andra", 5683),
	KEY_MASTER("Key Master", 5684),
	REVENANT_CAVES("Revenant Caves", 6056),
	WATSON("Watson", 8253),
	;

	private final String name;
	private final int varbit;

	public int getIndex()
	{
		return this.ordinal();
	}

	public static Scroll get(int idx)
	{
		return values()[idx];
	}

	public static int size()
	{
		return values().length;
	}
}

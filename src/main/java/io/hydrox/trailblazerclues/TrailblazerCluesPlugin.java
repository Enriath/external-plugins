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

import com.google.common.collect.ImmutableSet;
import io.hydrox.trailblazerclues.requirements.ANDGroupedRequirements;
import io.hydrox.trailblazerclues.requirements.RegionRequirement;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@PluginDescriptor(
	name = "Trailblazer Clues",
	description = "Know what Trailblazer regions each clue requires",
	tags = {"league","leagues","trailblazer","two","2","II","clue","clues","clue scrolls","clue scroll","area","region","locked"}
)
public class TrailblazerCluesPlugin extends Plugin
{
	private static final Pattern ANAGRAMS_AND_CIPHERS = Pattern.compile("^th(is|e) (anagram|cipher) reveals who to speak to next: ");
	private static final String THREE_STEP_CRYPTIC_SPLITTER = "<br>\\s*<br>";
	private static final Set<Integer> CHOSEN_REGION_VARBITS = ImmutableSet.of(10662, 10663, 10664, 10665, 10666, 10667);

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TrailblazerCluesOverlay overlay;

	private int currentClueHash = 0;

	@Getter
	private RegionRequirement currentReqs = null;

	@Getter
	private int groupID = -1;

	@Getter
	private int childID = -1;

	@Getter
	private Set<Region> unlockedRegions = new HashSet<>();

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		updateUnlockedRegions();
	}

	private void updateUnlockedRegions()
	{
		unlockedRegions.clear();
		for (int varb : CHOSEN_REGION_VARBITS)
		{
			int regionID = client.getVarbitValue(varb);
			if (regionID == 0) continue;
			unlockedRegions.add(Region.fromID(regionID));
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Widget clueScrollText = client.getWidget(WidgetInfo.CLUE_SCROLL_TEXT);

		if (clueScrollText == null || clueScrollText.getText().hashCode() == currentClueHash)
		{
			return;
		}

		currentClueHash = clueScrollText.getText().hashCode();
		groupID = WidgetID.CLUE_SCROLL_GROUP_ID;
		childID = 2;

		//String rawText = clueScrollText.getText().toLowerCase();
		String rawText = "come brave adventurer, your sense is on fire. if you talk to me, it's an old god you desire.<br><br>you will have to fly high where a sword cannot help you.<br><br>a massive battle rages beneath so be careful when you dig by the large broken crossbow.";
		String[] threeStep = rawText.split(THREE_STEP_CRYPTIC_SPLITTER);

		if (threeStep.length == 3)
		{
			List<RegionRequirement> reqs = new ArrayList<>();
			for (String clue : threeStep)
			{
				String text = Text.sanitizeMultilineText(clue);
				reqs.add(Clue.fromText(text));
			}
			if (reqs.contains(null))
			{
				currentReqs = null;
			}
			else
			{
				currentReqs = new ANDGroupedRequirements(reqs.toArray(new RegionRequirement[0]));
			}
			return;
		}

		String text = Text.sanitizeMultilineText(rawText);

		if (text.contains("degrees") && text.contains("minutes"))
		{
			currentReqs = Clue.fromWorldPoint(coordinatesToWorldPoint(text));
			return;
		}

		text = ANAGRAMS_AND_CIPHERS.matcher(text).replaceAll("");

		currentReqs = Clue.fromText(text);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() < WidgetID.BEGINNER_CLUE_MAP_CHAMPIONS_GUILD
			|| event.getGroupId() > WidgetID.BEGINNER_CLUE_MAP_WIZARDS_TOWER)
		{
			return;
		}

		currentReqs = Clue.fromBeginnerMap(event.getGroupId());
		groupID = event.getGroupId();
		childID = 0;
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
	{
		if (event.getMenuOption() != null && event.getMenuOption().equals("Read"))
		{
			final ItemComposition itemComposition = itemManager.getItemComposition(event.getId());

			if (itemComposition != null && itemComposition.getName().startsWith("Clue scroll"))
			{
				currentClueHash = itemComposition.getId();
				currentReqs = Clue.fromMap(currentClueHash);
				groupID = 359;
				childID = 0;
			}
		}
	}

	/*
		Both coordinatesToWorldPoint functions from ClueScrollPlugin
	 */
	private WorldPoint coordinatesToWorldPoint(String text)
	{
		String[] splitText = text.split(" ");

		if (splitText.length != 10)
		{
			return null;
		}

		if (!splitText[1].startsWith("degree") || !splitText[3].startsWith("minute"))
		{
			return null;
		}

		int degY = Integer.parseInt(splitText[0]);
		int minY = Integer.parseInt(splitText[2]);

		if (splitText[4].equals("south"))
		{
			degY *= -1;
			minY *= -1;
		}

		int degX = Integer.parseInt(splitText[5]);
		int minX = Integer.parseInt(splitText[7]);

		if (splitText[9].equals("west"))
		{
			degX *= -1;
			minX *= -1;
		}

		return coordinatesToWorldPoint(degX, minX, degY, minY);
	}

	/**
	 * This conversion is explained on
	 * https://oldschool.runescape.wiki/w/Treasure_Trails/Guide/Coordinates
	 */
	private WorldPoint coordinatesToWorldPoint(int degX, int minX, int degY, int minY)
	{
		// Center of the Observatory
		int x2 = 2440;
		int y2 = 3161;

		x2 += degX * 32 + Math.round(minX / 1.875);
		y2 += degY * 32 + Math.round(minY / 1.875);

		return new WorldPoint(x2, y2, 0);
	}
}

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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PluginDescriptor(
	name = "Coffin Counter",
	description = "Track what remains your coffin contains.",
	tags = {"shades", "remains", "coffin", "count", "loar", "phrin", "riyl", "asyn", "fiyr", "urium", "morton", "mort'ton", "sacred"}
)
@Slf4j
public class CoffinCounterPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "coffincounter";
	private static final String CONFIG_STORED_KEY = "stored";

	private static final String CHECK_START = "Loar ";
	private static final Pattern CHECK_PATTERN = Pattern.compile("Loar (\\d{1,2}) / Phrin (\\d{1,2}) / Riyl (\\d{1,2}) / Asyn (\\d{1,2}) / Fiyr (\\d{1,2}) / Urium (\\d{1,2})");
	private static final String PICK_UP_START = "You put ";
	private static final Pattern PICK_UP_PATTERN = Pattern.compile("You put the (\\w+) remains into your open coffin\\.");

	@Inject
	private Client client;

	@Inject
	private CoffinCounterOverlay overlay;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Getter
	private final Map<Shade, Integer> stored = Arrays.stream(Shade.values())
		.collect(LinkedHashMap::new, (map, shade) -> map.put(shade, -1), Map::putAll);

	private Multiset<Integer> inventorySnapshot;
	private boolean checkFill;
	private boolean usingRemains;
	private boolean usingCoffin;

	@Override
	public void startUp()
	{
		loadCoffinState();
		overlayManager.add(overlay);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGING_IN)
		{
			loadCoffinState();
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		String message = Text.removeTags(event.getMessage());
		if (message.startsWith(CHECK_START))
		{
			Matcher m = CHECK_PATTERN.matcher(message);
			if (!m.matches())
			{
				return;
			}
			for (Shade s : Shade.values())
			{
				store(s, Integer.parseInt(m.group(s.ordinal() + 1)));
			}
			saveCoffinState();
		}
		else if (message.startsWith(PICK_UP_START))
		{
			Matcher m = PICK_UP_PATTERN.matcher(message);
			if (!m.matches())
			{
				return;
			}
			Shade shade = Shade.fromName(m.group(1));
			if (shade == null)
			{
				return;
			}
			store(shade, stored.get(shade) + 1);
			saveCoffinState();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Handle when the player uses remains on the coffin
		if (usingRemains || usingCoffin)
		{
			if (event.getMenuAction() == MenuAction.ITEM_USE_ON_WIDGET_ITEM &&
				((usingRemains && Coffin.getFromItem(event.getId()) != null)
				|| (usingCoffin && Shade.fromRemainsID(event.getId()) != null)))
			{
				inventorySnapshot = createInventorySnapshot();
				checkFill = true;
			}
			// There is no situation in which a menu action can be clicked but the item will stay selected, so cancel it
			usingRemains = false;
			usingCoffin = false;
		}
		// Handle when the fill option is used. CC_OP is for when the coffin is equipped.
		else if (event.getMenuOption().equals("Fill") &&
			((event.getMenuAction() == MenuAction.ITEM_FIRST_OPTION && Coffin.getFromItem(event.getId()) != null)
			|| (event.getMenuAction() == MenuAction.CC_OP && event.getId() == 2)))
		{
			inventorySnapshot = createInventorySnapshot();
			checkFill = true;
		}
		// First half of checking if the player has selected either remains or a coffin
		else if (event.getMenuAction() == MenuAction.ITEM_USE)
		{
			if (Shade.fromRemainsID(event.getId()) != null)
			{
				usingRemains = true;
			}
			else if (Coffin.getFromItem(event.getId()) != null)
			{
				usingCoffin = true;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!checkFill)
		{
			return;
		}
		// Check if there was a message saying that the coffin is full.
		final Widget chatDialogueSprite = client.getWidget(WidgetInfo.DIALOG_SPRITE_SPRITE);
		if (chatDialogueSprite != null && Coffin.getFromItem(chatDialogueSprite.getItemId()) != null)
		{
			checkFill = false;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INVENTORY.getId())
		{
			return;
		}
		Multiset<Integer> current = createInventorySnapshot();
		// Clear stored data if broken coffin exists, since only one coffin can be owned, making it impossible to have
		// both a real coffin and a broken one, let alone 2 real ones.
		if (current != null && current.contains(ItemID.BROKEN_COFFIN))
		{
			stored.replaceAll((s, v) -> -1);
			saveCoffinState();
			return;
		}

		if (!checkFill || inventorySnapshot == null)
		{
			return;
		}
		checkFill = false;

		Multiset<Integer> delta = Multisets.difference(inventorySnapshot, current);

		delta.forEachEntry((id, change) ->
		{
			Shade shade = Shade.fromRemainsID(id);
			store(shade, stored.get(shade) + change);
		});
		saveCoffinState();
	}

	private Multiset<Integer> createInventorySnapshot()
	{
		ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		if (itemContainer != null)
		{
			Multiset<Integer> snapshot = HashMultiset.create();
			Arrays.stream(itemContainer.getItems())
				.filter(item -> Shade.REMAINS().contains(item.getId()))
				.forEach(item -> snapshot.add(item.getId(), item.getQuantity()));
			return snapshot;
		}
		return null;
	}

	private void saveCoffinState()
	{
		configManager.setRSProfileConfiguration(
			CONFIG_GROUP,
			CONFIG_STORED_KEY,
			Text.toCSV(stored.values().stream().map(Object::toString).collect(Collectors.toList()))
		);
	}

	private void loadCoffinState()
	{
		String state = configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_STORED_KEY);
		if (state == null)
		{
			return;
		}
		List<String> states = Text.fromCSV(state);
		for (int i = 0; i < Shade.values().length; i++)
		{
			store(Shade.values()[i], Integer.parseInt(states.get(i)));
		}
	}

	private void store(Shade shade, int value)
	{
		stored.put(shade, value);
		updateInfoboxes();
	}

	private void updateInfoboxes()
	{
		infoBoxManager.removeIf(i -> i instanceof ShadeRemainsInfobox);
		for (Shade s : Shade.values())
		{
			infoBoxManager.addInfoBox(new ShadeRemainsInfobox(itemManager.getImage(s.getRemainsID()), s, this));
		}
	}
}

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
package io.hydrox.planksack;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@PluginDescriptor(
	name = "Plank Sack",
	description = "See the contents of your Plank Sack at a glance",
	tags = {"plank", "construction", "viewer", "mahogany", "teak", "oak", "homes"}
)
public class PlankSackPlugin extends Plugin
{
	private static final List<Integer> PLANKS = Arrays.asList(ItemID.PLANK, ItemID.OAK_PLANK, ItemID.TEAK_PLANK, ItemID.MAHOGANY_PLANK);
	private static final List<String> PLANK_NAMES = Arrays.asList("Plank", "Oak plank", "Teak plank", "Mahogany plank");
	private static final Set<Integer> MAHOGANY_HOMES_REPAIRS = Sets.newHashSet(
		39982, 39995, 40011, 40089, 40099, 40158, 40159, 40163, 40168, 40170, 40177, 40295, 40298);
	private static final int CONSTRUCTION_WIDGET_GROUP = 458;
	private static final int CONSTRUCTION_WIDGET_BUILD_IDX_START = 4;
	private static final int CONSTRUCTION_SUBWIDGET_MATERIALS = 3;
	private static final int CONSTRUCTION_SUBWIDGET_CANT_BUILD = 5;
	private static final int CONSTRUCTION_IMCANDO_MAHOGANY_HOMES = 8912;

	@Data
	private static class BuildMenuItem
	{
		private final Item[] planks;
		private final boolean canBuild;
	}

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlankSackConfig config;

	@Inject
	private PlankSackOverlay overlay;

	@Provides
	PlankSackConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlankSackConfig.class);
	}

	@Getter
	private int plankCount = -1;

	private PlankSackCounter plankSackCounter;

	private Multiset<Integer> inventorySnapshot;
	private boolean checkForUpdate = false;

	private int menuItemsToCheck = 0;
	private final List<BuildMenuItem> buildMenuItems = new ArrayList<>();

	private boolean watchForAnimations = false;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);

		plankSackCounter = new PlankSackCounter(itemManager.getImage(ItemID.PLANK_SACK), this);
		clientThread.invoke(() -> updateInfobox(client.getItemContainer(InventoryID.INVENTORY)));
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
		infoBoxManager.removeInfoBox(plankSackCounter);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			plankCount = Optional.ofNullable(configManager.getRSProfileConfiguration(PlankSackConfig.CONFIG_GROUP, PlankSackConfig.CONFIG_SACK_KEY, int.class)).orElse(-1);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INVENTORY.getId())
		{
			return;
		}

		if (checkForUpdate)
		{
			checkForUpdate = false;
			Multiset<Integer> currentInventory = createSnapshot(event.getItemContainer());
			Multiset<Integer> deltaMinus = Multisets.difference(currentInventory, inventorySnapshot);
			Multiset<Integer> deltaPlus = Multisets.difference(inventorySnapshot, currentInventory);
			deltaPlus.forEachEntry((id, c) -> plankCount += c);
			deltaMinus.forEachEntry((id, c) -> plankCount -= c);
			setPlankCount(plankCount);
		}

		updateInfobox(event.getItemContainer());
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(PlankSackConfig.CONFIG_GROUP))
		{
			clientThread.invoke(() -> updateInfobox(client.getItemContainer(InventoryID.INVENTORY)));
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Interact in inventory
		// Right click use in bank
		if ((event.getId() == ItemID.PLANK_SACK && (event.getMenuOption().equals("Fill") || event.getMenuOption().equals("Empty")))
		|| (event.getMenuTarget().equals("<col=ff9040>Plank sack</col>") && event.getMenuOption().equals("Use")))
		{
			inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
			checkForUpdate = true;
		}
		// Shift click use in bank
		else if (event.getMenuOption().equals("Use") && event.getId() == 9 && event.getMenuAction() == MenuAction.CC_OP)
		{
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if (inventory != null)
			{
				Item[] items = inventory.getItems();
				int idx = event.getActionParam();
				if (idx < items.length && items[idx].getId() == ItemID.PLANK_SACK)
				{
					inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
					checkForUpdate = true;
				}
			}
		}
		// Use plank on sack or sack on plank
		else if (event.getMenuOption().equals("Use") && event.getMenuAction() == MenuAction.ITEM_USE_ON_WIDGET_ITEM &&
			(event.getId() == ItemID.PLANK_SACK || PLANKS.contains(event.getId())))
		{
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if (inventory != null)
			{
				Item[] items = inventory.getItems();
				int idx = event.getSelectedItemIndex();
				if (idx < items.length)
				{
					int selectedItemID = items[idx].getId();
					if ((selectedItemID == ItemID.PLANK_SACK && PLANKS.contains(event.getId()))
						|| (PLANKS.contains(selectedItemID) && event.getId() == ItemID.PLANK_SACK))
					{
						inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
						checkForUpdate = true;
					}
				}

			}
		}
		else if (event.getMenuOption().equals("Repair"))
		{
			watchForAnimations = MAHOGANY_HOMES_REPAIRS.contains(event.getId());
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		// Construction menu option selected
		// Consutrction menu option selected with keybind
		if (event.getScriptId() != 1405 && event.getScriptId() != 1632)
		{
			return;
		}

		Widget widget = event.getScriptEvent().getSource();
		int idx = WidgetInfo.TO_CHILD(widget.getId()) - CONSTRUCTION_WIDGET_BUILD_IDX_START;
		if (idx >= buildMenuItems.size())
		{
			return;
		}
		BuildMenuItem item = buildMenuItems.get(idx);
		if (item != null && item.canBuild)
		{
			Multiset<Integer> snapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
			if (snapshot != null)
			{
				for (Item i : item.planks)
				{
					if (!snapshot.contains(i.getId()))
					{
						plankCount -= i.getQuantity();
					}
					else if (snapshot.count(i.getId()) < i.getQuantity())
					{
						plankCount -= i.getQuantity() - snapshot.count(i.getId());
					}
				}
				setPlankCount(plankCount);
			}
		}

		buildMenuItems.clear();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() != 1404)
		{
			return;
		}
		// Construction menu add object
		menuItemsToCheck += 1;
		// Cancel repair-based animation checking
		watchForAnimations = false;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (menuItemsToCheck > 0)
		{
			for (int i = 0; i < menuItemsToCheck; i++)
			{
				int idx = CONSTRUCTION_WIDGET_BUILD_IDX_START + i;
				Widget widget = client.getWidget(CONSTRUCTION_WIDGET_GROUP, idx);
				if (widget != null)
				{
					boolean canBuild = widget.getDynamicChildren()[CONSTRUCTION_SUBWIDGET_CANT_BUILD].isHidden();
					Widget materialWidget = widget.getDynamicChildren()[CONSTRUCTION_SUBWIDGET_MATERIALS];
					if (materialWidget != null)
					{
						String[] materialLines = materialWidget.getText().split("<br>");
						List<Item> materials = new ArrayList<>();
						for (String line : materialLines)
						{
							String[] data = line.split(": ");
							String name = data[0];
							int count = Integer.parseInt(data[1]);
							if (PLANK_NAMES.contains(name))
							{
								materials.add(new Item(PLANKS.get(PLANK_NAMES.indexOf(name)), count));
							}
						}
						buildMenuItems.add(new BuildMenuItem(materials.toArray(new Item[0]), canBuild));
					}
				}
			}
			menuItemsToCheck = 0;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (watchForAnimations && (event.getActor().getAnimation() == AnimationID.CONSTRUCTION || event.getActor().getAnimation() == CONSTRUCTION_IMCANDO_MAHOGANY_HOMES))
		{
			setPlankCount(plankCount - 1);
			watchForAnimations = false;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}
		if (event.getMessage().startsWith("Basic\u00A0planks:"))
		{
			String message = Text.removeTags(event.getMessage());
			setPlankCount(Arrays.stream(message.split(",")).mapToInt(s -> Integer.parseInt(s.split(":\u00A0")[1])).sum());
		}
		else if (event.getMessage().equals("You haven't got any planks that can go in the sack."))
		{
			checkForUpdate = false;
		}
		else if (event.getMessage().equals("Your sack is full."))
		{
			setPlankCount(28);
			checkForUpdate = false;
		}
		else if (event.getMessage().equals("Your sack is empty."))
		{
			setPlankCount(0);
			checkForUpdate = false;
		}
	}

	private void setPlankCount(int count)
	{
		plankCount = Ints.constrainToRange(count, 0, 28);
		configManager.setRSProfileConfiguration(PlankSackConfig.CONFIG_GROUP, PlankSackConfig.CONFIG_SACK_KEY, plankCount);
	}

	private Multiset<Integer> createSnapshot(ItemContainer container)
	{
		if (container == null)
		{
			return null;
		}
		Multiset<Integer> snapshot = HashMultiset.create();
		Arrays.stream(container.getItems())
			.filter(item -> PLANKS.contains(item.getId()))
			.forEach(i -> snapshot.add(i.getId(), i.getQuantity()));
		return snapshot;
	}

	private void updateInfobox(ItemContainer container)
	{
		infoBoxManager.removeInfoBox(plankSackCounter);
		if (container == null)
		{
			return;
		}
		boolean val = container.contains(ItemID.PLANK_SACK);
		if (val && config.showInfobox())
		{
			infoBoxManager.addInfoBox(plankSackCounter);
		}
	}

	Color getColour()
	{
		if (plankCount <= 0)
		{
			return Color.RED;
		}
		else if (plankCount < 14)
		{
			return Color.YELLOW;
		}
		else
		{
			return Color.WHITE;
		}
	}
}

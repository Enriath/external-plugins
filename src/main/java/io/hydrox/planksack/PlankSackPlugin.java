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
import java.util.HashMap;
import java.util.Map;
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
	private static final Map<Integer, Integer> MAHOGANY_HOMES_REPAIRS = new HashMap<>();

	static {
		MAHOGANY_HOMES_REPAIRS.put(39981, 4); // Bob large table
		MAHOGANY_HOMES_REPAIRS.put(39985, 2); // Bob bookcase (1)
		MAHOGANY_HOMES_REPAIRS.put(39986, 2); // Bob bookcase (2)
		MAHOGANY_HOMES_REPAIRS.put(39983, 2); // Bob cabinet (1)
		MAHOGANY_HOMES_REPAIRS.put(39984, 2); // Bob cabinet (2)
		MAHOGANY_HOMES_REPAIRS.put(39982, 1); // Bob clock
		MAHOGANY_HOMES_REPAIRS.put(39987, 2); // Bob wardrobe
		MAHOGANY_HOMES_REPAIRS.put(39988, 2); // Bob drawers
		MAHOGANY_HOMES_REPAIRS.put(40007, 2); // Leela small table (1)
		MAHOGANY_HOMES_REPAIRS.put(40008, 2); // Leela small table (2)
		MAHOGANY_HOMES_REPAIRS.put(40009, 3); // Leela table
		MAHOGANY_HOMES_REPAIRS.put(40010, 1); // Leela mirror
		MAHOGANY_HOMES_REPAIRS.put(40291, 3); // Leela double Bed
		MAHOGANY_HOMES_REPAIRS.put(40292, 2); // Leela cupboard
		MAHOGANY_HOMES_REPAIRS.put(40084, 3); // Tau table (1)
		MAHOGANY_HOMES_REPAIRS.put(40085, 3); // Tau table (2)
		MAHOGANY_HOMES_REPAIRS.put(40086, 2); // Tau cupboard
		MAHOGANY_HOMES_REPAIRS.put(40087, 2); // Tau shelves (1)
		MAHOGANY_HOMES_REPAIRS.put(40088, 2); // Tau shelves (2)
		MAHOGANY_HOMES_REPAIRS.put(40295, 1); // Tau hat stand
		MAHOGANY_HOMES_REPAIRS.put(40095, 2); // Larry drawers (1)
		MAHOGANY_HOMES_REPAIRS.put(40096, 2); // Larry drawers (2)
		MAHOGANY_HOMES_REPAIRS.put(40099, 1); // Larry clock
		MAHOGANY_HOMES_REPAIRS.put(40298, 1); // Larry hat stand
		MAHOGANY_HOMES_REPAIRS.put(40097, 3); // Larry table (1)
		MAHOGANY_HOMES_REPAIRS.put(40098, 3); // Larry table (2)
		MAHOGANY_HOMES_REPAIRS.put(40002, 3); // Mariah table
		MAHOGANY_HOMES_REPAIRS.put(40003, 2); // Mariah shelves
		MAHOGANY_HOMES_REPAIRS.put(40004, 2); // Mariah bed
		MAHOGANY_HOMES_REPAIRS.put(40005, 2); // Mariah small table (1)
		MAHOGANY_HOMES_REPAIRS.put(40006, 2); // Mariah small table (2)
		MAHOGANY_HOMES_REPAIRS.put(40288, 2); // Mariah cupboard
		MAHOGANY_HOMES_REPAIRS.put(40289, 1); // Mariah hat Stand
		MAHOGANY_HOMES_REPAIRS.put(40165, 2); // Ross drawers (1)
		MAHOGANY_HOMES_REPAIRS.put(40166, 2); // Ross drawers (2)
		MAHOGANY_HOMES_REPAIRS.put(40167, 3); // Ross double bed
		MAHOGANY_HOMES_REPAIRS.put(40168, 1); // Ross hat stand
		MAHOGANY_HOMES_REPAIRS.put(40169, 2); // Ross bed
		MAHOGANY_HOMES_REPAIRS.put(40170, 1); // Ross mirror
		MAHOGANY_HOMES_REPAIRS.put(39989, 3); // Jeff table
		MAHOGANY_HOMES_REPAIRS.put(39990, 2); // Jeff bookcase
		MAHOGANY_HOMES_REPAIRS.put(39991, 2); // Jeff shelves
		MAHOGANY_HOMES_REPAIRS.put(39992, 3); // Jeff bed
		MAHOGANY_HOMES_REPAIRS.put(39993, 2); // Jeff drawers
		MAHOGANY_HOMES_REPAIRS.put(39994, 2); // Jeff dresser
		MAHOGANY_HOMES_REPAIRS.put(39995, 1); // Jeff mirror
		MAHOGANY_HOMES_REPAIRS.put(39996, 1); // Jeff chair
		MAHOGANY_HOMES_REPAIRS.put(40011, 1); // Barbara clock
		MAHOGANY_HOMES_REPAIRS.put(40012, 3); // Barbara table
		MAHOGANY_HOMES_REPAIRS.put(40013, 2); // Barbara bed
		MAHOGANY_HOMES_REPAIRS.put(40014, 1); // Barbara chair (1)
		MAHOGANY_HOMES_REPAIRS.put(40015, 1); // Barbara chair (2)
		MAHOGANY_HOMES_REPAIRS.put(40294, 2); // Barbara drawers
		MAHOGANY_HOMES_REPAIRS.put(40156, 2); // Noella dresser
		MAHOGANY_HOMES_REPAIRS.put(40157, 2); // Noella cupboard
		MAHOGANY_HOMES_REPAIRS.put(40158, 1); // Noella hat Stand
		MAHOGANY_HOMES_REPAIRS.put(40159, 1); // Noella mirror
		MAHOGANY_HOMES_REPAIRS.put(40160, 2); // Noella drawers
		MAHOGANY_HOMES_REPAIRS.put(40161, 3); // Noella table (1)
		MAHOGANY_HOMES_REPAIRS.put(40162, 3); // Noella table (2)
		MAHOGANY_HOMES_REPAIRS.put(40163, 1); // Noella clock
		MAHOGANY_HOMES_REPAIRS.put(40089, 1); // Norman clock
		MAHOGANY_HOMES_REPAIRS.put(40090, 3); // Norman table
		MAHOGANY_HOMES_REPAIRS.put(40091, 3); // Norman double bed
		MAHOGANY_HOMES_REPAIRS.put(40092, 2); // Norman bookshelf
		MAHOGANY_HOMES_REPAIRS.put(40093, 2); // Norman drawers
		MAHOGANY_HOMES_REPAIRS.put(40094, 2); // Norman small table
		MAHOGANY_HOMES_REPAIRS.put(39997, 3); // Sarah table
		MAHOGANY_HOMES_REPAIRS.put(39998, 2); // Sarah bed
		MAHOGANY_HOMES_REPAIRS.put(39999, 2); // Sarah dresser
		MAHOGANY_HOMES_REPAIRS.put(40000, 2); // Sarah small table
		MAHOGANY_HOMES_REPAIRS.put(40001, 2); // Sarah shelves
		MAHOGANY_HOMES_REPAIRS.put(40171, 2); // Jess drawers (1)
		MAHOGANY_HOMES_REPAIRS.put(40172, 2); // Jess drawers (2)
		MAHOGANY_HOMES_REPAIRS.put(40173, 2); // Jess cabinet (1)
		MAHOGANY_HOMES_REPAIRS.put(40174, 2); // Jess cabinet (2)
		MAHOGANY_HOMES_REPAIRS.put(40175, 3); // Jess bed
		MAHOGANY_HOMES_REPAIRS.put(40176, 3); // Jess table
		MAHOGANY_HOMES_REPAIRS.put(40177, 1); // Jess clock
	}

	private static final Set<Integer> HALLOWED_SEPULCHRE_FIXES = Sets.newHashSet(39527, 39528);
	private static final int CONSTRUCTION_WIDGET_GROUP = 458;
	private static final int CONSTRUCTION_WIDGET_BUILD_IDX_START = 4;
	private static final int CONSTRUCTION_SUBWIDGET_MATERIALS = 3;
	private static final int CONSTRUCTION_SUBWIDGET_CANT_BUILD = 5;
	private static final int SCRIPT_CONSTRUCTION_OPTION_CLICKED = 1405;
	private static final int SCRIPT_CONSTRUCTION_OPTION_KEYBIND = 1632;
	private static final int SCRIPT_BUILD_CONSTRUCTION_MENU_ENTRY = 1404;

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
	private int buildCost = 0;

	private PlankSackCounter plankSackCounter;

	private Multiset<Integer> inventorySnapshot;
	private boolean checkForUpdate = false;

	private int menuItemsToCheck = 0;
	private final List<BuildMenuItem> buildMenuItems = new ArrayList<>();

	private boolean watchForAnimations = false;
	private int lastAnimation = -1;

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
			plankCount = Optional.ofNullable((Integer)configManager.getRSProfileConfiguration(PlankSackConfig.CONFIG_GROUP, PlankSackConfig.CONFIG_SACK_KEY, Integer.class)).orElse(-1);
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
		if (event.getWidget() != null)
		{
			// Interact in inventory
			// Right click use in bank
			if (event.getWidget().getItemId() == ItemID.PLANK_SACK &&
				(event.getMenuOption().equals("Fill") || event.getMenuOption().equals("Empty") || event.getMenuOption().equals("Use")))
			{
				inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
				checkForUpdate = true;
			}
			// Use plank on sack or sack on plank
			else if (event.getMenuOption().equals("Use")
				&& event.getMenuAction() == MenuAction.WIDGET_TARGET_ON_WIDGET
				&& client.getSelectedWidget() != null)
			{
				int firstSelectedItemID = client.getSelectedWidget().getItemId();
				int secondSelectedItemID = event.getWidget().getItemId();

				if ((firstSelectedItemID == ItemID.PLANK_SACK && PLANKS.contains(secondSelectedItemID))
					|| (PLANKS.contains(firstSelectedItemID) && secondSelectedItemID == ItemID.PLANK_SACK))
				{
					inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
					checkForUpdate = true;
				}
			}
		}
		else if ((event.getMenuOption().equals("Repair") || event.getMenuOption().equals("Build")) &&
				MAHOGANY_HOMES_REPAIRS.containsKey(event.getId()) && !watchForAnimations)
		{
			watchForAnimations = true;
			buildCost = MAHOGANY_HOMES_REPAIRS.get(event.getId());
			inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
		}
		else if (event.getMenuOption().equals("Fix") && HALLOWED_SEPULCHRE_FIXES.contains(event.getId()))
		{
			inventorySnapshot = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		// Construction menu option selected
		// Construction menu option selected with keybind
		if (event.getScriptId() != SCRIPT_CONSTRUCTION_OPTION_CLICKED
			&& event.getScriptId() != SCRIPT_CONSTRUCTION_OPTION_KEYBIND)
		{
			return;
		}

		Widget widget = event.getScriptEvent().getSource();
		int idx = TO_CHILD(widget.getId()) - CONSTRUCTION_WIDGET_BUILD_IDX_START;
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
		if (event.getScriptId() != SCRIPT_BUILD_CONSTRUCTION_MENU_ENTRY)
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
		if (menuItemsToCheck <= 0)
		{
			return;
		}

		for (int i = 0; i < menuItemsToCheck; i++)
		{
			int idx = CONSTRUCTION_WIDGET_BUILD_IDX_START + i;
			Widget widget = client.getWidget(CONSTRUCTION_WIDGET_GROUP, idx);
			if (widget == null)
			{
				continue;
			}

			boolean canBuild = widget.getDynamicChildren()[CONSTRUCTION_SUBWIDGET_CANT_BUILD].isHidden();
			Widget materialWidget = widget.getDynamicChildren()[CONSTRUCTION_SUBWIDGET_MATERIALS];
			if (materialWidget == null)
			{
				continue;
			}

			String[] materialLines = materialWidget.getText().split("<br>");
			List<Item> materials = new ArrayList<>();
			for (String line : materialLines)
			{
				String[] data = line.split(": ");
				if (data.length < 2)
				{
					continue;
				}

				String name = data[0];
				int count = Integer.parseInt(data[1]);
				if (PLANK_NAMES.contains(name))
				{
					materials.add(new Item(PLANKS.get(PLANK_NAMES.indexOf(name)), count));
				}
			}
			buildMenuItems.add(new BuildMenuItem(materials.toArray(new Item[0]), canBuild));
		}
		menuItemsToCheck = 0;
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!watchForAnimations || event.getActor() != client.getLocalPlayer() || client.getLocalPlayer() == null)
		{
			return;
		}

		int anim = client.getLocalPlayer().getAnimation();
		if ((lastAnimation == AnimationID.CONSTRUCTION || lastAnimation == AnimationID.CONSTRUCTION_IMCANDO)
			&& anim != lastAnimation)
		{
			Multiset<Integer> current = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
			Multiset<Integer> delta = Multisets.difference(inventorySnapshot, current);

			int planksUsedFromInventory = delta.size();
			int planksUsedFromSack = buildCost - planksUsedFromInventory;

			if(planksUsedFromSack > 0)
			{
				setPlankCount(plankCount - planksUsedFromSack);
			}

			watchForAnimations = false;
			lastAnimation = -1;
			buildCost = 0;
		}
		else
		{
			lastAnimation = anim;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() == ChatMessageType.SPAM)
		{
			int maxUsedPlanks;
			// Hallowed Sepulchre
			if (event.getMessage().equals("You repair the broken bridge."))
			{
				maxUsedPlanks = 2;
			}
			// Port Piscarilius Cranes
			else if (event.getMessage().equals("You successfully repair the fishing crane."))
			{
				maxUsedPlanks = 3;
			}
			else
			{
				return;
			}

			// Any planks in the inventory are prioritised, and are removed after this chat message.
			// Therefore, we need to delay this check by a bit to make sure it picks up inventory planks being used.
			clientThread.invokeLater(() -> {
				Multiset<Integer> current = createSnapshot(client.getItemContainer(InventoryID.INVENTORY));
				Multiset<Integer> delta = Multisets.difference(inventorySnapshot, current);
				int usedPlanks = Math.max(0, maxUsedPlanks - delta.size());
				if (usedPlanks > 0)
				{
					setPlankCount(plankCount - usedPlanks);
				}
			});
		}

		// Sack checking/updating
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}
		final String message = event.getMessage();
		if (message.startsWith("Basic\u00A0planks:"))
		{
			String stripped = Text.removeTags(event.getMessage());
			setPlankCount(Arrays.stream(stripped.split(",")).mapToInt(s -> Integer.parseInt(s.split(":\u00A0")[1])).sum());
		}
		else if (message.equals("You haven't got any planks that can go in the sack."))
		{
			checkForUpdate = false;
		}
		else if (message.equals("Your sack is full."))
		{
			setPlankCount(28);
			checkForUpdate = false;
		}
		else if (message.equals("Your sack is empty."))
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

	private static int TO_CHILD(int id)
	{
		return id & 0xFFFF;
	}
}

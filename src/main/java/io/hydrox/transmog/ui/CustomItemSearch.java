package io.hydrox.transmog.ui;

import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import io.hydrox.transmog.TransmogSlot;
import lombok.Getter;
import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Singleton
public class CustomItemSearch extends ChatboxTextInput
{
	private static final int ICON_HEIGHT = 32;
	private static final int ICON_WIDTH = 36;
	private static final int PADDING = 6;
	private static final int MAX_RESULTS = 24;
	private static final int FONT_SIZE = 16;
	private static final int HOVERED_OPACITY = 128;

	private final ChatboxPanelManager chatboxPanelManager;
	private final ItemManager itemManager;
	private final Client client;

	private Map<Integer, ItemComposition> results = new LinkedHashMap<>();
	private String tooltipText;
	private int index = -1;

	private TransmogSlot slot;

	@Getter
	private BiConsumer<Integer, String> onItemSelected;

	@Value
	private static class ItemIcon
	{
		private final int modelId;
		private final short[] colorsToReplace;
		private final short[] texturesToReplace;
	}

	@Inject
	private CustomItemSearch(ChatboxPanelManager chatboxPanelManager, ClientThread clientThread,
							 ItemManager itemManager, Client client)
	{
		super(chatboxPanelManager, clientThread);
		this.chatboxPanelManager = chatboxPanelManager;
		this.itemManager = itemManager;
		this.client = client;

		lines(1);
		onChanged(searchString ->
			clientThread.invokeLater(() ->
			{
				filterResults();
				update();
			}));
	}

	@Override
	protected void update()
	{
		Widget container = chatboxPanelManager.getContainerWidget();
		container.deleteAllChildren();

		Widget promptWidget = container.createChild(-1, WidgetType.TEXT);
		promptWidget.setText(getPrompt());
		promptWidget.setTextColor(0x800000);
		promptWidget.setFontId(getFontID());
		promptWidget.setOriginalX(0);
		promptWidget.setOriginalY(5);
		promptWidget.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		promptWidget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		promptWidget.setOriginalHeight(FONT_SIZE);
		promptWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
		promptWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
		promptWidget.setWidthMode(WidgetSizeMode.MINUS);
		promptWidget.revalidate();

		buildEdit(0, 5 + FONT_SIZE, container.getWidth(), FONT_SIZE);

		Widget separator = container.createChild(-1, WidgetType.LINE);
		separator.setOriginalX(0);
		separator.setOriginalY(8 + (FONT_SIZE * 2));
		separator.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		separator.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		separator.setOriginalHeight(0);
		separator.setOriginalWidth(16);
		separator.setWidthMode(WidgetSizeMode.MINUS);
		separator.setTextColor(0x666666);
		separator.revalidate();

		int x = PADDING;
		int y = PADDING * 3;
		int idx = 0;
		for (ItemComposition itemComposition : results.values())
		{
			Widget item = container.createChild(-1, WidgetType.GRAPHIC);
			item.setXPositionMode(WidgetPositionMode.ABSOLUTE_LEFT);
			item.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
			item.setOriginalX(x);
			item.setOriginalY(y + FONT_SIZE * 2);
			item.setOriginalHeight(ICON_HEIGHT);
			item.setOriginalWidth(ICON_WIDTH);
			item.setName("<col=ff9040>" + itemComposition.getName());
			item.setItemId(itemComposition.getId());
			item.setItemQuantity(10000);
			item.setItemQuantityMode(ItemQuantityMode.NEVER);
			item.setBorderType(1);
			item.setAction(0, tooltipText);
			item.setHasListener(true);

			if (index == idx)
			{
				item.setOpacity(HOVERED_OPACITY);
			}
			else
			{
				item.setOnMouseOverListener((JavaScriptCallback) ev -> item.setOpacity(HOVERED_OPACITY));
				item.setOnMouseLeaveListener((JavaScriptCallback) ev -> item.setOpacity(0));
			}

			item.setOnOpListener((JavaScriptCallback) ev ->
			{
				if (onItemSelected != null)
				{
					onItemSelected.accept(itemComposition.getId(), itemComposition.getName());
				}

				chatboxPanelManager.close();
			});

			x += ICON_WIDTH + PADDING;
			if (x + ICON_WIDTH >= container.getWidth())
			{
				y += ICON_HEIGHT + PADDING;
				x = PADDING;
			}

			item.revalidate();
			++idx;
		}
	}

	@Override
	public void keyPressed(KeyEvent ev)
	{
		switch (ev.getKeyCode())
		{
			case KeyEvent.VK_ENTER:
				ev.consume();
				if (index > -1)
				{
					if (onItemSelected != null)
					{
						final int id = results.keySet().toArray(new Integer[results.size()])[index];
						onItemSelected.accept(id, results.get(id).getName());
					}

					chatboxPanelManager.close();
				}
				break;
			case KeyEvent.VK_TAB:
			case KeyEvent.VK_RIGHT:
				ev.consume();
				if (!results.isEmpty())
				{
					index++;
					if (index >= results.size())
					{
						index = 0;
					}
					clientThread.invokeLater(this::update);
				}
				break;
			case KeyEvent.VK_LEFT:
				ev.consume();
				if (!results.isEmpty())
				{
					index--;
					if (index < 0)
					{
						index = results.size() - 1;
					}
					clientThread.invokeLater(this::update);
				}
				break;
			case KeyEvent.VK_UP:
				ev.consume();
				if (results.size() >= (MAX_RESULTS / 2))
				{
					index -= MAX_RESULTS / 2;
					if (index < 0)
					{
						if (results.size() == MAX_RESULTS)
						{
							index += results.size();
						}
						else
						{
							index += MAX_RESULTS;
						}
						index = Ints.constrainToRange(index, 0, results.size() - 1);
					}

					clientThread.invokeLater(this::update);
				}
				break;
			case KeyEvent.VK_DOWN:
				ev.consume();
				if (results.size() >= (MAX_RESULTS / 2))
				{
					index += MAX_RESULTS / 2;
					if (index >= MAX_RESULTS)
					{
						if (results.size() == MAX_RESULTS)
						{
							index -= results.size();
						}
						else
						{
							index -= MAX_RESULTS;
						}
						index = Ints.constrainToRange(index, 0, results.size() - 1);
					}

					clientThread.invokeLater(this::update);
				}
				break;
			default:
				super.keyPressed(ev);
		}
	}

	@Override
	protected void close()
	{
		// Clear search string when closed
		value("");
		results.clear();
		index = -1;
		super.close();
	}

	@Override
	@Deprecated
	public ChatboxTextInput onDone(Consumer<String> onDone)
	{
		throw new UnsupportedOperationException();
	}

	private void filterResults()
	{
		results.clear();
		index = -1;

		String search = getValue().toLowerCase();
		if (search.isEmpty())
		{
			return;
		}

		Set<ItemIcon> itemIcons = new HashSet<>();
		for (int i = 0; i < client.getItemCount() && results.size() < MAX_RESULTS; i++)
		{
			ItemComposition itemComposition = itemManager.getItemComposition(itemManager.canonicalize(i));
			ItemStats itemStats = itemManager.getItemStats(itemComposition.getId(), false);
			if (itemStats == null || !itemStats.isEquipable())
			{
				continue;
			}
			ItemEquipmentStats stats = itemStats.getEquipment();
			String name = itemComposition.getName().toLowerCase();

			// The client assigns "null" to item names of items it doesn't know about
			// and the item might already be in the results from canonicalize
			if (!name.equals("null") && name.contains(search) && !results.containsKey(itemComposition.getId()) && stats.getSlot() == slot.getKitIndex())
			{
				// Check if the results already contain the same item image
				ItemIcon itemIcon = new ItemIcon(itemComposition.getInventoryModel(),
					itemComposition.getColorToReplaceWith(), itemComposition.getTextureToReplaceWith());
				if (itemIcons.contains(itemIcon))
				{
					continue;
				}

				itemIcons.add(itemIcon);
				results.put(itemComposition.getId(), itemComposition);
			}
		}
	}

	public CustomItemSearch onItemSelected(BiConsumer<Integer, String> onItemSelected)
	{
		this.onItemSelected = onItemSelected;
		return this;
	}

	public CustomItemSearch tooltipText(final String text)
	{
		tooltipText = text;
		return this;
	}

	public CustomItemSearch prompt(String prompt)
	{
		super.prompt(prompt);
		return this;
	}

	public CustomItemSearch build()
	{
		super.build();
		return this;
	}

	public CustomItemSearch slot(TransmogSlot slot)
	{
		this.slot = slot;
		return this;
	}
}
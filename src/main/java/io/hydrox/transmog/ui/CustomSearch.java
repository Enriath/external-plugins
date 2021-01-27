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
package io.hydrox.transmog.ui;

import com.google.common.primitives.Ints;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Based on ChatboxItemSearch.java
public abstract class CustomSearch extends ChatboxTextInput
{
	protected static final int ICON_HEIGHT = 32;
	protected static final int ICON_WIDTH = 36;
	protected static final int PADDING = 6;
	protected static final int MAX_RESULTS = 24;
	protected static final int FONT_SIZE = 16;
	protected static final int HOVERED_OPACITY = 128;

	protected final ChatboxPanelManager chatboxPanelManager;
	protected final Client client;

	protected List<Object> results = new ArrayList<>();

	protected String tooltipText;
	protected int index = -1;
	protected int page = 0;

	protected CustomSearch(ChatboxPanelManager chatboxPanelManager, ClientThread clientThread, Client client)
	{
		super(chatboxPanelManager, clientThread);
		this.chatboxPanelManager = chatboxPanelManager;
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

		// Make sure the page is always valid, even when narrowing a search while looking at a higher page
		// This is preferable to resetting the page to 1 each time.
		int lastPage = results.size() / MAX_RESULTS;
		if (page > lastPage)
		{
			page = lastPage;
		}

		Widget pageBack = container.createChild(-1, WidgetType.GRAPHIC);
		pageBack.setHidden(page == 0 || results.isEmpty());
		pageBack.setAction(0, "Previous Page");
		pageBack.setSpriteId(SpriteID.BACK_ARROW_BUTTON_SMALL);
		pageBack.setOriginalWidth(20);
		pageBack.setOriginalHeight(20);
		pageBack.setOriginalX(container.getWidth() - 90);
		pageBack.setOriginalY(0);
		pageBack.setOnOpListener((JavaScriptCallback) e ->
		{
			page = page <= 0 ? 0 : page - 1;
			update();
		});
		pageBack.setHasListener(true);
		pageBack.revalidate();

		Widget pageNum = container.createChild(-1, WidgetType.TEXT);
		// We don't want to show the page number if there's only one page
		pageNum.setHidden((isSearchOnLastPage() && page == 0) || results.isEmpty());
		pageNum.setFontId(495);
		pageNum.setText((page + 1) + "");
		pageNum.setOriginalWidth(20);
		pageNum.setOriginalHeight(20);
		pageNum.setOriginalX(container.getWidth() - 70);
		pageNum.setOriginalY(2);
		pageNum.setXTextAlignment(WidgetTextAlignment.CENTER);
		pageNum.setYTextAlignment(WidgetTextAlignment.CENTER);
		pageNum.revalidate();

		Widget pageNext = container.createChild(-1, WidgetType.GRAPHIC);
		pageNext.setHidden(isSearchOnLastPage() || results.isEmpty());
		pageNext.setAction(0, "Next Page");
		pageNext.setSpriteId(SpriteID.FORWARD_ARROW_BUTTON_SMALL);
		pageNext.setOriginalWidth(20);
		pageNext.setOriginalHeight(20);
		pageNext.setOriginalX(container.getWidth() - 50);
		pageNext.setOriginalY(0);
		pageNext.setOnOpListener((JavaScriptCallback) e ->
		{
			page = isSearchOnLastPage() ? page : page + 1;
			update();
		});
		pageNext.setHasListener(true);
		pageNext.revalidate();

		Widget exit = container.createChild(-1, WidgetType.GRAPHIC);
		exit.setOriginalX(container.getWidth() - 20);
		exit.setOriginalY(0);
		exit.setOriginalWidth(20);
		exit.setOriginalHeight(20);
		exit.setSpriteId(SpriteID.GE_CANCEL_OFFER_BUTTON);
		exit.setAction(0, "Close");
		exit.setOnOpListener((JavaScriptCallback) e -> chatboxPanelManager.close());
		exit.setOnMouseRepeatListener((JavaScriptCallback) e -> exit.setSpriteId(SpriteID.GE_CANCEL_OFFER_BUTTON_HOVERED));
		exit.setOnMouseLeaveListener((JavaScriptCallback) e -> exit.setSpriteId(SpriteID.GE_CANCEL_OFFER_BUTTON));
		exit.setHasListener(true);
		exit.revalidate();

		createContents(container);
	}

	protected abstract void createContents(Widget container);

	@Override
	public void keyPressed(KeyEvent ev)
	{
		switch (ev.getKeyCode())
		{
			case KeyEvent.VK_ENTER:
				ev.consume();
				if (index > -1)
				{
					if (hasCallback())
					{
						runCallback(results.get(index));
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
		page = 0;
		super.close();
	}

	@Override
	@Deprecated
	public ChatboxTextInput onDone(Consumer<String> onDone)
	{
		throw new UnsupportedOperationException();
	}

	protected abstract void filterResults();

	protected abstract void runCallback(Object o);

	protected abstract boolean hasCallback();

	public void setTooltipText(final String text)
	{
		tooltipText = text;
	}

	public void setPrompt(String prompt)
	{
		super.prompt(prompt);
	}

	private boolean isSearchOnLastPage()
	{
		return (page + 1) * MAX_RESULTS >= results.size();
	}
}
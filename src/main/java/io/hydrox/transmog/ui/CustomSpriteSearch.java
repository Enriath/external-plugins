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

import com.google.inject.Inject;
import io.hydrox.transmog.Mapping;
import io.hydrox.transmog.MappingMapping;
import io.hydrox.transmog.TransmogSlot;
import io.hydrox.transmog.TransmogrificationManager;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import javax.inject.Singleton;
import java.util.function.Consumer;

@Singleton
public class CustomSpriteSearch extends CustomSearch
{
	private final TransmogrificationManager transmogrificationManager;

	@Setter
	protected Consumer<Mapping> onItemSelected;

	@Setter
	private Mapping[] source;

	@Setter
	private TransmogSlot slot;

	@Inject
	private CustomSpriteSearch(ChatboxPanelManager chatboxPanelManager, ClientThread clientThread,
							   Client client, TransmogrificationManager transmogrificationManager)
	{
		super(chatboxPanelManager, clientThread, client);
		this.transmogrificationManager = transmogrificationManager;
	}

	@Override
	protected void createContents(Widget container)
	{
		MappingMapping mappingMapping = MappingMapping.fromSlot(slot);
		int x = PADDING;
		int y = PADDING * 3;
		int idx = 0;
		for (int i = page * MAX_RESULTS; i < (page + 1) * MAX_RESULTS && i < results.size(); i++)
		{
			final Mapping mapping = (Mapping) results.get(i);

			Widget model = container.createChild(-1, WidgetType.MODEL);
			model.setXPositionMode(WidgetPositionMode.ABSOLUTE_LEFT);
			model.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
			model.setOriginalX(x);
			model.setOriginalY(y + FONT_SIZE * 2 + mappingMapping.getYOffset());
			model.setOriginalHeight(ICON_HEIGHT);
			model.setOriginalWidth(ICON_WIDTH);
			model.setModelId(mapping.modelId());
			model.setModelZoom(mappingMapping.getModelZoom());
			model.setRotationZ(150);

			Widget clickBox = container.createChild(-1, WidgetType.RECTANGLE);
			clickBox.setOpacity(255);
			clickBox.setXPositionMode(WidgetPositionMode.ABSOLUTE_LEFT);
			clickBox.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
			clickBox.setOriginalX(x);
			clickBox.setOriginalY(y + FONT_SIZE * 2);
			clickBox.setOriginalHeight(ICON_HEIGHT);
			clickBox.setOriginalWidth(ICON_WIDTH);
			clickBox.setName("<col=ff9040>" + mapping.prettyName());
			clickBox.setAction(0, tooltipText);
			clickBox.setHasListener(true);

			if (index == idx)
			{
				model.setOpacity(HOVERED_OPACITY);
			}
			else
			{
				clickBox.setOnMouseOverListener((JavaScriptCallback) ev -> model.setOpacity(HOVERED_OPACITY));
				clickBox.setOnMouseLeaveListener((JavaScriptCallback) ev -> model.setOpacity(0));
			}

			clickBox.setOnOpListener((JavaScriptCallback) ev ->
			{
				if (hasCallback())
				{
					runCallback(mapping);
				}

				chatboxPanelManager.close();
			});

			x += ICON_WIDTH + PADDING;
			if (x + ICON_WIDTH >= container.getWidth())
			{
				y += ICON_HEIGHT + PADDING;
				x = PADDING;
			}

			model.revalidate();
			clickBox.revalidate();
			++idx;
		}
	}

	@Override
	protected void filterResults()
	{
		results.clear();
		index = -1;
		String search = getValue().toLowerCase();

		for (Mapping mapping : source)
		{
			if (mapping.prettyName().toLowerCase().contains(search) &&
				mapping.gender() == transmogrificationManager.getGender())
			{
				results.add(mapping);
			}
		}
	}

	@Override
	protected void runCallback(Object o)
	{
		onItemSelected.accept((Mapping) o);
	}

	@Override
	protected boolean hasCallback()
	{
		return onItemSelected != null;
	}
}
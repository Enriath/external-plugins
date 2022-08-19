/*
 * Copyright (c) 2022, Enriath <ikada@protonmail.ch>
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
package io.hydrox.transmog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.hydrox.transmog.config.TransmogrificationConfigManager;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TransmogPartyManager
{
	private final Client client;
	private final TransmogrificationManager transmogManager;
	private final TransmogrificationConfigManager configManager;
	private final PartyService partyService;

	private final Map<String, Player> playerMapByName = new HashMap<>();

	private final Map<Long, String> playerMapByMemberId = new HashMap<>();
	private final Map<String, Long> memberMapByPlayerName = new HashMap<>();

	@Inject
	TransmogPartyManager(Client client, TransmogrificationManager transmogManager, PartyService partyService,
						 TransmogrificationConfigManager configManager)
	{
		this.client = client;
		this.transmogManager = transmogManager;
		this.partyService = partyService;
		this.configManager = configManager;
	}

	public void onPlayerSpawned(Player spawned)
	{
		if (spawned == client.getLocalPlayer())
		{
			return;
		}

		playerMapByName.put(spawned.getName(), spawned);

		transmogManager.updateCurrent(spawned.getName(), spawned.getPlayerComposition().getEquipmentIds());
		transmogManager.applyTransmog(spawned, transmogManager.getPartyPreset(spawned.getName()));
	}

	public void onPlayerDespawned(Player despawned)
	{
		playerMapByName.remove(despawned.getName());
	}

	public void onTransmogUpdateMessage(TransmogUpdateMessage e)
	{
		if (partyService.getLocalMember().getMemberId() == e.getMemberId())
		{
			return;
		}
		PartyMember member = partyService.getMemberById(e.getMemberId());
		String name = member.getDisplayName();
		TransmogPreset preset = e.getPresetData() == null ? null : TransmogPreset.fromConfig(-1, e.getPresetData());
		transmogManager.setPartyPreset(name, preset);

		// If the player is in the scene, update their transmog
		Player player = playerMapByName.getOrDefault(name, null);
		if (player == null)
		{
			return;
		}
		transmogManager.updateTransmog(player, preset);
	}

	public void onTransmogEmptyMessage(TransmogEmptyMessage e)
	{
		if (partyService.getLocalMember().getMemberId() == e.getMemberId())
		{
			return;
		}
		PartyMember member = partyService.getMemberById(e.getMemberId());
		String name = member.getDisplayName();
		transmogManager.updateDefault(name, e.getEmptyState());
	}

	public void onUserSync()
	{
		playerMapByMemberId.clear();
		memberMapByPlayerName.clear();
		for (PartyMember pm : partyService.getMembers())
		{
			// Ignore self
			if (pm.getMemberId() == partyService.getLocalMember().getMemberId())
			{
				continue;
			}
			// Ignore those with uninitialised usernames
			String name = pm.getDisplayName();
			if ("<unknown>".equals(name))
			{
				continue;
			}
			playerMapByMemberId.put(pm.getMemberId(), name);
			memberMapByPlayerName.put(name, pm.getMemberId());
			// Don't bother trying to apply transmog if the user isn't present
			Player player = playerMapByName.getOrDefault(name, null);
			if (player == null)
			{
				continue;
			}

			transmogManager.updateTransmog(player, transmogManager.getPartyPreset(name));
		}
	}

	public void clearUser(long memberId)
	{
		String name = playerMapByMemberId.getOrDefault(memberId, null);
		if (name == null)
		{
			return;
		}

		transmogManager.setPartyPreset(name, null);
		if (playerMapByName.containsKey(name))
		{
			transmogManager.removeTransmog(playerMapByName.get(name));
		}
		playerMapByMemberId.remove(memberId);
	}

	public Runnable onPartyChanged(PartyChanged e)
	{
		if (e.getPartyId() == null)
		{
			for (long id : playerMapByMemberId.keySet())
			{
				clearUser(id);
			}
		}
		// TODO: This stuff may not be needed, not 100% sure
		if (client.getGameState() != GameState.LOGGED_IN || e.getPartyId() == null || client.getLocalPlayer().getName() == null)
		{
			return null;
		}
		return () ->
		{
			shareEmptyState();
			shareCurrentPreset();
		};
	}

	void shareEmptyState()
	{
		if (partyService.isInParty())
		{
			partyService.send(new TransmogEmptyMessage(
				transmogManager.getEmptyStateMap().get(client.getLocalPlayer().getName())
			));
		}
	}

	private boolean shouldSharePreset()
	{
		return configManager.transmogActive() && configManager.transmitToParty();
	}

	void shareCurrentPreset()
	{
		if (partyService.isInParty())
		{
			partyService.send(new TransmogUpdateMessage(
				shouldSharePreset()
					? transmogManager.getCurrentPreset().toMessageData()
					: null
			));
		}
	}

	void requestDefaultStates()
	{
		if (partyService.isInParty())
		{
			partyService.send(new TransmogDefaultStateRequest());
		}
	}

	void clearSharedPreset()
	{
		if (partyService.isInParty())
		{
			partyService.send(new TransmogUpdateMessage(null));
		}
	}

	public void setShareWithParty(boolean state)
	{
		configManager.transmitToParty(state);
		shareCurrentPreset();
	}

	boolean isPlayerInParty(String name)
	{
		return memberMapByPlayerName.containsKey(name);
	}
}

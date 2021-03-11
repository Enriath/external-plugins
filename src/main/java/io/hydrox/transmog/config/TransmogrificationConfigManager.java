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
package io.hydrox.transmog.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.hydrox.transmog.TransmogPreset;
import net.runelite.client.config.ConfigManager;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

// Don't you just hate it when the config becomes null when it has only hidden configs?
@Singleton
public class TransmogrificationConfigManager
{
	private static final String CONFIG_GROUP = "transmog";
	private static final String CONFIG_DEFAULT = "default";
	private static final String CONFIG_OVERRIDE = "override";
	private static final String CONFIG_PRESET = "preset_";
	private static final String CONFIG_MAX_PRESET = "max_index";
	private static final String CONFIG_TRANSMOG_ACTIVE = "transmogActive";
	private static final String CONFIG_CURRENT_PRESET = "currentPreset";

	private final ConfigManager configManager;

	@Inject
	TransmogrificationConfigManager(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	public boolean transmogActive()
	{
		return Optional.ofNullable(configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_TRANSMOG_ACTIVE, boolean.class)).orElse(false);
	}

	public void transmogActive(boolean value)
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_TRANSMOG_ACTIVE, value);
	}

	public int currentPreset()
	{
		return Optional.ofNullable(configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_CURRENT_PRESET, int.class)).orElse(nextIndex());
	}

	public void currentPreset(int value)
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_CURRENT_PRESET, value);
	}

	/**
	 * Gets the next index to be used by a preset.
	 * Could also be considered the number of presets.
	 */
	public int nextIndex()
	{
		return Optional.ofNullable(configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_MAX_PRESET, int.class)).orElse(0);
	}

	/**
	 * Sets the next index to be used by a preset.
	 * Could also be considered the number of presets.
	 */
	public void nextIndex(int value)
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_MAX_PRESET, value);
	}

	public void savePreset(TransmogPreset preset)
	{
		String key = CONFIG_OVERRIDE + "." + CONFIG_PRESET + preset.getId();
		String value = preset.toConfig();
		if (value != null)
		{
			configManager.setRSProfileConfiguration(CONFIG_GROUP, key, value);
		}
	}

	public void savePreset(int presetID, TransmogPreset preset)
	{
		String key = CONFIG_OVERRIDE + "." + CONFIG_PRESET + presetID;
		if (preset != null)
		{
			String value = preset.toConfig();
			if (value != null)
			{
				configManager.setRSProfileConfiguration(CONFIG_GROUP, key, value);
				return;
			}
		}
		configManager.unsetRSProfileConfiguration(CONFIG_GROUP, key);
	}

	public void saveDefaultState(int[] state)
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_DEFAULT, Arrays.stream(state)
			.mapToObj(String::valueOf)
			.collect(Collectors.joining(","))
		);
	}

	public String getPresetData(int index)
	{
		return configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_OVERRIDE + "." + CONFIG_PRESET + index);
	}

	public String getDefaultStateData()
	{
		return configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_DEFAULT);
	}
}

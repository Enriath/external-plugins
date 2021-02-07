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
package io.hydrox.transmog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import static io.hydrox.transmog.TransmogPreset.PRESET_COUNT;
import net.runelite.client.config.ConfigManager;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
// Don't you just hate it when the config becomes null when it has only hidden configs?
public class TransmogrificationConfigManager
{
	private static final String CONFIG_GROUP = "transmog";
	private static final String CONFIG_DEFAULT = "default";
	private static final String CONFIG_OVERRIDE = "override";
	private static final String CONFIG_PRESET = "preset_";

	private final ConfigManager configManager;

	@Inject
	TransmogrificationConfigManager(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	public boolean transmogActive()
	{
		return Optional.ofNullable(configManager.getRSProfileConfiguration(CONFIG_GROUP, "transmogActive", boolean.class)).orElse(false);
	}

	public void transmogActive(boolean value)
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, "transmogActive", value);
	}

	public int currentPreset()
	{
		return Optional.ofNullable(configManager.getRSProfileConfiguration(CONFIG_GROUP, "currentPreset", int.class)).orElse(1);
	}

	void currentPreset(int value)
	{
		if (value > 0 && value <= PRESET_COUNT)
		{
			configManager.setRSProfileConfiguration(CONFIG_GROUP, "currentPreset", value);
		}
	}

	void savePreset(TransmogPreset preset, int presetID)
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

	void saveDefault(int[] state)
	{
		configManager.setRSProfileConfiguration(CONFIG_GROUP, CONFIG_DEFAULT, Arrays.stream(state)
			.mapToObj(String::valueOf)
			.collect(Collectors.joining(","))
		);
	}

	String loadPreset(int index)
	{
		return configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_OVERRIDE + "." + CONFIG_PRESET + index);
	}

	String loadDefault()
	{
		return  configManager.getRSProfileConfiguration(CONFIG_GROUP, CONFIG_DEFAULT);
	}
}

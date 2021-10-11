package io.hydrox.subtlevirtuallevels;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SubtleVirtualLevelsPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SubtleVirtualLevelsPlugin.class);
		RuneLite.main(args);
	}
}
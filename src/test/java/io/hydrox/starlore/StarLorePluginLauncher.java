package io.hydrox.starlore;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class StarLorePluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(StarLorePlugin.class);
		RuneLite.main(args);
	}
}
package io.hydrox.coffincounter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CoffinCounterPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CoffinCounterPlugin.class);
		RuneLite.main(args);
	}
}
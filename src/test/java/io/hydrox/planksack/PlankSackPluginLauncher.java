package io.hydrox.planksack;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PlankSackPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PlankSackPlugin.class);
		RuneLite.main(args);
	}
}
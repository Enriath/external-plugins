package io.hydrox.shatteredrelicxp;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ShatteredRelicXPPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ShatteredRelicXPPlugin.class);
		RuneLite.main(args);
	}
}
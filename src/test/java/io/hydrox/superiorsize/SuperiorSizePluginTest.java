package io.hydrox.superiorsize;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SuperiorSizePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SuperiorSizePlugin.class);
		RuneLite.main(args);
	}
}
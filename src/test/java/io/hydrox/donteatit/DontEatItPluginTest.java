package io.hydrox.donteatit;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DontEatItPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DontEatItPlugin.class);
		RuneLite.main(args);
	}
}
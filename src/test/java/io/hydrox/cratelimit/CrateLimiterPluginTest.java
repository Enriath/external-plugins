package io.hydrox.cratelimit;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CrateLimiterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CrateLimiterPlugin.class);
		RuneLite.main(args);
	}
}
package io.hydrox.trailblazerclues;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TrailblazerCluesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TrailblazerCluesPlugin.class);
		RuneLite.main(args);
	}
}
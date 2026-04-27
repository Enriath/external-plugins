package io.enriath.minioninfo;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MinionInfoLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MinionInfoPlugin.class);
		RuneLite.main(args);
	}
}
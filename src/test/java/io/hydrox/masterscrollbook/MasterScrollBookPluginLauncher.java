package io.hydrox.masterscrollbook;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MasterScrollBookPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MasterScrollBookPlugin.class);
		RuneLite.main(args);
	}
}
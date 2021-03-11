package io.hydrox.transmog;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TransmogrificationPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TransmogrificationPlugin.class);
		RuneLite.main(args);
	}
}
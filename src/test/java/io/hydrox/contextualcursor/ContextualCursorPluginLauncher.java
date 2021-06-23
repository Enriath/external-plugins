package io.hydrox.contextualcursor;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ContextualCursorPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ContextualCursorPlugin.class);
		RuneLite.main(args);
	}
}

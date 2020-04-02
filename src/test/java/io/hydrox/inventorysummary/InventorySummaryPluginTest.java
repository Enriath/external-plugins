package io.hydrox.inventorysummary;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class InventorySummaryPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(InventorySummaryPlugin.class);
		RuneLite.main(args);
	}
}
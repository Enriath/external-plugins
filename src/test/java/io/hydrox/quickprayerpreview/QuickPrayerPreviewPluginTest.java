package io.hydrox.quickprayerpreview;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class QuickPrayerPreviewPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(QuickPrayerPreviewPlugin.class);
		RuneLite.main(args);
	}
}
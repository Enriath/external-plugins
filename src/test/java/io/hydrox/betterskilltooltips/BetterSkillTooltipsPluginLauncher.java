package io.hydrox.betterskilltooltips;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BetterSkillTooltipsPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BetterSkillTooltipsPlugin.class);
		RuneLite.main(args);
	}
}
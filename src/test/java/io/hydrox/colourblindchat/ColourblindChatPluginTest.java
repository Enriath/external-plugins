package io.hydrox.colourblindchat;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ColourblindChatPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ColourblindChatPlugin.class);
		RuneLite.main(args);
	}
}
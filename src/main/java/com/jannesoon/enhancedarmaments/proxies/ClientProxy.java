package com.jannesoon.enhancedarmaments.proxies;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	public static KeyBinding keyBinding;
	
	@Override
	public void preInit()
	{
		
	}
	
	@Override
	public void init()
	{
		keyBinding = new KeyBinding("key.gui.weapon_interface", 75, "key.enhancedarmaments");
		
		ClientRegistry.registerKeyBinding(keyBinding);
	}
}
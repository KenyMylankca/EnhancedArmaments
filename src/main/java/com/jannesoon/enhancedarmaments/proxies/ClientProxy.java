package com.jannesoon.enhancedarmaments.proxies;

import org.lwjgl.input.Keyboard;

import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.event.EventRenderOverlay;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	public static KeyBinding keyBinding;
	
	@Override
	public void preInit()
	{
		if (Config.enemyLeveling)
		{
			MinecraftForge.EVENT_BUS.register(new EventRenderOverlay());
		}
	}
	
	@Override
	public void init()
	{
		keyBinding = new KeyBinding("key.gui.weapon_interface", Keyboard.KEY_K, "key.enhancedarmaments");
		
		ClientRegistry.registerKeyBinding(keyBinding);
	}
}

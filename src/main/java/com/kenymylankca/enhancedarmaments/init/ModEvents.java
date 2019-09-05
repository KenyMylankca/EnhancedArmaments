package com.kenymylankca.enhancedarmaments.init;

import com.kenymylankca.enhancedarmaments.event.EventInput;
import com.kenymylankca.enhancedarmaments.event.EventItemTooltip;
import com.kenymylankca.enhancedarmaments.event.EventLivingDeath;
import com.kenymylankca.enhancedarmaments.event.EventLivingHurt;
import com.kenymylankca.enhancedarmaments.event.EventLivingUpdate;

import net.minecraftforge.common.MinecraftForge;

public class ModEvents 
{
	public static void registerEvents()
	{
		MinecraftForge.EVENT_BUS.register(new EventItemTooltip());
		MinecraftForge.EVENT_BUS.register(new EventLivingUpdate());
		MinecraftForge.EVENT_BUS.register(new EventInput());
		MinecraftForge.EVENT_BUS.register(new EventLivingHurt());
		MinecraftForge.EVENT_BUS.register(new EventLivingDeath());
	}
}
package com.jannesoon.enhancedarmaments.init;

import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.event.EventInput;
import com.jannesoon.enhancedarmaments.event.EventItemTooltip;
import com.jannesoon.enhancedarmaments.event.EventLivingDeath;
import com.jannesoon.enhancedarmaments.event.EventLivingHurt;
import com.jannesoon.enhancedarmaments.event.EventLivingUpdate;
import com.jannesoon.enhancedarmaments.event.EventPlayerTracking;

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
		
		if (Config.enemyLeveling)
		{
			MinecraftForge.EVENT_BUS.register(new EventPlayerTracking());
		}
	}
}

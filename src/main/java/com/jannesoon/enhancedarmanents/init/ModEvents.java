package com.jannesoon.enhancedarmanents.init;

import com.jannesoon.enhancedarmanents.config.Config;
import com.jannesoon.enhancedarmanents.event.EventInput;
import com.jannesoon.enhancedarmanents.event.EventItemTooltip;
import com.jannesoon.enhancedarmanents.event.EventLivingDeath;
import com.jannesoon.enhancedarmanents.event.EventLivingHurt;
import com.jannesoon.enhancedarmanents.event.EventLivingUpdate;
import com.jannesoon.enhancedarmanents.event.EventPlayerTracking;

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

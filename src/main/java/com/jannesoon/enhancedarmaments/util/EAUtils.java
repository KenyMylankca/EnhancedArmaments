package com.jannesoon.enhancedarmaments.util;

import com.jannesoon.enhancedarmaments.config.Config;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;

public class EAUtils
{
	public static boolean canEnhance(Item item)
	{
		if(Config.extraItems.length != 0)
		{
			boolean allowed=false;
			for(int k = 0; k < Config.extraItems.length; k++)
				if(Config.extraItems[k].equals(item.getRegistryName().getResourceDomain() + ":" + item.getRegistryName().getResourcePath()))
					allowed=true;
			return (allowed || item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemBow || item instanceof ItemArmor) ? true : false;
		}
		else
			return (item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemBow || item instanceof ItemArmor) ? true : false;
	}
	
	public static boolean canEnhanceWeapon(Item item)
	{
		return (canEnhance(item) && !(item instanceof ItemArmor)) ? true : false;
	}
	
	public static boolean canEnhanceMelee(Item item)
	{
		return (canEnhance(item) && !(item instanceof ItemArmor) && !(item instanceof ItemBow)) ? true : false;
	}
	
	public static boolean canEnhanceRanged(Item item)
	{
		return (canEnhance(item) && item instanceof ItemBow) ? true : false;
	}
	
	public static boolean canEnhanceArmor(Item item)
	{
		return (canEnhance(item) && item instanceof ItemArmor) ? true : false;
	}
	
	public static boolean isDamageSourceAllowed(DamageSource damageSource)
	{
		return !(damageSource == DamageSource.FALL ||
				damageSource == DamageSource.DROWN ||
				damageSource == DamageSource.CACTUS ||
				damageSource == DamageSource.STARVE ||
				damageSource == DamageSource.IN_WALL ||
				damageSource == DamageSource.IN_FIRE ||
				damageSource == DamageSource.OUT_OF_WORLD) || damageSource.getTrueSource() instanceof EntityLivingBase;
	}
}
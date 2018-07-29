package com.jannesoon.enhancedarmaments.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSword;

public class EAUtils
{
	public static boolean canEnhance(Item item)
	{
		return (item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemBow || item instanceof ItemArmor) ? true : false;
	}
	
	public static boolean canEnhanceWeapon(Item item)
	{
		return (item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemBow) ? true : false;
	}
	
	public static boolean canEnhanceMelee(Item item)
	{
		return (item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe) ? true : false;
	}
	
	public static boolean canEnhanceRanged(Item item)
	{
		return (item instanceof ItemBow) ? true : false;
	}
	
	public static boolean canEnhanceArmor(Item item)
	{
		return (item instanceof ItemArmor) ? true : false;
	}
}

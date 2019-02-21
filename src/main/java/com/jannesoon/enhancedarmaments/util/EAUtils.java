package com.jannesoon.enhancedarmaments.util;

import com.jannesoon.enhancedarmaments.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;

import java.util.UUID;

public class EAUtils
{
	public static boolean canEnhance(Item item)
	{
		if(Config.onlyModdedItems)
			if(item == Items.IRON_SWORD || item == Items.IRON_AXE || item == Items.IRON_HOE || item == Items.IRON_BOOTS || item == Items.IRON_CHESTPLATE || item == Items.IRON_HELMET || item == Items.IRON_LEGGINGS
					|| item == Items.DIAMOND_AXE || item == Items.DIAMOND_HOE || item == Items.DIAMOND_SWORD || item == Items.DIAMOND_BOOTS || item == Items.DIAMOND_CHESTPLATE || item == Items.DIAMOND_HELMET || item == Items.DIAMOND_LEGGINGS
					|| item == Items.GOLDEN_AXE || item == Items.GOLDEN_HOE || item == Items.GOLDEN_SWORD || item == Items.GOLDEN_BOOTS || item == Items.GOLDEN_CHESTPLATE || item == Items.GOLDEN_HELMET || item == Items.GOLDEN_LEGGINGS
					|| item == Items.STONE_AXE || item == Items.STONE_HOE || item == Items.STONE_SWORD
					|| item == Items.WOODEN_AXE || item == Items.WOODEN_HOE || item == Items.WOODEN_SWORD
					|| item == Items.BOW
					|| item == Items.CHAINMAIL_BOOTS || item == Items.CHAINMAIL_CHESTPLATE || item == Items.CHAINMAIL_HELMET || item == Items.CHAINMAIL_LEGGINGS)
				return false;

		if(Config.extraItems.size() != 0)
		{
			boolean allowed=false;
			for(int k = 0; k < Config.extraItems.size(); k++)
				if(Config.extraItems.get(k).equals(item.getRegistryName().getPath()))
					allowed=true;
			return allowed || item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemBow || item instanceof ItemArmor;
		}
		else
			return item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemBow || item instanceof ItemArmor;
	}
	
	public static boolean canEnhanceWeapon(Item item)
	{
		return canEnhance(item) && !(item instanceof ItemArmor);
	}
	
	public static boolean canEnhanceMelee(Item item)
	{
		return canEnhance(item) && !(item instanceof ItemArmor) && !(item instanceof ItemBow);
	}
	
	public static boolean canEnhanceRanged(Item item)
	{
		return canEnhance(item) && item instanceof ItemBow;
	}
	
	public static boolean canEnhanceArmor(Item item)
	{
		return canEnhance(item) && item instanceof ItemArmor;
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
	
	public static Entity getEntityByUniqueId(UUID uniqueId)
	{
	    for (Entity entity : Minecraft.getInstance().world.loadedEntityList)
	    	{
	    		if (entity.getUniqueID().equals(uniqueId))
	                return entity;
	    	}

	    return null;
	}
}
package com.jannesoon.enhancedarmaments.essentials;

import com.jannesoon.enhancedarmaments.config.Config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class Experience 
{	
	public static int getNextLevel(EntityPlayer player, ItemStack stack, NBTTagCompound nbt, int currentLevel, int experience)
	{
		int newLevel = currentLevel;
		
		while (currentLevel < Config.maxLevel && experience >= Experience.getMaxLevelExp(currentLevel))
		{
			newLevel = currentLevel + 1;
			currentLevel++;
			Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) + 1);
			player.sendMessage(new TextComponentString(stack.getDisplayName() + TextFormatting.GRAY + " " + new TextComponentTranslation("enhancedarmaments.misc.level.leveledup").getFormattedText() + " " + TextFormatting.GOLD + "" + newLevel + TextFormatting.GRAY + "!"));
		}
		
		return newLevel;
	}
	
	public static int getLevel(NBTTagCompound nbt)
	{
		return nbt != null ? Math.max(nbt.getInteger("LEVEL"), 1) : 1;
	}
	
	public static boolean canLevelUp(NBTTagCompound nbt)
	{
		return getLevel(nbt) < Config.maxLevel;
	}
	
	public static void setLevel(NBTTagCompound nbt, int level)
	{
		if (nbt != null)
		{
			if (level > 1)
				nbt.setInteger("LEVEL", level);
			else
				nbt.removeTag("LEVEL");
		}
	}
	
	public static int getNeededExpForNextLevel(NBTTagCompound nbt)
	{
		int neededexp = Experience.getMaxLevelExp(Experience.getLevel(nbt)) - Experience.getExperience(nbt);
		return nbt != null ? neededexp : 0;
	}
	
	public static int getExperience(NBTTagCompound nbt)
	{
		return nbt.hasKey("EXPERIENCE") ? nbt.getInteger("EXPERIENCE") : 0;
	}
	
	public static void setExperience(NBTTagCompound nbt, int experience)
	{
		if (nbt != null)
		{
			if (experience > 0)
				nbt.setInteger("EXPERIENCE", experience);
			else
				nbt.removeTag("EXPERIENCE");
		}
	}
	
	public static int getMaxLevelExp(int level)
	{
		int maxLevelExp=Config.level1Experience;
		for(int i=1; i<level; i++)
			maxLevelExp*=Config.experienceMultiplier;
		return maxLevelExp;
	}
	
	public static void setAbilityTokens(NBTTagCompound nbt, int tokens)
	{
		if (nbt != null)
		{
			if (tokens > 0)
				nbt.setInteger("TOKENS", tokens);
			else
				nbt.removeTag("TOKENS");
		}
	}
	
	public static int getAbilityTokens(NBTTagCompound nbt)
	{
		return nbt != null ? nbt.getInteger("TOKENS") : 0;
	}
	
	public static void enable(NBTTagCompound nbt, boolean value)
	{
		if (nbt != null)
		{
			if (value)
				nbt.setBoolean("EA_ENABLED", value);
			else
				nbt.removeTag("EA_ENABLED");
		}
	}
	
	public static boolean isEnabled(NBTTagCompound nbt)
	{
		return nbt != null ? nbt.getBoolean("EA_ENABLED") : false;
	}
}
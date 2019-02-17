package com.jannesoon.enhancedarmaments.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper 
{
	public static NBTTagCompound loadStackNBT(ItemStack stack)
	{
		return stack.hasTag() ? stack.getTag() : new NBTTagCompound();
	}

	public static void saveStackNBT(ItemStack stack, NBTTagCompound nbt)
	{
		if (!stack.hasTag())
		{
			stack.setTag(nbt);
		}
	}
}
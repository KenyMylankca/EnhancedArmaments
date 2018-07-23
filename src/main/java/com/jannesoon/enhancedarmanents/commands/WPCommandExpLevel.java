package com.jannesoon.enhancedarmanents.commands;

import java.util.List;

import com.google.common.collect.Lists;
import com.jannesoon.enhancedarmanents.leveling.Experience;
import com.jannesoon.enhancedarmanents.util.NBTHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;

public class WPCommandExpLevel extends CommandBase
{
	private final List<String> aliases = Lists.newArrayList("addlevel");

	@Override
	public String getName() {
		return "addlevel";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "addlevel <level count>";
	}
	
	@Override
	public List<String> getAliases() {
		return aliases;
	}
	@Override
	public int getRequiredPermissionLevel()
    {
        return 3;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws UsageException
	{
		if((args.length < 1) || (args.length > 1)) throw new UsageException("Usage: /addlevel <count>");
		
		if((args.length < 1) || (args.length > 1)) return;
		
		if(!(isInteger(args[0]))) throw new UsageException("Enter a number! Example: /addlevel 4");
		
		if((sender instanceof EntityPlayer) && (isInteger(args[0])))
		{
			AddLevel((EntityPlayer)sender, args[0]);
		}
	}
	
	public static void AddLevel(EntityPlayer player, String count) throws UsageException
	{	
		int c = Integer.parseInt(count);
		if(c < 1) throw new UsageException("Level count must be bigger than 0!");
		
		if (((player.getHeldItemMainhand().getItem() instanceof ItemBow) || (player.getHeldItemMainhand().getItem() instanceof ItemSword) ||
			 (player.getHeldItemMainhand().getItem() instanceof ItemAxe) || (player.getHeldItemMainhand().getItem() instanceof ItemHoe) ||
			 (player.getHeldItemMainhand().getItem() instanceof ItemArmor)) && (c > 0))
		{
		ItemStack item = player.getHeldItemMainhand();
		NBTTagCompound nbt = NBTHelper.loadStackNBT(item);
		for(int i=0; i<c; i++)
		{
			if (Experience.canLevelUp(nbt))
			{
			Experience.setExperience(nbt, Experience.getExperience(nbt) + Experience.getNeededExpForNextLevel(nbt));
			Experience.setLevel(nbt, Experience.getLevel(nbt)+1);
			Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt)+1);
			}
		}
		NBTHelper.saveStackNBT(item, nbt);
		player.setHeldItem(EnumHand.MAIN_HAND, item);
		}
		
		if ( !((player.getHeldItemMainhand().getItem() instanceof ItemBow) ||
			  (player.getHeldItemMainhand().getItem() instanceof ItemSword) ||
			  (player.getHeldItemMainhand().getItem() instanceof ItemAxe) ||
			  (player.getHeldItemMainhand().getItem() instanceof ItemHoe) ||
			  (player.getHeldItemMainhand().getItem() instanceof ItemArmor))) throw new UsageException("Hold a weapon or an armor in your mainhand!");
	}
	
	public static boolean isInteger(String s)
	{
		return isInteger(s, 10);
	}
	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
}
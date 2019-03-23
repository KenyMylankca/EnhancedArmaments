package com.jannesoon.enhancedarmaments.commands;

import java.util.List;

import com.google.common.collect.Lists;
import com.jannesoon.enhancedarmaments.essentials.Rarity;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandRarity
{
	private final List<String> aliases = Lists.newArrayList("changerarity");

	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("changerarity")
				.requires(cmd -> cmd.hasPermissionLevel(3))
				.then(Commands.argument("rarityid", IntegerArgumentType.integer()))
				.executes(cmd -> changeRarity(cmd.getSource(), cmd.getSource().asPlayer(), IntegerArgumentType.getInteger(cmd, "rarityid"))));
	}
	
	public static int changeRarity(CommandSource src, EntityPlayer player, int rarityid)
	{
		if((rarityid < 1) || (rarityid > 6))
			src.sendFeedback(new TextComponentTranslation("Rarity ID must be 1, 2, 3, 4, 5 or 6!"), true);
		else
		{
			if (!EAUtils.canEnhance(player.getHeldItemMainhand().getItem()))
				src.sendFeedback(new TextComponentTranslation("Hold a weapon or an armor in your mainhand!"), true);
			else
			{
				ItemStack item = player.getHeldItemMainhand();
				NBTTagCompound nbt = NBTHelper.loadStackNBT(item);
				Rarity.setRarity(nbt, String.valueOf(rarityid));
				NBTHelper.saveStackNBT(item, nbt);
				player.setHeldItem(EnumHand.MAIN_HAND, item);
			}
		}
		return rarityid;
	}
}
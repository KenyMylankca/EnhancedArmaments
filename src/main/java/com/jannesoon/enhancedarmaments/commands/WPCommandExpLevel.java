package com.jannesoon.enhancedarmaments.commands;

import com.jannesoon.enhancedarmaments.essentials.Experience;
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

public class WPCommandExpLevel
{
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("addlevel")
				.requires(cmd -> cmd.hasPermissionLevel(3))
				.then(Commands.argument("level", IntegerArgumentType.integer()))
				.executes(cmd -> addLevel(cmd.getSource(), cmd.getSource().asPlayer(), IntegerArgumentType.getInteger(cmd, "level"))));
	}
	
	private static int addLevel(CommandSource cmd, EntityPlayer player, int count)
	{
		if (count < 1) cmd.sendFeedback(new TextComponentTranslation("Level count must be bigger than 0!"), true);
		else
		{
			if (!EAUtils.canEnhance(player.getHeldItemMainhand().getItem()))
				cmd.sendFeedback(new TextComponentTranslation("Hold a weapon or an armor in your mainhand!"), true);
			else
			{
				ItemStack item = player.getHeldItemMainhand();
				NBTTagCompound nbt = NBTHelper.loadStackNBT(item);
				for (int i = 0; i < count; i++) {
					if (Experience.canLevelUp(nbt)) {
						Experience.setExperience(nbt, Experience.getExperience(nbt) + Experience.getNeededExpForNextLevel(nbt));
						Experience.setLevel(nbt, Experience.getLevel(nbt) + 1);
						Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) + 1);
					}
				}
				NBTHelper.saveStackNBT(item, nbt);
				player.setHeldItem(EnumHand.MAIN_HAND, item);
			}
		}

		return count;
	}
}
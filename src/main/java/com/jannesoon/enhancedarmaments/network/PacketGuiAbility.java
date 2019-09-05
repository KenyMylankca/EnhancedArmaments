package com.jannesoon.enhancedarmaments.network;

import javax.xml.ws.handler.MessageContext;

import com.jannesoon.enhancedarmaments.essentials.Ability;
import com.jannesoon.enhancedarmaments.essentials.Experience;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketGuiAbility implements IMessage
{
	private int index;
	
	public PacketGuiAbility() {}
	
	public PacketGuiAbility(int index)
	{
		this.index = index;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(index);
	}
	
	public static class Handler implements IMessageHandler<PacketGuiAbility, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketGuiAbility message, final MessageContext ctx) 
		{			
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run() 
				{
					EntityPlayer player = ctx.getServerHandler().player;
					
					if (player != null)
					{
						ItemStack stack = player.inventory.getCurrentItem();
						
						if (stack != null)
						{
							NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
							
							if (EAUtils.canEnhanceWeapon(stack.getItem()))
							{
								if (Ability.WEAPON_ABILITIES.get(message.index).hasAbility(nbt))
								{
									Ability.WEAPON_ABILITIES.get(message.index).setLevel(nbt, Ability.WEAPON_ABILITIES.get(message.index).getLevel(nbt) + 1);
									Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) - Ability.WEAPON_ABILITIES.get(message.index).getTier());
								}
								else
								{
									Ability.WEAPON_ABILITIES.get(message.index).addAbility(nbt);
									if(!player.isCreative())
										player.addExperienceLevel(-Ability.WEAPON_ABILITIES.get(message.index).getExpLevel(nbt) + 1);
								}
							}
							else if (EAUtils.canEnhanceArmor(stack.getItem()))
							{
								if (Ability.ARMOR_ABILITIES.get(message.index).hasAbility(nbt))
								{
									Ability.ARMOR_ABILITIES.get(message.index).setLevel(nbt, Ability.ARMOR_ABILITIES.get(message.index).getLevel(nbt) + 1);
									Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) - Ability.ARMOR_ABILITIES.get(message.index).getTier());
								}
								else
								{
									Ability.ARMOR_ABILITIES.get(message.index).addAbility(nbt);
									if(!player.isCreative())
										player.addExperienceLevel(-Ability.ARMOR_ABILITIES.get(message.index).getExpLevel(nbt) + 1);
								}
							}
						}
					}
				}
			});
			
			return null;
		}
	}
}
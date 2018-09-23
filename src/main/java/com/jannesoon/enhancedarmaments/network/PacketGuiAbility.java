package com.jannesoon.enhancedarmaments.network;

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
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
								if (Ability.WEAPONS.get(message.index).hasAbility(nbt))
								{
									Ability.WEAPONS.get(message.index).setLevel(nbt, Ability.WEAPONS.get(message.index).getLevel(nbt) + 1);
									Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) - Ability.WEAPONS.get(message.index).getTier());
								}
								else
								{
									Ability.WEAPONS.get(message.index).addAbility(nbt, 1);
									if(!player.isCreative())
										player.addExperienceLevel(-Ability.getExpLevel(Ability.WEAPONS.get(message.index), nbt) + 1);
								}
							}
							else if (EAUtils.canEnhanceArmor(stack.getItem()))
							{
								if (Ability.ARMORS.get(message.index).hasAbility(nbt))
								{
									Ability.ARMORS.get(message.index).setLevel(nbt, Ability.ARMORS.get(message.index).getLevel(nbt) + 1);
									Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) - Ability.ARMORS.get(message.index).getTier());
								}
								else
								{
									Ability.ARMORS.get(message.index).addAbility(nbt, 1);
									if(!player.isCreative())
										player.addExperienceLevel(-Ability.getExpLevel(Ability.ARMORS.get(message.index), nbt) + 1);
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
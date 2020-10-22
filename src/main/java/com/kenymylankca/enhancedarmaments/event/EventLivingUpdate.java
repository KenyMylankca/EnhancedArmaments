package com.kenymylankca.enhancedarmaments.event;

import com.kenymylankca.enhancedarmaments.config.Config;
import com.kenymylankca.enhancedarmaments.essentials.Ability;
import com.kenymylankca.enhancedarmaments.essentials.Experience;
import com.kenymylankca.enhancedarmaments.essentials.Rarity;
import com.kenymylankca.enhancedarmaments.util.EAUtils;
import com.kenymylankca.enhancedarmaments.util.NBTHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventLivingUpdate
{
	private int count=0;
	@SubscribeEvent
	public void onUpdate(LivingEvent.LivingUpdateEvent event)
	{
		if (event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			
			if (player != null)
			{
				NonNullList<ItemStack> main = player.inventory.mainInventory;
				
				if (!player.world.isRemote)
				{
					for (ItemStack stack : player.inventory.armorInventory)
					{
						if (stack != null && EAUtils.canEnhanceArmor(stack.getItem()))
						{
							NBTTagCompound nbtcompound = stack.getTagCompound();
							float heal=Ability.REMEDIAL.getLevel(nbtcompound);
							if (Ability.REMEDIAL.hasAbility(nbtcompound))
								if(this.count < 120)
								{
									this.count++;
								}
								else
								{
									this.count = 0;
									player.heal(heal);
								}
						}
					}
					for (int i = 0; i < main.size(); i++)
					{
						if (main.get(i) != null)
						{
							Item item = main.get(i).getItem();
							
							if (EAUtils.canEnhance(item))
							{
								ItemStack stack = main.get(i);
								NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);

								if (nbt != null)
								{
									if (!Experience.isEnabled(nbt))
									{
										boolean flag = true;
										
										for (int j = 0; j < Config.itemBlacklist.length; j++)
										{
											if (Config.itemBlacklist[j].equals(stack.getItem().getRegistryName().getResourceDomain() + ":" + stack.getItem().getRegistryName().getResourcePath()))
												flag=false;
										}
										
										if (Config.itemWhitelist.length != 0)
										{
											flag=false;
											for(int k = 0; k < Config.itemWhitelist.length; k++)
												if(Config.itemWhitelist[k].equals(stack.getItem().getRegistryName().getResourceDomain() + ":" + stack.getItem().getRegistryName().getResourcePath()))
													flag=true;
										}
										
										if (flag)
										{
											Experience.enable(nbt, true);
											Rarity rarity = Rarity.getRarity(nbt);
											
											if (rarity == Rarity.DEFAULT)
											{
												rarity = Rarity.createRandomRarity();
												rarity.setRarity(nbt);
												NBTHelper.saveStackNBT(stack, nbt);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
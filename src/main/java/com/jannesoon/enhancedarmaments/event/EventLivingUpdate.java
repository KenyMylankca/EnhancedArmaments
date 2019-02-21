package com.jannesoon.enhancedarmaments.event;

import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.essentials.Ability;
import com.jannesoon.enhancedarmaments.essentials.Experience;
import com.jannesoon.enhancedarmaments.essentials.Rarity;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber
public class EventLivingUpdate
{
	//this needs to be a player capability or this will be really random in Multiplayer!
	private int count = 0;
	
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
							NBTTagCompound nbt = stack.getTag();
							float heal = Ability.REMEDIAL.getLevel(nbt);
							if (Ability.REMEDIAL.hasAbility(nbt))
								if(count < 120)
								{
									count++;
								}
								else
								{
									count = 0;
									player.heal(heal);
								}
						}
					}
					for (ItemStack stack : main) {
						if (stack != ItemStack.EMPTY) {
							Item item = stack.getItem();

							if (EAUtils.canEnhance(item)) {
								NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);

								if (nbt != null) {
									if (!Experience.isEnabled(nbt)) {
										boolean okay = true;

										for (int j = 0; j < Config.itemBlacklist.size(); j++) {
											if (Config.itemBlacklist.get(j).equals(stack.getItem().getRegistryName().getPath()))
												okay = false;
										}

										if (Config.itemWhitelist.size() != 0) {
											okay = false;
											for (int k = 0; k < Config.itemWhitelist.size(); k++)
												if (Config.itemWhitelist.get(k).equals(stack.getItem().getRegistryName().getPath()))
													okay = true;
										}

										if (okay) {
											Experience.enable(nbt, true);
											Rarity rarity = Rarity.getRarity(nbt);
											Random rand = player.world.rand;

											if (rarity == Rarity.DEFAULT) {
												rarity = Rarity.getRandomRarity(rand);
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
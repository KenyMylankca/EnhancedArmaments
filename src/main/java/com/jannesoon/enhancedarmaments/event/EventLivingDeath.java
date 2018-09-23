package com.jannesoon.enhancedarmaments.event;

import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.essentials.Ability;
import com.jannesoon.enhancedarmaments.essentials.Experience;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Updates weapon information when killing a target with a valid weapon. Used to update experience,
 * level, abilities, and so on.
 *
 */
public class EventLivingDeath
{
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
		if (event.getSource().getTrueSource() instanceof EntityPlayer && !(event.getSource().getTrueSource() instanceof FakePlayer))
		{
			EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
			ItemStack stack = player.getHeldItem(EventLivingHurt.bowfriendlyhand);
			
			if (stack != null && EAUtils.canEnhanceMelee(stack.getItem()))
			{
				NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
				
				if (nbt != null)
					if(nbt.hasKey("ENABLED"))
					{
						if (Ability.ETHEREAL.hasAbility(nbt))
						{
							player.inventory.getCurrentItem().setItemDamage((int) (player.inventory.getCurrentItem().getItemDamage() - (Ability.ETHEREAL.getLevel(nbt)*2+1)));
						}
						addBonusExperience(event, nbt);
						updateLevel(player, stack, nbt);
						NBTHelper.saveStackNBT(stack, nbt);
					}
			}
			else if (stack != null && EAUtils.canEnhanceRanged(stack.getItem()))
			{
				if (stack != null)
				{
					NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
					
					if (nbt != null)
						if(nbt.hasKey("ENABLED"))
						{
							if (Ability.ETHEREAL.hasAbility(nbt))
							{
								player.inventory.getCurrentItem().setItemDamage((int) (player.inventory.getCurrentItem().getItemDamage() - (Ability.ETHEREAL.getLevel(nbt)*2+1)));
							}
							addBonusExperience(event, nbt);
							updateLevel(player, stack, nbt);
						}
				}
			}
		}
		else if (event.getSource().getTrueSource() instanceof EntityArrow)
		{
			EntityArrow arrow = (EntityArrow) event.getSource().getTrueSource();
			
			if (arrow.shootingEntity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
				ItemStack stack = player.inventory.getCurrentItem();
				
				if (stack != null)
				{
					NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
					
					if (nbt != null)
					{
						addBonusExperience(event, nbt);
						updateLevel(player, stack, nbt);
					}
				}
			}
		}
	}
	
	/**
	 * Called everytime a target dies. Adds bonus experience based on how much health the target had.
	 * @param event
	 * @param nbt
	 */
	private void addBonusExperience(LivingDeathEvent event, NBTTagCompound nbt)
	{
		if (Experience.getLevel(nbt) < Config.maxLevel)
		{
			if (event.getEntityLiving() instanceof EntityLivingBase)
			{
				EntityLivingBase target = event.getEntityLiving();
				int bonusExperience = 0;
				
				if (target.getMaxHealth() < 10) bonusExperience = 3;
				else if (target.getMaxHealth() > 9 && target.getMaxHealth() < 20) bonusExperience = 8;
				else if (target.getMaxHealth() > 19 && target.getMaxHealth() < 50) bonusExperience = 20;
				else if (target.getMaxHealth() > 49 && target.getMaxHealth() < 100) bonusExperience = 55;
				else if (target.getMaxHealth() > 99) bonusExperience = 80;
				
				Experience.setExperience(nbt, Experience.getExperience(nbt) + bonusExperience);
			}
		}
	}
	
	/**
	 * Called everytime a target dies. Used to update the level of the weapon.
	 * @param player
	 * @param stack
	 * @param nbt
	 */
	private void updateLevel(EntityPlayer player, ItemStack stack, NBTTagCompound nbt)
	{
		int level = Experience.getNextLevel(player, stack, nbt, Experience.getLevel(nbt), Experience.getExperience(nbt));
		Experience.setLevel(nbt, level);
	}
}
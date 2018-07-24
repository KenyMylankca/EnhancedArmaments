package com.jannesoon.enhancedarmaments.event;

import com.jannesoon.enhancedarmaments.capabilities.CapabilityEnemyLevel;
import com.jannesoon.enhancedarmaments.capabilities.IEnemyLevel;
import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.leveling.Ability;
import com.jannesoon.enhancedarmaments.leveling.Experience;
import com.jannesoon.enhancedarmaments.util.NBTHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Updates weapon information when killing an enemy with a valid weapon. Used to update experience,
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
			ItemStack stack = player.inventory.getCurrentItem();
			
			if (stack != null && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe))
			{
				NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
				
				if (nbt != null)
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
			else if (stack != null && stack.getItem() instanceof ItemBow)
			{
				if (stack != null)
				{
					NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
					
					if (nbt != null)
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
	 * Called everytime an enemy dies. Adds bonus experience based on how much health the enemy had.
	 * @param event
	 * @param nbt
	 */
	private void addBonusExperience(LivingDeathEvent event, NBTTagCompound nbt)
	{
		if (Experience.getLevel(nbt) < Config.maxLevel)
		{
			if (event.getEntityLiving() instanceof EntityLivingBase)
			{
				EntityLivingBase enemy = event.getEntityLiving();
				IEnemyLevel enemyLevel = enemy.getCapability(CapabilityEnemyLevel.ENEMY_LEVEL_CAP, null);
				int bonusExperience = 0;
				
				if (enemy.getMaxHealth() <= 10) bonusExperience = 3;
				else if (enemy.getMaxHealth() > 10 && enemy.getMaxHealth() <= 20) bonusExperience = 9;
				else if (enemy.getMaxHealth() > 20 && enemy.getMaxHealth() <= 50) bonusExperience = 20;
				else if (enemy.getMaxHealth() > 50 && enemy.getMaxHealth() <= 100) bonusExperience = 55;
				else if (enemy.getMaxHealth() > 100) bonusExperience = 80;
				
				if (enemyLevel != null && enemyLevel.getEnemyLevel() > 0)
				{
					int level = enemyLevel.getEnemyLevel();
					
					if (level == 1) bonusExperience *= 0;
					else if (level == 2) bonusExperience *= 1;
					else if (level == 3) bonusExperience *= 1.2D;
					else if (level == 4) bonusExperience *= 2;
					else if (level == 5) bonusExperience *= 2.2D;
					else if (level == 6) bonusExperience *= 3;
				}
				
				Experience.setExperience(nbt, Experience.getExperience(nbt) + bonusExperience);
			}
		}
	}
	
	/**
	 * Called everytime an enemy dies. Used to update the level of the weapon.
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

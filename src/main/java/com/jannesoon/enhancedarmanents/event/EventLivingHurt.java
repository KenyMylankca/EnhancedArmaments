package com.jannesoon.enhancedarmanents.event;

import com.jannesoon.enhancedarmanents.config.Config;
import com.jannesoon.enhancedarmanents.leveling.Ability;
import com.jannesoon.enhancedarmanents.leveling.Experience;
import com.jannesoon.enhancedarmanents.leveling.Rarity;
import com.jannesoon.enhancedarmanents.util.NBTHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 */
public class EventLivingHurt 
{
	@SubscribeEvent
	public void onHurt(LivingHurtEvent event)
	{
		if (event.getSource().getTrueSource() instanceof EntityPlayer && !(event.getSource().getTrueSource() instanceof FakePlayer))
		{
			EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
			EntityLivingBase enemy = event.getEntityLiving();
			ItemStack stack = player.inventory.getCurrentItem();
			
			if (stack != null && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe))
			{
				NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
				
				if (nbt != null)
				{
					updateExperience(nbt);
					useRarity(event, stack, nbt);
					useWeaponAbilities(event, player, enemy, nbt);
					updateLevel(player, stack, nbt);
				}
			}
			else if (stack != null && stack.getItem() instanceof ItemBow)
			{
				NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
				
				if (nbt != null)
				{
					updateExperience(nbt);
					useRarity(event, stack, nbt);
					useWeaponAbilities(event, player, enemy, nbt);
					updateLevel(player, stack, nbt);
				}
			}
		}
		else if (event.getSource().getTrueSource() instanceof EntityLivingBase && event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			EntityLivingBase enemy = (EntityLivingBase) event.getSource().getTrueSource();
			
			for (ItemStack stack : player.inventory.armorInventory)
			{
				if (stack != null)
				{
					if (stack.getItem() instanceof ItemArmor)	
					{
						NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
						
						if (nbt != null)
						{
							updateExperience(nbt);
							useRarity(event, stack, nbt);
							useArmorAbilities(event, player, enemy, nbt);
							updateLevel(player, stack, nbt);
						}
					}
				}
			}
		}
		else if (event.getSource().getTrueSource() instanceof EntityArrow)
		{
			EntityArrow arrow = (EntityArrow) event.getSource().getTrueSource();
			
			if (arrow.shootingEntity instanceof EntityLivingBase && event.getEntityLiving() instanceof EntityPlayer)
			{
				EntityLivingBase enemy = (EntityLivingBase) arrow.shootingEntity;
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				
				for (ItemStack stack : player.inventory.armorInventory)
				{
					if (stack != null)
					{
						if (stack.getItem() instanceof ItemArmor)	
						{
							NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
							
							if (nbt != null)
							{
								updateExperience(nbt);
								useRarity(event, stack, nbt);
								useArmorAbilities(event, player, enemy, nbt);
								updateLevel(player, stack, nbt);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Called everytime an enemy is hurt. Used to add experience to weapons dealing damage.
	 * @param nbt
	 */
	private void updateExperience(NBTTagCompound nbt)
	{
		if (Experience.getLevel(nbt) < Config.maxLevel)
		{
			boolean isDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
			
			if (isDev)
				Experience.setExperience(nbt, Experience.getExperience(nbt) + Experience.getNeededExpForNextLevel(nbt) + 1);
			else
				Experience.setExperience(nbt, Experience.getExperience(nbt) + 1);
		}
	}
	
	/**
	 * Called everytime an enemy is hurt. Used to add dealing more damage or getting less damage.
	 * @param nbt
	 */
	private void useRarity(LivingHurtEvent event, ItemStack stack, NBTTagCompound nbt)
	{
		Rarity rarity = Rarity.getRarity(nbt);
		double damageMultiplier = rarity.getEffect();
		
		if (rarity != Rarity.DEFAULT)
			if (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemBow)
				event.setAmount((float) (event.getAmount() + (event.getAmount() * damageMultiplier)));
			else if (stack.getItem() instanceof ItemArmor)
				event.setAmount((float) (event.getAmount() / (1.0F + (damageMultiplier/5.2F))));
	}
	
	/**
	 * Called everytime an enemy is hurt. Used to use the current abilities a weapon might have.
	 * @param event
	 * @param player
	 * @param enemy
	 * @param nbt
	 */
	private void useWeaponAbilities(LivingHurtEvent event, EntityPlayer player, EntityLivingBase enemy, NBTTagCompound nbt)
	{
		if (enemy != null)
		{
			// active
			if (Ability.FIRE.hasAbility(nbt) && (int) (Math.random() * Config.firechance) == 0)
			{
				double multiplier = (Ability.FIRE.getLevel(nbt) + Ability.FIRE.getLevel(nbt)*4)/4;
				enemy.setFire((int) (multiplier));
			}
			
			if (Ability.FROST.hasAbility(nbt) && (int) (Math.random() * Config.frostchance) == 0)
			{
				double multiplier = (Ability.FROST.getLevel(nbt) + Ability.FROST.getLevel(nbt)*4)/3;
				enemy.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) (20 * multiplier), 10));
			}
			
			if (Ability.POISON.hasAbility(nbt) && (int) (Math.random() * Config.poisonchance) == 0)
			{
				double multiplier = (Ability.POISON.getLevel(nbt) + Ability.POISON.getLevel(nbt)*4)/2;
				enemy.addPotionEffect(new PotionEffect(MobEffects.POISON, (int) (20 * multiplier), Ability.POISON.getLevel(nbt)));
			}
			
			if (Ability.INNATE.hasAbility(nbt) && (int) (Math.random() * Config.innatechance) == 0)
			{
				double multiplier = (Ability.INNATE.getLevel(nbt) + Ability.INNATE.getLevel(nbt)*4)/3;
				enemy.addPotionEffect(new PotionEffect(MobEffects.WITHER, (int) (20 * multiplier), Ability.INNATE.getLevel(nbt)));
			}

			if (Ability.BOMBASTIC.hasAbility(nbt) && (int) (Math.random() * Config.bombasticchance) == 0)
			{
				double multiplierD = (Ability.BOMBASTIC.getLevel(nbt) + Ability.BOMBASTIC.getLevel(nbt)*4)/4;
				float multiplier = (float)multiplierD;
				World world = enemy.getEntityWorld();
					
					if (enemy instanceof EntityLivingBase && !(enemy instanceof EntityAnimal))
					{
						world.createExplosion(enemy, enemy.lastTickPosX, enemy.lastTickPosY, enemy.lastTickPosZ, multiplier, true);
					}
			}
			
			if (Ability.VOID.hasAbility(nbt) && (int) (Math.random() * Config.voidachance) == 0)
			{
				float multiplier = 0F;
				
				if (Ability.VOID.getLevel(nbt) == 1) multiplier = 0.21F;
				else if (Ability.VOID.getLevel(nbt) == 2) multiplier = 0.42F;
				else if (Ability.VOID.getLevel(nbt) == 3) multiplier = 0.63F;

				float damage = enemy.getMaxHealth() * multiplier;
				event.setAmount(damage);
			}
			
			// passive
			if (Ability.ILLUMINATION.hasAbility(nbt))
			{
				enemy.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) (20 * 6), Ability.ILLUMINATION.getLevel(nbt)));
			}
			
			if (Ability.BLOODTHIRST.hasAbility(nbt))
			{
				float addition =(float)(event.getAmount() * (Ability.BLOODTHIRST.getLevel(nbt) * 12) / 100);
				player.setHealth(player.getHealth()+addition);
			}
		}
	}
	
	private void useArmorAbilities(LivingHurtEvent event, EntityPlayer player, EntityLivingBase enemy, NBTTagCompound nbt)
	{
		if (enemy != null)
		{
			// active
			if (Ability.MOLTEN.hasAbility(nbt) && (int) (Math.random() * Config.moltenchance) == 0)
			{
				double multiplier = (Ability.MOLTEN.getLevel(nbt) + Ability.MOLTEN.getLevel(nbt)*5)/4 ;
				enemy.setFire((int) (multiplier));
			}
			
			if (Ability.FROZEN.hasAbility(nbt) && (int) (Math.random() * Config.frozenchance) == 0)
			{
				double multiplier = (Ability.FROZEN.getLevel(nbt) + Ability.FROZEN.getLevel(nbt)*5)/6 ;
				enemy.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) (20 * multiplier), 10));
			}
			
			if (Ability.TOXIC.hasAbility(nbt) && (int) (Math.random() * Config.toxicchance) == 0)
			{
				double multiplier = (Ability.TOXIC.getLevel(nbt) + Ability.TOXIC.getLevel(nbt)*4)/4 ;
				enemy.addPotionEffect(new PotionEffect(MobEffects.POISON, (int) (20 * multiplier), Ability.TOXIC.getLevel(nbt)));
			}
			
			if (Ability.ABSORB.hasAbility(nbt) && (int) (Math.random() * Config.absorbchance) == 0)
			{
				double multiplier = (Ability.ABSORB.getLevel(nbt) + Ability.ABSORB.getLevel(nbt)*5)/3 ;
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int) (20 * (multiplier)), Ability.ABSORB.getLevel(nbt)));
			}

			// passive
			if (Ability.BEASTIAL.hasAbility(nbt))
			{
				if (player.getHealth() <= (player.getMaxHealth() * 0.2F))
					player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 7, 0));
			}
			
			if (Ability.HARDENED.hasAbility(nbt) && (int) (Math.random() * Config.hardenedchance) == 0)
			{
				event.setAmount(0F);
			}
		}
	}
	
	/**
	 * Called everytime an enemy is hurt. Used to check whether or not the weapon should level up.
	 * @param player
	 * @param stack
	 * @param nbt
	 */
	private void updateLevel(EntityPlayer player, ItemStack stack, NBTTagCompound nbt)
	{
		int level = Experience.getNextLevel(player, stack, nbt, Experience.getLevel(nbt), Experience.getExperience(nbt));
		Experience.setLevel(nbt, level);
		NBTHelper.saveStackNBT(stack, nbt);
	}
}
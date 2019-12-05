package com.kenymylankca.enhancedarmaments.event;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.kenymylankca.enhancedarmaments.config.Config;
import com.kenymylankca.enhancedarmaments.essentials.Ability;
import com.kenymylankca.enhancedarmaments.essentials.Experience;
import com.kenymylankca.enhancedarmaments.essentials.Rarity;
import com.kenymylankca.enhancedarmaments.util.EAUtils;
import com.kenymylankca.enhancedarmaments.util.NBTHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventLivingHurt
{
	public static EnumHand bowfriendlyhand;
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent event)
	{
		if(event.getEntity() instanceof EntityArrow)
		{
			if(((EntityArrow)event.getEntity()).shootingEntity instanceof EntityPlayer)
			{
				EntityPlayer player=(EntityPlayer) ((EntityArrow)event.getEntity()).shootingEntity;
				if(event.getRayTraceResult().entityHit == null)
					bowfriendlyhand=player.getActiveHand();
			}
		}
	}
	
	@SubscribeEvent
	public void onArrowShoot(ArrowLooseEvent event)
	{
		bowfriendlyhand=event.getEntityPlayer().getActiveHand();
	}
	
	@SubscribeEvent
	public void onHurt(LivingHurtEvent event)
	{
		if (bowfriendlyhand == null) bowfriendlyhand = EnumHand.MAIN_HAND;
		
		if (event.getSource().getTrueSource() instanceof EntityPlayer && !(event.getSource().getTrueSource() instanceof FakePlayer))
		{//PLAYER IS ATTACKER
			EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
			EntityLivingBase target = event.getEntityLiving();
			ItemStack stack = player.getHeldItem(bowfriendlyhand);
			
			if (stack != null && EAUtils.canEnhanceWeapon(stack.getItem()))
			{
				NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
				
				if (nbt != null)
					if(nbt.hasKey("EA_ENABLED"))
					{
						updateExperience(nbt, event.getAmount());
						useRarity(event, stack, nbt);
						useWeaponAbilities(event, player, target, nbt);
						updateLevel(player, stack, nbt);
					}
			}
			bowfriendlyhand = player.getActiveHand();
		}
		else if (event.getEntityLiving() instanceof EntityPlayer)
		{//PLAYER IS GETTING HURT
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			Entity target = event.getSource().getTrueSource();
			
			for (ItemStack stack : player.inventory.armorInventory)
			{
				if (stack != null)
				{
					if (EAUtils.canEnhanceArmor(stack.getItem()))	
					{
						NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
						
						if (nbt != null)
							if(nbt.hasKey("EA_ENABLED"))
							{
								if(EAUtils.isDamageSourceAllowed(event.getSource()))
								{
									if(event.getAmount() < (player.getMaxHealth() + player.getTotalArmorValue()))
										updateExperience(nbt, event.getAmount());
									else
										updateExperience(nbt, 1);
									updateLevel(player, stack, nbt);
								}
								useRarity(event, stack, nbt);
								useArmorAbilities(event, player, target, nbt);
							}
					}
				}
			}
		}
	}
	
	/**
	 * Called everytime a target is hurt. Used to add experience to weapons dealing damage.
	 * @param nbt
	 */
	private void updateExperience(NBTTagCompound nbt, float dealedDamage)
	{
		if (Experience.getLevel(nbt) < Config.maxLevel)
		{
			boolean isDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
			
			if (isDev)
				Experience.setExperience(nbt, Experience.getExperience(nbt) + Experience.getNeededExpForNextLevel(nbt) + 1);
			else
				Experience.setExperience(nbt, Experience.getExperience(nbt) + 1 + (int)dealedDamage/4);
		}
	}
	
	/**
	 * Called everytime a target is hurt. Used to add dealing more damage or getting less damage.
	 * @param nbt
	 */
	private void useRarity(LivingHurtEvent event, ItemStack stack, NBTTagCompound nbt)
	{
		Rarity rarity = Rarity.getRarity(nbt);
		
		if (rarity != Rarity.DEFAULT)
			if (EAUtils.canEnhanceMelee(stack.getItem()))
			{
				Multimap<String, AttributeModifier> map = stack.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
				Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
				AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
				double damage = damageModifier.getAmount();
				event.setAmount((float) (event.getAmount() + damage * rarity.getEffect()));
			}
			else if(EAUtils.canEnhanceRanged(stack.getItem()))
			{
				float newdamage = (float) (event.getAmount() + (event.getAmount() * rarity.getEffect()/3));
				event.setAmount(newdamage);
			}
			else if (EAUtils.canEnhanceArmor(stack.getItem()))
				event.setAmount((float) (event.getAmount() / (1.0F + (rarity.getEffect()/5F))));
	}
	
	/**
	 * Called everytime a target is hurt. Used to use the current abilities a weapon might have.
	 * @param event
	 * @param player
	 * @param target
	 * @param nbt
	 */
	private void useWeaponAbilities(LivingHurtEvent event, EntityPlayer player, EntityLivingBase target, NBTTagCompound nbt)
	{
		if (target != null)
		{
			// active
			if (Ability.FIRE.hasAbility(nbt) && (int) (Math.random() * Config.firechance) == 0)
			{
				double multiplier = (Ability.FIRE.getLevel(nbt) + Ability.FIRE.getLevel(nbt)*4)/4;
				target.setFire((int) (multiplier));
			}
			
			if (Ability.FROST.hasAbility(nbt) && (int) (Math.random() * Config.frostchance) == 0)
			{
				double multiplier = (Ability.FROST.getLevel(nbt) + Ability.FROST.getLevel(nbt)*4)/3;
				target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) (20 * multiplier), 10));
			}
			
			if (Ability.POISON.hasAbility(nbt) && (int) (Math.random() * Config.poisonchance) == 0)
			{
				double multiplier = (Ability.POISON.getLevel(nbt) + Ability.POISON.getLevel(nbt)*4)/2;
				target.addPotionEffect(new PotionEffect(MobEffects.POISON, (int) (20 * multiplier), Ability.POISON.getLevel(nbt)));
			}
			
			if (Ability.INNATE.hasAbility(nbt))
			{
				double multiplier = (Ability.INNATE.getLevel(nbt) + Ability.INNATE.getLevel(nbt)*4)/3;
				target.addPotionEffect(new PotionEffect(MobEffects.WITHER, (int) (20 * multiplier), Ability.INNATE.getLevel(nbt)));
			}

			if (Ability.BOMBASTIC.hasAbility(nbt) && (int) (Math.random() * Config.bombasticchance) == 0)
			{
				double multiplierD = (Ability.BOMBASTIC.getLevel(nbt) + Ability.BOMBASTIC.getLevel(nbt)*4)/4;
				float multiplier = (float)multiplierD;
				World world = target.getEntityWorld();
					
					if (target instanceof EntityLivingBase && !(target instanceof EntityAnimal))
					{
						world.createExplosion(target, target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ, multiplier, true);
					}
			}
			
			if (Ability.CRITICAL_POINT.hasAbility(nbt) && (int) (Math.random() * Config.criticalpointchance) == 0)
			{
				float multiplier = 0F;
				
				if (Ability.CRITICAL_POINT.getLevel(nbt) == 1) multiplier = 0.17F;
				else if (Ability.CRITICAL_POINT.getLevel(nbt) == 2) multiplier = 0.34F;
				else if (Ability.CRITICAL_POINT.getLevel(nbt) == 3) multiplier = 0.51F;

				float damage = target.getMaxHealth() * multiplier;
				event.setAmount(event.getAmount() + damage);
			}
			
			// passive
			if (Ability.ILLUMINATION.hasAbility(nbt))
			{
				target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (20 * 5), Ability.ILLUMINATION.getLevel(nbt)));
			}
			
			if (Ability.BLOODTHIRST.hasAbility(nbt))
			{
				float addition =(float)(event.getAmount() * (Ability.BLOODTHIRST.getLevel(nbt) * 12) / 100);
				player.setHealth(player.getHealth()+addition);
			}
		}
	}
	
	private void useArmorAbilities(LivingHurtEvent event, EntityPlayer player, Entity target, NBTTagCompound nbt)
	{
		if (target != null)
		{
			// active
			if (Ability.MOLTEN.hasAbility(nbt) && (int) (Math.random() * Config.moltenchance) == 0 && target instanceof EntityLivingBase)
			{
				EntityLivingBase realTarget = (EntityLivingBase) target;
				double multiplier = (Ability.MOLTEN.getLevel(nbt) + Ability.MOLTEN.getLevel(nbt)*5)/4 ;
				realTarget.setFire((int) (multiplier));
			}
			
			if (Ability.FROZEN.hasAbility(nbt) && (int) (Math.random() * Config.frozenchance) == 0 && target instanceof EntityLivingBase)
			{
				EntityLivingBase realTarget = (EntityLivingBase) target;
				double multiplier = (Ability.FROZEN.getLevel(nbt) + Ability.FROZEN.getLevel(nbt)*5)/6 ;
				realTarget.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) (20 * multiplier), 10));
			}
			
			if (Ability.TOXIC.hasAbility(nbt) && (int) (Math.random() * Config.toxicchance) == 0 && target instanceof EntityLivingBase)
			{
				EntityLivingBase realTarget = (EntityLivingBase) target;
				double multiplier = (Ability.TOXIC.getLevel(nbt) + Ability.TOXIC.getLevel(nbt)*4)/4 ;
				realTarget.addPotionEffect(new PotionEffect(MobEffects.POISON, (int) (20 * multiplier), Ability.TOXIC.getLevel(nbt)));
			}
			
			if (Ability.ADRENALINE.hasAbility(nbt) && (int) (Math.random() * Config.adrenalinechance) == 0)
			{
				double multiplier = (Ability.ADRENALINE.getLevel(nbt) + Ability.ADRENALINE.getLevel(nbt)*5)/3 ;
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int) (20 * (multiplier)), Ability.ADRENALINE.getLevel(nbt)));
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
	 * Called everytime a target is hurt. Used to check whether or not the weapon should level up.
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
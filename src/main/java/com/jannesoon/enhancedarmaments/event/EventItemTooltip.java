package com.jannesoon.enhancedarmaments.event;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Multimap;
import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.essentials.Ability;
import com.jannesoon.enhancedarmaments.essentials.Experience;
import com.jannesoon.enhancedarmaments.essentials.Rarity;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Displays information about the weapon when hovered over in an inventory.
 *
 */
public class EventItemTooltip 
{
	/**
	 * Gets called whenever the tooltip for an item needs to appear.
	 * @param event
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void addInformation(ItemTooltipEvent event)
	{
		ArrayList<String> tooltip = (ArrayList<String>) event.getToolTip();
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if (item != null)
		{
			if (EAUtils.canEnhance(item))
			{
				NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
				
				if (nbt != null && Experience.isEnabled(nbt))
				{
					Rarity rarity = Rarity.getRarity(nbt);
					int level = Experience.getLevel(nbt);
					int experience = Experience.getExperience(nbt);
					int maxExperience = Experience.getMaxLevelExp(level);
					
					changeTooltips(tooltip, stack, rarity);
					
				// add tooltips
					
					// level
					if (level >= Config.maxLevel)
						tooltip.add(I18n.format("enhancedarmaments.misc.level") + ": " + TextFormatting.RED + I18n.format("enhancedarmaments.misc.max"));
					else
						tooltip.add(I18n.format("enhancedarmaments.misc.level") + ": " + TextFormatting.WHITE + level);
					
					// experience
					if (level >= Config.maxLevel)
						tooltip.add(I18n.format("enhancedarmaments.misc.experience") + ": " + I18n.format("enhancedarmaments.misc.max"));
					else
						tooltip.add(I18n.format("enhancedarmaments.misc.experience") + ": " + experience + " / " + maxExperience);
					
					// durability
					if (Config.showDurability)
					{
						tooltip.add(I18n.format("enhancedarmaments.misc.durability") + ": " + (stack.getMaxDamage() - stack.getItemDamage()) + " / " + stack.getMaxDamage());
					}
					
					// abilities
					tooltip.add("");
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
					{
						tooltip.add(rarity.getColor() + "" + TextFormatting.ITALIC + I18n.format("enhancedarmaments.misc.abilities"));
						tooltip.add("");
						
						if (EAUtils.canEnhanceWeapon(item))
						{
							for (Ability ability : Ability.WEAPON_ABILITIES)
							{
								if (ability.hasAbility(nbt))
								{
									tooltip.add("-" + ability.getColor() + ability.getName(nbt));
								}
							}
						}
						else if (EAUtils.canEnhanceArmor(item))
						{
							for (Ability ability : Ability.ARMOR_ABILITIES)
							{
								if (ability.hasAbility(nbt))
								{
									tooltip.add("-" + ability.getColor() + ability.getName(nbt));
								}
							}
						}
					}
					else
						tooltip.add(rarity.getColor() + "" + TextFormatting.ITALIC + I18n.format("enhancedarmaments.misc.abilities.shift"));
				}
			}
		}
	}
	
	private void changeTooltips(ArrayList<String> tooltip, ItemStack stack, Rarity rarity)
	{
		// rarity after the name
		tooltip.set(0, stack.getDisplayName() + rarity.getColor() + " (" + TextFormatting.ITALIC + I18n.format("enhancedarmaments.rarity." + rarity.getName()) + ")");
		
		if (tooltip.indexOf("When in main hand:") != -1)
		{
			Multimap<String, AttributeModifier> map = stack.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
			Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
			double damage = ((damageModifier.getAmount() + 1) * rarity.getEffect()) + damageModifier.getAmount() + 1;
			String d = String.format("%.1f", damage);
			
			if(rarity.getEffect() != 0)
				tooltip.set(tooltip.indexOf("When in main hand:") + 2, rarity.getColor()+" " + d + TextFormatting.GRAY +" "+ I18n.format("enhancedarmaments.misc.tooltip.attackdamage"));
		}
		if (tooltip.indexOf("When on head:") != -1 || tooltip.indexOf("When on body:") != -1 || tooltip.indexOf("When on legs:") != -1 || tooltip.indexOf("When on feet:") != -1)
		{
			String p = String.format("%.1f", 100-(100/(1.0F + (rarity.getEffect()/4F))));
			float percentage = Float.valueOf(p);
			int line = 2;
			if(tooltip.indexOf("When on head:") != -1) line = tooltip.indexOf("When on head:");
			if(tooltip.indexOf("When on body:") != -1) line = tooltip.indexOf("When on body:");
			if(tooltip.indexOf("When on legs:") != -1) line = tooltip.indexOf("When on legs:");
			if(tooltip.indexOf("When on feet:") != -1) line = tooltip.indexOf("When on feet:");
			if(percentage != 0)
				tooltip.add(line + 1," " + TextFormatting.BLUE + "+" + rarity.getColor() + percentage + TextFormatting.BLUE + "% " + I18n.format("enhancedarmaments.misc.rarity.armorreduction"));
		}
		
		if(EAUtils.canEnhanceRanged(stack.getItem()) && rarity.getEffect() != 0)
		{
			String b = String.format("%.1f", rarity.getEffect()/3*100);
			tooltip.add(1, I18n.format("enhancedarmaments.misc.rarity.arrowpercentage") + " " + rarity.getColor() + "+" + b + "%");
		}
	}
}
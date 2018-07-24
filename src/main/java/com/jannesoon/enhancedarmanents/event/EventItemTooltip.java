package com.jannesoon.enhancedarmanents.event;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Multimap;
import com.jannesoon.enhancedarmanents.config.Config;
import com.jannesoon.enhancedarmanents.leveling.Ability;
import com.jannesoon.enhancedarmanents.leveling.Experience;
import com.jannesoon.enhancedarmanents.leveling.Rarity;
import com.jannesoon.enhancedarmanents.util.NBTHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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
			if (item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemArmor || item instanceof ItemBow)
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
						tooltip.add(TextFormatting.GRAY + I18n.format("enhancedarmanents.misc.level") + ": " + I18n.format("enhancedarmanents.misc.max"));
					else
						tooltip.add(TextFormatting.GRAY + I18n.format("enhancedarmanents.misc.level") + ": " + level);
					
					// experience
					if (level >= Config.maxLevel)
						tooltip.add(TextFormatting.GRAY + I18n.format("enhancedarmanents.misc.experience") + ": " + I18n.format("enhancedarmanents.misc.max"));
					else
						tooltip.add(TextFormatting.GRAY + I18n.format("enhancedarmanents.misc.experience") + ": " + experience + " / " + maxExperience);
					
					// durability
					if (Config.showDurability)
					{
						tooltip.add(TextFormatting.GRAY + I18n.format("enhancedarmanents.misc.durability") + ": " + (stack.getMaxDamage() - stack.getItemDamage()) + " / " + stack.getMaxDamage());
					}
					
					// abilities
					tooltip.add("");
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
					{
						tooltip.add(rarity.getColor() + "" + TextFormatting.ITALIC + I18n.format("enhancedarmanents.misc.abilities"));
						tooltip.add("");
						
						if (item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemHoe || item instanceof ItemBow)
						{
							for (Ability ability : Ability.WEAPONS)
							{
								if (ability.hasAbility(nbt))
								{
									tooltip.add(ability.getColor() + ability.getName(nbt));
								}
							}
						}
						else if (item instanceof ItemArmor)
						{
							for (Ability ability : Ability.ARMOR)
							{
								if (ability.hasAbility(nbt))
								{
									tooltip.add(ability.getColor() + ability.getName(nbt));
								}
							}
						}
					}
					else
						tooltip.add(rarity.getColor() + "" + TextFormatting.ITALIC + I18n.format("enhancedarmanents.misc.abilities.shift"));
				}
			}
		}
	}
	
	private void changeTooltips(ArrayList<String> tooltip, ItemStack stack, Rarity rarity)
	{	
		// rarity after the name
		tooltip.set(0, stack.getDisplayName() + rarity.getColor() + " (" + TextFormatting.ITALIC + I18n.format("enhancedarmanents.rarity." + rarity.getName()) + ")");
		
		if (tooltip.indexOf("When in main hand:") != -1)
		{
			Multimap<String, AttributeModifier> map = stack.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
			Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
			
			double damage = damageModifier.getAmount();
			
			tooltip.set(tooltip.indexOf("When in main hand:") + 2, tooltip.get(tooltip.indexOf("When in main hand:") + 2) + rarity.getColor() + " (+" + rarity.getEffect() + ")");
		}
		if (tooltip.indexOf("When on head:") != -1 || tooltip.indexOf("When on body:") != -1 || tooltip.indexOf("When on legs:") != -1 || tooltip.indexOf("When on feet:") != -1)
		{
			String p = String.format("%.2f", 100-(100/(1.0F + (rarity.getEffect()/5.2F))));
			float percentage = Float.valueOf(p);
			int line = 2;
			if(tooltip.indexOf("When on head:") != -1) line = tooltip.indexOf("When on head:");
			if(tooltip.indexOf("When on body:") != -1) line = tooltip.indexOf("When on body:");
			if(tooltip.indexOf("When on legs:") != -1) line = tooltip.indexOf("When on legs:");
			if(tooltip.indexOf("When on feet:") != -1) line = tooltip.indexOf("When on feet:");
			if(percentage != 0)
				tooltip.add(line + 1," " + TextFormatting.BLUE + "+" + rarity.getColor() + percentage + TextFormatting.BLUE + "% " + I18n.format("enhancedarmanents.misc.rarity.armorreduction"));
		}
	}
}
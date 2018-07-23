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
					// formatting
					tooltip.add("");
					tooltip.add(rarity.getColor() + "---------------");
					tooltip.add("");
					
					// rarity
					tooltip.add(rarity.getColor() + TextFormatting.ITALIC + I18n.format("enhancedarmanents.rarity." + rarity.getName()));
					
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
						tooltip.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + I18n.format("enhancedarmanents.misc.abilities"));
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
						tooltip.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + I18n.format("enhancedarmanents.misc.abilities.shift"));
					
					// formatting
					tooltip.add("");
					tooltip.add(rarity.getColor() + "---------------");
					tooltip.add("");
				}
			}
		}
	}
	
	private void changeTooltips(ArrayList<String> tooltip, ItemStack stack, Rarity rarity)
	{	
		if (tooltip.indexOf("When in main hand:") != -1)
		{
			Multimap<String, AttributeModifier> map = stack.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
			Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
			
			double damage = damageModifier.getAmount();
			
			switch (rarity)
			{
				case BASIC:
					int basicD = (int) (Config.basicDamage * (damage + 1F));
					tooltip.set(tooltip.indexOf("When in main hand:") + 2, tooltip.get(tooltip.indexOf("When in main hand:") + 2) + rarity.getColor() + " (+" + basicD + ")");
					break;
				case UNCOMMON:
					int uncommonD = (int) (Config.uncommonDamage * (damage + 1F));
					tooltip.set(tooltip.indexOf("When in main hand:") + 2, tooltip.get(tooltip.indexOf("When in main hand:") + 2) + rarity.getColor() + " (+" + uncommonD + ")");
					break;
				case RARE:
					int rareD = (int) (Config.rareDamage * (damage + 1F));
					tooltip.set(tooltip.indexOf("When in main hand:") + 2, tooltip.get(tooltip.indexOf("When in main hand:") + 2) + rarity.getColor() + " (+" + rareD + ")");
					break;
				case ULTRA_RARE:
					int ultrarareD = (int) (Config.ultraRareDamage * (damage + 1F));
					tooltip.set(tooltip.indexOf("When in main hand:") + 2, tooltip.get(tooltip.indexOf("When in main hand:") + 2) + rarity.getColor() + " (+" + ultrarareD + ")");
					break;
				case LEGENDARY:
					int legendaryD = (int) (Config.legendaryDamage * (damage + 1F));
					tooltip.set(tooltip.indexOf("When in main hand:") + 2, tooltip.get(tooltip.indexOf("When in main hand:") + 2) + rarity.getColor() + " (+" + legendaryD + ")");
					break;
				case ARCHAIC:
					int archaicD = (int) (Config.archaicDamage * (damage + 1F));
					tooltip.set(tooltip.indexOf("When in main hand:") + 2, tooltip.get(tooltip.indexOf("When in main hand:") + 2) + rarity.getColor() + " (+" + archaicD + ")");
					break;
				default:
					break;
			}
		}
	}
}

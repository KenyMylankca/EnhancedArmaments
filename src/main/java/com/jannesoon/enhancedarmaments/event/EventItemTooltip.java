package com.jannesoon.enhancedarmaments.event;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Multimap;
import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.essentials.Ability;
import com.jannesoon.enhancedarmaments.essentials.Experience;
import com.jannesoon.enhancedarmaments.essentials.Rarity;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;
import com.sun.jna.platform.KeyboardUtils;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Displays information about the weapon when hovered over in an inventory.
 *
 */
@Mod.EventBusSubscriber
public class EventItemTooltip 
{
	/**
	 * Gets called whenever the tooltip for an item needs to appear.
	 * @param event
	 */
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void addInformation(ItemTooltipEvent event)
	{
		List<ITextComponent> tooltip = (List<ITextComponent>) event.getToolTip();
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
						tooltip.add(new TextComponentString(I18n.format("enhancedarmaments.misc.level") + ": " + TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")));
					else
						tooltip.add(new TextComponentString(I18n.format("enhancedarmaments.misc.level") + ": " + TextFormatting.WHITE + level));
					
					// experience
					if (level >= Config.maxLevel)
						tooltip.add(new TextComponentString(I18n.format("enhancedarmaments.misc.experience") + ": " + I18n.format("enhancedarmaments.misc.max")));
					else
						tooltip.add(new TextComponentString(I18n.format("enhancedarmaments.misc.experience") + ": " + experience + " / " + maxExperience));
					
					// durability
					if (Config.showDurability)
					{
						tooltip.add(new TextComponentString(I18n.format("enhancedarmaments.misc.durability") + ": " + (stack.getMaxDamage() - stack.getDamage()) + " / " + stack.getMaxDamage()));
					}
					
					// abilities
					tooltip.add(new TextComponentString(""));
					if (KeyboardUtils.isPressed(75)) //TODO check this out
					{
						tooltip.add(new TextComponentString(rarity.getColor() + "" + TextFormatting.ITALIC + I18n.format("enhancedarmaments.misc.abilities")));
						tooltip.add(new TextComponentString(""));
						
						if (EAUtils.canEnhanceWeapon(item))
						{
							for (Ability ability : Ability.WEAPON_ABILITIES)
							{
								if (ability.hasAbility(nbt))
								{
									tooltip.add(new TextComponentTranslation("-" + ability.getColor() + ability.getName(nbt)));
								}
							}
						}
						else if (EAUtils.canEnhanceArmor(item))
						{
							for (Ability ability : Ability.ARMOR_ABILITIES)
							{
								if (ability.hasAbility(nbt))
								{
									tooltip.add(new TextComponentTranslation("-" + ability.getColor() + ability.getName(nbt)));
								}
							}
						}
					}
					else
						tooltip.add(new TextComponentString(rarity.getColor() + "" + TextFormatting.ITALIC + I18n.format("enhancedarmaments.misc.abilities.shift")));
				}
			}
		}
	}
	
	private void changeTooltips(List<ITextComponent> tooltip, ItemStack stack, Rarity rarity)
	{
		// rarity after the name
		tooltip.set(0, new TextComponentString(stack.getDisplayName() + rarity.getColor() + " (" + TextFormatting.ITALIC + I18n.format("enhancedarmaments.rarity." + rarity.getName()) + ")"));
		
		
		if (tooltip.stream().filter(text -> text.getString().equals("When in Main Hand")).findAny().map(tooltip::indexOf).get() != -1 && !(stack.getItem() instanceof ItemBow))
		{
			Multimap<String, AttributeModifier> map = stack.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
			Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
			double damage = ((damageModifier.getAmount() + 1) * rarity.getEffect()) + damageModifier.getAmount() + 1;
			String d = String.format("%.1f", damage);
			
			if(rarity.getEffect() != 0)
				tooltip.set(tooltip.stream().filter(text -> text.getString().equals("When in Main Hand")).findAny().map(tooltip::indexOf).get() + 2, new TextComponentString(rarity.getColor() + " " + d + TextFormatting.GRAY +" "+ I18n.format("enhancedarmaments.misc.tooltip.attackdamage")));
		}
		
		if (tooltip.stream().filter(text -> text.getString().equals("When on head")).findAny().map(tooltip::indexOf).get() != -1
				|| tooltip.stream().filter(text -> text.getString().equals("When on body")).findAny().map(tooltip::indexOf).get() != -1
				|| tooltip.stream().filter(text -> text.getString().equals("When on legs")).findAny().map(tooltip::indexOf).get() != -1
				|| tooltip.stream().filter(text -> text.getString().equals("When on feet")).findAny().map(tooltip::indexOf).get() != -1)
		{
			String p = String.format("%.1f", 100-(100/(1.0F + (rarity.getEffect()/5F))));
			float percentage = Float.valueOf(p);
			int line = 2;
			if(tooltip.stream().filter(text -> text.getString().equals("When on head")).findAny().map(tooltip::indexOf).get() != -1) line = tooltip.stream().filter(text -> text.getString().equals("When on head")).findAny().map(tooltip::indexOf).get();
			if(tooltip.stream().filter(text -> text.getString().equals("When on body")).findAny().map(tooltip::indexOf).get() != -1) line = tooltip.stream().filter(text -> text.getString().equals("When on body")).findAny().map(tooltip::indexOf).get();
			if(tooltip.stream().filter(text -> text.getString().equals("When on legs")).findAny().map(tooltip::indexOf).get() != -1) line = tooltip.stream().filter(text -> text.getString().equals("When on legs")).findAny().map(tooltip::indexOf).get();
			if(tooltip.stream().filter(text -> text.getString().equals("When on feet")).findAny().map(tooltip::indexOf).get() != -1) line = tooltip.stream().filter(text -> text.getString().equals("When on feet")).findAny().map(tooltip::indexOf).get();
			if(percentage != 0)
				tooltip.add(line + 1, new TextComponentString(" " + TextFormatting.BLUE + "+" + rarity.getColor() + percentage + TextFormatting.BLUE + "% " + I18n.format("enhancedarmaments.misc.rarity.armorreduction")));
		}
		
		if(EAUtils.canEnhanceRanged(stack.getItem()) && rarity.getEffect() != 0)
		{
			String b = String.format("%.1f", rarity.getEffect()/3*100);
			tooltip.add(1, new TextComponentString(I18n.format("enhancedarmaments.misc.rarity.arrowpercentage") + " " + rarity.getColor() + "+" + b + "%"));
		}
	}
}
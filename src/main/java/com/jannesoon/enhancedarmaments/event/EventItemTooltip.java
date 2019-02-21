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

import net.minecraft.client.gui.GuiScreen;
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
		List<ITextComponent> tooltip = event.getToolTip();
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if (EAUtils.canEnhance(item))
		{
			NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);

			if (Experience.isEnabled(nbt))
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
				if (Config.showDurabilityInTooltip)
				{
					tooltip.add(new TextComponentString(I18n.format("enhancedarmaments.misc.durability") + ": " + (stack.getMaxDamage() - stack.getDamage()) + " / " + stack.getMaxDamage()));
				}

				// abilities
				tooltip.add(new TextComponentString(""));
				if (GuiScreen.isShiftKeyDown())
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
	
	private void changeTooltips(List<ITextComponent> tooltip, ItemStack stack, Rarity rarity)
	{
		// rarity after the name
		tooltip.set(0, new TextComponentString(stack.getDisplayName().getFormattedText() + rarity.getColor() + " (" + TextFormatting.ITALIC + I18n.format("enhancedarmaments.rarity." + rarity.getName()) + ")"));
		
		if (EAUtils.containsString(tooltip, "When in main hand:") && !(stack.getItem() instanceof ItemBow))
		{
			Multimap<String, AttributeModifier> map = stack.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
			Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
			double damage = ((damageModifier.getAmount() + 1) * rarity.getEffect()) + damageModifier.getAmount() + 1;
			String d = String.format("%.1f", damage);
			
			if(rarity.getEffect() != 0)
				tooltip.set(EAUtils.lineContainsString(tooltip, "When in main hand:") + 2, new TextComponentString(rarity.getColor() + " " + d + TextFormatting.GRAY +" "+ I18n.format("enhancedarmaments.misc.tooltip.attackdamage")));
		}
		
		if (EAUtils.containsString(tooltip, "When on head:") || EAUtils.containsString(tooltip, "When on body:") || EAUtils.containsString(tooltip, "When on legs:") || EAUtils.containsString(tooltip, "When on feet:"))
		{
			String p = String.format("%.1f", 100-(100/(1.0F + (rarity.getEffect()/5F))));
			float percentage = Float.valueOf(p);
			int line = 2;
			if(EAUtils.containsString(tooltip, "When on head:")) line = EAUtils.lineContainsString(tooltip, "When on head:");
			if(EAUtils.containsString(tooltip, "When on body:")) line = EAUtils.lineContainsString(tooltip, "When on body:");
			if(EAUtils.containsString(tooltip, "When on legs:")) line = EAUtils.lineContainsString(tooltip, "When on legs:");
			if(EAUtils.containsString(tooltip, "When on feet:")) line = EAUtils.lineContainsString(tooltip, "When on feet:");
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
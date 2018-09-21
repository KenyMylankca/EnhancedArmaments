package com.jannesoon.enhancedarmaments.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jannesoon.enhancedarmaments.EnhancedArmaments;
import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.essentials.Ability;
import com.jannesoon.enhancedarmaments.essentials.Experience;
import com.jannesoon.enhancedarmaments.essentials.Rarity;
import com.jannesoon.enhancedarmaments.network.PacketGuiAbility;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
public class GuiAbilitySelection extends GuiScreen
{
	private GuiButton[] weaponAbilities;
	private GuiButton[] armorAbilities;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initGui() 
	{
		EntityPlayer player = this.mc.player;
	    
	    if (player != null)
	    {
	    	ItemStack stack = player.inventory.getCurrentItem();
	    	
	    	if (stack != null)
	    	{
	    		if (EAUtils.canEnhanceWeapon(stack.getItem()))
		    	{
		    		weaponAbilities = new GuiButton[Ability.WEAPON_ABILITIES];
		    		NBTTagCompound nbt = stack.getTagCompound();
		    		
		    		if (nbt != null)
		    		{
		    			int j = 0;
		    			
		    			for (int i = 0; i < weaponAbilities.length; i++)
		    			{
		    				if (Ability.WEAPONS.get(i).getType().equals("active"))
			    			{
		    					weaponAbilities[i] = new GuiButton(i, width / 2 - 215, 100 + (i * 21), 100, 20, I18n.format("enhancedarmaments.ability." + Ability.WEAPONS.get(i).getName()) + " (" + Ability.WEAPONS.get(i).getTier() + ")");
		    					j++;
			    			}
		    				else
			    				weaponAbilities[i] = new GuiButton(i, width / 2 - 100, 100 + ((i - j) * 21), 105, 20, I18n.format("enhancedarmaments.ability." + Ability.WEAPONS.get(i).getName()) + " (" + Ability.WEAPONS.get(i).getTier() + ")");
		    				
		    				this.buttonList.add(weaponAbilities[i]);
		    				weaponAbilities[i].enabled = false;
		    			}
		    		}
		    	}
		    	else if (EAUtils.canEnhanceArmor(stack.getItem()))
		    	{
		    		armorAbilities = new GuiButton[Ability.ARMOR_ABILITIES];
		    		NBTTagCompound nbt = stack.getTagCompound();

		    		if (nbt != null)
		    		{
		    			int j = 0;
		    			
		    			for (int i = 0; i < armorAbilities.length; i++)
		    			{
		    				if (Ability.ARMOR.get(i).getType().equals("active"))
			    			{
		    					armorAbilities[i] = new GuiButton(i, width / 2 - 215, 100 + (i * 21), 100, 20, I18n.format("enhancedarmaments.ability." + Ability.ARMOR.get(i).getName()) + " (" + Ability.ARMOR.get(i).getTier() + ")");
		    					j++;
			    			}
		    				else
		    					armorAbilities[i] = new GuiButton(i, width / 2 - 100, 100 + ((i - j) * 21), 105, 20, I18n.format("enhancedarmaments.ability." + Ability.ARMOR.get(i).getName()) + " (" + Ability.ARMOR.get(i).getTier() + ")");
		    				
		    				this.buttonList.add(armorAbilities[i]);
		    				armorAbilities[i].enabled = false;
		    			}
		    		}
		    	}
	    	}
	    }
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		EntityPlayer player = this.mc.player;
	    
	    if (player != null)
	    {
	    	ItemStack stack = player.inventory.getCurrentItem();
	    	
	    	if (stack != null)
	    	{
	    		if (EAUtils.canEnhance(stack.getItem()))
	    		{
	    			NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
		    		
		    		if (nbt != null)
		    		{	
		    			if (EAUtils.canEnhanceWeapon(stack.getItem()))
		    			{
		    				drawStrings(stack, Ability.WEAPONS, nbt);
		    				displayButtons(weaponAbilities, Ability.WEAPONS, nbt);
		    				drawTooltips(weaponAbilities, Ability.WEAPONS, mouseX, mouseY);
		    			}
		    			else if (EAUtils.canEnhanceArmor(stack.getItem()))
		    			{
		    				drawStrings(stack, Ability.ARMOR, nbt);
		    				displayButtons(armorAbilities, Ability.ARMOR, nbt);
		    				drawTooltips(armorAbilities, Ability.ARMOR, mouseX, mouseY);
		    			}
		    		}
	    		}
	    	}
	    }
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected void actionPerformed(GuiButton button) throws IOException 
	{
		EntityPlayerSP player = mc.player;
		
		if (player != null)
		{
			ItemStack stack = player.inventory.getCurrentItem();
			
			if (stack != null)
			{
				NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
				
				if (nbt != null)
				{
					if (Experience.getAbilityTokens(nbt) > 0)
					{
						if (EAUtils.canEnhanceWeapon(stack.getItem()))
						{
							for (int i = 0; i < weaponAbilities.length; i++)
							{
								if (button == weaponAbilities[i])
								{
									EnhancedArmaments.network.sendToServer(new PacketGuiAbility(i));
								}
							}
						}
						else if (EAUtils.canEnhanceArmor(stack.getItem()))
						{
							for (int i = 0; i < armorAbilities.length; i++)
							{
								if (button == armorAbilities[i])
								{
									EnhancedArmaments.network.sendToServer(new PacketGuiAbility(i));
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Draws the strings for the ability selection screen.
	 * @param stack
	 * @param abilities
	 * @param nbt
	 */
	private void drawStrings(ItemStack stack, ArrayList<Ability> abilities, NBTTagCompound nbt)
	{
		Rarity rarity = Rarity.getRarity(nbt);
		
		drawCenteredString(fontRenderer, stack.getDisplayName(), width / 2, 20, 0xFFFFFF);
		drawString(fontRenderer, I18n.format("enhancedarmaments.misc.rarity") + ": ", width / 2 - 50, 40, 0xFFFFFF);
		drawString(fontRenderer, I18n.format("enhancedarmaments.rarity." + rarity.getName()), width / 2 - 15, 40, rarity.getHex());
		drawCenteredString(fontRenderer, TextFormatting.ITALIC + I18n.format("enhancedarmaments.misc.abilities"), width / 2 - 100, 73, 0xFFFFFF);
		drawCenteredString(fontRenderer, TextFormatting.DARK_GREEN + I18n.format("enhancedarmaments.misc.abilities.tokens") + ": " + Experience.getAbilityTokens(nbt), width / 2 - 100, 86, 0xFFFFFF);
		drawCenteredString(fontRenderer, TextFormatting.GOLD + I18n.format("enhancedarmaments.misc.abilities.purchased"), width / 2 + 112, 100, 0xFFFFFF);
		drawCenteredString(fontRenderer, TextFormatting.BOLD + I18n.format("enhancedarmaments.ability.type.active"), width / 2 + 75, 120, 0xFFFFFF);
		drawCenteredString(fontRenderer, TextFormatting.BOLD + I18n.format("enhancedarmaments.ability.type.passive"), width / 2 + 150, 120, 0xFFFFFF);
		
		if (Experience.getLevel(nbt) == Config.maxLevel)
		{
			drawString(fontRenderer, I18n.format("enhancedarmaments.misc.level") + ": " + Experience.getLevel(nbt) + TextFormatting.DARK_RED +" (" + I18n.format("enhancedarmaments.misc.max") + ")", width / 2 - 50, 50, 0xFFFFFF);
			drawString(fontRenderer, I18n.format("enhancedarmaments.misc.experience") + ": " + Experience.getExperience(nbt), width / 2 - 50, 60, 0xFFFFFF);
		}
		else
		{
			drawString(fontRenderer, I18n.format("enhancedarmaments.misc.level") + ": " + Experience.getLevel(nbt), width / 2 - 50, 50, 0xFFFFFF);
			drawString(fontRenderer, I18n.format("enhancedarmaments.misc.experience") + ": " + Experience.getExperience(nbt) + " / " + Experience.getMaxLevelExp(Experience.getLevel(nbt)), width / 2 - 50, 60, 0xFFFFFF);
		}
		
		int j = -1;
		int k = -1;
		
		for (int i = 0; i < abilities.size(); i++)
		{
			if (abilities.get(i).hasAbility(nbt))
			{
				if (abilities.get(i).getType().equals("active"))
				{
					j++;
					drawCenteredString(fontRenderer, I18n.format(abilities.get(i).getName(nbt)), width / 2 + 75, 135 + (j * 12), abilities.get(i).getHex());
				}
				else if (abilities.get(i).getType().equals("passive"))
				{
					k++;
					drawCenteredString(fontRenderer, abilities.get(i).getName(nbt), width / 2 + 150, 135 + (k * 12), abilities.get(i).getHex());
				}
			}
		}
	}
	
	/**
	 * Determines which buttons need to be enabled based on available ability tokens and if the
	 * weapon is of a high enough level to enable.
	 * @param buttons
	 * @param abilities
	 * @param nbt
	 */
	private void displayButtons(GuiButton[] buttons, ArrayList<Ability> abilities, NBTTagCompound nbt)
	{
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i].enabled = false;
		}
		
		for (int i = 0; i < buttons.length; i++)
		{
			if(Experience.getAbilityTokens(nbt) >= abilities.get(i).getTier())
			{
				if(!(abilities.get(i).hasAbility(nbt)))
				{
					if(Ability.hasEnoughExp(nbt, player, abilities.get(i)))
						buttons[i].enabled = true;
				}
				else if (abilities.get(i).canUpgradeLevel(nbt))
					buttons[i].enabled = true;
				else
					buttons[i].enabled = false;
			}
		}
	}
	
	private void drawTooltips(GuiButton[] buttons, ArrayList<Ability> abilities, int mouseX, int mouseY)
	{
		EntityPlayer player = this.mc.player;
		ItemStack stack = player.inventory.getCurrentItem();
		NBTTagCompound nbt = stack.getTagCompound();
		
		for (int i = 0; i < buttons.length; i++)
		{
			HoverChecker checker = new HoverChecker(buttons[i], 0);

			if (checker.checkHover(mouseX, mouseY))
			{
				List<String> list = new ArrayList<String>();
				list.add(abilities.get(i).getColor() + I18n.format("enhancedarmaments.ability." + abilities.get(i).getName()) + " (" + abilities.get(i).getTypeName() + abilities.get(i).getColor() + ")");
				list.add("");
				list.add(I18n.format("enhancedarmaments.abilities.info." + abilities.get(i).getName()));
				list.add("");
				if (EAUtils.canEnhanceWeapon(stack.getItem()))
				{
					if (i == 0)//FIRE
					{
						float chance = (float) (1.0 / (Config.firechance))*100;
						float currentduration = (Ability.FIRE.getLevel(nbt) + Ability.FIRE.getLevel(nbt)*4)/4;
						float nextlevelduration = (Ability.FIRE.getLevel(nbt)+1 + (Ability.FIRE.getLevel(nbt)+1)*4)/4;
						int c = (int) chance;
						
						if (!(Ability.FIRE.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
							list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
							list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								if(!(Ability.FIRE.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 1)//FROST
					{
						float chance = (float) (1.0 / (Config.frostchance))*100;
						float currentduration = (Ability.FROST.getLevel(nbt) + Ability.FROST.getLevel(nbt)*4)/3;
						float nextlevelduration = (Ability.FROST.getLevel(nbt)+1 + (Ability.FROST.getLevel(nbt)+1)*4)/3;
						int c = (int) chance;
						
						if (!(Ability.FROST.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								if(!(Ability.FROST.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 2)//POISON
					{
						float chance = (float) (1.0 / (Config.poisonchance))*100;
						float currentduration = (Ability.POISON.getLevel(nbt) + Ability.POISON.getLevel(nbt)*4)/2;
						float nextlevelduration = (Ability.POISON.getLevel(nbt)+1 + (Ability.POISON.getLevel(nbt)+1)*4)/2;
						int c = (int) chance;
						
						if (!(Ability.POISON.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								if(!(Ability.POISON.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 3)//INNATE
					{
						float chance = (float) ((1.0 / (Config.innatechance))*100);
						float currentduration = (Ability.INNATE.getLevel(nbt) + Ability.INNATE.getLevel(nbt)*4)/3;
						float nextlevelduration = (Ability.INNATE.getLevel(nbt)+1 + (Ability.INNATE.getLevel(nbt)+1)*4)/3;
						float currentbleedingspeed = (Ability.INNATE.getLevel(nbt));
						float nextlevelbleedingspeed = (Ability.INNATE.getLevel(nbt)+1);
						int c = (int) chance;
						
						if (!(Ability.INNATE.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
								list.add(I18n.format("enhancedarmaments.abilities.info.bleedingspeed")+": 0 "+ TextFormatting.GREEN + "+" + nextlevelbleedingspeed);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
								list.add(I18n.format("enhancedarmaments.abilities.info.bleedingspeed")+": "+ currentbleedingspeed + " " + TextFormatting.GREEN + "+" + (nextlevelbleedingspeed-currentbleedingspeed));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								list.add(I18n.format("enhancedarmaments.abilities.info.bleedingspeed")+": " + currentbleedingspeed);
								if(!(Ability.INNATE.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 4)//BOMBASTIC
					{
						float chance = (float) ((1.0 / (Config.bombasticchance))*100);
						float currentexplosionintensity = (Ability.BOMBASTIC.getLevel(nbt));
						float nextlevelexplosionintensity = (Ability.BOMBASTIC.getLevel(nbt)+1);
						int c = (int) chance;
						
						if (!(Ability.BOMBASTIC.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.explosionintensity")+": 0 "+ TextFormatting.GREEN + "+" + nextlevelexplosionintensity);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.explosionintensity")+": "+ currentexplosionintensity + " " + TextFormatting.GREEN + "+" + (nextlevelexplosionintensity-currentexplosionintensity));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.explosionintensity")+": "+ currentexplosionintensity);
								if(!(Ability.BOMBASTIC.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 5)//CRITICAL_POINT
					{
						float chance = (float) ((1.0 / (Config.criticalpointchance))*100);
						float currentdamage = (Ability.CRITICAL_POINT.getLevel(nbt)*21);
						float nextleveldamage = ((Ability.CRITICAL_POINT.getLevel(nbt)+1)*21);
						int c = (int) chance;
						
						if (!(Ability.CRITICAL_POINT.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.healthpercentage")+": %0"+ TextFormatting.GREEN + " + %" + nextleveldamage);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.healthpercentage")+": %"+ currentdamage + " " + TextFormatting.GREEN + "+ %" + (nextleveldamage-currentdamage));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.healthpercentage")+": %"+ currentdamage);
								if(!(Ability.CRITICAL_POINT.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 6)//ILLUMINATION
					{
						if (!(Ability.ILLUMINATION.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + 6.0);
							}
						}
						else
						{
								list.add(I18n.format("enhancedarmaments.abilities.info.duration") + ": " + 6.0 + " " + I18n.format("enhancedarmaments.abilities.info.seconds"));
						}
						if(!(Ability.ILLUMINATION.canUpgradeLevel(nbt)) && (!(buttons[i].enabled)))
								list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
					}
					if (i == 7)//ETHEREAL
					{
						float currentrepair = (Ability.ETHEREAL.getLevel(nbt)*2+1);
						float nextlevelrepair = ((Ability.ETHEREAL.getLevel(nbt)+1)*2+1);
						
						if (!(Ability.ETHEREAL.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.durability")+": 0" + TextFormatting.GREEN + " +" + 3.0);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.durability")+": "+ currentrepair + " " + TextFormatting.GREEN + "+" + (nextlevelrepair-currentrepair));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.durability")+": "+ currentrepair);
								if(!(Ability.ETHEREAL.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 8)//BLOODTHIRST
					{
						float currentpercentage =(float) (Ability.BLOODTHIRST.getLevel(nbt) * 12);
						float nextlevelpercentage =(float) ((Ability.BLOODTHIRST.getLevel(nbt)+1) * 12);
						
						if (!(Ability.BLOODTHIRST.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
							list.add(I18n.format("enhancedarmaments.abilities.info.damagepercentage")+": %0"+ TextFormatting.GREEN + " + %" + nextlevelpercentage);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.damagepercentage")+": %"+ currentpercentage + " " + TextFormatting.GREEN + "+ %" + (nextlevelpercentage-currentpercentage));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.damagepercentage")+": %"+ currentpercentage);
								if(!(Ability.BLOODTHIRST.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
				}
				else if (EAUtils.canEnhanceArmor(stack.getItem()))
				{
					if (i == 0)//MOLTEN
					{
						float chance = (float) (1.0 / (Config.moltenchance))*100;
						float currentduration = (Ability.MOLTEN.getLevel(nbt) + Ability.MOLTEN.getLevel(nbt)*5)/4;
						float nextlevelduration = (Ability.MOLTEN.getLevel(nbt)+1 + (Ability.MOLTEN.getLevel(nbt)+1)*5)/4;
						int c = (int) chance;
						
						if (!(Ability.MOLTEN.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								if(!(Ability.MOLTEN.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 1)//FROZEN
					{
						float chance = (float) (1.0 / (Config.frozenchance))*100;
						float currentduration = (Ability.FROZEN.getLevel(nbt) + Ability.FROZEN.getLevel(nbt)*5)/6;
						float nextlevelduration = (Ability.FROZEN.getLevel(nbt)+1 + (Ability.FROZEN.getLevel(nbt)+1)*5)/6;
						int c = (int) chance;
						
						if (!(Ability.FROZEN.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								if(!(Ability.FROZEN.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 2)//TOXIC
					{
						float chance = (float) (1.0 / (Config.toxicchance))*100;
						float currentduration = (Ability.TOXIC.getLevel(nbt) + Ability.TOXIC.getLevel(nbt)*4)/4;
						float nextlevelduration = (Ability.TOXIC.getLevel(nbt)+1 + (Ability.TOXIC.getLevel(nbt)+1)*4)/4;
						int c = (int) chance;
						
						if (!(Ability.TOXIC.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								if(!(Ability.TOXIC.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
					if (i == 3)//BEASTIAL
					{
						if (!(Ability.BEASTIAL.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + 7.0);
							}
						}
						else
						{
							list.add(I18n.format("enhancedarmaments.abilities.info.duration") + ": " + 7.0 + " " + I18n.format("enhancedarmaments.abilities.info.seconds"));
						}
						if(!(Ability.BEASTIAL.canUpgradeLevel(nbt)) && (!(buttons[i].enabled)))
							list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
					}
					if (i == 4)//REMEDIAL
					{
						float heal = (float) Ability.REMEDIAL.getLevel(nbt);
						if (!(Ability.REMEDIAL.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								heal=1f;
								list.add(I18n.format("enhancedarmaments.abilities.info.heal_amount") + ": 0 " + TextFormatting.GREEN + "+" + heal);
							}
						}
						else
						{
							if (buttons[i].enabled)
								list.add(I18n.format("enhancedarmaments.abilities.info.heal_amount") +": "+ heal + TextFormatting.GREEN + " +" + 1.0);
							else
								list.add(I18n.format("enhancedarmaments.abilities.info.heal_amount") +": "+ heal);
						}
						if(!(Ability.REMEDIAL.canUpgradeLevel(nbt)) && (!(buttons[i].enabled)))
								list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
					}
					if (i == 5)//HARDENED
					{
						float chance = (float) ((1.0 / (Config.hardenedchance))*100);
						
						if (!(Ability.HARDENED.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + chance);
							}
						}
						else
						{
							list.add(I18n.format("enhancedarmaments.abilities.info.chance")+ ": %" + chance);
						}
						if(!(Ability.HARDENED.canUpgradeLevel(nbt)) && (!(buttons[i].enabled)))
								list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
					}
					if (i == 6)//ADRENALINE
					{
						float chance = (float) (1.0 / (Config.adrenalinechance))*100;
						float currentduration = (Ability.ADRENALINE.getLevel(nbt) + Ability.ADRENALINE.getLevel(nbt)*5)/3;
						float nextlevelduration = (Ability.ADRENALINE.getLevel(nbt)+1 + (Ability.ADRENALINE.getLevel(nbt)+1)*5)/3;
						int c = (int) chance;
						
						if (!(Ability.ADRENALINE.hasAbility(nbt)))
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance")+": %0"+ TextFormatting.GREEN + " + %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": 0 " + I18n.format("enhancedarmaments.abilities.info.seconds")+ TextFormatting.GREEN + " +" + nextlevelduration);
							}
						}
						else
						{
							if (buttons[i].enabled)
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration + " " + I18n.format("enhancedarmaments.abilities.info.seconds") + TextFormatting.GREEN + " +" + (nextlevelduration-currentduration));
							}
							else
							{
								list.add(I18n.format("enhancedarmaments.abilities.info.chance") + ": %" + c);
								list.add(I18n.format("enhancedarmaments.abilities.info.duration")+": " + currentduration +" "+ I18n.format("enhancedarmaments.abilities.info.seconds"));
								if(!(Ability.ADRENALINE.canUpgradeLevel(nbt)))
									list.add(TextFormatting.RED + I18n.format("enhancedarmaments.misc.max")+" " + I18n.format("enhancedarmaments.misc.level"));
							}
						}
					}
				}
							
				drawHoveringText(list, mouseX + 3, mouseY + 3);
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
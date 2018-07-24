package com.jannesoon.enhancedarmaments.event;

import com.jannesoon.enhancedarmaments.EnhancedArmaments;
import com.jannesoon.enhancedarmaments.proxies.ClientProxy;
import com.jannesoon.enhancedarmaments.util.GuiHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Opens the weapon ability selection gui on key press. Default set to 'L'.
 *
 */
public class EventInput 
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event)
	{
		KeyBinding key = ClientProxy.keyBinding;
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		
		if (player != null)
		{
			ItemStack stack = player.inventory.getCurrentItem();
			
			if (stack != null)
			{
				Item current = stack.getItem();
				
				if (current != null)
				{
					if (current instanceof ItemSword || current instanceof ItemAxe || current instanceof ItemHoe || current instanceof ItemArmor || current instanceof ItemBow)
					{
						if (key.isPressed())
						{
							player.openGui(EnhancedArmaments.instance, GuiHandler.ABILITY_SELECTION, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
						}
					}
				}
			}
		}
	}
}
package com.jannesoon.enhancedarmaments.event;

import com.jannesoon.enhancedarmaments.EnhancedArmaments;
import com.jannesoon.enhancedarmaments.client.gui.GuiAbilitySelection;
import com.jannesoon.enhancedarmaments.init.ClientProxy;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.InputEvent;

/**
 * Opens the weapon ability selection gui on key press.
 *
 */
@Mod.EventBusSubscriber
public class EventInput
{
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event)
	{
		KeyBinding key = ((ClientProxy)EnhancedArmaments.proxy).abilityKey;
		Minecraft mc = Minecraft.getInstance();
		EntityPlayer player = mc.player;
		
		if (player != null)
		{
			ItemStack stack = player.inventory.getCurrentItem();
			
			if (stack != ItemStack.EMPTY)
			{
				if (EAUtils.canEnhance(stack.getItem()))
				{
					if (key.isPressed() && stack.getTag() != null)
						if(stack.getTag().hasKey("EA_ENABLED"))
							mc.displayGuiScreen(new GuiAbilitySelection());
				}
			}
		}
	}
}
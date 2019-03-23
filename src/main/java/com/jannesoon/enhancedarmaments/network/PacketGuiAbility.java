package com.jannesoon.enhancedarmaments.network;

import java.util.function.Supplier;

import com.jannesoon.enhancedarmaments.essentials.Ability;
import com.jannesoon.enhancedarmaments.essentials.Experience;
import com.jannesoon.enhancedarmaments.util.EAUtils;
import com.jannesoon.enhancedarmaments.util.NBTHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketGuiAbility
{
	private int index;
	
	public PacketGuiAbility(int index)
	{
		this.index = index;
	}

	public static void encode(PacketGuiAbility msg, PacketBuffer buffer) {
		buffer.writeInt(msg.index);
	}

	public static PacketGuiAbility decode(PacketBuffer buf) {
		return new PacketGuiAbility(
				buf.readInt()
		);
	}

	public static void handle(PacketGuiAbility msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork
		(() ->{
					EntityPlayer player = ctx.get().getSender();

					if (player != null)
					{
						ItemStack stack = player.inventory.getCurrentItem();

						if (stack != ItemStack.EMPTY)
						{
							NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);

							if (EAUtils.canEnhanceWeapon(stack.getItem()))
							{
								if (Ability.WEAPON_ABILITIES.get(msg.index).hasAbility(nbt))
								{
									Ability.WEAPON_ABILITIES.get(msg.index).setLevel(nbt, Ability.WEAPON_ABILITIES.get(msg.index).getLevel(nbt) + 1);
									Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) - Ability.WEAPON_ABILITIES.get(msg.index).getTier());
								}
								else
								{
									Ability.WEAPON_ABILITIES.get(msg.index).addAbility(nbt);
									if(!player.isCreative())
										player.addExperienceLevel(-Ability.WEAPON_ABILITIES.get(msg.index).getExpLevel(nbt) + 1);
								}
							}
							else if (EAUtils.canEnhanceArmor(stack.getItem()))
							{
								if (Ability.ARMOR_ABILITIES.get(msg.index).hasAbility(nbt))
								{
									Ability.ARMOR_ABILITIES.get(msg.index).setLevel(nbt, Ability.ARMOR_ABILITIES.get(msg.index).getLevel(nbt) + 1);
									Experience.setAbilityTokens(nbt, Experience.getAbilityTokens(nbt) - Ability.ARMOR_ABILITIES.get(msg.index).getTier());
								}
								else
								{
									Ability.ARMOR_ABILITIES.get(msg.index).addAbility(nbt);
									if(!player.isCreative())
										player.addExperienceLevel(-Ability.ARMOR_ABILITIES.get(msg.index).getExpLevel(nbt) + 1);
								}
							}
						}
					}
				});
	}
}
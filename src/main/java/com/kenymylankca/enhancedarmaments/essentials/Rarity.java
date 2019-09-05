package com.kenymylankca.enhancedarmaments.essentials;

import java.util.Random;

import com.kenymylankca.enhancedarmaments.config.Config;
import com.kenymylankca.enhancedarmaments.util.RandomCollection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public enum Rarity 
{
	DEFAULT("", 0, 0.0, 0.0),
	BASIC(TextFormatting.WHITE, 0xFFFFFF, Config.basicChance, Config.basicDamage),
	UNCOMMON(TextFormatting.DARK_GREEN, 0x00AA00, Config.uncommonChance, Config.uncommonDamage),
	RARE(TextFormatting.AQUA, 0x55FFFF, Config.rareChance, Config.rareDamage),
	ULTRA_RARE(TextFormatting.DARK_PURPLE, 0xAA00AA, Config.ultraRareChance, Config.ultraRareDamage),
	LEGENDARY(TextFormatting.GOLD, 0xFFAA00, Config.legendaryChance, Config.legendaryDamage),
	ARCHAIC(TextFormatting.LIGHT_PURPLE, 0xFF55FF, Config.archaicChance, Config.archaicDamage);
	
	private String color;
	private int hex;
	private double weight;
	private double effect;
	private static final Rarity[] RARITIES = Rarity.values();
	private static final RandomCollection<Rarity> RANDOM_RARITIES = new RandomCollection<Rarity>();
	
	Rarity(Object color, int hex, double weight, double effect)
	{
		this.color = color.toString();
		this.hex = hex;
		this.weight = weight;
		this.effect = effect;
	}
	
	/**
	 * Returns one of the enums above, according to their weight.
	 * @param random
	 * @return
	 */
	public static Rarity getRandomRarity(Random random)
	{
		return RANDOM_RARITIES.next(random);
	}

	/**
	 * Retrieves the rarity applied.
	 * @param nbt
	 * @return
	 */
	public static Rarity getRarity(NBTTagCompound nbt)
	{
		return nbt != null && nbt.hasKey("RARITY") ? RARITIES[nbt.getInteger("RARITY")] : DEFAULT;
	}
	
	public void setRarity(NBTTagCompound nbt)
	{
		if (nbt != null)
		{
			nbt.setInteger("RARITY", ordinal());
		}
	}
	
	public static void setRarity(NBTTagCompound nbt, String rarityName)
	{
		int rarity = Integer.parseInt(rarityName);
		nbt.setInteger("RARITY", rarity);
	}

	public String getName()
	{
		return this.toString();
	}
	
	public String getColor()
	{
		return color;
	}
	
	public int getHex()
	{
		return hex;
	}
	
	public double getEffect()
	{
		return effect;
	}

	static
	{
		for (Rarity rarity : RARITIES)
		{
			if (rarity.weight > 0.0D)
			{
				RANDOM_RARITIES.add(rarity.weight, rarity);
			}
		}
	}
}
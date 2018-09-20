package com.jannesoon.enhancedarmaments;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jannesoon.enhancedarmaments.commands.WPCommandExpLevel;
import com.jannesoon.enhancedarmaments.commands.WPCommandRarity;
import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.init.ModEvents;
import com.jannesoon.enhancedarmaments.network.PacketGuiAbility;
import com.jannesoon.enhancedarmaments.proxies.CommonProxy;
import com.jannesoon.enhancedarmaments.util.GuiHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * A simple Minecraft mod focused on the aspect of leveling certain areas
 * of the game. On top of that, other interesting leveling systems are
 * planned to enhance the overall feel of Minecraft.
 */
@Mod(modid = EnhancedArmaments.MODID, name = EnhancedArmaments.NAME, version = EnhancedArmaments.VERSION)
public class EnhancedArmaments 
{
	public static final String MODID = "enhancedarmaments";
	public static final String NAME = "Enhanced Armaments";
	public static final String VERSION = "1.2.7";
	public static final String COMMON = "com.jannesoon.enhancedarmaments.proxies.CommonProxy";
	public static final String CLIENT = "com.jannesoon.enhancedarmaments.proxies.ClientProxy";
	
	@SidedProxy(clientSide = EnhancedArmaments.CLIENT, serverSide = EnhancedArmaments.COMMON)
	public static CommonProxy proxy;
	@Instance(EnhancedArmaments.MODID)
	public static EnhancedArmaments instance;
	public static final Logger LOGGER = LogManager.getLogger("EnhancedArmaments");
	public static SimpleNetworkWrapper network;
	private static File configDir;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configDir = new File(event.getModConfigurationDirectory() + "/" + EnhancedArmaments.MODID);
		configDir.mkdirs();
		Config.init(configDir);
		
		ModEvents.registerEvents();
		proxy.preInit();
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("enhancedarmaments");
		network.registerMessage(PacketGuiAbility.Handler.class, PacketGuiAbility.class, 0, Side.SERVER);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}
	
	public static File getConfigDir()
	{
		return configDir;
	}
	
	@EventHandler
	public static void serverInit(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new WPCommandRarity());
		event.registerServerCommand(new WPCommandExpLevel());
	}
}

package com.jannesoon.enhancedarmaments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jannesoon.enhancedarmaments.commands.WPCommandExpLevel;
import com.jannesoon.enhancedarmaments.commands.WPCommandRarity;
import com.jannesoon.enhancedarmaments.config.Config;
import com.jannesoon.enhancedarmaments.event.EventInput;
import com.jannesoon.enhancedarmaments.event.EventItemTooltip;
import com.jannesoon.enhancedarmaments.event.EventLivingDeath;
import com.jannesoon.enhancedarmaments.event.EventLivingHurt;
import com.jannesoon.enhancedarmaments.event.EventLivingUpdate;
import com.jannesoon.enhancedarmaments.init.ClientProxy;
import com.jannesoon.enhancedarmaments.init.ISidedProxy;
import com.jannesoon.enhancedarmaments.network.PacketGuiAbility;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * A simple Minecraft mod focused on the aspect of leveling certain areas
 * of the game. On top of that, other interesting leveling systems are
 * planned to enhance the overall feel of Minecraft.
 */
@Mod(value = EnhancedArmaments.MODID)
public class EnhancedArmaments 
{
	public static final String MODID = "enhancedarmaments";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	private static final String PROTOCOL_VERSION = "1.0";
	public static final String VERSION = "1.3.15";

	public static final ISidedProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, null);

	public static SimpleChannel network = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "networking"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public EnhancedArmaments()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void setup(FMLCommonSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new EventItemTooltip());
		MinecraftForge.EVENT_BUS.register(new EventLivingUpdate());
		MinecraftForge.EVENT_BUS.register(new EventInput());
		MinecraftForge.EVENT_BUS.register(new EventLivingHurt());
		MinecraftForge.EVENT_BUS.register(new EventLivingDeath());

		network.registerMessage(0, PacketGuiAbility.class, PacketGuiAbility::encode, PacketGuiAbility::decode, PacketGuiAbility::handle);
	}

	private void serverInit(FMLServerStartingEvent event)
	{
		WPCommandExpLevel.register(event.getCommandDispatcher());
		WPCommandRarity.register(event.getCommandDispatcher());
	}

	private void clientInit(FMLClientSetupEvent event)
	{
		proxy.init();
	}

	private void config(ModConfig.ModConfigEvent event)
	{
		if (event.getConfig().getSpec() == Config.SPEC)
			Config.load();
	}
}
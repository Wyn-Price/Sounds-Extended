package com.wynprice.Sound.config;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.net.InetAddresses;
import com.wynprice.Sound.MainRegistry;
import com.wynprice.Sound.References;

import akka.actor.Address;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SoundConfig 
{
	private static Boolean isClient = false;
	private static Configuration config = null;
	public static final String CATEGORY_SOUNDS_ENABLED = "Sounds that are enabled", CATEGORY_SERVER_SETTINGS = "Server Settings", CATEGORY_MODDED_BIOMES_SUPPORT = "Config for use with other mods that have biomes";
	
	public static Boolean isFire, isForest, isForestStorm, isBeach, isCricket, isWind, isHell, isEndDragon, isWither, isEnd, isShulkerSoundEnd, runOnServer, useList, foliage, isJungle;
	public static String[] blackServers;
	public static int[] moddedForest, moddedBeach, moddedStorm, moddedCricket, moddedNether, moddedOverworld, moddedEnd, moddedJungle;
	public static ArrayList<String> readServers = new ArrayList<String>();
	
	public static Configuration getConfig()
	{
		return config;
	}
	public static void preInit()
	{
		File configFile = new File(Loader.instance().getConfigDir(), "SoundExtended.cfg");
		config = new Configuration(configFile);
		syncFromFiles();
	}
	public static void clientpreInit()
	{
		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
		isClient = true;
	}
	public static void syncFromFiles()
	{
		syncConfig(true, true);
	}
	public static void syncFromGui()
	{
		syncConfig(false, true);
	}
	public static void syncFromFields()
	{
		syncConfig(false, false);
	}
	
	private static void syncConfig(Boolean load, Boolean read)
	{
		if(load)
			config.load(); 
		List<String> enabledOrder = new ArrayList<String>();
		List<String> serverOrder = new ArrayList<String>();
		List<String> moddedOrder = new ArrayList<String>();
		
		
		Property useFoliage = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "useFoliage", true);
		useFoliage.setLanguageKey("gui.useFoliage");
		useFoliage.setComment(isClient? I18n.format("gui.useFoliage.comment") : "");
		moddedOrder.add(useFoliage.getName());
		
		int[] emptyIntArray = {};
		Property forestBiomes = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "forestBiomes", emptyIntArray);
		forestBiomes.setLanguageKey("gui.moddedForest");
		forestBiomes.setComment(isClient? I18n.format("gui.forestBiomes.comment") : "");
		moddedOrder.add(forestBiomes.getName());
		
		Property cricketBiomes = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "cricketBiomes", emptyIntArray);
		cricketBiomes.setLanguageKey("gui.moddedCricket");
		cricketBiomes.setComment(isClient? I18n.format("gui.moddedCricket.comment") : "");
		moddedOrder.add(cricketBiomes.getName());
		
		Property stormBiomes = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "stormBiomes", emptyIntArray);
		stormBiomes.setLanguageKey("gui.moddedStorm");
		stormBiomes.setComment(isClient? I18n.format("gui.moddedStorm.comment") : "");
		moddedOrder.add(stormBiomes.getName());
		
		Property beachBiomes = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "beachBiomes", emptyIntArray);
		beachBiomes.setLanguageKey("gui.moddedBeach");
		beachBiomes.setComment(isClient? I18n.format("gui.moddedBeach.comment") : "");
		moddedOrder.add(beachBiomes.getName());
		
		Property jungleBiomes = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "moddedJungle", emptyIntArray);
		jungleBiomes.setLanguageKey("gui.moddedJungle");
		jungleBiomes.setComment(isClient? I18n.format("gui.moddedJungle.comment") : "");
		moddedOrder.add(jungleBiomes.getName());
		
		Property netherDimension = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "netherDimension", emptyIntArray);
		netherDimension.setLanguageKey("gui.netherDimension");
		netherDimension.setComment(isClient? I18n.format("gui.netherDimension.comment") : "");
		moddedOrder.add(netherDimension.getName());
		
		Property endDimension = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "endDimension", emptyIntArray);
		endDimension.setLanguageKey("gui.endDimension");
		endDimension.setComment(isClient? I18n.format("gui.endDimension.comment") : "");
		moddedOrder.add(endDimension.getName());
		
		Property overworldDimension = config.get(CATEGORY_MODDED_BIOMES_SUPPORT, "overworldDimension", emptyIntArray);
		overworldDimension.setLanguageKey("gui.overworldDimension");
		overworldDimension.setComment(isClient? I18n.format("gui.overworldDimension.comment") : "");
		moddedOrder.add(overworldDimension.getName());
		
		
		
		Property useServer = config.get(CATEGORY_SERVER_SETTINGS, "useServer", true);
		useServer.setLanguageKey("gui.useServer");
		useServer.setComment(isClient? I18n.format("gui.useServer.comment") : "");
		serverOrder.add(useServer.getName());

		Property useBlackList = config.get(CATEGORY_SERVER_SETTINGS, "useBlackList", true);
		useBlackList.setLanguageKey("gui.useBlackList");
		useBlackList.setComment(isClient? I18n.format("gui.useBlackList.comment") : "");
		serverOrder.add(useBlackList.getName());
		
		String[] defaultServers = {};
		Property blackListedServers = config.get(CATEGORY_SERVER_SETTINGS, "blackListedServers", defaultServers);
		blackListedServers.setLanguageKey("gui.blackListedServers");
		blackListedServers.setComment(isClient? I18n.format("gui.blackListedServers.comment") : "");
		serverOrder.add(blackListedServers.getName());
		
		
		
		Property isForestSound = config.get(CATEGORY_SOUNDS_ENABLED, "isForestSound", true);
		isForestSound.setLanguageKey("gui.isForest");
		isForestSound.setComment(isClient? I18n.format("gui.isForest.comment") : "");
		enabledOrder.add(isForestSound.getName());
		
		Property isBeachSound = config.get(CATEGORY_SOUNDS_ENABLED, "isBeachSound", true);
		isBeachSound.setLanguageKey("gui.isBeach");
		isBeachSound.setComment(isClient? I18n.format("gui.isBeach.comment") : "");
		enabledOrder.add(isBeachSound.getName());
		
		Property isJungleSound = config.get(CATEGORY_SOUNDS_ENABLED, "isJungle", true);
		isJungleSound.setLanguageKey("gui.isJungle");
		isJungleSound.setComment(isClient? I18n.format("gui.isJungle.comment") : "");
		enabledOrder.add(isJungleSound.getName());
		
		Property isFireSound = config.get(CATEGORY_SOUNDS_ENABLED, "isFireSound", true);
		isFireSound.setLanguageKey("gui.isFire");
		isFireSound.setComment(isClient? I18n.format("gui.isFire.comment") : "");
		enabledOrder.add(isFireSound.getName());
		
		Property isCricketSound = config.get(CATEGORY_SOUNDS_ENABLED, "isCricketSound", true);
		isCricketSound.setLanguageKey("gui.isCricket");
		isCricketSound.setComment(isClient? I18n.format("gui.isCricket.comment") : "");
		enabledOrder.add(isCricketSound.getName());
		
		Property isWindSound = config.get(CATEGORY_SOUNDS_ENABLED, "isWindSound", true);
		isWindSound.setLanguageKey("gui.isWind");
		isWindSound.setComment(isClient? I18n.format("gui.isWind.comment") : "");
		enabledOrder.add(isWindSound.getName());
	
		Property isForestStormSound = config.get(CATEGORY_SOUNDS_ENABLED, "isForestStormSound", true);
		isForestStormSound.setLanguageKey("gui.isForestStorm");
		isForestStormSound.setComment(isClient? I18n.format("gui.isForestStorm.comment") : "");
		enabledOrder.add(isForestStormSound.getName());
		
		Property isHellSound = config.get(CATEGORY_SOUNDS_ENABLED, "isHellSound", true);
		isHellSound.setLanguageKey("gui.isHell");
		isHellSound.setComment(isClient? I18n.format("gui.isHell.comment") : "");
		enabledOrder.add(isHellSound.getName());
		
		Property isEndDragonFightSound = config.get(CATEGORY_SOUNDS_ENABLED, "isEndDragon", true);
		isEndDragonFightSound.setLanguageKey("gui.isEndDragon");
		isEndDragonFightSound.setComment(isClient? I18n.format("gui.isEndDragon.comment") : "");
		enabledOrder.add(isEndDragonFightSound.getName());
		
		Property isWitherSound = config.get(CATEGORY_SOUNDS_ENABLED, "isWither", true);
		isWitherSound.setLanguageKey("gui.isWither");
		isWitherSound.setComment(isClient? I18n.format("gui.isWither.comment") : "");
		enabledOrder.add(isWitherSound.getName());

		Property isEndSound = config.get(CATEGORY_SOUNDS_ENABLED, "isEnd", true);
		isEndSound.setLanguageKey("gui.isEnd");
		isEndSound.setComment(isClient? I18n.format("gui.isEnd.comment") : "");
		enabledOrder.add(isEndSound.getName());
		
		Property isShulkerEndSound = config.get(CATEGORY_SOUNDS_ENABLED, "isShulkerEnd", true);
		isShulkerEndSound.setLanguageKey("gui.isShulkerEnd");
		isShulkerEndSound.setComment(isClient? I18n.format("gui.isShulkerEnd.comment") : "");
		enabledOrder.add(isShulkerEndSound.getName());
		
		config.setCategoryPropertyOrder(CATEGORY_SOUNDS_ENABLED, enabledOrder);
		config.setCategoryPropertyOrder(CATEGORY_SERVER_SETTINGS, serverOrder);
		config.setCategoryPropertyOrder(CATEGORY_MODDED_BIOMES_SUPPORT, moddedOrder);
		
		if(read)
		{
			isForest = isForestSound.getBoolean();
			isBeach = isBeachSound.getBoolean();
			isJungle = isJungleSound.getBoolean();
			isFire = isFireSound.getBoolean();
			isCricket = isCricketSound.getBoolean();
			isWind = isWindSound.getBoolean();
			isForestStorm = isForestStormSound.getBoolean();
			isHell = isHellSound.getBoolean();
			isEndDragon = isEndDragonFightSound.getBoolean();
			isWither = isWitherSound.getBoolean();
			isEnd = isEndSound.getBoolean();
			isShulkerSoundEnd = isShulkerEndSound.getBoolean();
			
			runOnServer = useServer.getBoolean();
			useList = useBlackList.getBoolean();
			blackServers = blackListedServers.getStringList();
			
			moddedBeach = beachBiomes.getIntList();
			moddedCricket = cricketBiomes.getIntList();
			moddedJungle = jungleBiomes.getIntList();
			moddedStorm = stormBiomes.getIntList();
			moddedForest = forestBiomes.getIntList();
			moddedNether = netherDimension.getIntList();
			moddedEnd = endDimension.getIntList();
			moddedOverworld = overworldDimension.getIntList();
			foliage = useFoliage.getBoolean();
		}
		
		isForestSound.set(isForest);
		isBeachSound.set(isBeach);
		isJungleSound.set(isJungle);
		isFireSound.set(isFire);
		isCricketSound.set(isCricket);
		isWindSound.set(isWind);
		isForestStormSound.set(isForestStorm);
		isHellSound.set(isHell);
		isEndDragonFightSound.set(isEndDragon);
		isWindSound.set(isWither);
		isEndSound.set(isEnd);
		isShulkerEndSound.set(isShulkerSoundEnd);
		
		useServer.set(runOnServer);
		useBlackList.set(useList);
		blackListedServers.set(blackServers);
		
		beachBiomes.set(moddedBeach);
		cricketBiomes.set(moddedCricket);
		jungleBiomes.set(moddedJungle);
		stormBiomes.set(moddedStorm);
		forestBiomes.set(moddedForest);
		netherDimension.set(moddedNether);
		endDimension.set(moddedEnd);
		overworldDimension.set(moddedOverworld);
		useFoliage.set(foliage);
		
		
		readServers.clear();
		for(String IP : blackServers)
		{
			Boolean isError = false;
			InetAddress address = null;
			try {
				address = InetAddress.getByName(new URL("http://" + IP).getHost());
			} catch (UnknownHostException e) {
				MainRegistry.getlogger().info("Error in config: Ip '" + IP + "' does not exist");
				isError = true;
			} catch (MalformedURLException e) {
				MainRegistry.getlogger().info("Error in config: Ip '" + IP + "' is not valid");
				isError = true;
			}
			if(!isError)
				readServers.add(address.getHostAddress());
		}
		if(readServers.size() != 0)
			MainRegistry.getlogger().info("Block IP's:" + readServers);
		if(config.hasChanged())
			config.save();
	}
	
	public static class ConfigEventHandler
	{
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if(event.getModID() == References.MODID)
				syncFromGui();
		}
	}
}

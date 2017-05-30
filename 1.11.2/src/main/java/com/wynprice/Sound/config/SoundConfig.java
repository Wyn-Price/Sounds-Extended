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
	public static final String CATEGORY_SOUNDS_ENABLED = "Sounds that are enabled", CATEGORY_SERVER_SETTINGS = "Server Settings";
	
	public static Boolean isFire, isForest, isForestStorm, isBeach, isCricket, isWind, runOnServer, useList;
	public static String[] blackServers;
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
		
		config.setCategoryPropertyOrder(CATEGORY_SOUNDS_ENABLED, enabledOrder);
		config.setCategoryPropertyOrder(CATEGORY_SERVER_SETTINGS, serverOrder);
		
		if(read)
		{
			isForest = isForestSound.getBoolean();
			isBeach = isBeachSound.getBoolean();
			isFire = isFireSound.getBoolean();
			isCricket = isCricketSound.getBoolean();
			isWind = isWindSound.getBoolean();
			isForestStorm = isForestStormSound.getBoolean();
			
			runOnServer = useServer.getBoolean();
			useList = useBlackList.getBoolean();
			blackServers = blackListedServers.getStringList();
		}
		
		isForestSound.set(isForest);
		isBeachSound.set(isBeach);
		isFireSound.set(isFire);
		isCricketSound.set(isCricket);
		isWindSound.set(isWind);
		isForestStormSound.set(isForestStorm);
		
		useServer.set(runOnServer);
		useBlackList.set(useList);
		blackListedServers.set(blackServers);
		
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

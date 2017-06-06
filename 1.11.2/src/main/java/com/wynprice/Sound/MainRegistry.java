package com.wynprice.Sound;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wynprice.Sound.config.SoundConfig;
import com.wynprice.Sound.proxys.CommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = References.MODID , name = References.NAME , version =References.VERSION, guiFactory = References.GUI_FACTORY)
public class MainRegistry
{
	
	@SidedProxy(clientSide = References.CLIENT_PROXY, serverSide = References.SERVER_PROXY)
	public static CommonProxy proxy;
		
	@Mod.Instance(References.MODID)
	public static MainRegistry instance;
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent e)
	{
		getlogger().info("Playing that noteblock nicely");
		SoundConfig.preInit();
		proxy.PreInit(e);
		
	}
	
	@EventHandler
	public static void Init(FMLInitializationEvent e)
	{
		proxy.Init(e);
	}
	
	@EventHandler
	public static void PostInit(FMLPostInitializationEvent e)
	{
		proxy.PostInit(e);
	}
	
	
	private static Logger logger; 
	public static Logger getlogger()
	{
		if(logger == null)
		{
			logger = LogManager.getFormatterLogger(References.MODID);
		}
		return logger;
	}
}
 

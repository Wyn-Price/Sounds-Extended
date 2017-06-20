package com.wynprice.Sound.proxys;

import com.wynprice.Sound.config.SoundConfig;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ClientProxy extends CommonProxy
{
	public void PreInit(FMLPreInitializationEvent e)
	{
		super.PreInit(e);
		SoundConfig.clientpreInit();
	}
	
	public void Init(FMLInitializationEvent e)
	{
		super.Init(e);
	}
	
	
	public void PostInit(FMLPostInitializationEvent e)
	{
		super.PostInit(e);
	}
	
}

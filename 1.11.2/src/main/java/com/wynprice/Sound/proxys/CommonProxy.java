package com.wynprice.Sound.proxys;

import com.wynprice.Sound.SoundEventPlay;
import com.wynprice.Sound.SoundHandler;

import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{

	public void PreInit(FMLPreInitializationEvent e)
	{	
		SoundHandler.init();
	}

	public void Init(FMLInitializationEvent e)
	{
	SoundEventPlay handler = new SoundEventPlay();
	System.out.println(handler);
	MinecraftForge.EVENT_BUS.register(handler);
	FMLCommonHandler.instance().bus().register(handler);
	
	}

	public void PostInit(FMLPostInitializationEvent e)
	{

		
	}

}

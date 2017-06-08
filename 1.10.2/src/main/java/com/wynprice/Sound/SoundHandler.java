package com.wynprice.Sound;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SoundHandler 
{
	public static ArrayList<SoundEvent> soundForest, highWind, fireCrack, beachWave, soundForestStorm, endDrip;
	public static SoundEvent cricketNight, hell, bossMusic, witherTimer, endAmbience;
	private static int soundEventId;
	public static final RegistryNamespaced<ResourceLocation, SoundEvent> REGISTRY = net.minecraftforge.fml.common.registry.GameData.getSoundEventRegistry();
	
	
	public static void init()
	{
		fireCrack = new ArrayList<SoundEvent>(Arrays.asList(register("fire.crack.1"), register("fire.crack.2"), register("fire.crack.3")));
		highWind = new ArrayList<SoundEvent>(Arrays.asList(register("wind.high.1"), register("wind.high.2"), register("wind.high.3"), register("wind.high.4")));
		soundForest = new ArrayList<SoundEvent>(Arrays.asList(register("forest.1"), register("forest.2"), register("forest.3"), register("forest.4"), register("forest.5"),
				register("forest.6"), register("forest.7"), register("forest.8"), register("forest.9"), register("forest.10")));
		soundForestStorm = new ArrayList<SoundEvent>(Arrays.asList(register("forest.storm.1")));
		cricketNight = register("night.cricket");
		beachWave = new ArrayList<SoundEvent>(Arrays.asList(register("beach.wave.1"), register("beach.wave.2"), register("beach.wave.3"), register("beach.wave.4"), register("beach.wave.5")));
		hell = register("hell");
		bossMusic = register("boss.fight");
		endDrip = new ArrayList<SoundEvent>(Arrays.asList(register("end.drip.1"), register("end.drip.2"), register("end.drip.3"), register("end.drip.4"), register("end.drip.5"),
				register("end.drip.6"), register("end.drip.7"), register("end.drip.8"), register("end.drip.9"), register("end.drip.10")));
		endAmbience = register("end.reverse");
		witherTimer = register("wither.spawn.timer");
	}
	
	public static SoundEvent register(String name)
	{
		ResourceLocation loc = new ResourceLocation(References.MODID, name);
		return GameRegistry.register(new SoundEvent(loc).setRegistryName(loc));
	}
}

package com.wynprice.Sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.wynprice.Sound.config.SoundConfig;
import com.wynprice.Sound.vanillaOverride.PositionedSoundRecord;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class SoundEventPlay
{
	private ArrayList<Block> foliage = new ArrayList<Block>(Arrays.asList(Blocks.LEAVES, Blocks.LEAVES2, Blocks.GRASS, Blocks.DIRT, Blocks.GRASS, Blocks.TALLGRASS, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER));
	public ArrayList<BlockPos> firePositions = new ArrayList<BlockPos>();
	private ArrayList<BlockPos> foliagePositions = new ArrayList<BlockPos>();
	private ArrayList<Integer> beachIDs = new ArrayList<Integer>(), forestIDs = new ArrayList<Integer>(), stormIDs = new ArrayList<Integer>(), cricketIDs = new ArrayList<Integer>();
	private EntityPlayer player;
	private ISound bossMusic;
	private Entity dragon, wither;
	private World world;
	private float timer, backTimer, hellTimer;
	private static Boolean single = false, loadin = true, loadHell = true, previousFrameDragon = false, previousFrameWither = false,playMusic = false;
	@SubscribeEvent
	public void playerUpdate(LivingUpdateEvent e)
	{

		if(loadin)
		{
			loadin = false;
			try
			{
				String s = Minecraft.getMinecraft().getCurrentServerData().serverIP;
				MainRegistry.getlogger().info("Loading system for MultiPlayer");
				
			}
			catch (Exception exeption) 
			{
				
				MainRegistry.getlogger().info("Loading system for SinglePlayer");
				single = true;
			}
		}
		
		if(!SoundConfig.isBeach && !SoundConfig.isCricket && !SoundConfig.isFire && !SoundConfig.isForest && !SoundConfig.isForestStorm && !SoundConfig.isWind && !SoundConfig.isHell)
			return;
		
		else if(!single)
		{
			if((SoundConfig.runOnServer && SoundConfig.useList && SoundConfig.readServers.contains(Minecraft.getMinecraft().getCurrentServerData().serverIP)) || !SoundConfig.runOnServer)
				return;
		}
			
		
		
		
		if(playMusic && SoundConfig.isEndDragon)
		{
			playMusic = false;
			Minecraft.getMinecraft().getSoundHandler().playSound(bossMusic);
			
		}
		this.world = e.getEntity().getEntityWorld();
		if(e.getEntity() instanceof EntityPlayer)
		{
			this.player = (EntityPlayer) e.getEntityLiving();
			if(SoundConfig.isEndDragon || SoundConfig.isWither)
			{
				for(Entity entity : world.getLoadedEntityList())
				{
					if(entity instanceof EntityDragon)
					{
						dragon = entity;
					}
					else if(entity instanceof EntityWither)
					{
						wither = entity;
					}
				}
				if(SoundConfig.isEndDragon)
				{
					if(dragon != null)
					{
						if(!dragon.isDead && !previousFrameDragon)
							playMusic = true;
						if(dragon.isDead && previousFrameDragon)
							Minecraft.getMinecraft().getSoundHandler().stopSound(bossMusic);
						previousFrameDragon = !dragon.isDead;
					}
					if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(bossMusic) && player.dimension == 1 && previousFrameDragon)
						playMusic = true;
					
				}
				
				if(SoundConfig.isWither)
				{
					if(wither != null)
					{
						if(!wither.isDead && !previousFrameWither)
							playMusic = true;
						if(wither.isDead && previousFrameWither)
							Minecraft.getMinecraft().getSoundHandler().stopSound(bossMusic);
						previousFrameWither = !wither.isDead;
					}
					
					if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(bossMusic) && previousFrameWither)
						playMusic = true;
				}
				
			}
			if((hellTimer >= (18.5f * 15)) && player.dimension == -1 && SoundConfig.isHell)
			{
				hellTimer = 0f;
				world.playSound(player, player.getPosition(), SoundHandler.hell, SoundCategory.WEATHER, 100f, 1f);
			}
			else 
				hellTimer ++;
			
			if(timer >= 20f)
			{
				backTimer++;
				timer = 0f;
				int x = this.player.getPosition().getX() + randInt(-15, 15);
				int y = 0;
				int z = this.player.getPosition().getZ() + randInt(-15, 15);
				boolean isBlockAir = true;
				for(int i2 = 256; isBlockAir; i2--)
				{
					if(i2 == 0)
						isBlockAir = false;
					if(world.getBlockState(new BlockPos(x, i2, z)).getBlock() != Blocks.AIR)
					{
						if(world.getBlockState(new BlockPos(x, i2, z)).getBlock() == Blocks.FIRE && !firePositions.contains(new BlockPos(x, i2, z)))
							firePositions.add(new BlockPos(x, i2, z));
						else if(foliage.contains(world.getBlockState(new BlockPos(x, i2, z)).getBlock()) && !foliagePositions.contains(new BlockPos(x, i2, z)))
							foliagePositions.add(new BlockPos(x, i2, z));
						isBlockAir = false;
					}
					y = i2;
				}
				BiomeUpdate(new BlockPos(x, y, z));
				
			}
			else
				timer++;
			if(firePositions.size() > 0 && backTimer >= 5 && SoundConfig.isFire)
			{
				if(world.isRemote)
					backTimer = 0;
				for (int i = 0; i < firePositions.size(); i++)
				{
					try
					{
						if(player.getDistance(firePositions.get(i).getX(), firePositions.get(i).getY(), firePositions.get(i).getZ()) <= 10)
						{		
							if(world.getBlockState(firePositions.get(i)).getBlock() != Blocks.FIRE)
								firePositions.remove(firePositions.get(i));
							else 
								world.playSound(player, firePositions.get(i), SoundHandler.fireCrack.get(randInt(0, SoundHandler.fireCrack.size() - 1)), SoundCategory.BLOCKS, 2, 1);
						}	
					}
					catch (IndexOutOfBoundsException index) 
					{
						
					}
				
				}
			}
			
		}
		
	}
	
	private void BiomeUpdate(BlockPos position)
	{
		Biome biome = world.getBiome(position);
		Boolean isFoilage = false;
		for(BlockPos pos : foliagePositions)
		{
			if(player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 20)
				isFoilage = true;
		}
		if(!SoundConfig.foliage)
			isFoilage = true;
		if(biome.equals(biome.getBiome(7)))
			return;
		if(player.dimension == -1)
		{
			if(loadHell)
			{
				hellTimer = Integer.MAX_VALUE;
			}
			loadHell = false;
			return;
		}
		loadHell = true;
		
		
		
		
		if(player.dimension == 1)
		{
			//if(world.getBlockState(position).getBlock() == Blocks.END_STONE)
				//TODO play souynd
		}
		
		if(player.dimension != 0)
			return;
		if(beachIDs.contains(biome.getIdForBiome(biome)) && SoundConfig.isBeach)
		{
			world.playSound(player, position, SoundHandler.beachWave.get(randInt(0, SoundHandler.beachWave.size() - 1)), SoundCategory.WEATHER, 2, 1);
		}
		else if(isFoilage && (SoundConfig.isCricket || SoundConfig.isForest || SoundConfig.isForestStorm))
		{
			if(world.isThundering() && SoundConfig.isForestStorm)
			{
				Boolean canSeeSky = true;
				for(int i = 0; i < 9; i ++)
				{
					if(world.canSeeSky(new BlockPos(player.posX + Arrays.asList(-1f, 0f, 1f).get(i%3), player.posY, player.posZ + Arrays.asList(-1f,-1f,-1f,0f,0f,0f,1f,1f,1f).get(i))))
						canSeeSky = false; 
				}
				if(stormIDs.contains(biome.getIdForBiome(biome)) && !canSeeSky)
				{
					world.playSound(player, position, SoundHandler.soundForestStorm.get(0), SoundCategory.WEATHER, 1, 1);
				}
			}
			else if(SoundConfig.isForest || SoundConfig.isCricket)
			{
				if(world.getWorldTime() <= 23000 && world.getWorldTime() >= 13000)
				{
					if(world.getWorldTime() >= 22000 || world.getWorldTime() <= 14000)
					{
						
						if(cricketIDs.contains(biome.getIdForBiome(biome)))
						{
							
							float vol = world.getWorldTime() >= 22000? (world.getWorldTime() - 22000) / 500f : (14000f - world.getWorldTime()) / 500f;
							if(forestIDs.contains(biome.getIdForBiome(biome)) && !world.isThundering() && SoundConfig.isForest)
								world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.WEATHER, vol, 1);
							if(SoundConfig.isCricket)
								world.playSound(player, position, SoundHandler.cricketNight, SoundCategory.WEATHER, 2 - vol, 1);
								
						}
					}
					else
					{
						if(cricketIDs.contains(biome.getIdForBiome(biome)) && SoundConfig.isCricket)
						{
							world.playSound(player, position, SoundHandler.cricketNight, SoundCategory.WEATHER, 2, 1);
						}
					}
					
				}
				else
				{
					if(forestIDs.contains(biome.getIdForBiome(biome)) && SoundConfig.isForest)
					{
						world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.WEATHER, 2.5f, 1);
					}
				}
				
			}
			
		}
		if(player.posY > 85 && SoundConfig.isWind)
		{
			
			float vol = 1 - ((130 - (float) player.posY) / 45);
			vol = vol > 1? 1f : vol;
			world.playSound(player, player.getPosition(), SoundHandler.highWind.get(randInt(0, SoundHandler.highWind.size() - 1)), SoundCategory.WEATHER, vol, 1);
		}
		
		 
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	@SubscribeEvent
	public void blockUpdat(BlockEvent e)
	{
		
		if(e.getState().getBlock() == Blocks.FIRE)
		{
			
			if(!firePositions.contains(e.getPos()))
			{
				firePositions.add(e.getPos());
			}
		}
	}
	
	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void playerQuit(ClientDisconnectionFromServerEvent e)
	{
		Minecraft.getMinecraft().getSoundHandler().stopSound(bossMusic);
		this.loadin = true;
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e)
	{
		this.bossMusic = PositionedSoundRecord.getMasterRecord(SoundHandler.bossMusic, 1f);
		beachIDs.clear(); cricketIDs.clear(); stormIDs.clear(); forestIDs.clear();
		for(Integer i : SoundConfig.moddedBeach){beachIDs.add(i);}
		for(Integer i : Arrays.asList(16,25,26)){beachIDs.add(i);}
		for(Integer i : SoundConfig.moddedCricket){cricketIDs.add(i);}
		for(Integer i : Arrays.asList(1,4,5,6,18,19,21,22,23,27,28,29,30,31,32,33,35)){cricketIDs.add(i);}
		for(Integer i : SoundConfig.moddedStorm){stormIDs.add(i);}
		for(Integer i : Arrays.asList(1,4,5,18,19,21,22,23,27,28,29,30,31,32,33)){stormIDs.add(i);}
		for(Integer i : SoundConfig.moddedForest){forestIDs.add(i);}
		for(Integer i : Arrays.asList(4,5,18,19,21,22,23,27,28,29,30,31,32,33)){forestIDs.add(i);}
	}
	
	@SubscribeEvent
	public void quit(PlayerLoggedOutEvent e)
	{
		Minecraft.getMinecraft().getSoundHandler().stopSound(bossMusic);
	}
	
}

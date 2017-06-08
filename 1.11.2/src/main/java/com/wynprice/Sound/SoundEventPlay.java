package com.wynprice.Sound;

import static net.minecraftforge.common.ForgeVersion.Status.AHEAD;
import static net.minecraftforge.common.ForgeVersion.Status.BETA;
import static net.minecraftforge.common.ForgeVersion.Status.BETA_OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.PENDING;
import static net.minecraftforge.common.ForgeVersion.Status.UP_TO_DATE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.wynprice.Sound.config.SoundConfig;
import com.wynprice.Sound.vanillaOverride.PositionedSoundRecord;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class SoundEventPlay
{
	private ArrayList<Block> foliage = new ArrayList<Block>(Arrays.asList(Blocks.LEAVES, Blocks.LEAVES2, Blocks.GRASS, Blocks.DIRT, Blocks.GRASS, Blocks.TALLGRASS, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER));
	public ArrayList<BlockPos> firePositions = new ArrayList<BlockPos>();
	private ArrayList<BlockPos> foliagePositions = new ArrayList<BlockPos>();
	private ArrayList<Integer> beachIDs = new ArrayList<Integer>(), forestIDs = new ArrayList<Integer>(), stormIDs = new ArrayList<Integer>(), cricketIDs = new ArrayList<Integer>(),
			overworld = new ArrayList<Integer>(), nether = new ArrayList<Integer>(), end = new ArrayList<Integer>();
	private EntityPlayer player;
	private ISound bossMusic, hell;
	private Entity dragon, wither;
	private BlockPos nearestEndCityLocation, nearestStrongholdLocation;
	private World world;
	private float timer, backTimer, endTimer, strongholdTimer = 10000f, witherInvulvTimer = 1;
	private static Boolean single = false, loadin = true, previousFrameDragon = false, previousFrameWither = false,playMusic = false, doUpdate = true,
			endCityPlay = false, strongholdPlay = false;
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
		
		if(!SoundConfig.isBeach && !SoundConfig.isCricket && !SoundConfig.isFire && !SoundConfig.isForest && !SoundConfig.isForestStorm && !SoundConfig.isWind 
				&& !SoundConfig.isHell && !SoundConfig.isEndDragon && !SoundConfig.isWither && !SoundConfig.isEnd && !SoundConfig.isShulkerSoundEnd && !SoundConfig.isEndCity)
			return;
		
		else if(!single)
		{
			if((SoundConfig.runOnServer && SoundConfig.useList && SoundConfig.readServers.contains(Minecraft.getMinecraft().getCurrentServerData().serverIP)) || !SoundConfig.runOnServer)
				return;
		}
			
		
		
		
		if(playMusic && !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(bossMusic))
		{
			Minecraft.getMinecraft().getSoundHandler().stop(References.MODID + ":wither.spawn.timer", SoundCategory.MASTER);
			playMusic = false;
			if(previousFrameDragon || previousFrameWither)
				try{ Minecraft.getMinecraft().getSoundHandler().playSound(bossMusic); }
				catch (IllegalArgumentException i) {}
			
			
		}
		this.world = e.getEntity().getEntityWorld();
		if(e.getEntity() instanceof EntityPlayer)
		{
			this.player = (EntityPlayer) e.getEntityLiving();
			if(SoundConfig.isEndDragon || SoundConfig.isWither)
			{
				Iterator<Entity> iWorldLoadedEntityList = world.loadedEntityList.iterator();
				while(iWorldLoadedEntityList.hasNext())
				{
					Entity entity = iWorldLoadedEntityList.next();
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
						EntityDragon d = (EntityDragon) dragon;
						if(!dragon.isDead && !previousFrameDragon)
							playMusic = true;
						if((dragon.isDead || d.getHealth() == 0) && previousFrameDragon)
						{
							Minecraft.getMinecraft().getSoundHandler().stopSounds();
							world.playSound(player, dragon.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 5f, 1f);
						}
							
						previousFrameDragon = !(dragon.isDead || d.getHealth() == 0);
					}
					if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(bossMusic) && previousFrameDragon)
						playMusic = true;
					
				}
				
				if(SoundConfig.isWither)
				{
					if(wither != null)
					{
						EntityWither w = (EntityWither) wither;
						witherInvulvTimer = w.getInvulTime();
						if(witherInvulvTimer > 0)
						{
							world.playSound(player, w.getPosition(), SoundHandler.witherTimer, SoundCategory.MASTER, 1f, 2 - (4 * (witherInvulvTimer / 220)));
						}
						if((wither.isDead || w.getHealth() == 0) && previousFrameWither)
						{
							Minecraft.getMinecraft().getSoundHandler().stopSounds();;
						}
						previousFrameWither = !(wither.isDead || w.getHealth() == 0);
					}
					else witherInvulvTimer = 1f;
					if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(bossMusic) && previousFrameWither && witherInvulvTimer == 0)
						playMusic = true;
						
				}
				
			}
			if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(hell) && nether.contains(player.dimension)&& SoundConfig.isHell)
			{
				try{Minecraft.getMinecraft().getSoundHandler().playSound(hell);} catch (Exception ex) {}
			}
			if(endTimer >= (18.5 * 7) && endCityPlay && SoundConfig.isEndCity)
			{
				endTimer = 0f;
				world.playSound(player, nearestEndCityLocation, SoundHandler.endAmbience.get(0), SoundCategory.MASTER, 5f, 1f);
				world.playSound(player, new BlockPos(nearestEndCityLocation.getX(), nearestEndCityLocation.getY() + 25f, nearestEndCityLocation.getX()), SoundHandler.endAmbience.get(0), SoundCategory.MASTER, 5f, 1f);
			}
			else endTimer ++;
			if(strongholdTimer >= (39 * 60 * 2.5) && strongholdPlay && SoundConfig.isStronghold)
			{
				for(int i = 0; i < 9; i++)
				{
					float x = Arrays.asList(-25, 0, 25).get(i%3) + nearestStrongholdLocation.getX();
					float z = Arrays.asList(-25, 0, 25).get(Math.floorDiv(i, 3)) + nearestStrongholdLocation.getZ();
					world.playSound(player, new BlockPos(x, nearestStrongholdLocation.getY(), z), SoundHandler.stronghold, SoundCategory.MASTER, 5f, 1f);
				}
				strongholdTimer = 0f;
			}
			else strongholdTimer ++;
			
			if(timer >= 20f)
			{
				backTimer++;
				timer = 0f;
				if(!end.contains(player.dimension)|| (end.contains(player.dimension) && (!player.isElytraFlying() && player.posY > 49)))
				{
					int x = this.player.getPosition().getX() + randInt(-15, 15);
					int y = 0;
					int z = this.player.getPosition().getZ() + randInt(-15, 15);
					boolean isBlockAir = true;
					BlockPos highestBlock = world.getTopSolidOrLiquidBlock(new BlockPos(x, y, z));
					if(world.getBlockState(highestBlock).getBlock() == Blocks.FIRE && !firePositions.contains(highestBlock))
						firePositions.add(highestBlock);
					else if(foliage.contains(world.getBlockState(highestBlock).getBlock()) && !foliagePositions.contains(highestBlock))
						foliagePositions.add(highestBlock);
					BiomeUpdate(highestBlock);
				}
				else
					for(int t = 0; t < 3; t ++) BiomeUpdate(new BlockPos(this.player.getPosition().getX() + randInt(-15, 15), this.player.getPosition().getY() + randInt(-10, 20), this.player.getPosition().getZ() + randInt(-15, 15)));
				
				
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
		position = new BlockPos(position.getX(), position.getY() - 1f, position.getZ());
		Biome biome = world.getBiome(position);
		if(biome.equals(biome.equals(7)))
			return;
		
		
		
		
		if(end.contains(player.dimension) && (SoundConfig.isEnd || SoundConfig.isShulkerSoundEnd))
		{
			if(world.getBlockState(position).getBlock() == Blocks.END_STONE && randInt(0, 2) == 0 && SoundConfig.isEnd)
				world.playSound(player, position, SoundHandler.endDrip.get(randInt(0, SoundHandler.endDrip.size() - 1)), SoundCategory.MASTER, 3f, 2 - (randInt(0, 400) / 100));
			Iterator<Entity> iWorldLoadedEntityList = world.loadedEntityList.iterator();
			while(iWorldLoadedEntityList.hasNext() && SoundConfig.isShulkerSoundEnd)
			{
				Entity e = iWorldLoadedEntityList.next();
				if(e instanceof EntityShulker)
				{
					if(Math.sqrt(e.getDistanceSq(player.getPosition())) < 25 && randInt(0, 5) == 1) world.playSound(player, e.getPosition(), SoundHandler.endAmbience.get(1), SoundCategory.MASTER, 10f, 2 - (randInt(0, 400) / 100));
				}
			}
			if(SoundConfig.isEndCity)
			{
				BlockPos endCityLocation = world.findNearestStructure("EndCity", player.getPosition(), false);
				if(endCityLocation != null)
					if(Math.sqrt(player.getDistanceSq(endCityLocation)) < 250)
					{
						nearestEndCityLocation = endCityLocation;
						endCityPlay = true;
					}
					else endCityPlay = false;
				else if (Math.sqrt(player.getDistanceSq(nearestEndCityLocation)) >= 250) endCityPlay = false;
			}	
		}
		
		if(!overworld.contains(player.dimension))
			return;
		if(SoundConfig.isStronghold)
		{
			BlockPos strongHoldLocation = world.findNearestStructure("Stronghold", player.getPosition(), false);
			if(strongHoldLocation != null)
				if(Math.sqrt(player.getDistanceSq(strongHoldLocation)) < 100)
				{
					if(!strongholdPlay && strongholdTimer >= (40 * 60 * 2.5))
						strongholdTimer = Integer.MAX_VALUE;
					nearestStrongholdLocation = strongHoldLocation;
					strongholdPlay = true;
				}
				else strongholdPlay = false;
			else if(Math.sqrt(player.getDistanceSq(nearestStrongholdLocation)) >= 350) strongholdPlay = false;
		}
		Boolean isFoilage = false;
		Iterator<BlockPos> iFoliagePositions = foliagePositions.iterator();
		while(iFoliagePositions.hasNext())
		{
			BlockPos pos = iFoliagePositions.next();
			if(player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 20)
				isFoilage = true;
		}
		if(!SoundConfig.foliage)
			isFoilage = true;
		if(beachIDs.contains(biome.getIdForBiome(biome)) && SoundConfig.isBeach)
		{
			world.playSound(player, position, SoundHandler.beachWave.get(randInt(0, SoundHandler.beachWave.size() - 1)), SoundCategory.MASTER, 2, 1);
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
					world.playSound(player, position, SoundHandler.soundForestStorm.get(0), SoundCategory.MASTER, 1, 1);
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
								world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, vol, 1);
							if(SoundConfig.isCricket)
								world.playSound(player, position, SoundHandler.cricketNight, SoundCategory.MASTER, 2 - vol, 1);
								
						}
					}
					else
					{
						if(cricketIDs.contains(biome.getIdForBiome(biome)) && SoundConfig.isCricket)
						{
							world.playSound(player, position, SoundHandler.cricketNight, SoundCategory.MASTER, 2, 1);
						}
					}
					
				}
				else
				{
					if(forestIDs.contains(biome.getIdForBiome(biome)) && SoundConfig.isForest)
					{
						world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, 2.5f, 1);
					}
				}
				
			}
			
		}
		if(player.posY > 85 && SoundConfig.isWind)
		{
			
			float vol = 1 - ((130 - (float) player.posY) / 45);
			vol = vol > 1? 1f : vol;
			world.playSound(player, player.getPosition(), SoundHandler.highWind.get(randInt(0, SoundHandler.highWind.size() - 1)), SoundCategory.MASTER, vol, 1);
		}
		
		 
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	@SubscribeEvent
	public void blockUpdate(BlockEvent e)
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
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
		this.loadin = true;
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e) throws IOException
	{
		this.hell = PositionedSoundRecord.getMasterRecord(SoundHandler.hell, 1f, 1f);
		beachIDs.clear(); cricketIDs.clear(); stormIDs.clear(); forestIDs.clear();
		for(Integer i : SoundConfig.moddedBeach){beachIDs.add(i);}
		for(Integer i : Arrays.asList(16,25,26)){beachIDs.add(i);}
		for(Integer i : SoundConfig.moddedCricket){cricketIDs.add(i);}
		for(Integer i : Arrays.asList(1,4,5,6,18,19,21,22,23,27,28,29,30,31,32,33,35)){cricketIDs.add(i);}
		for(Integer i : SoundConfig.moddedStorm){stormIDs.add(i);}
		for(Integer i : Arrays.asList(1,4,5,18,19,21,22,23,27,28,29,30,31,32,33)){stormIDs.add(i);}
		for(Integer i : SoundConfig.moddedForest){forestIDs.add(i);}
		for(Integer i : Arrays.asList(4,5,18,19,21,22,23,27,28,29,30,31,32,33)){forestIDs.add(i);}
		
		for(Integer i : SoundConfig.moddedNether){nether.add(i);}nether.add(-1);
		for(Integer i : SoundConfig.moddedEnd){end.add(i);}end.add(1);
		for(Integer i : SoundConfig.moddedOverworld){overworld.add(i);}overworld.add(0);
		
		if(doUpdate)
		{
			doUpdate = false;
			
			Status status = PENDING;
	        ComparableVersion target = null;
			URL url = new URL(References.UPDATE_URL);
			InputStream con = url.openStream();
	        String data = new String(ByteStreams.toByteArray(con), "UTF-8");
	        con.close();
	        @SuppressWarnings("unchecked")
	        Map<String, Object> json = new Gson().fromJson(data, Map.class);
	        @SuppressWarnings("unchecked")
	        Map<String, String> promos = (Map<String, String>)json.get("promos");
	        String display_url = (String)json.get("homepage");

	        String rec = promos.get(MinecraftForge.MC_VERSION + "-recommended");
	        String lat = promos.get(MinecraftForge.MC_VERSION + "-latest");
	        ComparableVersion current = new ComparableVersion(References.VERSION);

	        if (rec != null)
	        {
	            ComparableVersion recommended = new ComparableVersion(rec);
	            int diff = recommended.compareTo(current);

	            if (diff == 0)
	                status = UP_TO_DATE;
	            else if (diff < 0)
	            {
	                status = AHEAD;
	                if (lat != null)
	                {
	                    ComparableVersion latest = new ComparableVersion(lat);
	                    if (current.compareTo(latest) < 0)
	                    {
	                        status = OUTDATED;
	                        target = latest;
	                    }
	                }
	            }
	            else
	            {
	                status = OUTDATED;
	                target = recommended;
	            }
	        }
	        else if (lat != null)
	        {
	            ComparableVersion latest = new ComparableVersion(lat);
	            if (current.compareTo(latest) < 0)
	            {
	                status = BETA_OUTDATED;
	                target = latest;
	            }
	            else
	                status = BETA;
	        }
	        else
	            status = BETA;
	        if(status == Status.OUTDATED)
	        	e.player.sendMessage((ITextComponent)  new TextComponentTranslation("version", References.VERSION, target));
			MainRegistry.getlogger().info("Update checker returned: " + status);
			
		}
	}	
	
	
	@SubscribeEvent
	public void quit(PlayerLoggedOutEvent e)
	{
		endTimer = 10000;
		strongholdTimer = 10000;
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
	}
	
}

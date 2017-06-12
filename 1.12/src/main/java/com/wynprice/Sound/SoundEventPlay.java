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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.oredict.OreDictionary;

public class SoundEventPlay
{
	private ArrayList<Block> foliage = new ArrayList<Block>();
	public ArrayList<BlockPos> firePositions = new ArrayList<BlockPos>();
	private ArrayList<BlockPos> foliagePositions = new ArrayList<BlockPos>();
	private ArrayList<ResourceLocation> beach = new ArrayList<ResourceLocation>(), forest = new ArrayList<ResourceLocation>(), storm = new ArrayList<ResourceLocation>(), cricket = new ArrayList<ResourceLocation>(), jungle = new ArrayList<ResourceLocation>();
	private ArrayList<Integer> overworld = new ArrayList<Integer>(), nether = new ArrayList<Integer>(), end = new ArrayList<Integer>();
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
					int z = this.player.getPosition().getZ() + randInt(-15, 15);
					boolean isBlockAir = true;
					for(int i = 256; isBlockAir; i--)
					{
						if(!Arrays.asList(Blocks.AIR, Blocks.SNOW_LAYER).contains(world.getBlockState(new BlockPos(x, i, z)).getBlock()))
						{
							isBlockAir = false;
							BlockPos highestBlock = new BlockPos(x, i, z);
							if(world.getBlockState(highestBlock).getBlock() == Blocks.FIRE && !firePositions.contains(highestBlock))
								firePositions.add(highestBlock);
							else if(foliage.contains(world.getBlockState(highestBlock).getBlock()) && !foliagePositions.contains(highestBlock))
								foliagePositions.add(highestBlock);
							BiomeUpdate(highestBlock);
						}
						if(i == -1)
							isBlockAir = false;
					}
					
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
				else try { if (Math.sqrt(player.getDistanceSq(nearestEndCityLocation)) >= 250) endCityPlay = false; } catch (NullPointerException n) {}
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
			else try { if(Math.sqrt(player.getDistanceSq(nearestStrongholdLocation)) >= 350) strongholdPlay = false; } catch (NullPointerException n) {}
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
		if(beach.contains(biome.getRegistryName()) && SoundConfig.isBeach)
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
				if(storm.contains(biome.getRegistryName()) && !canSeeSky)
				{
					world.playSound(player, position, SoundHandler.soundForestStorm.get(0), SoundCategory.MASTER, 1, 1);
				}
			}
			else if(SoundConfig.isForest || SoundConfig.isCricket)
			{
				long time = world.getWorldTime() % 24000;
				if(time <= 23000 && time >= 13000)
				{
					if(time >= 22000 || time <= 14000)
					{
						
						if(cricket.contains(biome.getRegistryName()))
						{
							
							float vol = time >= 22000? (time - 22000) / 500f : (14000f - time) / 500f;
							if(forest.contains(biome.getRegistryName()) && !world.isThundering() && SoundConfig.isForest && randInt(0, 1) != 0)
								world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, vol, 1);
							if(SoundConfig.isCricket)
								world.playSound(player, position, SoundHandler.cricketNight, SoundCategory.MASTER, 2 - vol, 1);
								
						}
					}
					else
					{
						if(cricket.contains(biome.getRegistryName()) && SoundConfig.isCricket)
						{
							world.playSound(player, position, SoundHandler.cricketNight, SoundCategory.MASTER, 2, 1);
						}
					}
					
				}
				else
				{
					if(forest.contains(biome.getRegistryName()) && SoundConfig.isForest)
					{
						if(time >= 23000)
							world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, 2.5f, 1);
						if(randInt(0, 1) != 0)
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
		this.bossMusic = PositionedSoundRecord.getMasterRecord(SoundHandler.bossMusic, 1f, 1f);
		this.hell = PositionedSoundRecord.getMasterRecord(SoundHandler.hell, 1f, 1f);
		beach.clear(); cricket.clear(); storm.clear(); forest.clear(); nether.clear(); end.clear(); overworld.clear(); foliage.clear();
		int lineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber() + 3;
		try
		{
			for(Integer i : SoundConfig.moddedBeach){beach.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : Arrays.asList(16,25,26)){beach.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedCricket){cricket.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : Arrays.asList(1,4,5,6,18,19,27,28,29,30,31,32,33,35)){cricket.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedStorm){storm.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : Arrays.asList(1,4,5,18,19,27,28,29,30,31,32,33)){storm.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedForest){forest.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : Arrays.asList(4,5,18,19,27,28,29,30,31,32,33)){forest.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedJungle){jungle.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : Arrays.asList(21,22,23)){jungle.add(Biome.getBiome(i).getRegistryName());}
			
			for(Integer i : SoundConfig.moddedNether){nether.add(i);}nether.add(-1);
			for(Integer i : SoundConfig.moddedEnd){end.add(i);}end.add(1);
			for(Integer i : SoundConfig.moddedOverworld){overworld.add(i);}overworld.add(0);
		}
		catch (Exception ex) 
		{
			e.player.sendMessage((ITextComponent) new TextComponentTranslation("id.notexist", Arrays.asList("Beach", "Beach", "Cricket", "Cricket", "Storm", "Storm", "Forest", "Forest", "Jungle", "Jungle", "", "Nether", "End", "Overworld").get(ex.getStackTrace()[0].getLineNumber() - lineNumber)));
		}
		
		String bop = "biomesoplenty";
		if(Loader.isModLoaded(bop)) 
		{
			for(String s : Arrays.asList("bamboo_forest", "bayou", "bog", "boreal_forest", "brushland", "chaparral", "cold_desert", "crag", "dead_swamp", "eucalyptus_forest", 
					"grassland", "grove", "highland", "heathland", "lavender_fields", "lush_desert", "lush_swamp", "moor", "mountain_peaks", "mystic_grove","ominous_woods", 
					"quagmire", "rainforest", "redwood_forest", "sacred_springs", "seasonal_forest", "shrubland","snowy_coniferous_forest", "steppe", "temperate_rainforest", 
					"wasteland", "wetland", "xeric_shrubland", "kelp_forest", "mangrove", "origin_island", "maple_woods", "snowy_forest", "shield", "coniferous_forest"))
			{
				ResourceLocation loc = new ResourceLocation(bop, s);
				forest.add(loc);
				storm.add(loc);
				cricket.add(loc);
			}
			for(String s : Arrays.asList("dead_forest", "fen", "flower_field", "land_of_lakes", "marsh", "meadow", "orchard", "outback", "overgrown_cliffs"))
				cricket.add(new ResourceLocation(bop, s));
			for(String s : Arrays.asList("gravel_beach"))
				beach.add(new ResourceLocation(bop, s));
			for(int i = 0; i < 16; i++) foliage.add(Block.getBlockFromItem(new ItemStack(Item.getByNameOrId("biomesoplenty:plant_0"),1,i).getItem()));
		}
		String ac = "abyssalcraft";
		if(Loader.isModLoaded(ac))
		{
			ResourceLocation loc = new ResourceLocation(ac, "coralium_infested_swamp");
			forest.add(loc);
			storm.add(loc);
			cricket.add(loc);
		}
		String t = "traverse";
		if(Loader.isModLoaded(t))
		{
			for(String s : Arrays.asList("autumnal_woods", "woodlands","mini_jungle", "green_swamp", "temperate_rainforest", "forested_hills", "birch_forested_hills",  "autumnal_wooded_hills"))
			{
				ResourceLocation loc = new ResourceLocation(t, s);
				forest.add(loc);
				storm.add(loc);
				cricket.add(loc);
			}
			for(String s : Arrays.asList("meadow", "badlands", "rocky_plateau"))
				cricket.add(new ResourceLocation(t, s));
		}
		String id = "integrateddynamics";
		if(Loader.isModLoaded(id))
		{
			ResourceLocation loc = new ResourceLocation(id, "meneglin");
			forest.add(loc);
			storm.add(loc);
			cricket.add(loc);
		}
		if(Loader.isModLoaded("realworld"))
		{
			for(String s : Arrays.asList("rw_birch_autumn_forest", "rw_blue_oak_forest", "rw_bombona_beach", "rw_flatland_thicket", "rw_silver_birch_hills", "rw_spiny_forest", "rw_spruce_mountains"))
			{
				ResourceLocation loc = new ResourceLocation(s);
				forest.add(loc);
				storm.add(loc);
				cricket.add(loc);
				if(s.equals("rw_bombona_beach"))
					beach.add(loc);
			}
		}
		for(ItemStack i : OreDictionary.getOres("treeLeaves"))
			foliage.add(Block.getBlockFromItem(i.getItem()));
		for(ItemStack i : OreDictionary.getOres("dirt"))
			foliage.add(Block.getBlockFromItem(i.getItem()));
		for(ItemStack i : OreDictionary.getOres("grass"))
			foliage.add(Block.getBlockFromItem(i.getItem()));
		for(ItemStack i : OreDictionary.getOres("logWood"))
			foliage.add(Block.getBlockFromItem(i.getItem()));
		
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

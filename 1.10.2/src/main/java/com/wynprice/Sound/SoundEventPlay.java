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

import javax.sound.sampled.Clip;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.wynprice.Sound.config.SoundConfig;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
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
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.oredict.OreDictionary;

public class SoundEventPlay
{
	private ArrayList<Block> foliage = new ArrayList<Block>();
	public ArrayList<BlockPos> firePositions = new ArrayList<BlockPos>();
	private ArrayList<BlockPos> foliagePositions = new ArrayList<BlockPos>();
	public static ArrayList<ResourceLocation> beach = new ArrayList<ResourceLocation>(), forest = new ArrayList<ResourceLocation>(), storm = new ArrayList<ResourceLocation>(), cricket = new ArrayList<ResourceLocation>(), jungle = new ArrayList<ResourceLocation>();
	private ArrayList<Integer> overworld = new ArrayList<Integer>(), nether = new ArrayList<Integer>(), end = new ArrayList<Integer>();	
	private EntityPlayer player;
	private Entity dragon, wither;
	private World world;
	private float timer, backTimer, relativeDistance, witherInvulvTimer = 1;
	private static Boolean single = false, loadin = true, previousFrameDragon = false, previousFrameWither = false, playMusic = false, doUpdate = true, isInCredits = false, isInCreditsFirst = false, inPauseMenu = true;;
	private static Clip glassworkOpen, bossMusic, hell, mPiarate, mPiarateB;
	private static final String glassLoc = "glasswork_opening.wav", bossLoc = "boss_fight.wav", hellLoc = "hell.wav",
			mPiarateLoc = "mPiarate.wav", mPiarateBLoc = "mPiarateB.wav";
	void define()
	{
		SoundSystem.sClips.clear();
		for(String s : Arrays.asList(glassLoc, bossLoc, hellLoc, mPiarateLoc, mPiarateBLoc))
			SoundSystem.sClips.add(SoundSystem.sound(s));
		glassworkOpen = SoundSystem.sClips.get(0);
		bossMusic = SoundSystem.sClips.get(1);
		hell = SoundSystem.sClips.get(2);
		mPiarate = SoundSystem.sClips.get(3);
		mPiarateB = SoundSystem.sClips.get(4);
		
	}
	@SubscribeEvent
	public void MultiUpdate(Event e)
	{
		if(Minecraft.getMinecraft().currentScreen != null && !loadin)
		{
			isInCredits = Minecraft.getMinecraft().currentScreen.getClass().getName() == "net.minecraft.client.gui.GuiWinGame";
			if(isInCredits)
			{
				bossMusic = SoundSystem.pauseSound(bossMusic, 1);
				hell = SoundSystem.pauseSound(hell, 2);
				mPiarate = SoundSystem.pauseSound(mPiarate, 3);
				mPiarateB = SoundSystem.pauseSound(mPiarateB, 4);
			}
		}
		if(isInCredits && !isInCreditsFirst)
		{
			glassworkOpen.start();
		}
			
		if(!isInCredits && isInCreditsFirst)
			glassworkOpen = SoundSystem.resetSound(glassworkOpen,0);
		isInCreditsFirst = isInCredits;
		if(world == null || Minecraft.getMinecraft().isGamePaused() && inPauseMenu)
		{
			inPauseMenu = false;
			bossMusic = SoundSystem.pauseSound(bossMusic, 1);
			hell = SoundSystem.pauseSound(hell, 2);
			mPiarate = SoundSystem.pauseSound(mPiarate, 3);
			mPiarateB = SoundSystem.pauseSound(mPiarateB, 4);
		}
	}
	
	@SubscribeEvent
	public void playerUpdate(LivingUpdateEvent e)
	{
		inPauseMenu = true;
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
				&& !SoundConfig.isHell && !SoundConfig.isEndDragon && !SoundConfig.isWither && !SoundConfig.isEnd && !SoundConfig.isShulkerSoundEnd)
				return;
		
		else if(!single)
		{
			if((SoundConfig.runOnServer && SoundConfig.useList && SoundConfig.readServers.contains(Minecraft.getMinecraft().getCurrentServerData().serverIP)) || !SoundConfig.runOnServer)
				return;
		}
			
		
		
		
		if(playMusic && !bossMusic.isRunning())
		{
			Minecraft.getMinecraft().getSoundHandler().stop(References.MODID + ":wither.spawn.timer", SoundCategory.MASTER);
			playMusic = false;
			if(previousFrameDragon || previousFrameWither)
				bossMusic.start();
		}
		this.world = e.getEntity().getEntityWorld();
		if(e.getEntity() instanceof EntityPlayer)
		{
			this.player = (EntityPlayer) e.getEntityLiving();
			if(SoundConfig.mode2)
				if(player.isRiding() && player.getRidingEntity() instanceof EntityBoat && player.getRidingEntity().isInWater() && world.isRemote)
				{
					EntityBoat boat = (EntityBoat) player.getRidingEntity();
					double x = boat.motionX > 0? boat.motionX : -boat.motionX;
					double z = boat.motionZ > 0? boat.motionZ : -boat.motionZ;
					double velo = Math.sqrt((x*x) + (z*z));
					double vol = Math.round((velo > 0.35d? 1d : (velo < 0.1d?  0.1d : velo + 0.1d / 0.25d)) * 1000) / 1000d;
					if(!(mPiarate.isRunning() || mPiarateB.isRunning()))
						(velo > 0.35 ? mPiarateB : mPiarate).start();
					if(velo > 0.35)
					{
						if(!Minecraft.getMinecraft().entityRenderer.isShaderActive())
							Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blobs2.json"));
						if(!mPiarateB.isRunning())
						{
							mPiarateB.setFramePosition(mPiarate.getFramePosition());
							mPiarate = SoundSystem.resetSound(mPiarate, 3);
							mPiarateB.start();
						}	
					}
					else
					{
						if(Minecraft.getMinecraft().entityRenderer.isShaderActive())
							Minecraft.getMinecraft().entityRenderer.stopUseShader();
						if(!mPiarate.isRunning())
						{
							if(mPiarateB.getFramePosition() != 0)
								mPiarate.setFramePosition(mPiarateB.getFramePosition());
							mPiarateB = SoundSystem.resetSound(mPiarateB, 4);
							mPiarate.start();
						}
					}
				}
				else if(!(player.isRiding() && player.getRidingEntity() instanceof EntityBoat))
				{
					if(Minecraft.getMinecraft().entityRenderer.isShaderActive())
						try{Minecraft.getMinecraft().entityRenderer.stopUseShader();} catch (RuntimeException run) {}
					if(mPiarate.isRunning())
						mPiarate = SoundSystem.resetSound(mPiarate, 3);
					if(mPiarateB.isRunning())
						mPiarateB = SoundSystem.resetSound(mPiarateB, 4);
				}
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
							bossMusic = SoundSystem.resetSound(bossMusic,1);
							world.playSound(player, dragon.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 5f, 1f);
						}
						if(!world.loadedEntityList.contains(dragon) && previousFrameDragon)
							bossMusic = SoundSystem.resetSound(bossMusic,1);
						previousFrameDragon = !(dragon.isDead || d.getHealth() == 0 || !world.loadedEntityList.contains(dragon));
						
					}
					if(!bossMusic.isRunning() && previousFrameDragon)
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
							bossMusic = SoundSystem.resetSound(bossMusic,1);
						}
						previousFrameWither = !(wither.isDead || w.getHealth() == 0 || !world.loadedEntityList.contains(wither));
						if(!world.loadedEntityList.contains(wither) && previousFrameWither)
							previousFrameWither = false;
						
					}
					else witherInvulvTimer = 1f;
					if(!bossMusic.isRunning() && previousFrameWither && witherInvulvTimer == 0)
						playMusic = true;
						
				}
				
			}
			if(!previousFrameWither && !hell.isRunning() && nether.contains(player.dimension)&& SoundConfig.isHell)
			{
				hell.start();
			}
			if((previousFrameWither && hell.isRunning() && nether.contains(player.dimension)&& SoundConfig.isHell) || (!nether.contains(player.dimension) && hell.isRunning()))
			{
				hell = SoundSystem.resetSound(hell,2);	
			}
			if(timer >= 20f)
			{
				backTimer++;
				timer = 0f;
				if(!end.contains(player.dimension) || (end.contains(player.dimension) && (!player.isElytraFlying() && player.posY > 49)))
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
					if(Math.sqrt(e.getDistanceSq(player.getPosition())) < 25 && randInt(0, 5) == 1) 
						world.playSound(player, e.getPosition(), SoundHandler.endAmbience, SoundCategory.MASTER, 10f, 2 - (randInt(0, 400) / 100));
			}
				
		}
		
		if(!overworld.contains(player.dimension))
			return;
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
			else if(SoundConfig.isForest || SoundConfig.isJungle || SoundConfig.isCricket)
			{
				long time = world.getWorldTime() % 24000;
				if(time <= 23000 && time >= 13000)
				{
					if(time >= 22000 || time <= 14000)
					{
						
						if(cricket.contains(biome.getRegistryName()))
						{
							
							float vol = time >= 22000? (time - 22000) / 500f : (14000f - time) / 500f;
							if(!world.isThundering())
							{
								if(SoundConfig.isForest && forest.contains(biome.getRegistryName()) && randInt(0, 1) != 0)
									world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, vol, 1);
								if(SoundConfig.isJungle && jungle.contains(biome.getRegistryName()))
									world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, vol, 1);

							}
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
					else if(jungle.contains(biome.getRegistryName()) && SoundConfig.isJungle)
					{
						if(time >= 23000)
						{
							world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, 2.5f, 1);
							world.playSound(player, position, SoundHandler.soundForest.get(randInt(0, SoundHandler.soundForest.size() - 1)), SoundCategory.MASTER, 2.5f, 1);
						}
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
			if(!firePositions.contains(e.getPos()))
				firePositions.add(e.getPos());
	}
	
	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void playerQuit(ClientDisconnectionFromServerEvent e)
	{
		this.loadin = true;
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e) throws IOException
	{
		define();
		beach.clear(); cricket.clear(); storm.clear(); forest.clear(); nether.clear(); end.clear(); overworld.clear(); foliage.clear();
		for(Integer i : Arrays.asList(16,25,26)){beach.add(Biome.getBiome(i).getRegistryName());}
		for(Integer i : Arrays.asList(1,4,5,6,18,19,27,28,29,30,31,32,33,35)){cricket.add(Biome.getBiome(i).getRegistryName());}
		for(Integer i : Arrays.asList(1,4,5,18,19,27,28,29,30,31,32,33)){storm.add(Biome.getBiome(i).getRegistryName());}
		for(Integer i : Arrays.asList(4,5,18,19,27,28,29,30,31,32,33)){forest.add(Biome.getBiome(i).getRegistryName());}
		for(Integer i : Arrays.asList(21,22,23)){jungle.add(Biome.getBiome(i).getRegistryName());}
		nether.add(-1);
		end.add(1);
		overworld.add(0);
		int lineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber() + 3;
		try
		{
			for(Integer i : SoundConfig.moddedBeach){beach.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedCricket){cricket.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedStorm){storm.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedForest){forest.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedJungle){jungle.add(Biome.getBiome(i).getRegistryName());}
			for(Integer i : SoundConfig.moddedNether){nether.add(i);}
			for(Integer i : SoundConfig.moddedEnd){end.add(i);}
			for(Integer i : SoundConfig.moddedOverworld){overworld.add(i);}
		}
		catch (NullPointerException nul) 
		{
			e.player.addChatMessage((ITextComponent) new TextComponentTranslation("id.notexist", Arrays.asList("Beach", "Cricket", "Storm", "Forest", "Jungle", "Nether", "End", "Overworld").get(nul.getStackTrace()[0].getLineNumber() - lineNumber)));
		}
		
		String bop = "biomesoplenty";
		if(Loader.isModLoaded(bop)) 
		{
			for(String s : Arrays.asList("bamboo_forest", "bayou", "bog", "boreal_forest", "brushland", "chaparral", "oasis",
					"grassland", "grove", "heathland", "lavender_fields", "lush_desert", "lush_swamp", "moor", "mountain_peaks", "mystic_grove","ominous_woods", 
					"quagmire", "rainforest", "redwood_forest", "sacred_springs", "seasonal_forest", "shrubland","snowy_coniferous_forest", "steppe", 
					"wasteland", "wetland", "xeric_shrubland", "kelp_forest", "mangrove", "origin_island", "maple_woods", "snowy_forest", "shield", "coniferous_forest"))
			{
				ResourceLocation loc = new ResourceLocation(bop, s);
				forest.add(loc);
				storm.add(loc);
				cricket.add(loc);
			}
			for(String s : Arrays.asList("dead_forest", "fen", "flower_field", "land_of_lakes", "marsh", "meadow", "orchard", "outback", "overgrown_cliffs", "crag", "dead_swamp", "highland"))
				cricket.add(new ResourceLocation(bop, s));
			beach.add(new ResourceLocation(bop, "gravel_beach"));
			for(String s : Arrays.asList("eucalyptus_forest", "temperate_rainforest"))
				jungle.add(new ResourceLocation(bop, s));
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
	        	e.player.addChatMessage((ITextComponent)  new TextComponentTranslation("version", References.VERSION, target));
			MainRegistry.getlogger().info("Update checker returned: " + status);
			
		}
	}	
	
}

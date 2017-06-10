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
	private ArrayList<Integer> beachIDs = new ArrayList<Integer>(), forestIDs = new ArrayList<Integer>(), stormIDs = new ArrayList<Integer>(), cricketIDs = new ArrayList<Integer>(),
			overworld = new ArrayList<Integer>(), nether = new ArrayList<Integer>(), end = new ArrayList<Integer>();	private EntityPlayer player;
	private ISound bossMusic, hell;
	private Entity dragon, wither;
	private World world;
	private float timer, backTimer, relativeDistance, witherInvulvTimer = 1;
	private static Boolean single = false, loadin = true, previousFrameDragon = false, previousFrameWither = false, playMusic = false, doUpdate = true;
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
				&& !SoundConfig.isHell && !SoundConfig.isEndDragon && !SoundConfig.isWither && !SoundConfig.isEnd && !SoundConfig.isShulkerSoundEnd)
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
						if(world.getBlockState(new BlockPos(x, i, z)).getBlock() != Blocks.AIR)
						{
							isBlockAir = false;
							BlockPos highestBlock = new BlockPos(x, i, z);
							if(world.getBlockState(highestBlock).getBlock() == Blocks.FIRE && !firePositions.contains(highestBlock))
								firePositions.add(highestBlock);
							else if(foliage.contains(world.getBlockState(highestBlock).getBlock()) && !foliagePositions.contains(highestBlock))
								foliagePositions.add(highestBlock);
							BiomeUpdate(highestBlock);
						}
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
		
		System.out.println(biome.getIdForBiome(biome));
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
		this.bossMusic = PositionedSoundRecord.getMasterRecord(SoundHandler.bossMusic, 1f, 1f);
		this.hell = PositionedSoundRecord.getMasterRecord(SoundHandler.hell, 1f, 1f);
		beachIDs.clear(); cricketIDs.clear(); stormIDs.clear(); forestIDs.clear(); nether.clear(); end.clear(); overworld.clear(); foliage.clear();
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
		
		if(Loader.isModLoaded("biomesoplenty"))
		{
			for(Integer i : Arrays.asList(42,43,44,45,46,47,48,50,52,54,55,58,59,61,63,64,65,69,70,71,
					72,74,77,78,79,80,81,83,84,86,87,90,91,93,98,99,100)){forestIDs.add(i);stormIDs.add(i);}
			for(Integer i : Arrays.asList(42,43,44,45,46,47,48,50,52,53,54,55,56,57,58,59,60,61,62,63,
					64,65,66,67,68,69,70,71,72,73,74,75,77,78,79,80,81,82,83,84,85,86,87,90,91,93,98,99,100)){cricketIDs.add(i);}
			for(int i = 0; i < 16; i++) foliage.add(Block.getBlockFromItem(new ItemStack(Item.getByNameOrId("biomesoplenty:plant_0"),1,i).getItem()));
			
		}
		
		for(ItemStack i : OreDictionary.getOres("treeLeaves"))
			foliage.add(Block.getBlockFromItem(i.getItem()));
		for(ItemStack i : OreDictionary.getOres("dirt"))
			foliage.add(Block.getBlockFromItem(i.getItem()));
		for(ItemStack i : OreDictionary.getOres("grass"))
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
	
	
	@SubscribeEvent
	public void quit(PlayerLoggedOutEvent e)
	{
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
	}
	
}

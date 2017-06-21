package com.wynprice.Sound;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.wynprice.Sound.commands.CommandBiomeDictonary;
import com.wynprice.Sound.config.SoundConfig;
import com.wynprice.Sound.proxys.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import paulscode.sound.SoundSystemConfig;

@Mod(modid = References.MODID , name = References.NAME , version =References.VERSION, guiFactory = References.GUI_FACTORY, canBeDeactivated=true)
public class MainRegistry
{
	
	@SidedProxy(clientSide = References.CLIENT_PROXY, serverSide = References.SERVER_PROXY)
	public static CommonProxy proxy;
		
	@Mod.Instance(References.MODID)
	public static MainRegistry instance;

	private static File optionsFile;
	public static final Splitter COLON_SPLITTER = Splitter.on(':');
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent e) throws IOException
	{
		getlogger().info("Playing that noteblock nicely");
		SoundConfig.preInit();
		SoundSystemConfig.setNumberStreamingChannels(11);
		SoundSystemConfig.setNumberNormalChannels(21); 
		
		if(SoundConfig.forceMusic)
		{
			try
			{
				changeFiles();
			}
			catch (FileNotFoundException fnfe) 
			{
				createNew();
			}
			
		}
		proxy.PreInit(e);
		
	}
	
	public static void createNew()
	{
		createOptions();
		try {
			changeFiles();
		} catch (FileNotFoundException e) {
			getlogger().error("Unable to create new Options file. Music will be at 100%");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void changeFiles() throws FileNotFoundException, IOException
	{
		optionsFile = new File(Minecraft.getMinecraft().mcDataDir, "options.txt");
		List<String> list = IOUtils.readLines(new FileInputStream(optionsFile));
		ArrayList<String> changedList = new ArrayList<String>();
		String finalList = "";
		for(String s : list)
			if(s.split(":")[0].equals("soundCategory_music"))
				changedList.add("soundCategory_music:0.0");
			else
				changedList.add(s);		
		for(String s : changedList)
			finalList += s + "\n";
		writeFile(optionsFile.getAbsolutePath(), finalList);
	}
	
	@EventHandler
	public static void Init(FMLInitializationEvent e)
	{
		SoundEventPlay.define();
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
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandBiomeDictonary());
	}
	
	 private static NBTTagCompound dataFix(NBTTagCompound p_189988_1_)
	    {
	        int i = 0;

	        try
	        {
	            i = Integer.parseInt(p_189988_1_.getString("version"));
	        }
	        catch (RuntimeException var4)
	        {
	            ;
	        }

	        return Minecraft.getMinecraft().getDataFixer().process(FixTypes.OPTIONS, p_189988_1_, i);
	    }
	 
	 @SuppressWarnings("resource")
	public static void writeFile(String filename, String text) throws IOException {
		    FileOutputStream fos = null;
		    try {
		        fos = new FileOutputStream(filename);
		        fos.write(text.getBytes("UTF-8"));
		    } catch (IOException e) {
		        close(fos);
		        throw e;
		    }
		}

	public static void close(Closeable closeable) {
		    try {
		        closeable.close();
		    } catch(IOException ignored) {
		    }
		}
	
	
	
	
	
	
	

	
	
		
	public static void createOptions()
	{
		MainRegistry.getlogger().info("Options file does not exist. Creating now");
		PrintWriter printwriter = null;
		GameSettings g = new GameSettings();
		File optionsFile = new File(Minecraft.getMinecraft().mcDataDir, "options.txt");
		final Gson GSON = new Gson();
        try
        {
            printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(optionsFile), StandardCharsets.UTF_8));
            printwriter.println("version:1139");
            printwriter.println("invertYMouse:" + g.invertMouse);
            printwriter.println("mouseSensitivity:" + g.mouseSensitivity);
            printwriter.println("fov:" + (g.fovSetting - 70.0F) / 40.0F);
            printwriter.println("gamma:" + g.gammaSetting);
            printwriter.println("saturation:" + g.saturation);
            printwriter.println("renderDistance:" + g.renderDistanceChunks);
            printwriter.println("guiScale:" + g.guiScale);
            printwriter.println("particles:" + g.particleSetting);
            printwriter.println("bobView:" + g.viewBobbing);
            printwriter.println("anaglyph3d:" + g.anaglyph);
            printwriter.println("maxFps:" + g.limitFramerate);
            printwriter.println("fboEnable:" + g.fboEnable);
            printwriter.println("difficulty:" + g.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + g.fancyGraphics);
            printwriter.println("ao:" + g.ambientOcclusion);

            switch (g.clouds)
            {
                case 0:
                    printwriter.println("renderClouds:false");
                    break;
                case 1:
                    printwriter.println("renderClouds:fast");
                    break;
                case 2:
                    printwriter.println("renderClouds:true");
            }

            printwriter.println("resourcePacks:" + GSON.toJson(g.resourcePacks));
            printwriter.println("incompatibleResourcePacks:" + GSON.toJson(g.incompatibleResourcePacks));
            printwriter.println("lastServer:" + g.lastServer);
            printwriter.println("lang:" + g.language);
            printwriter.println("chatVisibility:" + g.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + g.chatColours);
            printwriter.println("chatLinks:" + g.chatLinks);
            printwriter.println("chatLinksPrompt:" + g.chatLinksPrompt);
            printwriter.println("chatOpacity:" + g.chatOpacity);
            printwriter.println("snooperEnabled:" + g.snooperEnabled);
            printwriter.println("fullscreen:" + g.fullScreen);
            printwriter.println("enableVsync:" + g.enableVsync);
            printwriter.println("useVbo:" + g.useVbo);
            printwriter.println("hideServerAddress:" + g.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + g.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + g.pauseOnLostFocus);
            printwriter.println("touchscreen:" + g.touchscreen);
            printwriter.println("overrideWidth:" + g.overrideWidth);
            printwriter.println("overrideHeight:" + g.overrideHeight);
            printwriter.println("heldItemTooltips:" + g.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + g.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + g.chatHeightUnfocused);
            printwriter.println("chatScale:" + g.chatScale);
            printwriter.println("chatWidth:" + g.chatWidth);
            printwriter.println("mipmapLevels:" + g.mipmapLevels);
            printwriter.println("forceUnicodeFont:" + g.forceUnicodeFont);
            printwriter.println("reducedDebugInfo:" + g.reducedDebugInfo);
            printwriter.println("useNativeTransport:" + g.useNativeTransport);
            printwriter.println("entityShadows:" + g.entityShadows);
            printwriter.println("mainHand:" + (g.mainHand == EnumHandSide.LEFT ? "left" : "right"));
            printwriter.println("attackIndicator:" + g.attackIndicator);
            printwriter.println("showSubtitles:" + g.showSubtitles);
            printwriter.println("realmsNotifications:" + g.realmsNotifications);
            printwriter.println("enableWeakAttacks:" + g.enableWeakAttacks);
            printwriter.println("autoJump:" + g.autoJump);
            printwriter.println("narrator:" + g.narrator);
            printwriter.println("tutorialStep:" + g.tutorialStep.getName());

            for (KeyBinding keybinding : g.keyBindings)
            {
                String keyString = "key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode();
                printwriter.println(keybinding.getKeyModifier() != net.minecraftforge.client.settings.KeyModifier.NONE ? keyString + ":" + keybinding.getKeyModifier() : keyString);
            }

            for (SoundCategory soundcategory : SoundCategory.values())
            {
                printwriter.println("soundCategory_" + soundcategory.getName() + ":" +  (soundcategory.getName().equals("music")? "false" : "true"));
            }

            for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
            {
                printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":true");
            }
        }
        catch (Exception exception)
        {
            MainRegistry.getlogger().error("Failed to save options", (Throwable)exception);
        }
        finally
        {
            IOUtils.closeQuietly((Writer)printwriter);
        }
	}	
}
 

package com.wynprice.Sound;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Splitter;
import com.wynprice.Sound.config.SoundConfig;
import com.wynprice.Sound.proxys.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
	public static void PreInit(FMLPreInitializationEvent e) throws FileNotFoundException, IOException 
	{
		getlogger().info("Playing that noteblock nicely");
		SoundConfig.preInit();
		if(SoundConfig.forceMusic)
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
}
 

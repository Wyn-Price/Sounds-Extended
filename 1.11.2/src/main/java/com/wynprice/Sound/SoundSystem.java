package com.wynprice.Sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class SoundSystem 
{
	private static boolean f = false;
	private static Thread glassWorksThread;
	public static ArrayList<Clip> sClips = new ArrayList<Clip>(); 
	
	public static Clip pauseSound(Clip clip, int i)
	{
		Clip c = clip;
		if(c != null)
		{
			int tic = clip.getFramePosition();
			if(clip.isRunning())
				c = resetSound(clip,i);
			c.setFramePosition(tic);
		}
		return c;
	}
	
	public static Clip sound(String location)
	{
		location = "/assets/" + References.MODID + "/sounds/" + location;
		Clip clip = null;
		URL url = new SoundSystem().c().getResource(location);
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clip;
	}
	
	public static Clip resetSound(Clip clip, int i)
	{
		clip.stop();
		Clip c = sClips.get(i);
		c.setFramePosition(0);
		return c;
	}
	
	private Class<? extends SoundSystem> c()
	{
		return getClass();
	}
	
	private static Thread glassworks()
	{
		return new Thread(){
			  public void run(){
				  	try {
				  		registerGlassworks().play();
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
			  }
			};
	}
	
	public static void playGlassworks()
	{
		if(glassWorksThread == null)
			glassWorksThread = glassworks();
		glassWorksThread.start(); 
	}
	
	
	public static void stopGlassworks()
	{
		glassWorksThread.stop();
		glassWorksThread = null;
	}
	
	public static AdvancedPlayer registerGlassworks()
	{
		AdvancedPlayer player = null;
		String location = "/assets/" + References.MODID + "/sounds/glasswork_opening.mp3";
		try {
			player = new AdvancedPlayer(new SoundSystem().c().getResourceAsStream(location)); 
		    FactoryRegistry.systemRegistry().createAudioDevice();
		} catch (JavaLayerException e) {
		    e.printStackTrace();
		}
		return player;
	}
}

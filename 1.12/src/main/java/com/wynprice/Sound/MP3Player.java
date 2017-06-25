package com.wynprice.Sound;

import java.util.ArrayList;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class MP3Player 
{
	private AdvancedPlayer player;
	/** The AudioDevice the audio samples are written to. */
	private AudioDevice device;
	/** The Thread that holds the playing function */
	private Thread thread;
	/** The name of the file */
	private final  String name;
	/**The frame at which the audio is paused on*/
	private int frameOnPaused;
	public static ArrayList<MP3Player> allMp3 = new ArrayList<MP3Player>();
	private Boolean isRunning = false;
	
	public MP3Player (String name)
	{
		this.name = name;
		try {
			device = FactoryRegistry.systemRegistry().createAudioDevice();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
		allMp3.add(this);
	}
	
	private Thread getThread()
	{
		return new Thread(){
			  public void run(){
				  	try {
				  		registerThread().play();
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
			  }
			};
	}
	
	public void play()
	{
		if(thread == null)
			thread = getThread();
		try
		{
			thread.start(); 
		}
		catch (IllegalThreadStateException e) {
			System.err.println("Unable to play Music\n" + e.getStackTrace());
		}
	}
	
	
	public void stop()
	{
		if(thread != null)
			thread.stop();
		thread = null;
	}
	
	private AdvancedPlayer registerThread()
	{
		AdvancedPlayer player = null;
		String location = "/assets/" + References.MODID + "/sounds/" + name + ".mp3";
		try {
			player = new AdvancedPlayer(new getClass().get().getResourceAsStream(location), device);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		} 
		return player;
	}
	
	public void pause()
	{
		frameOnPaused = milliSecondsToFrames();
		stop();
	}
	
	private int milliSecondsToFrames()
	{
		return (int) Math.floor(getPosition() * 0.028f);
	}
	
	public void resume()
	{
		play();
	}
	
	private void playSound(int frame) throws JavaLayerException
	{
		player.play(frame, Integer.MAX_VALUE);
	}
	
	public int getPosition()
	{
		return device.getPosition();
	}
	
	public Boolean isPlayer()
	{
		return device.isOpen();
	}
	
	public String getName()
	{
		return name;
	}
	
	private Class<? extends MP3Player> c()
	{
		return getClass();
	}

	public boolean isRunning() 
	{
		return isRunning;
	}
	
	public static MP3Player findWithName(String name)
	{
		for(MP3Player m : allMp3)
			if(m.getName().equals(name))
				return m;
		return null;
	}
	
	private void registerPlayFromThread() throws JavaLayerException
	{
		player.play();
	}
	
	public MP3Player playSound(MP3Player mp3)
	{
		MP3Player m = mp3;
		m.play();
		return m;
	}
}


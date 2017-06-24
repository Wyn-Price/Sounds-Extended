package com.wynprice.Sound;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class MP3Player 
{
	/** The AudioDevice the audio samples are written to. */
	private AudioDevice device;
	/** The Thread that holds the playing function */
	private Thread thread;
	/** The name of the file */
	private final  String name;
	/** If the Audio is playing */
	public Boolean isPlaying = false;
	
	public MP3Player (String name)//"glasswork_opening"
	{
		this.name = name;
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
		thread.stop();
		thread = null;
	}
	
	private AdvancedPlayer registerThread()
	{
		AdvancedPlayer player = null;
		String location = "/assets/" + References.MODID + "/sounds/" + name + ".mp3";
		try {
			device = FactoryRegistry.systemRegistry().createAudioDevice();
			player = new AdvancedPlayer(new MP3Player(name).c().getResourceAsStream(location), device); 
		} catch (JavaLayerException e) {
		    e.printStackTrace();
		}
		return player;
	}
	
	public int getPosition()
	{
		return device.getPosition();
	}
	
	private Class<? extends MP3Player> c()
	{
		return getClass();
	}
}


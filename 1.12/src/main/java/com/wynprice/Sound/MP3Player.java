package com.wynprice.Sound;

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
	
	public MP3Player (String name)
	{
		this.name = name;
	}
	
	private Thread getThread(int frame)
	{
		return new Thread(){
			  public void run(){
				  	try {
				  		player = registerThread();
				  		player.play(frame, Integer.MAX_VALUE);
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
			  }
			};
	}
	
	public void play(int frame)
	{
		if(thread == null)
			thread = getThread(frame);
		try
		{
			thread.start(); 
		}
		catch (IllegalThreadStateException e) {
			System.err.println("Unable to play Music\n" + e.getStackTrace());
		}
	}
	
	public void play()
	{
		play(0);
	}
	
	
	public void stop()
	{
		thread.stop();
		thread = null;
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
		play(frameOnPaused);
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
	
	public Boolean isPlayer()
	{
		return device.isOpen();
	}
	
	private Class<? extends MP3Player> c()
	{
		return getClass();
	}
}


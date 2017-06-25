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
	private Runnable runnable;
	/** The name of the file */
	private final  String name;
	/**The frame at which the audio is paused on*/
	private int frameOnPaused;
	private Boolean isRunning = false;
	
	public MP3Player (String name)
	{
		this.name = name;
		player = register();
	}
	private class getThread implements Runnable
	{
		int frame;
		AdvancedPlayer player;
		private  getThread(AdvancedPlayer player, int frame)
		{
			this.frame = frame;
			this.player = player;
		}

		@Override
		public void run() {
			try {
		  		player.play(frame, Integer.MAX_VALUE);/ERROR HERE
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public void start(int frame)
	{
		isRunning = true;
		if(thread == null)
		{
			runnable = new getThread(player, frame);
			thread = new Thread(runnable);
		}
			
		try
		{
			thread.start(); 
		}
		catch (IllegalThreadStateException e) {
			System.err.println("Unable to play Music\n");
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		start(0);
	}
	
	
	public void stop()
	{
		isRunning = false;
		if(thread != null)
			thread.stop();
		else
			System.err.println("stop() was called before start() for " + name); 
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
		start(frameOnPaused);
	}
	
	private AdvancedPlayer register()
	{
		AdvancedPlayer player = null;
		String location = "/assets/" + References.MODID + "/sounds/" + name + ".mp3";
		try {
			device = FactoryRegistry.systemRegistry().createAudioDevice();
			player = new AdvancedPlayer(new getClass().get().getResourceAsStream(location), device); 
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

	public boolean isRunning() 
	{
		return isRunning;
	}
}


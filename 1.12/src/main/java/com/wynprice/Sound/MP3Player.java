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
	private Runnable runnable;
	/** The name of the file */
	private final  String name;
	/**The frame at which the audio is paused on*/
	private int frameOnPaused;
	public static ArrayList<MP3Player> allMp3 = new ArrayList<MP3Player>();
	private Boolean isRunning = false;
	
	public MP3Player (String name)
	{
		this.name = name;
		player = register();
		allMp3.add(this);
	}
	private class getThread implements Runnable
	{
		int frame;
		AdvancedPlayer player;
		private  getThread(int frame)
		{
			this.frame = frame;
		}

		@Override
		public void run() {
			try {
		  		register().play();
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
			runnable = new getThread(frame);
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
	
	private void playSound(int frame) throws JavaLayerException
	{
		player.play(frame, Integer.MAX_VALUE);
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
		this.player = player;
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
}


package com.wynprice.Sound;

import java.util.ArrayList;
import java.util.Set;

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
		this.player = register();
		allMp3.add(this);
	}
	
	private Thread getThread()
	{
		return new Thread(){
			  public void run(){
				  	try {
				  		player.play();
				  		isRunning = false;
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
			  }
			};
	}
	
	public void play()
	{
		isRunning = true;
		player = register();
		Thread t = getThread();
		t.start();
		t.setName(name + " soundPlayer");
	}
	
	
	public void stop()
	{
		isRunning = false;
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for(Thread t : threadArray)
		{
			if(t.getName().equals(name + " soundPlayer"))
				t.suspend();
				
		}
	}
	
	private AdvancedPlayer register()
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


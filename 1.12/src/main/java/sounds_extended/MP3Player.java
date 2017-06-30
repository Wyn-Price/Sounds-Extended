package sounds_extended;

import java.util.ArrayList;
import java.util.Set;

import com.wynprice.Sound.References;

import sounds_extended.decoder.JavaLayerException;
import sounds_extended.player.AudioDevice;
import sounds_extended.player.FactoryRegistry;
import sounds_extended.player.advanced.AdvancedPlayer;

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
			{
				t.suspend();	
			}
				
		}
		reset();
	}
	
	private void reset()
	{
		try {
			device = FactoryRegistry.systemRegistry().createAudioDevice();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
		this.player = register();
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

	public void pause() {
		stop();
	}
}


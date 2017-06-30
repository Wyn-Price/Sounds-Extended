package sounds_extended;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.wynprice.Sound.References;

public class WAVPlayer {
	private Clip clip;
	private String name;
	
	public WAVPlayer(String name, float vol)
	{
		this.name = name;
		String location = "/assets/" + References.MODID + "/sounds/" + name + ".wav";
		Clip clip = null;
		URL url = new getClass().get().getResource(location);
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
		this.clip = clip;
		setVolume(vol);
	}
	
	public void setVolume(float vol)
	{
		vol = vol > 1? 1 : (vol < 0? 0 : vol);
		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volume.setValue((float) (Math.log(vol) / Math.log(10.0) * 20.0));
	}
	
	public void play()
	{
		clip.start();
	}
	
	public void stop()
	{
		clip.stop();
		resetClip();
	}
	
	public void pause()
	{
		int position = clip.getFramePosition();
		stop();
		clip.setFramePosition(position);
	}
	
	private void resetClip()
	{
		clip.setFramePosition(0);
	}
	
	private Class<? extends WAVPlayer> c()
	{
		return getClass();
	}
	
	public Boolean isRunning()
	{
		return clip.isRunning();
	}
	
	public float getMicrosecondPosition()
	{
		return clip.getMicrosecondPosition();
	}

	public int getFramePosition() 
	{
		return clip.getFramePosition();
	}

	public void setFramePosition(int framePosition) 
	{
		clip.setFramePosition(framePosition);
	}

	public String toString()
	{
		return name;
	}
}

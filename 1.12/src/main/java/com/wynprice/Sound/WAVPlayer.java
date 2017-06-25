package com.wynprice.Sound;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WAVPlayer {
	private Clip originalClip;
	private Clip playingClip;
	
	public WAVPlayer(String location)
	{
		location = "/assets/" + References.MODID + "/sounds/" + location + ".wav";
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
		this.originalClip = clip;
		this.playingClip = clip;
	}
	
	public void start()
	{
		playingClip.start();
	}
	
	public void stop()
	{
		playingClip.stop();
		resetClip();
	}
	
	public void pause()
	{
		int position = playingClip.getFramePosition();
		stop();
		playingClip.setFramePosition(position);
	}
	
	private void resetClip()
	{
		playingClip = originalClip;
	}
	
	private Class<? extends WAVPlayer> c()
	{
		return getClass();
	}
	
	public Boolean isRunning()
	{
		return playingClip.isRunning();
	}
	
	public float getMicrosecondPosition()
	{
		return originalClip.getMicrosecondPosition();
	}

	public int getFramePosition() 
	{
		return playingClip.getFramePosition();
	}

	public void setFramePosition(int framePosition) 
	{
		playingClip.setFramePosition(framePosition);
	}

}

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
	private Clip clip;
	private String name;
	
	public WAVPlayer(String name)
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

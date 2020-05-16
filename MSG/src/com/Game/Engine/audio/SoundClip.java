package com.Game.Engine.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.Game.Engine.GameContainer;

public class SoundClip {
	
	private Clip clip;
	private FloatControl gainControl;
	
	public SoundClip(String path) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(GameContainer.getMainPath() + path));
			
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
														baseFormat.getSampleRate(),
														16,
														baseFormat.getChannels(),
														baseFormat.getChannels()*2,
														baseFormat.getSampleRate(),
														false);
			
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			
			clip = AudioSystem.getClip();
			clip.open(dais);
			
			gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			
			e.printStackTrace();
		}
	}
	
	public void play() {
		if(clip==null) 
			return;
		stop();
		clip.setFramePosition(0);
		while(!clip.isRunning()) {
			clip.start();
		}
		
	}
	public void stop() {
		if(clip.isRunning())
			clip.stop();
	}
	
	public void close() {
		stop();
		clip.drain();
		clip.close();
	}
	/*
	private void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		play();
	}
	*/
	public void setVolume(float value) {
		gainControl.setValue(value);
	}
	
	public boolean isRunning() {
		return clip.isRunning(); 
	}
}

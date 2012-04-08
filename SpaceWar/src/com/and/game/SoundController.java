package com.and.game;

import java.io.IOException;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;

import android.content.Context;

public class SoundController extends andActivity{
	private Sound shootSound;
	private Sound explosionSound;
	private Music bgMusic;
	
	public SoundController(Context c)
	{
		SoundFactory.setAssetBasePath("sounds/");
		MusicFactory.setAssetBasePath("sounds/");
		try {
			shootSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(),
					c, "shoot2.mp3");
			explosionSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(),
					c, "explosion.mp3");
			shootSound.setVolume(0.4f);
			explosionSound.setVolume(1.5f);
			bgMusic = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(),
					c, "bg_music2.mp3");
			bgMusic.setLooping(true);
			bgMusic.setVolume(2.5f);
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}
	public void playShootSound()
	{
		shootSound.play();
	}
	public void playExplosionSound()
	{
		explosionSound.play();
	}
	public void playBGSound()
	{
		bgMusic.play();
	}
	
}

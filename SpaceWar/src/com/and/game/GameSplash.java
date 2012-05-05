package com.and.game;

import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.anddev.andengine.ui.activity.BaseSplashActivity;

import android.app.Activity;


public class GameSplash extends BaseSplashActivity {
    private static final int splash_duration = 6;
    private static final float splash_scale_from = 1.0f;
  
    
	@Override
	protected ScreenOrientation getScreenOrientation() {
		
		return ScreenOrientation.LANDSCAPE;
	}

	@Override
	protected IBitmapTextureAtlasSource onGetSplashTextureAtlasSource() {
		try{
			MusicFactory.setAssetBasePath("sounds/");
			MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "wave.mp3");
			
			  
		}catch(Exception e)
		{
			
		}
		
		return new AssetBitmapTextureAtlasSource(this, "gfx/androidcopy.png");
	}
    @Override
    protected float getSplashScaleFrom()
    {
    	return splash_scale_from;
    }
	@Override
	protected float getSplashDuration() {
		
		return splash_duration;
	}

	@Override
	protected Class<? extends Activity> getFollowUpActivity() {
		
		return MainMenu.class;
	}

}

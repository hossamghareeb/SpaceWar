package com.and.game;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;


public class MainMenu extends BaseGameActivity implements IOnMenuItemClickListener{
    
	
	private Camera mCamera;
    private BitmapTextureAtlas menuTexture;
    private BitmapTextureAtlas fontTexture;
    private TextureRegion BgTextureRegion;
    private Font font;
    private Scene mainScene;
    private MenuScene menuScene;
    private Handler handler;
    static final int MENU_PLAY = 2;
    static final int MENU_About = 3;
    static final int MENU_Exit = 4;
    
	@Override
	public Engine onLoadEngine() {
		final Display display = getWindowManager().getDefaultDisplay();
		mCamera = new Camera(0, 0, display.getWidth(), display.getHeight());
		mEngine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy
				(mCamera.getWidth() , mCamera.getHeight()), mCamera).setNeedsMusic(true).setNeedsSound(true));
		return mEngine;
	}

	@Override
	public void onLoadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		FontFactory.setAssetBasePath("fonts/");
		menuTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTexture,
				this, "bg my Game.jpg",0,0);
		fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		font = FontFactory.createFromAsset(fontTexture, this, "4STAFF__.TTF", 40, true, Color.WHITE);
		mEngine.getTextureManager().loadTextures(menuTexture, fontTexture);
		mEngine.getFontManager().loadFont(font);
	}

	@Override
	public Scene onLoadScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		mainScene = new Scene();
		handler = new Handler();
		createMainMenu();
		final SpriteBackground sbg = new SpriteBackground(new Sprite(0, 0, BgTextureRegion));
		mainScene.setBackground(sbg);
		mainScene.setChildScene(menuScene, false, true, true);
		return mainScene;
	}

	@Override
	public void onLoadComplete() {
		
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_PLAY:
			mainScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
			menuScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
			handler.postDelayed(LaunchGame, 1000);
			return true;
		case MENU_About:
			 return true;
		case MENU_Exit:
			askForExit();
			return true;
		default:
			return false;
		}
		
	}
	public void createMainMenu(){
		menuScene = new MenuScene(mCamera);
		menuScene.setPosition(mCamera.getWidth()/5, mCamera.getHeight()/5);
		
		final IMenuItem playItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_PLAY, font, "Play"), 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		playItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(playItem);
		
		final IMenuItem aboutItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_About, font, "About"), 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		aboutItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(aboutItem);
		
		final IMenuItem exitItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_Exit, font, "Exit"), 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		exitItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(exitItem);
		
		menuScene.setMenuAnimator(new SlideMenuAnimator());
		menuScene.buildAnimations();
		menuScene.setBackgroundEnabled(false);
		menuScene.setOnMenuItemClickListener(this);
	}
	 private Runnable LaunchGame = new Runnable() {
	        public void run() {
	    		Intent myIntent = new Intent(MainMenu.this, andActivity.class);
	    		MainMenu.this.startActivity(myIntent);
	    		finish();
	        }
	     };
	     public void askForExit()
	 	{
	 		new AlertDialog.Builder(this).setTitle("Confirmation !!")
	 		.setMessage("Are you sure you want to Exit?")
	 		.setPositiveButton("Yes", new OnClickListener() {
	 			
	 			@Override
	 			public void onClick(DialogInterface dialog, int which) {
	 				finish();
	 			}
	 		})
	 		.setNegativeButton("No", new OnClickListener() {
	 			
	 			@Override
	 			public void onClick(DialogInterface dialog, int which) {				
	 			}
	 		}).show();
	 		
	 	}
	     @Override
	 	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent)
	 	{
	 		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN)
	 		{
	 			askForExit();
	 			return false;
	 		}
	 		   return super.onKeyDown(pKeyCode, pEvent);
	 	}

}

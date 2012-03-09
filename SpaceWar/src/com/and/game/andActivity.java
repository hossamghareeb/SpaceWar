package com.and.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Display;
import javax.microedition.khronos.opengles.GL10;

public class andActivity extends BaseGameActivity  {
	//private
	private Camera mCamera;
	private Scene mainScene;
    private BitmapTextureAtlas bitmap;
    private TiledTextureRegion playTextureRegion;
    private TextureRegion enemyTextureRegion;
    // controller
    private BitmapTextureAtlas onScreenControlTexture;
    private TextureRegion onScreenControlBase;
    private TextureRegion onScreenControlKnob;    
    private ArrayList<Sprite> targets = new ArrayList<Sprite>();
    private LinkedList enemies;
    private LinkedList enemiesToBeAdded;
    private AnimatedSprite player;
	@Override
	public Engine onLoadEngine() {
		final Display display = getWindowManager().getDefaultDisplay();
		mCamera = new Camera(0, 0, display.getWidth(), display.getHeight());
		
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				         new RatioResolutionPolicy(mCamera.getWidth(), mCamera.getHeight()),
				         mCamera).setNeedsMusic(true).setNeedsSound(true));
	}

	@Override
	public void onLoadResources() {
	
		bitmap = new BitmapTextureAtlas(512, 512, // Resolution
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// for controller
     	onScreenControlTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);    	
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		playTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				bitmap, this, "helicopter.png", 0, 0,2,2);
		playTextureRegion.setFlippedHorizontal(true);
		enemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmap, this, "Target.png", 128, 0);
		///////////////// for controller /////////
		onScreenControlBase = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				onScreenControlTexture, this, "onscreen_control_base.png",0,0);
		onScreenControlKnob = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				onScreenControlTexture, this, "onscreen_control_knob.png",128,0);
		///////////////////////////////////////////////////////////
	    mEngine.getTextureManager().loadTextures(bitmap, onScreenControlTexture);	
	}

	@Override
	public Scene onLoadScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		mainScene = new Scene();
		mainScene.setBackground(new ColorBackground(0.09f, 0.6f, 0.8f));
		int playerX = playTextureRegion.getWidth() / 2;
		int playerY = (int)(mCamera.getHeight() - playTextureRegion.getHeight()) / 2;
		
		player = new AnimatedSprite(playerX, playerY, playTextureRegion);
		player.animate(new long[] { 100, 100 }, 1, 2, true);
		
		////////// controller/////////////////////
		final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(physicsHandler);
		///////////////////////////////////
		
		mainScene.attachChild(player);
		
		///////////////////// controller //////////////
		IAnalogOnScreenControlListener IanalogController= new IAnalogOnScreenControlListener() {
			
			@Override
			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl,
					float pValueX, float pValueY) {	
				physicsHandler.setVelocity(pValueX * 200, pValueY * 200);
				System.out.println(player.getX() +">>>>>>>>>>" + player.getY());
			}
			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
//                player.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(
//                		0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));	
			}
		};
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0f, 
				mCamera.getHeight() - onScreenControlBase.getHeight(), mCamera, 
				onScreenControlBase, onScreenControlKnob, 0.1f, 200, IanalogController);
		analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        analogOnScreenControl.getControlBase().setAlpha(0.5f);
        analogOnScreenControl.getControlBase().setScaleCenter(0, 64);
        analogOnScreenControl.getControlBase().setScale(1f);
        analogOnScreenControl.getControlKnob().setScale(1f);
        analogOnScreenControl.refreshControlKnobPosition();

        mainScene.setChildScene(analogOnScreenControl);

		/////////////////////////////////////////////
		enemies = new LinkedList();
		enemiesToBeAdded = new LinkedList();
		createEnemiesTimeHandler(); // create random enemies 
		return mainScene;
	}

	@Override
	public void onLoadComplete() {
		
		
	}
    public void AddEnemey()
    {
    	Random rand = new Random();
    	int x = (int)mCamera.getWidth() + enemyTextureRegion.getWidth();
    	int minY = enemyTextureRegion.getHeight();
    	int maxY = (int)mCamera.getHeight() - minY;
    	int rangY = maxY - minY;
    	int y = rand.nextInt(rangY) + minY; //  random y in range
    	Sprite target = new Sprite(x, y, enemyTextureRegion.deepCopy());
    	mainScene.attachChild(target);
    	int minSpeed = 2;
    	int maxSpeed = 4;
    	int speedNow = rand.nextInt(maxSpeed - minSpeed)+minSpeed;
    	MoveXModifier mov = new MoveXModifier(speedNow, target.getX(), -target.getHeight());
    	target.registerEntityModifier(mov.deepCopy());
    	enemiesToBeAdded.add(target);
    	
    }
    // create new enemies periodically
    public void createEnemiesTimeHandler()
    {
    	TimerHandler spriteTimesHandler;
    	float delay = 1f;
    	spriteTimesHandler = new TimerHandler(delay, true,new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				AddEnemey();
				
			}
		});
    	getEngine().registerUpdateHandler(spriteTimesHandler);
    }
    public void deleteEnemy(final Sprite sprite, Iterator it)
    {
    	// run it in different thread
    	runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
			 mainScene.detachChild(sprite);
				
			}
		});
    	it.remove();
    }
  
}
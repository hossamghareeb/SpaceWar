package com.and.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.IUpdateHandler;
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
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Toast;

import javax.microedition.khronos.opengles.GL10;

public class andActivity extends BaseGameActivity implements IOnMenuItemClickListener{
	//private
	private Camera mCamera;
	private Scene mainScene;
    private BitmapTextureAtlas bitmap;
    private TiledTextureRegion playTextureRegion;
    private TiledTextureRegion fireTextureRegion;
    private TiledTextureRegion playerExplosionTextureRegion;
    private TextureRegion enemyTextureRegion;
   
    ////pausing
	 private MenuScene menuScene;
     private BitmapTextureAtlas menuTexture;
     private TextureRegion menuResumeTextureRegion;
     private TextureRegion menuRestartTextureRegion;
     private TextureRegion menuWeaponsTextureRegion;
     private TextureRegion menuSettingTextureRegion;
     private TextureRegion menuMainTextureRegion;
     private AlertDialog alertSound;
    private boolean playSound = true;
    private boolean selectedSound = true;
    private boolean paused = false;
    /////////////
    // controller
    private BitmapTextureAtlas onScreenControlTexture;
    private BitmapTextureAtlas fireControlTexture;
    private BitmapTextureAtlas bombTexture;
    private TextureRegion onScreenControlBase;
    private TextureRegion onScreenControlKnob;
    private TextureRegion xBtnTexture;
    private TextureRegion trBtnTexture;
    private TextureRegion oBtnTexture;
    private TextureRegion sqBtnTexture;
    private TextureRegion bulletTexture;
    private TextureRegion BgTextureRegion;
    private AnalogOnScreenControl analogOnScreenControl;
    //score
    private BitmapTextureAtlas fontTexture;
    private Font font;
    private ChangeableText score;
    private ChangeableText levelNumText;
    private AnimatedSprite imgLefts;
    private ChangeableText leftsText;
    private int lefts = 3;
    private int level = 1;
    private Sprite startIcon;
    private TextureRegion startIconTexture;
    private float currentScore = 0.0f;
    //////////////
    private LinkedList enemies;
    private LinkedList enemiesToBeAdded;
    private LinkedList bullets;
    private LinkedList bulletsToBeAdded;
    private AnimatedSprite player;
    private AnimatedSprite bomb;
    private AnimatedSprite bombPlayer;
    private Sprite SBG;
	private SpriteBackground BG;
	
	private Sound shootSound;
	private Sound explosionSound;
	private Music bgMusic;
    
    private Sprite xBtn, trBtn, oBtn, sqBtn;
    private boolean isDead = false;
    
    static int   currentSpriteX = 0;
    static int   currentSpriteY = 0;
    static float currentBulletX = 0;
    static float currentBulletY = 0;
    // the pool
    private SpritesPool SpritePool;
    private BulletsPool BulletPool;
	@Override
	public Engine onLoadEngine() {
		final Display display = getWindowManager().getDefaultDisplay();
		mCamera = new Camera(0, 0, display.getWidth(), display.getHeight());
		Engine engine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
		         new RatioResolutionPolicy(mCamera.getWidth(), mCamera.getHeight()),
		         mCamera).setNeedsMusic(true).setNeedsSound(true));
		try {
			if(MultiTouch.isSupported(this)) {
				engine.setTouchController(new MultiTouchController());
				if(MultiTouch.isSupportedDistinct(this)) {
					Toast.makeText(this, "MultiTouch detected --> Drag multiple Sprites with multiple fingers!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, "MultiTouch detected --> Drag multiple Sprites with multiple fingers!\n\n(Your device might have problems to distinguish between separate fingers.)", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)", Toast.LENGTH_LONG).show();
			}
		} catch (final MultiTouchException e) {
			Toast.makeText(this, "Sorry your Android Version does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)", Toast.LENGTH_LONG).show();
		}
		return engine;
	}
	@Override
	public void onLoadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		bitmap = new BitmapTextureAtlas(1024, 1024, // Resolution
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		////////////Pause buttons/////
		menuTexture = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		menuResumeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset
		                          (menuTexture, this, "resume.png", 0, 0);
		menuRestartTextureRegion =  BitmapTextureAtlasTextureRegionFactory.createFromAsset
        						  (menuTexture, this, "restart.png", 0, 70);
		menuWeaponsTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset
		  						   (menuTexture, this, "weapons.png", 0, 140);
		menuSettingTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset
		   						   (menuTexture, this, "setting.png", 0, 210);
		menuMainTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset
		   (menuTexture, this, "main.png", 0, 280);
		////////////////
		// for controller		
     	onScreenControlTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
     	fireControlTexture  = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
     	bombTexture  = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
     	fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
     	FontFactory.setAssetBasePath("fonts/");
		font = FontFactory.createFromAsset(fontTexture, this, "4STAFF__.TTF", 30, true, Color.BLACK);
		playTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				bitmap, this, "helicopter.png", 0, 0,2,2);
		fireTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				bombTexture, this, "explosion.png", 0, 0,5,5);
		playerExplosionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				bombTexture, this, "player-explosion.png", 512, 512,4,4);
		playTextureRegion.setFlippedHorizontal(true);	
		enemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmap, this, "Target.png", 128, 0);
		bulletTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset
		(bitmap, this, "Projectile.png",256,0);
		BgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmap,
				this, "city.jpg",512,0);
		/////the pool
		SpritePool = new SpritesPool(enemyTextureRegion);
		BulletPool = new BulletsPool(bulletTexture);
		
		///////////////// for controller /////////
		onScreenControlBase = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				onScreenControlTexture, this, "onscreen_control_base.png",0,0);
		onScreenControlKnob = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				onScreenControlTexture, this, "onscreen_control_knob.png",128,0);
	xBtnTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				fireControlTexture, this, "xPS.png",0,0);
	oBtnTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
			fireControlTexture, this, "oPS.png",128,0);
	trBtnTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
			fireControlTexture, this, "trPS.png",256,0);
	sqBtnTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
			fireControlTexture, this, "squarePS.png",384,0);
	startIconTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(fireControlTexture, this, "start.png",512,0);
		///////////////////////////////////////////////////////////
	//////create sounds
	 CreateSounds();
	 CreateBGMusic();
     mEngine.getTextureManager().loadTextures(bitmap, onScreenControlTexture,
    		 fireControlTexture,bombTexture, fontTexture,menuTexture);
     mEngine.getFontManager().loadFont(font);
	}

	@Override
	public Scene onLoadScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		mainScene = new Scene();
		SBG = new Sprite(0, 0, mCamera.getWidth(),mCamera.getHeight(),
				BgTextureRegion);
		BG = new SpriteBackground(SBG);
		mainScene.setBackground(BG);
		AddThePlayer();
		createToolbar();
		createMenuScene();
		final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(physicsHandler);
		bombPlayer = new AnimatedSprite(0, 0, playerExplosionTextureRegion.deepCopy()); // first position and will be set in collision
		mainScene.attachChild(bombPlayer);
		bombPlayer.setVisible(false);
		bomb = new AnimatedSprite(0, 0, fireTextureRegion.deepCopy()); // first position and will be set in collision
		mainScene.attachChild(bomb);
		bomb.setVisible(false);
		AddTheFireButtons();
		AddTheAnalogControler(physicsHandler);
		createEnemiesTimeHandler(); // create random enemies 
		mainScene.registerUpdateHandler(detectSpriteOutOfScreen); // detect when outside
		this.mainScene.setTouchAreaBindingEnabled(true);
		bgMusic.play();
		return mainScene;
	}
	public void createToolbar()
	{
		imgLefts = new AnimatedSprite(mCamera.getWidth()/2 - 15, 5, 32, 32, playTextureRegion.deepCopy());
		imgLefts.animate(new long[] { 10,10}, 1, 2, false);
		imgLefts.stopAnimation();
		mainScene.attachChild(imgLefts);
		leftsText = new ChangeableText(imgLefts.getX() + 20, 5, font, " x " + lefts);
		mainScene.attachChild(leftsText);
		score = new ChangeableText(0, 5, font, "Score : "+currentScore);
		score.setPosition(mCamera.getWidth() - score.getWidth() - 40, 5);
		levelNumText = new ChangeableText(3, 5, font, "Level " + level);		
		startIcon = new Sprite(levelNumText.getWidth() + 25, 5, 40, 40,startIconTexture ){
			
		      @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
          		  final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {     	  
                         
                    }
            });
                    return false;
            }
		};;
		
		mainScene.registerTouchArea(startIcon);
		
		mainScene.attachChild(startIcon);
		mainScene.attachChild(score);
		mainScene.attachChild(levelNumText);
	}

	@Override
	public void onLoadComplete() {
		
		
	}
	
	public void CreateSounds()
	{
		SoundFactory.setAssetBasePath("sounds/");
		try {
			shootSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(),
					this, "shoot2.mp3");
			explosionSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(),
					this, "explosion.mp3");
			shootSound.setVolume(0.4f);
			explosionSound.setVolume(1.5f);
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}
	public void CreateBGMusic()
	{
		MusicFactory.setAssetBasePath("sounds/");
		try {
			bgMusic = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(),
					this, "bg_music2.mp3");
			bgMusic.setLooping(true);
			bgMusic.setVolume(2.5f);
		} catch (IllegalStateException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
	
			e.printStackTrace();
		}
		
	}
    public void AddEnemey()
    {
    	Random rand = new Random();
    	int x = (int)mCamera.getWidth() + enemyTextureRegion.getWidth();
    	int minY = enemyTextureRegion.getHeight();
    	int maxY = (int)mCamera.getHeight() - minY;
    	int rangY = maxY - minY;
    	int y = rand.nextInt(rangY) + minY; //  random y in range
    	currentSpriteX = x;
    	currentSpriteY = y;
    	Sprite target = SpritePool.obtainPoolItem();  // get Sprite from the pool
    	target.setPosition(currentSpriteX,currentSpriteY);
    	mainScene.attachChild(target);
    	int minSpeed = 2;
    	int maxSpeed = 4;
    	int speedNow = rand.nextInt(maxSpeed - minSpeed)+minSpeed;
    	MoveXModifier mov = new MoveXModifier(speedNow, target.getX(), -target.getHeight());
    	target.registerEntityModifier(mov.deepCopy());
   
    	enemiesToBeAdded.add(target);
    	
    }
    public void removeSprite(Sprite toRemoved, Iterator it)
    {
    	SpritePool.recyclePoolItem(toRemoved);
		it.remove();
    }
    public void removeBullet(Sprite toRemoved, Iterator it)
    {
    	BulletPool.recyclePoolItem(toRemoved);
		it.remove();
    }
    
    public void AddBullet()
    {
    	if (!Cool.shareCool().checkValidity()) 
    		    return;

    	currentBulletX = player.getX() + player.getWidth();
    	currentBulletY = player.getY() + player.getHeight() / 2;
    	
    	Sprite bullet = BulletPool.obtainPoolItem();
    	bullet.setPosition(currentBulletX, currentBulletY);
    	
    	  if(playSound)shootSound.play();
    	mainScene.attachChild(bullet);
    	int speed = 3;
    	MoveXModifier mov = new MoveXModifier(speed, bullet.getX(), mCamera.getWidth());
    	bullet.registerEntityModifier(mov.deepCopy());
    	bulletsToBeAdded.add(bullet);
    }
    // create new enemies periodically
    public void createEnemiesTimeHandler()
    {
    	enemies = new LinkedList();
		enemiesToBeAdded = new LinkedList();
    	TimerHandler spriteTimesHandler;
    	float delay = 1f;
    	spriteTimesHandler = new TimerHandler(delay, true,new ITimerCallback() {	
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if(!paused)
				   AddEnemey();
			}
		});
    	getEngine().registerUpdateHandler(spriteTimesHandler);
    }
    public void effectOfCollision(Sprite toRemoved, Iterator it)
    {
    	addBomb(toRemoved.getX(), toRemoved.getY());
		if(playSound)explosionSound.play();
		removeBullet(toRemoved, it);
		currentScore += 10.0;
		score.setText("Score : "+ currentScore);
    	
    }
    // detect when Sprite gets out of screen (or collision detection)
    IUpdateHandler detectSpriteOutOfScreen = new IUpdateHandler() {
		
		@Override
		public void reset() {
		}
		@Override
		public void onUpdate(float pSecondsElapsed) {
			
			Iterator<Sprite> it = enemies.iterator();
			Sprite enemy;
			boolean hit = false;
			while(it.hasNext() && !isDead )
			{
				enemy = it.next(); // enemy to be deleted of it is out of screen
				if(enemy.getX() <= -enemy.getWidth())
				{
					removeSprite(enemy, it);
					break;
				}
				if(enemy.collidesWith(player) )
				{
					player.setVisible(false);
					isDead = true;
					addBombPlayer(player.getX(), player.getY());
					if(playSound)explosionSound.play();
					
					break;
				}
				Iterator<Sprite> bulletIt = bullets.iterator();
				Sprite bullet;
				while(bulletIt.hasNext())
				{
					bullet = bulletIt.next();
					if(bullet.getX() >= mCamera.getWidth())
					{
						removeBullet(bullet, bulletIt);
						continue;
					}
					if(enemy.collidesWith(bullet))
					{
						effectOfCollision(bullet, bulletIt);
						hit = true;
						break;
					}
				}
				if(hit)
				{
					removeSprite(enemy, it);
					hit = false;
				}	
			}
			bullets.addAll(bulletsToBeAdded);
			bulletsToBeAdded.clear();
			enemies.addAll(enemiesToBeAdded);
			enemiesToBeAdded.clear();	
			
		}
	};

	public void AddTheAnalogControler(final PhysicsHandler physicsHandler)
	{
			IAnalogOnScreenControlListener IanalogController= new IAnalogOnScreenControlListener() {
			
			@Override
			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl,
					float pValueX, float pValueY) {

			    	
					if(pValueX < 0 && player.getX() <= 5)
					{
						pValueX = 0;
					}
					if(pValueY < 0 && player.getY() <= 5)
					{
						pValueY = 0;
					}
					if(pValueY > 0 && player.getY() >= mCamera.getHeight()- player.getHeight()-5)
					{
						pValueY = 0;
					}
					if(pValueX > 0 && player.getX() >= mCamera.getWidth()- player.getWidth()-5)
					{
						pValueX = 0;
					}
					physicsHandler.setVelocity(pValueX * 200, pValueY * 200);
			   
			}
			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
	
			}
		};
		 analogOnScreenControl = new AnalogOnScreenControl(0f, 
				mCamera.getHeight() - onScreenControlBase.getHeight(), mCamera, 
		onScreenControlBase, onScreenControlKnob, 0.1f, 200, IanalogController);
		analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        analogOnScreenControl.getControlBase().setAlpha(0.5f);
        analogOnScreenControl.getControlBase().setScaleCenter(0, 64);
        analogOnScreenControl.getControlBase().setScale(1f);
        analogOnScreenControl.getControlKnob().setScale(1f);
        analogOnScreenControl.refreshControlKnobPosition();

        mainScene.setChildScene(analogOnScreenControl);
	}
	
	public void addBomb(float x, float y)
	{
		
		bomb.setVisible(true);
		bomb.setPosition(x, y);
		bomb.animate(new long[] { 50, 50, 50, 50, 50
								 ,50, 50, 50, 50, 50
								 ,50, 50, 50, 50, 50
								 ,50, 50, 50, 50, 50
								 ,50, 50, 50, 50, 50}, 1, 25, 0, new IAnimationListener() {	
									@Override
									public void onAnimationEnd(AnimatedSprite pAnimatedSprite) {
										bomb.setVisible(false);					
									}
								});
		
	}
	public void addBombPlayer(float x, float y)
	{
//		bombPlayer = new AnimatedSprite(x, y, playerExplosionTextureRegion.deepCopy()); // first position and will be set in collision
		bombPlayer.setVisible(true);
		bombPlayer.setPosition(x, y);
		bombPlayer.animate(new long[] { 50, 50, 50, 50
								 ,50, 50, 50, 50
								 ,50, 50, 50, 50
								 ,50, 50, 50, 50 
								 }, 1, 16, 0, new IAnimationListener() {	
									@Override
									public void onAnimationEnd(AnimatedSprite pAnimatedSprite) {
										bombPlayer.setVisible(false);
										float playerX = playTextureRegion.getWidth() / 2;
										float playerY = (int)(mCamera.getHeight() - playTextureRegion.getHeight()) / 2;
										player.setPosition(playerX,playerY);
										isDead = false;
										player.setVisible(true);
									}
								});
		
	}
	public void AddThePlayer()
	{
		bullets = new LinkedList();
		bulletsToBeAdded = new LinkedList();
		
		int playerX = playTextureRegion.getWidth() / 2;
		int playerY = (int)(mCamera.getHeight() - playTextureRegion.getHeight()) / 2;
		player = new AnimatedSprite(playerX, playerY, playTextureRegion);
		player.animate(new long[] { 100, 100 }, 1, 2, true);
		final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(physicsHandler);		
		mainScene.attachChild(player);
	}
	public void AddTheFireButtons()
	{		
		AddTheXButton();
		AddTheOButton();
		AddTheTrButton();
		AddTheSquareButton();
		mainScene.setTouchAreaBindingEnabled(true);
	}
	public void AddTheXButton()
	{
		xBtn = new Sprite(mCamera.getWidth()-2*xBtnTexture.getWidth(),
				mCamera.getHeight()-xBtnTexture.getHeight(), xBtnTexture){
		      @Override
              public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
            		  final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                      runOnUpdateThread(new Runnable() {
                      @Override
                      public void run() {
                    	 if(!isDead )
                           AddBullet();
                      }
              });
                      return false;
              }
			
		};
		mainScene.registerTouchArea(xBtn);
		mainScene.attachChild(xBtn);
	}
	public void AddTheOButton()
	{
		oBtn = new Sprite(mCamera.getWidth()-xBtnTexture.getWidth(),
				mCamera.getHeight()-1.5f*xBtnTexture.getHeight(), oBtnTexture){
			
		      @Override
              public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
            		  final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                      runOnUpdateThread(new Runnable() {
                      @Override
                      public void run() {     	  
                           player.stopAnimation();
                      }
              });
                      return false;
              }
		};
		mainScene.registerTouchArea(oBtn);
	    mainScene.attachChild(oBtn);
	}
	public void AddTheTrButton()
	{
		trBtn = new Sprite(mCamera.getWidth()-2*xBtnTexture.getWidth(),
				mCamera.getHeight()-2f*xBtnTexture.getHeight(), trBtnTexture){
			
		      @Override
              public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
            		  final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                      runOnUpdateThread(new Runnable() {
                      @Override
                      public void run() {     	  
                           player.stopAnimation();
                      }
              });
                      return false;
              }
		};
		mainScene.registerTouchArea(trBtn);
	    mainScene.attachChild(trBtn);	
	}
	public void AddTheSquareButton()
	{
		sqBtn = new Sprite(mCamera.getWidth()-3*xBtnTexture.getWidth(),
				mCamera.getHeight()-1.5f*xBtnTexture.getHeight(), sqBtnTexture){
			
		      @Override
              public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
            		  final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                      runOnUpdateThread(new Runnable() {
                      @Override
                      public void run() {     	  
                           player.stopAnimation();
                      }
              });
                      return false;
              }
		};
		mainScene.registerTouchArea(sqBtn);
	    mainScene.attachChild(sqBtn);
		
	}
	/////////////////////////////////////// Pause Scene/////////////////////
	public void createMenuScene()
	{
		menuScene = new MenuScene(mCamera);
		final SpriteMenuItem resumeMenuItem = new SpriteMenuItem(GameConstants.MENU_RESUME, menuResumeTextureRegion);
		resumeMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(resumeMenuItem);
		final SpriteMenuItem restartMenuItem = new SpriteMenuItem(GameConstants.MENU_RESTART, menuRestartTextureRegion);
		restartMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);		
		menuScene.addMenuItem(restartMenuItem);
		final SpriteMenuItem weaponsMenuItem = new SpriteMenuItem(GameConstants.MENU_WEAPONS, menuWeaponsTextureRegion);
		restartMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);		
		menuScene.addMenuItem(weaponsMenuItem);
		final SpriteMenuItem settingMenuItem = new SpriteMenuItem(GameConstants.MENU_SETTING, menuSettingTextureRegion);
		restartMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);		
		menuScene.addMenuItem(settingMenuItem);
		final SpriteMenuItem mainMenuItem = new SpriteMenuItem(GameConstants.MENU_MAIN, menuMainTextureRegion);
		restartMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);		
		menuScene.addMenuItem(mainMenuItem);
		menuScene.buildAnimations();
		menuScene.setBackgroundEnabled(false);
		menuScene.setOnMenuItemClickListener(this);
	}

	
	public void pauseGame()
	{
		paused = true;
		mainScene.setChildScene(menuScene, false, true, true);
		pauseMusic();
		
	}
	public void unpauseGame()
	{
		paused = false;
		resumeMusic();
		mainScene.clearChildScene();
		mainScene.setChildScene(analogOnScreenControl);
		menuScene.reset();
	}
	public void pauseMusic()
	{
		if(bgMusic.isPlaying())
			bgMusic.pause();
	}
	public void resumeMusic()
	{
		if(!selectedSound)
		{
			bgMusic.pause();
			playSound = selectedSound;
			return;
		}
		
		if(playSound)
			bgMusic.resume();
		else
			bgMusic.play();
		
		playSound = selectedSound;
		
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent)
	{
		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN)
		{
			return false;
			
		}
		if((pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) )
		{
			if(menuScene.hasParent())
			{
				menuScene.back();
				mainScene.setChildScene(analogOnScreenControl);
				
			}
			else{
				pauseGame();
				
				}
			return true;
		}
		else
		   return super.onKeyDown(pKeyCode, pEvent);
	}
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case GameConstants.MENU_RESUME:
				unpauseGame();
				return true;
			case GameConstants.MENU_MAIN:
				askForExit();
				return true;
			case GameConstants.MENU_SETTING:
				 showAlertSound();
				 return true;
			default:
				return false;
		}
	}
	public void showAlertSound()
	{
		final CharSequence[] options = {"Sound ON", "Sound OFF"};
		AlertDialog.Builder alt = new AlertDialog.Builder(this);
		alt.setIcon(R.drawable.sound);
		alt.setTitle("Sound Setting");
		int i;
		if(playSound) i = 0; else i = 1; 
		alt.setSingleChoiceItems(options, i, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int item) {
			    if(item == 0) selectedSound = true; 
			    else
			    	selectedSound = false;
			    	
			 alertSound.cancel();   
			    	
			}
		});
		alertSound = alt.create();
		alertSound.show();
	}
	
	public void askForExit()
	{
		new AlertDialog.Builder(this).setTitle("Exit !!")
		.setMessage("Are you sure you wanna Exit?")
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
	
	/////////////////////////////////////////////////////////////////////////
  
}
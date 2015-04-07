package com.jawnnypoo.zed.game;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.jawnnypoo.zed.R;
import com.jawnnypoo.zed.graphics.BufferManager;
import com.jawnnypoo.zed.graphics.GLSurfaceView;
import com.jawnnypoo.zed.graphics.Texture;
import com.jawnnypoo.zed.graphics.TextureManager;
import com.jawnnypoo.zed.graphics.ZEDRenderer;
import com.jawnnypoo.zed.objects.BackgroundObject;
import com.jawnnypoo.zed.objects.ObjectManager;
import com.jawnnypoo.zed.objects.SpawnManager;
import com.jawnnypoo.zed.tools.SoundManager;
import com.jawnnypoo.zed.tools.UIManager;


/**
 * Holds all the stuff we need for a game, the renderer,
 * the object manager, etc! Contains a lot of information relating to
 * the game. Utilize it by using the sGame Singleton object once it has been bootstrapped.
 * @author Jawn
 *
 */
public class Game {
	private static final String TAG = Game.class.getSimpleName();
	
	//Singleton Game object we will use to control logic
	public static Game sGame;
	
	private boolean mRunning = false;
	private boolean mBootstrapComplete = false;
	private ObjectManager mObjectManager;
	private TextureManager mTextureManager;
	private SpawnManager mSpawnManager;
	private SoundManager mSoundSystem;
	private BufferManager mBufferManager;
	private GLSurfaceView mSurfaceView;
	private ZEDRenderer mRenderer;
	private int mGameWidth;
	private int mGameHeight;
	//Threading for game logic
	private GameThread mGameThread;
	//Same as GameThread, just cast to a normal thread so we can start it properly
	private Thread mGame;
	
	public Game() {
		mRunning = false;
		mBootstrapComplete = false;
	}
	
	public static void initGame() {
		sGame = new Game();
	}
	
	/**
	 * Create and register the core game objects that we need to run the game properly 
	 * @param context
	 * @param widthPixels
	 * @param heightPixels
	 * @param gameWidth
	 * @param gameHeight
	 */
	public void bootstrap(Context context, int widthPixels, int heightPixels, int gameWidth, int gameHeight) {
		if (!mBootstrapComplete) {

			//Create the renderer that will draw for us
			mRenderer = new ZEDRenderer(context, gameWidth, gameHeight);
			
			mGameWidth = gameWidth;
			mGameHeight = gameHeight;
			//Create all of the Singleton managers that will help us manage the game
			mObjectManager = new ObjectManager();
			mTextureManager = new TextureManager();
			mBufferManager = new BufferManager();
			mSoundSystem = new SoundManager(context);
			mSpawnManager = new SpawnManager();
			
			
			//Load in the textures and store their references in the object manager
			//NOTE: if you were to have multiple levels, it would be smart to only
			//load in the textures that you are going to use for that level, and deallocate
			//the ones you no longer need
			allocateTextures();
			allocateSounds();
			placeBaseObjects();

			//Update the player health on the UI
			UIManager.updatePlayerHealth();
			
			
			//Start up the game thread to run logic like physics and collision detection
			//Set to run at 60FPS
			mGameThread = new GameThread();
			start();
			
			mBootstrapComplete = true;
		}
		
	}

	private void allocateSounds() {
		mSoundSystem.load(R.raw.explosion);
		mSoundSystem.load(R.raw.hurt);
	}

	private void allocateTextures() {
		//Create a texture arraylist and pass it off into the object manager so that it can easily create the object with the right textures
		//You should load all the textures in here, even if you aren't going to use them immediately 
		ArrayList<Texture> textures = getTexturesFromIntIds(R.drawable.android_flying_1, R.drawable.android_flying_2,
				R.drawable.android_flying_3, R.drawable.android_flying_4, R.drawable.android_flying_5, R.drawable.android_flying_6, 
				R.drawable.android_flying_7, R.drawable.android_flying_7, R.drawable.android_flying_8, R.drawable.android_flying_9, R.drawable.android_flying_10);
		mObjectManager.putTextureInMap("Player", textures);
		
		textures = getTexturesFromIntIds(R.drawable.kitkatmissle1, R.drawable.kitkatmissle2, R.drawable.kitkatmissle3, R.drawable.kitkatmissle4);
		mObjectManager.putTextureInMap("Bullet", textures);
		
		textures = getTexturesFromIntIds(R.drawable.worm_1, R.drawable.worm_2, R.drawable.worm_3, R.drawable.worm_4);
		mObjectManager.putTextureInMap("Enemy", textures);
		
		textures = getTexturesFromIntIds(R.drawable.background_tile);
		mObjectManager.putTextureInMap("Background", textures);
		
		textures = getTexturesFromIntIds(R.drawable.androidshot1, R.drawable.androidshot2, R.drawable.androidshot3, R.drawable.androidshot4
				, R.drawable.androidshot5, R.drawable.androidshot6, R.drawable.androidshot7, R.drawable.androidshot8, R.drawable.androidshot9
				, R.drawable.androidshot10, R.drawable.androidshot11, R.drawable.androidshot12, R.drawable.androidshot13, R.drawable.androidshot14
				, R.drawable.androidshot15, R.drawable.androidshot16, R.drawable.androidshot17, R.drawable.androidshot18, R.drawable.androidshot19
				, R.drawable.androidshot20);
		mObjectManager.putTextureInMap("Spark", textures);
		
		textures = getTexturesFromIntIds(R.drawable.ship_explosion0, R.drawable.ship_explosion1, R.drawable.ship_explosion2, R.drawable.ship_explosion3
				, R.drawable.ship_explosion4, R.drawable.ship_explosion6, R.drawable.ship_explosion7, R.drawable.ship_explosion8, R.drawable.ship_explosion9
				, R.drawable.ship_explosion10, R.drawable.ship_explosion11, R.drawable.ship_explosion12, R.drawable.ship_explosion13, R.drawable.ship_explosion14
				, R.drawable.ship_explosion15, R.drawable.ship_explosion16, R.drawable.ship_explosion17, R.drawable.ship_explosion18, R.drawable.ship_explosion19
				, R.drawable.ship_explosion20, R.drawable.ship_explosion21, R.drawable.ship_explosion22, R.drawable.ship_explosion23);
		mObjectManager.putTextureInMap("Explosion", textures);
		
		textures = getTexturesFromIntIds(R.drawable.appleshot1, R.drawable.appleshot2, R.drawable.appleshot3, R.drawable.appleshot4
				, R.drawable.appleshot5, R.drawable.appleshot6, R.drawable.appleshot7, R.drawable.appleshot8, R.drawable.appleshot9
				, R.drawable.appleshot10, R.drawable.appleshot11, R.drawable.appleshot12, R.drawable.appleshot13, R.drawable.appleshot14
				, R.drawable.appleshot15, R.drawable.appleshot16, R.drawable.appleshot17, R.drawable.appleshot18, R.drawable.appleshot19
				, R.drawable.appleshot20);
		mObjectManager.putTextureInMap("EnemySpark", textures);
		
	}
	
	private void placeBaseObjects() {
		mObjectManager.createPlayerObject(100, 100, 256, 256, "Player", 50);
		//Two background objects pass behind the player, giving the illusion of moving
		mObjectManager.createBackground(0, 0, 1280, 1280, "Background", 0, BackgroundObject.BACKGROUND_X_VELOCITY, 0);
		mObjectManager.createBackground(1280, 0, 1280, 1280, "Background", 0, BackgroundObject.BACKGROUND_X_VELOCITY, 0);
	}

	/** 
	 * Looks odd, but this method basically takes as many resource IDs as specified and 
	 * allocates all of them to textures.
	 * @param resourceIds
	 * @return
	 */
	private ArrayList<Texture> getTexturesFromIntIds(Integer... resourceIds) {
		ArrayList<Texture> textures = new ArrayList<Texture>();
		for (Integer res : resourceIds) {
			textures.add(mTextureManager.allocateTexture(res));
		}
		return textures;
	}

	public void setSurfaceView(GLSurfaceView surface) {
		mSurfaceView = surface;
	}
	
	public void onSurfaceCreated() {
		mSurfaceView.loadTextures(mTextureManager);
		//mSurfaceView.loadBuffers(mBufferManager);
	}
	
	public void onSurfaceLost() {
		mTextureManager.invalidateAll();
		mBufferManager.invalidateHardwareBuffers();
		
	}
	
	public void onSurfaceReady() {
		if (mGameThread.getPaused()) {
			mGameThread.resumeGame();
		}
	}
	
	public void onPause() {
		mGameThread.pauseGame();
		mSoundSystem.pauseAll();
	}
	
	public void onResume(Context context) {
		if (mRunning) {
			mGameThread.resumeGame();
		}
		mRenderer.setContext(context);
		mSoundSystem.resumeBackground();
		
	}
	
	/** Start the game properly */
    public void start() {
        if (!mRunning) {
            // Now's a good time to run the GC.
            Runtime r = Runtime.getRuntime();
            r.gc();
            Log.d(TAG, "Starting the game!");
            mGame = new Thread(mGameThread);
            mGame.setName("Game");
            mGame.start();
            mRunning = true;
        } else {
            mGameThread.resumeGame();
        }
    }
    
    public void stop() {
    	if (mRunning) {
    		if (mGameThread.getPaused()) {
    			mGameThread.resumeGame();
    		}
    		mGameThread.stopThread();
    		try {
    			mGame.join();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		mRunning = false;
    	}
    	
    	UIManager.resetScore();
    	mSoundSystem.stopAll();
    	mSoundSystem.stopBackground();
    }
	
	public TextureManager getTextureManager() {
		return mTextureManager;
	}
	
	public ObjectManager getObjectManager() {
		return mObjectManager;
	}
	
	public SpawnManager getSpawnManager() {
		return mSpawnManager;
	}
	
	public SoundManager getSoundManager() {
		return mSoundSystem;
	}
	
	public ZEDRenderer getRenderer() {
		return mRenderer;
	}
	
	public int getGameWidth() {
		return mGameWidth;
	}
	
	public int getGameHeight() {
		return mGameHeight;
	}

	public void reset() {
		mObjectManager.reset();
		mSpawnManager.reset();
		UIManager.resetScore();
		placeBaseObjects();
		UIManager.resetScore();
		UIManager.updatePlayerHealth();
		UIManager.updateScoreUI();
	}
	
}

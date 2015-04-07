package com.jawnnypoo.zed.objects;


import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

import android.os.SystemClock;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.GraphicsManager;
import com.jawnnypoo.zed.graphics.Texture;
import com.jawnnypoo.zed.physics.Vector2;


/***
 * A base class for all game objects, which should derive from this class.
 * @author Jawn
 *
 */
public class GameObject {

	protected ArrayList<Texture> mTextures;
	protected String mName = "";
	protected int mNumTextures = 0;
	public Vector2 mPosition;
	protected float mWidth;
	protected float mHeight;
	protected Vector2 mVelocity;
	protected float mViewWidth;
	protected float mViewHeight;
	protected float mOpacity = 1.0f;
	protected int[] mCrop;
	//Related to animation
	protected int mCurrentFrame = 0;
	protected int mNumFrames = 0;
	protected long mLastFrameChangeTime = 0;
	protected boolean mHasCompletedAnimation = false;
	//Amount of time to spend on each frame in milliseconds
	protected long mFrameTime = 0;
	protected boolean mLoopAnimation = true;
	
	protected boolean mIsAnimated = false;
	protected boolean mHasAI = false;
	protected boolean mIsTimeSensitive = false;
	protected boolean mCanMove = false;
	protected long mBirthTime = 0;
	//Life time, specified in milliseconds
	protected int mLifeTime = 0;
	protected float mHealth = 0;
	
	/**
	 * BEWARE The constructor for GameObject should not be called directly. Instead, 
	 * call the create function inside of the ObjectManager, so that your object manager
	 * will have a reference to the object you have created, and will be able to draw it
	 * and perform other necessary steps.
	 */
	public GameObject(float x, float y, int width, int height, String name, ArrayList<Texture> texture, long frameTime) {
		mWidth = width;
		mHeight = height;
		mTextures = texture;
		mNumFrames = texture.size();
		if (mTextures.size() > 1) {
			mIsAnimated = true;
		}
		mCrop = new int[4];
		mPosition = new Vector2(x, y);
		mVelocity = new Vector2(0,0);
        setCrop(0, height, width, height);
        mFrameTime = frameTime;
        mName = name;
        mBirthTime = System.currentTimeMillis();
	}
	
	//The following functions are protected so that the other class's in the packages can access the 
	//methods (specifically, the objectmanager)
	
	/**
	 * The function to draw the game object, called by the object manager
	 * @param x
	 * @param y
	 * @param scaledX
	 * @param scaledY
	 */
	protected void draw(float x, float y, float scaledX, float scaledY) {
		GL10 gl = GraphicsManager.getGL();
        //final Texture texture = mTextures[mAnimationIndex];
		final Texture texture = mTextures.get(mCurrentFrame);
        if (gl != null && texture != null) {
            assert texture.loaded;
            
            final float snappedX = (int) x;
            final float snappedY = (int) y;
                 
            final float opacity = mOpacity;
            final float width = mWidth;
            final float height = mHeight;
            final float viewWidth = mViewWidth;
            final float viewHeight = mViewHeight;
            
            boolean cull = false;
            //check to see if the gameobject is outside of the viewport. If so, do not draw
            if (viewWidth > 0) {
                if (snappedX + width < 0.0f 
                		|| snappedX > viewWidth 
                        || snappedY + height < 0.0f
                        || snappedY > viewHeight 
                        || opacity == 0.0f
                        || !texture.loaded) {
                    cull = true;
                }
            }
            if (!cull) {
                GraphicsManager.bindTexture(GL10.GL_TEXTURE_2D, texture.name);
                
                // This is necessary because we could be drawing the same texture with different
                // crop (say, flipped horizontally) on the same frame.
                GraphicsManager.setTextureCrop(mCrop);
               
                if (opacity < 1.0f) {
                    gl.glColor4f(opacity, opacity, opacity, opacity);
                }
                
                ((GL11Ext) gl).glDrawTexfOES(snappedX * scaledX, snappedY * scaledY, getPriority(), width * scaledX, height * scaledY);
                
                if (opacity < 1.0f) {
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
            
            if (mIsAnimated) {
            	//Due time to change the animation frame
	            if (SystemClock.uptimeMillis() - mLastFrameChangeTime >= mFrameTime) {
	            	//Dont loop anymore if already looped through all frames
	            	if (mCurrentFrame == mNumFrames-1) {
	            		mHasCompletedAnimation = true;
	            		if (!mLoopAnimation) {
	            			return;
	            		}
	            	}
	            	advanceAnimationFrame();
	            }
            }
        }
	}
	
	/**
	 * Move on to the next frame of animation properly
	 */
	private void advanceAnimationFrame() {
    	mLastFrameChangeTime = SystemClock.uptimeMillis();
    	if (mCurrentFrame == mNumFrames-1) {
    		mCurrentFrame = 0; 
    	} else {
    		mCurrentFrame++;
    	}
	}

	public void setCrop(int left, int bottom, int width, int height) {
        // Negative width and height values will flip the image.
        mCrop[0] = left;
        mCrop[1] = bottom;
        mCrop[2] = width;
        mCrop[3] = -height;
    }

    public int[] getCrop() {
        return mCrop;
    }
	
	/**
	 * We do not use priority drawing in this game, just give everything the same priority
	 * @return
	 */
	private float getPriority() {
		return 1.0f;
	}
	
	protected void processAI() {
		
	}
	
	protected void processPhysics() {
		mPosition.x = mPosition.x + mVelocity.x;
		mPosition.y = mPosition.y + mVelocity.y;
	}
	
	protected void validateLifetime() {
		
	}
	
	protected void kill() {
		
	}
	
	public void setPosition(Vector2 position) {
		mPosition = position;
	}
	
	public void setHealth(float health) {
		mHealth = health;
	}
	
	public void setBirthTime(int time) {
		mBirthTime = time;
	}
	
	public void setLifeTime(int time) {
		mLifeTime = time;
	}
	
	public boolean isAnimated() {
		return mIsAnimated;
	}
	
	public boolean isTimeSensitive() {
		return mIsTimeSensitive;
	}
	
	public boolean hasAI() {
		return mHasAI;
	}
	
	public boolean canMove() {
		return mCanMove;
	}
	
	public int getCurrentFrame() {
		return mCurrentFrame;
	}
	
	public int getNumFrames() {
		return mNumFrames;
	}
	
	public float getHealth() {
		return mHealth;
	}
	
	public long getBirthTime() {
		return mBirthTime;
	}
	
	public int getLifeTime() {
		return mLifeTime;
	}
	
	public Vector2 getPosition() {
		return mPosition;
	}
	
	public Vector2 getVelocity() {
		return mVelocity;
	}
	
	/**
	 * Get the correct texture from the object manager, already loaded in the beginning
	 * so that we avoid slowdown during gameplay. Swap it in as the texture for the GameObject
	 * so that we can change animations on the fly
	 * @param texture
	 */		
	public void switchToTexture(String texture) {
		mTextures = Game.sGame.getObjectManager().getLoadedTextureArray(texture);
		mCurrentFrame = 0;
		mNumFrames = mTextures.size();
	}
	
	public void setLoopAnimation(boolean loop) {
		mLoopAnimation = loop;
	}
	
	public void setTimeSensitive(boolean sensitive) { 
		mIsTimeSensitive = sensitive;
	}
	
	public void setVelocity(Vector2 velocity) {
		mVelocity = velocity;
	}
	
	public void setVelocity(float x, float y) {
		setVelocity(new Vector2(x,y));
	}
}

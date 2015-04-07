package com.jawnnypoo.zed.graphics;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.GLSurfaceView.Renderer;
/***
 * The renderer that draws the objects onto the screen. If you really want to 
 * get your hands dirty with rendering, tread onward with caution.
 * Else, stay away!
 * 
 * We render objects using OpenGL, which takes the structure of acting like a state machine.
 * Instead of specifying things like the lighting, translation, etc for each object, we instead
 * have the entire renderer hold these values, then render the objects the settings apply to in 
 * a sort of batch fashion. For a great understanding of OpenGL, read the OpenGL Redbook, free online!
 * @author Jawn
 *
 */
public class ZEDRenderer implements Renderer{
	
	private Context mContext;
	
	private int mWidth;
	private int mHeight;
	private float mScaleX;
	private float mScaleY;
	
	
	/**
	 * Constructor for the renderer
	 * @param context
	 * @param width
	 * @param height
	 */
	public ZEDRenderer(Context context, int width, int height) {
		mContext = context;
		mScaleX = 1.0f;
		mScaleY = 1.0f;
		mWidth = width;
		mHeight = height;
	}
	
	/**
	 * Called when we first create the surface (duh?) Also called when the Android device is awakened. Good 
	 * place for all the rendering things that are only going to be called once.
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		

		//First time init stuff for OpenGL
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        
		//Some global states to specify for OpenGL
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        //Setting up texture environment
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        //Clear the buffers initially
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
        Game.sGame.onSurfaceCreated();
	}
	
	/**We call this when the size of the surface (screen) changes, but it is also called
	 * initially to setup the size and aspect ratio of the screen
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		float scaleX = (float)width / mWidth;
    	float scaleY =  (float)height / mHeight;
    	final int viewportWidth = (int)(mWidth * scaleX);
    	final int viewportHeight = (int)(mHeight * scaleY);
        gl.glViewport(0, 0, viewportWidth, viewportHeight);
        mScaleX = scaleX;
        mScaleY = scaleY;

        
        /*
         * Set our projection matrix. This doesn't have to be done each time we
         * draw, but usually a new projection needs to be set when the viewport
         * is resized.
         */
        float ratio = (float) mWidth / mHeight;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        
        Game.sGame.onSurfaceReady();
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		beginDrawingTextures(gl, mWidth, mHeight);
		//Set the gl10 object so we can use it to draw within our object manager
		GraphicsManager.setGL(gl);
		Game.sGame.getObjectManager().drawObjects(mScaleX, mScaleY);
		GraphicsManager.setGL(null);
		endDrawingTextures(gl);

	}
	
	/**
	 * Call this method to prepare the renderer for drawing textures, such as game objects
	 * @param gl
	 * @param viewWidth
	 * @param viewHeight
	 */
	private static void beginDrawingTextures(GL10 gl, int viewWidth, int viewHeight) {
		gl.glShadeModel(GL10.GL_FLAT);
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	
	    gl.glMatrixMode(GL10.GL_PROJECTION);
	    gl.glPushMatrix();
	    gl.glLoadIdentity();
	    gl.glOrthof(0.0f, viewWidth, 0.0f, viewHeight, 0.0f, 1.0f);
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glPushMatrix();
	    gl.glLoadIdentity();
	   
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	}
	
	/**
     * Ends the drawing and restores the OpenGL state.
     * 
     * @param gl  A pointer to the OpenGL context.
     */
    public static void endDrawingTextures(GL10 gl) {
        gl.glDisable(GL10.GL_BLEND);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPopMatrix();
    }
    
	/**
	 * Load in all the textures from the library at once
	 */
	public void loadTextures(GL10 gl, TextureManager manager) {
        if (gl != null) {
            manager.loadAll(mContext, gl);
            Log.d("Renderer", "Textures Loaded.");
        }
    }

	public void onSurfaceLost() {
		Game.sGame.onSurfaceLost();
	}

	public void flushTextures(GL10 gl, TextureManager manager) {
		if (gl != null) {
            manager.deleteAll(gl);
            Log.d("Renderer", "Textures Unloaded.");
        }
	}

	public void loadBuffers(GL10 gl, BufferManager manager) {
		if (gl != null) {
            manager.generateHardwareBuffers(gl);
            Log.d("Renderer", "Buffers Created.");
        }
	}

	public void flushBuffers(GL10 gl, BufferManager manager) {
		if (gl != null) {
			manager.releaseHardwareBuffers(gl);
			Log.d("Renderer", "Buffers Released.");
		}
		
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
}

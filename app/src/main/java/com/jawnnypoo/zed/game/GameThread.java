package com.jawnnypoo.zed.game;


import android.os.SystemClock;
import android.util.Log;

/**
 * Since we do not want to run the game thread at an unlimited rate, we want
 * a game thread that runs independent of the drawing thread and updates the logic
 * at a rate of 60 frames per second, or whatever we chose that rate to be.
 * @author Jawn
 *
 */
public class GameThread implements Runnable{

	private long mLastTime;
	private boolean mFinished;
    private boolean mPaused = false;
	
	public GameThread() {
		mLastTime = SystemClock.uptimeMillis();
	}
	
	/**
	 * Limit the run of the Game thread to match the framerate
	 */
	@Override
	public void run() {
		mLastTime = SystemClock.uptimeMillis();
		while (!mFinished) {
			if (!mPaused) {
	            mLastTime = SystemClock.uptimeMillis();
	            Game.sGame.getObjectManager().processGameObjects();
	            //Sleep the thread, allowing for 60 frames per second logic
	            try {
					Thread.sleep(17);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void stopThread() {
		Log.d("GameThread", "Stopping the thread");
		mFinished = true;
	}
	
	public void resumeGame() {
		Log.d("GameThread", "Resuming Game Thread");
		mPaused = false;
	}
	
	public void pauseGame() {
		Log.d("GameThread", "Pausing Game Thread");
		mPaused = true;
	}
	
	public boolean getPaused() {
		return mPaused;
	}

}

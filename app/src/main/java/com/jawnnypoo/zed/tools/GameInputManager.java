package com.jawnnypoo.zed.tools;

import com.jawnnypoo.zed.game.Game;

import android.hardware.SensorEvent;
import android.view.MotionEvent;


/**
 * Class that gets input information from the Android system and interprets it in 
 * the way it should be in the game
 * @author Jawn
 *
 */
public class GameInputManager {
	private static MotionEvent sMotionEvent;
	private static SensorEvent sSensorEvent;
	private static float x;
	private static float y;
	
	public static boolean sScreenBeingTouched = false;
	private static int mScreenRotation = 0;
    private static float mOrientationInput[] = new float[3];
    private static float mOrientationOutput[] = new float[3];
    private static float mGravity[] = new float[3];
	
	
	public static void onSensorInput(SensorEvent event) {
		sSensorEvent = event;
		
		x = sSensorEvent.values[1];
		y = sSensorEvent.values[2];
		
		setOrientation(x,y,0);
	}
	
	public static void onTouchInput(MotionEvent event) {
		sMotionEvent = event;
		switch (sMotionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			sScreenBeingTouched = true;
			break;
		case MotionEvent.ACTION_MOVE:
			//Moving around, but still down
			break;
		case MotionEvent.ACTION_UP:
			sScreenBeingTouched = false;
		default:
			break;
		}
	}
	
	public static void setOrientation(float x, float y, float z) {
    	// The order of orientation axes changes depending on the rotation of the screen.
    	// Some devices call landscape "ROTAION_90" (e.g. phones), while others call it
    	// "ROTATION_0" (e.g. tablets).  So we need to adjust the axes from canonical
    	// space into screen space depending on the rotation of the screen from
    	// whatever this device calls "default." 
    	mOrientationInput[0] = x;
    	mOrientationInput[1] = y;
    	mOrientationInput[2] = z;
    	
    	
    	//Tweaks for if you want to discount gravity with the accelerometer
//    	final float alpha = 0.8f;
//
//    	// Isolate the force of gravity with the low-pass filter.
//    	mGravity[0] = alpha * mGravity[0] + (1 - alpha) * mOrientationInput[0];
//    	mGravity[1] = alpha * mGravity[1] + (1 - alpha) * mOrientationInput[1];
//    	mGravity[2] = alpha * mGravity[2] + (1 - alpha) * mOrientationInput[2];
//    	
//    	 // Remove the gravity contribution with the high-pass filter.
//    	 mOrientationInput[0] = mOrientationInput[0] - mGravity[0];
//    	 mOrientationInput[1] = mOrientationInput[1] - mGravity[1];
//    	 mOrientationInput[2] = mOrientationInput[2] - mGravity[2];
    	
    	canonicalOrientationToScreenOrientation(mScreenRotation, mOrientationInput, mOrientationOutput);
    	
    	// Now we have screen space rotations around xyz.
    	final float horizontalMotion = mOrientationOutput[1] + 6.5f;// / 90.0f;
        //final float verticalMotion = mOrientationOutput[0] / 90.0f;
        
        //Log.d("GameInputManager", "Horizonal Motion: " + (-horizontalMotion));
        //A little confusing, but horizontal motion is the up and down motion when the device is held sideways
    	if (Game.sGame.getObjectManager().getPlayerObject() != null) {
    		Game.sGame.getObjectManager().getPlayerObject().moveFromSensor(-horizontalMotion);
    	}
        
    }
	
	public void setScreenRotation(int rotation) {
		mScreenRotation = rotation;
	}
	
	// Thanks to NVIDIA for this useful canonical-to-screen orientation function.
	// More here: http://developer.download.nvidia.com/tegra/docs/tegra_android_accelerometer_v5f.pdf
	static void canonicalOrientationToScreenOrientation(int displayRotation, float[] canVec, float[] screenVec) { 
		final int axisSwap[][] = { 
				{ 1, -1, 0, 1 },   // ROTATION_0 
				{-1, -1, 1, 0 },   // ROTATION_90 
				{-1,  1, 0, 1 },   // ROTATION_180 
				{ 1,  1, 1, 0 } }; // ROTATION_270 
			
		final int[] as = axisSwap[displayRotation]; 
		screenVec[0] = (float)as[0] * canVec[ as[2] ]; 
		screenVec[1] = (float)as[1] * canVec[ as[3] ]; 
		screenVec[2] = canVec[2]; 
		}

	public static float[] getGravity() {
		return mGravity;
	}

	public static void setGravity(float mGravity[]) {
		GameInputManager.mGravity = mGravity;
	} 

}

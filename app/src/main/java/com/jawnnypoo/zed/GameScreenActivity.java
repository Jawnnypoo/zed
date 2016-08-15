package com.jawnnypoo.zed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.GLSurfaceView;
import com.jawnnypoo.zed.tools.GameInputManager;
import com.jawnnypoo.zed.tools.UIManager;

/***
 * The main activity for the game, which sets its content to draw as the
 * OpenGL custom renderer and handles Android input, such as touch events.
 * Also contains elements of UI through Android View
 *
 * @author Jawn
 */
public class GameScreenActivity extends Activity implements SensorEventListener {
    private static final String TAG = GameScreenActivity.class.getSimpleName();
    private GLSurfaceView mGLSurfaceView;
    private TextView mViewScore;
    private TextView mViewHealth;
    private TextView mViewDebug;
    private ImageView mViewDebugButton;
    private ImageView mViewGameOver;
    private Animation mAnimationGameOver;
    private SharedPreferences mSharedPreferences;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //No title bar please!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Fullscreen, plz
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set our screen to the right layout, which includes the surface view, and UI elements on top
        setContentView(R.layout.game_screen);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        //Keep in mind, the following UI elements are handled as Android class Views, so
        //they will be handled in a different way than most other graphic elements.
        mViewScore = (TextView) findViewById(R.id.score);
        mViewHealth = (TextView) findViewById(R.id.health);
        mViewDebug = (TextView) findViewById(R.id.debug);
        mViewGameOver = (ImageView) findViewById(R.id.gameOver);
        mAnimationGameOver = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        //Not visible until we want it to be
        mViewGameOver.setVisibility(View.GONE);
        mViewGameOver.setAnimation(mAnimationGameOver);
        mViewGameOver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //We tapped on the Game over screen, restart
                mViewGameOver.setVisibility(View.GONE);
                Game.sGame.reset();
            }
        });
        mViewDebugButton = (ImageView) findViewById(R.id.debugButton);
        mViewDebugButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Game.sGame.getObjectManager().getPlayerObject() != null)
                    Game.sGame.getSpawnManager().spawnEnemy(1000, Game.sGame.getObjectManager().getPlayerObject().mPosition.y);
            }
        });
        //Take out this line if you want to use this debug button
        mViewDebugButton.setVisibility(View.GONE);
        //Get the screen specs for the renderer
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Assumes a display can show 720p. You can change this if targeting lower end devices that do not display in 
        //or close to 720p
        int defaultWidth = 1280;
        int defaultHeight = 720;

        //Uncomment these values if you wish to target all 4:3 devices
        //int defaultWidth = 480;
        //int defaultHeight = 320;

        if (dm.widthPixels != defaultWidth) {
            float ratio = ((float) dm.widthPixels) / dm.heightPixels;
            defaultWidth = (int) (defaultHeight * ratio);
        }
        UIManager.init(this);
        //Create the static singleton Game Object
        Game.initGame();
        //Game holds a lot of information for the higher level factors of running the game. INIT it here
        Game.sGame.setSurfaceView(mGLSurfaceView);
        //Bootstrap is pretty much the init for the static Game class. Gets all of the essential
        //game componenets ready to start working, such as the renderer and the object manager
        Game.sGame.bootstrap(this, dm.widthPixels, dm.heightPixels, defaultWidth, defaultHeight);
        //Get the shared preferences to see if sound is enabled.
        mSharedPreferences = getSharedPreferences(getString(R.string.SETTING_NAME), 0);
        boolean sound = true;
        if (mSharedPreferences != null) {
            sound = mSharedPreferences.getBoolean(getString(R.string.SETTING_SOUND), true);
        }
        Game.sGame.getSoundManager().setSoundEnabled(sound);
        if (Game.sGame.getSoundManager().getSoundEnabled()) {
            Game.sGame.getSoundManager().loadAndPlayBackgroundMusic(R.raw.background);
        }
        mGLSurfaceView.setRenderer(Game.sGame.getRenderer());
        //Getting ready to get the accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();

        //Since we are not active, stop listening to the sensor
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        Game.sGame.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Game.sGame.stop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume called");
        Game.sGame.onResume(this);
        mGLSurfaceView.onResume();

        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometer != null) {
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME, null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Pause the game
        pauseGame();
        //Dialog to confirm quit
        new AlertDialog.Builder(this).setMessage(R.string.quit_message).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onQuit();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Resume the game since we didnt leave
                Game.sGame.onResume(getApplicationContext());
            }
        }).setCancelable(false).show();
    }

    private void pauseGame() {
        Game.sGame.onPause();
    }

    protected void onQuit() {
        Game.sGame.stop();
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Reads the input from the motion sensor to move the character based on the angle of the device. Passes off
     * the event to the GameInputManager
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            GameInputManager.onSensorInput(event);
        }

    }

    /**
     * Keep in mind, this touch event is only called back if there is a change in the touch,
     * not if the touch is simply held down
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GameInputManager.onTouchInput(event);
        return super.onTouchEvent(event);
    }

    /**
     * We can only make changes to the UI in the class that
     * created the elements, so we get here from the UIManager
     *
     * @param text
     */
    public void updateScore(final String text) {
        //Android is really picky about who can mess with views,
        //so we have to run this on the UI thread
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mViewScore.setText(text);
            }
        });
    }

    public void updateDebug(final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mViewDebug.setText(text);
            }
        });
    }

    public void updateHealth(final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mViewHealth.setText(text);
            }
        });
    }

    public void showGameOver() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mViewGameOver.setVisibility(View.VISIBLE);
                mViewGameOver.startAnimation(mAnimationGameOver);
            }
        });
    }

}

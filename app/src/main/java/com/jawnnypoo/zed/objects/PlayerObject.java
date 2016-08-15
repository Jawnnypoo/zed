package com.jawnnypoo.zed.objects;


import android.os.SystemClock;
import android.util.Log;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.Texture;
import com.jawnnypoo.zed.physics.Vector2;
import com.jawnnypoo.zed.tools.UIManager;

import java.util.ArrayList;

/**
 * Our player object, whom we control and wish to keep alive.
 *
 * @author Jawn
 */
public class PlayerObject extends GameObject {

    private static final int Y_MIN = 10;
    private static final int Y_MAX = 720;
    public static final long STARTING_RATE_OF_FIRE = 500;
    private long mLastShootTime = 0;
    private long mLastDamageTime = 0;
    //Rate of fire, in ms
    private long mRateOfFire = STARTING_RATE_OF_FIRE;
    private long mInvulnerableTime = 1000;
    private int mDamageRate = 1;
    private static int mSensitivityValue = 10;

    public PlayerObject(float x, float y, int width, int height, String name, ArrayList<Texture> textures, long frameTime) {
        super(x, y, width, height, name, textures, frameTime);
        mHealth = 100;
        mCanMove = true;
        mIsAnimated = true;
        mPosition = new Vector2(x, y);
    }

    @Override
    protected void kill() {
        //Override, so that we can give special actions when the player is killed
        super.kill();
        Game.sGame.getSpawnManager().spawnExplosion(mPosition.x, mPosition.y);
        Game.sGame.getSoundManager().playExplosion();
        Game.sGame.getObjectManager().stopBackground();
    }

    public void moveUp() {
        mPosition.y = mPosition.y + 5;
    }

    public void moveDown() {
        mPosition.y = mPosition.y - 5;
    }

    public void moveFromSensor(float value) {

        double rounded = (double) Math.round(value * 10) / 10;
        //Make sure the player doesn't go out of bounds
        if (mPosition.y < Y_MIN) {
            mPosition.y = Y_MIN;
        } else if (mPosition.y + mHeight > Y_MAX) {
            mPosition.y = Y_MAX - 1 - mHeight;
        } else {
            mPosition.y = (float) (mPosition.y + rounded * mSensitivityValue);
        }

    }

    public void shoot() {
        //Limit the shooting to the rate of fire of the player
        if (SystemClock.uptimeMillis() - mLastShootTime >= mRateOfFire) {
            Game.sGame.getObjectManager().createBullet(mPosition.x + mWidth - mWidth / 3, mPosition.y + mHeight / 4, 128, 128, "Bullet", 100, 10, 0);
            mLastShootTime = SystemClock.uptimeMillis();
        }
    }

    public int getDamageRate() {
        return mDamageRate;
    }

    public void takeDamage(float damage) {

        //Make sure player can only get hit every so often by the enemy so we do not anger them
        if (SystemClock.uptimeMillis() - mLastDamageTime >= mInvulnerableTime) {
            mLastDamageTime = SystemClock.uptimeMillis();
            Game.sGame.getSpawnManager().spawnSpark(mPosition.x + mWidth - mWidth / 3, mPosition.y + mHeight / 4);
            mHealth = mHealth - damage;
            Game.sGame.getSoundManager().playHurt();
            if (mHealth <= 0) {
                //This will also invoke the kill function for the player object
                Game.sGame.getObjectManager().kill(this);
                UIManager.showGameOverScreen();
                return;
            }

            UIManager.updatePlayerHealth();
        }
    }

    public long getRateOfFire() {
        return mRateOfFire;
    }

    public void setRateOfFire(long rateOfFire) {
        mRateOfFire = rateOfFire;
    }

    public void modifyRateOfFire(long change) {
        mRateOfFire = mRateOfFire + change;

        Log.d("asdf", "RAte of fire is now : " + mRateOfFire);
    }
}

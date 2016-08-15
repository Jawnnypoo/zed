package com.jawnnypoo.zed.objects;


import android.text.TextUtils;
import android.util.Log;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.Texture;
import com.jawnnypoo.zed.physics.CollisionManager;
import com.jawnnypoo.zed.physics.Vector2;
import com.jawnnypoo.zed.tools.GameInputManager;
import com.jawnnypoo.zed.tools.UIManager;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * The manager of all objects. This is a convenience so that we can keep track
 * of all the objects in the game through a singleton object (which we will instantiate one of)
 * Contains a static ObjectManager, which should be initialized and used exclusively as a Singleton
 * that holds all the methods needed to access, create, and remove objects, as well as other custom
 *
 * @author Jawn
 */
public class ObjectManager {
    private String TAG = ObjectManager.class.getSimpleName();
    //These lists hold the objects that we create in the game. We iterate through
    //these lists, and update each of the objects within
    private ArrayList<GameObject> mGameObjectList;
    private ArrayList<EnemyObject> mEnemyObjectList;
    private ArrayList<BulletObject> mBulletObjectList;
    private ArrayList<BackgroundObject> mBackgroundObjectList;
    private HashMap<String, ArrayList<Texture>> mGameTextures;
    //Special reference to the player
    private PlayerObject mPlayerObject;


    public ObjectManager() {
        mGameObjectList = new ArrayList<GameObject>();
        mEnemyObjectList = new ArrayList<EnemyObject>();
        mBulletObjectList = new ArrayList<BulletObject>();
        mBackgroundObjectList = new ArrayList<BackgroundObject>();
        mGameTextures = new HashMap<String, ArrayList<Texture>>();
    }

    public void createGameObject(float x, float y, int width, int height, String name, ArrayList<Texture> texture, long frameTime) {
        GameObject object = new GameObject(x, y, width, height, name, mGameTextures.get(name), frameTime);
        Log.d(TAG, "Created the player");
        mGameObjectList.add(object);
    }

    public void createPlayerObject(float x, float y, int width, int height, String name, long frameTime) {
        mPlayerObject = new PlayerObject(x, y, width, height, name, mGameTextures.get(name), frameTime);

        mGameObjectList.add(mPlayerObject);
    }

    public void createEnemyObject(float x, float y, int width, int height, String name, long frameTime) {
        EnemyObject enemy = new EnemyObject(x, y, width, height, name, mGameTextures.get(name), frameTime);

        mEnemyObjectList.add(enemy);
        mGameObjectList.add(enemy);
    }

    public void createBullet(float x, float y, int width, int height, String name, int frameTime, int xVelocity, int yVelocity) {
        BulletObject bullet = new BulletObject(x, y, width, height, mGameTextures.get(name), name, frameTime, xVelocity, yVelocity);

        bullet.setLifeTime(BulletObject.BULLET_LIFE_TIME);
        mBulletObjectList.add(bullet);
        mGameObjectList.add(bullet);
    }

    public void createBackground(float x, float y, int width, int height, String name, int frameTime, int xVelocity, int yVelocity) {
        BackgroundObject background = new BackgroundObject(x, y, width, height, name, mGameTextures.get(name), frameTime, xVelocity, yVelocity);

        mBackgroundObjectList.add(background);
    }

    public void createEffect(float x, float y, int width, int height, String name, int frameTime, Vector2 velocity) {
        EffectObject effect = new EffectObject(x, y, width, height, name, mGameTextures.get(name), frameTime);
        effect.setVelocity(velocity);

        mGameObjectList.add(effect);
    }

    /**
     * Called on every frame, we process the AI and physics at 60 FPS. Called from the game thread
     */
    public void processGameObjects() {
        UIManager.updateDebugUI();
        Game.sGame.getSpawnManager().monitor();
        for (int i = 0; i < mGameObjectList.size(); i++) {
            GameObject object = mGameObjectList.get(i);

            if (object.hasAI()) {
                object.processAI();
            }
            if (object.canMove()) {
                object.processPhysics();
            }
            //Check if the life of the object is valid
            if (object.isTimeSensitive()) {
                if (System.currentTimeMillis() - object.mBirthTime >= object.mLifeTime) {
                    //Request the object manager to kill this object, so that it loses its references
                    kill(object);
                }
            }
        }

        //Since we have the background tiles in a different list, we process them in a
        //different iteration
        for (int i = 0; i < mBackgroundObjectList.size(); i++) {
            BackgroundObject background = mBackgroundObjectList.get(i);
            if (background.hasAI()) {
                background.processAI();
            }
            if (background.canMove()) {
                background.processPhysics();
            }
        }
        processCollisions();
        if (GameInputManager.sScreenBeingTouched) {
            if (mPlayerObject != null) {
                mPlayerObject.shoot();
            }
        }
    }

    /**
     * Draw all of the game objects to the screen
     *
     * @param xScale
     * @param yScale
     */
    public void drawObjects(float xScale, float yScale) {
        //First, we draw the background (see painters algorithm)
        for (int i = 0; i < mBackgroundObjectList.size(); i++) {
            BackgroundObject background = mBackgroundObjectList.get(i);
            background.draw(background.mPosition.x, background.mPosition.y, xScale, yScale);
        }
        //Iterate through the list that should contain each object and draw them all
        for (int i = 0; i < mGameObjectList.size(); i++) {
            GameObject object = mGameObjectList.get(i);
            object.draw(object.mPosition.x, object.mPosition.y, xScale, yScale);
        }
    }

    private void processCollisions() {
        //Compare enemy against all bullets
        for (int i = 0; i < mEnemyObjectList.size(); i++) {
            EnemyObject enemy = mEnemyObjectList.get(i);
            for (int j = 0; j < mBulletObjectList.size(); j++) {
                BulletObject bullet = mBulletObjectList.get(j);
                if (CollisionManager.distanceSquared(enemy.mPosition.x + enemy.mWidth, enemy.mPosition.y + (enemy.mHeight / 2),
                        bullet.mPosition.x + bullet.mWidth, bullet.mPosition.y + (bullet.mHeight / 2)) < CollisionManager.DISTANCE_BULLET_TO_ENEMY) {
                    //If they are this close together, go ahead and count it as a hit.
                    bulletToEnemyCollision(bullet, enemy);
                }
            }

            //We are looping on all enemies, so now check enemy against player
            if (mPlayerObject != null) {
                if (CollisionManager.distanceSquared(enemy.mPosition.x + enemy.mWidth, enemy.mPosition.y + (enemy.mHeight / 2),
                        mPlayerObject.mPosition.x + mPlayerObject.mWidth, mPlayerObject.mPosition.y + (mPlayerObject.mHeight / 2)) < CollisionManager.DISTANCE_PLAYER_TO_ENEMY) {
                    //If they are this close together, go ahead and count it as a hit.
                    enemyToPlayerCollision(mPlayerObject, enemy);
                }
            }
        }
    }

    private void bulletToEnemyCollision(BulletObject bullet, EnemyObject enemy) {
        kill(bullet);
        enemy.takeDamage(mPlayerObject.getDamageRate());
    }

    private void enemyToPlayerCollision(PlayerObject player, EnemyObject enemy) {
        kill(enemy);
        player.takeDamage(EnemyObject.DAMAGE_RATE);
    }

    public void putTextureInMap(String key, ArrayList<Texture> value) {
        mGameTextures.put(key, value);
    }

    public void stopBackground() {
        for (int i = 0; i < mBackgroundObjectList.size(); i++) {
            BackgroundObject bg = mBackgroundObjectList.get(i);
            bg.setVelocity(0, 0);
        }
    }

    /**
     * Calls the kill function on the object and gets rid of all references
     *
     * @param object
     */
    public void kill(GameObject object) {
        // Let the object do what it wants when it dies
        object.kill();

        remove(object);

    }

    public void remove(GameObject object) {
        if (TextUtils.equals(object.mName, "Bullet")) {
            mBulletObjectList.remove(object);
        }
        if (TextUtils.equals(object.mName, "Enemy")) {
            mEnemyObjectList.remove(object);
        }
        if (TextUtils.equals(object.mName, "Player")) {
            mPlayerObject = null;
        }
        if (TextUtils.equals(object.mName, "Background")) {
            mBackgroundObjectList.remove(object);
        }
        // Since GameObjectList holds all game objects, we must remove
        // from here as well
        mGameObjectList.remove(object);
    }

    /**
     * Clears out the objects that the manager has referenced
     */
    public void reset() {
        mGameObjectList.clear();
        mBulletObjectList.clear();
        mEnemyObjectList.clear();
        mBackgroundObjectList.clear();
        mPlayerObject = null;
    }

    public int getNumObjects() {
        return mGameObjectList.size();
    }

    public int getNumEnemies() {
        return mEnemyObjectList.size();
    }

    public PlayerObject getPlayerObject() {
        return mPlayerObject;
    }

    public ArrayList<Texture> getLoadedTextureArray(String texture) {
        return mGameTextures.get(texture);
    }
}

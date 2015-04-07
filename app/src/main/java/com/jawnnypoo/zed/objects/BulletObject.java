package com.jawnnypoo.zed.objects;


import java.util.ArrayList;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.Texture;
/**
 * Bullet object to shoot at the enemy!
 * @author Jawn
 *
 */
public class BulletObject extends GameObject{

	public static final int BULLET_LIFE_TIME = 5000;
	
	public BulletObject(float x, float y, int width, int height,
			ArrayList<Texture> texture, String name, long frameTime, int xVelocity, int yVelocity) {
		super(x, y, width, height, name, texture, frameTime);
		mVelocity.x = xVelocity;
		mVelocity.y = yVelocity;
		mCanMove = true;
		mIsTimeSensitive = true;
	}
	
	@Override
	protected void processPhysics() {
		super.processPhysics();
		if (mPosition.x > Game.sGame.getGameWidth()) {
			Game.sGame.getObjectManager().kill(this);
		}
	}

}

package com.jawnnypoo.zed.objects;


import java.util.ArrayList;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.Texture;
import com.jawnnypoo.zed.tools.UIManager;

public class EnemyObject extends GameObject{
	
	public static int DAMAGE_RATE = 10;
	public static float X_VELOCITY = -3;
	public static float ENEMY_HEALTH = 1;

	public EnemyObject(float x, float y, int width, int height, String name, ArrayList<Texture> texture, long frameTime) {
		super(x, y, width, height, name, texture, frameTime);
		mHealth = ENEMY_HEALTH;
		mVelocity.x = X_VELOCITY;
		mCanMove = true;
		mHasAI = true;
	}
	
	@Override
	protected void kill() {
		super.kill();
		Game.sGame.getSpawnManager().spawnExplosion(mPosition.x, mPosition.y);
		Game.sGame.getSoundManager().playExplosion();
	}
	
	@Override
	protected void processAI() {
		super.processAI();
		//If we go to far to the left, request death
		if (mPosition.x < -100) {
			Game.sGame.getObjectManager().remove(this);
		}
	}
	
	protected void takeDamage(int damage) {
		mHealth = mHealth - damage;
		Game.sGame.getSpawnManager().spawnEnemySpark(mPosition.x, mPosition.y + mHeight/4, mVelocity);
		if (mHealth <= 0) {
			//This will also invoke the kill function for the enemy
			Game.sGame.getObjectManager().kill(this);
			UIManager.addToScore(UIManager.SCORE_FOR_ENEMY);
		}
	}
	
	public static void changeEnemyDamageRate(int damageRate) {
		DAMAGE_RATE = damageRate;
	}
}

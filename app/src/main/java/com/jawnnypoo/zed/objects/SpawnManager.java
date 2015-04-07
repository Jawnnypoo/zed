package com.jawnnypoo.zed.objects;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.physics.Vector2;

/**
 * Partially an abstraction from the ObjectManager that only has to worry
 * about position of spawns, but also controls the logic behind difficulty
 * and how many objects spawn
 * @author Jawn
 *
 */
public class SpawnManager {

	private int mNumberOfEnemiesToSpawn = 10;
	private int mRateOfFireMod = 20;
	
	public SpawnManager() {
		mNumberOfEnemiesToSpawn = 10;
	}
	
	//Called on every frame to monitor enemy count
	public void monitor() {
		
		if (Game.sGame.getObjectManager().getNumEnemies() <= 0) {
			spawnWave();
			increaseDifficulty();
		}
	}
	
	private void spawnWave() {
		int xOffset = Game.sGame.getGameWidth() + 100;
		for (int i=0; i<mNumberOfEnemiesToSpawn; i++) {
			spawnEnemy(xOffset, (int )(Math.random() * Game.sGame.getGameHeight()/2 + 128));
			//Spread out the enemies we are spawning
			xOffset = xOffset + 200;
		}
	}
	
	//Easier call to spawn objects than going through the object manager
	public void spawnEnemy(float x, float y) {
			Game.sGame.getObjectManager().createEnemyObject(x, y, 128, 128, "Enemy", 100);
		}
			
		public void spawnSpark(float x, float y) {
			Game.sGame.getObjectManager().createEffect(x, y, 128, 128, "Spark", 10, new Vector2(0,0));
		}
		
		public void spawnEnemySpark(float x, float y, Vector2 velocity) {
			Game.sGame.getObjectManager().createEffect(x, y, 128, 128, "EnemySpark", 10, velocity);
			
		}
			
		public void spawnExplosion(float x, float y) {
			Game.sGame.getObjectManager().createEffect(x, y, 128, 128, "Explosion", 10, new Vector2(0,0));
		}
	
	private void increaseDifficulty() {
		mNumberOfEnemiesToSpawn = mNumberOfEnemiesToSpawn + 2;
		EnemyObject.X_VELOCITY = EnemyObject.X_VELOCITY - 0.2f;
		EnemyObject.ENEMY_HEALTH = EnemyObject.ENEMY_HEALTH + 0.2f;
		Game.sGame.getObjectManager().getPlayerObject().modifyRateOfFire(-mRateOfFireMod);
	}
	
	public void reset() {
		BackgroundObject.BACKGROUND_X_VELOCITY = -3;
		EnemyObject.DAMAGE_RATE = 10;
		EnemyObject.X_VELOCITY = -3;
		mNumberOfEnemiesToSpawn = 10;
	}
	
}

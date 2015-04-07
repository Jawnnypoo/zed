package com.jawnnypoo.zed.tools;


import com.jawnnypoo.zed.GameScreenActivity;
import com.jawnnypoo.zed.game.Game;
/**
 * Static class to keep up with and change all UI elements, including the player score
 * @author Jawn
 *
 */
public class UIManager {

	public static final int SCORE_FOR_ENEMY = 10;
	private static GameScreenActivity mActivity;
	private static long mScore;
	
	public static void init(GameScreenActivity activity) {
		mActivity = activity;
	}
	
	public static void resetScore() {
		mScore = 0;
		updateScoreUI();
	}
	
	public static void addToScore(int value) {
		mScore += value;
		updateScoreUI();
	}
	
	public static void subtractFromScore(int value) {
		addToScore(-value);
	}
	
	public static long getScore() {
		return mScore;
	}
	
	public static void setScore(int score) {
		mScore = score;
		updateScoreUI();
	}
	
	public static void updateScoreUI() {
		mActivity.updateScore("Score: " + mScore);
	}
	
	public static void updateDebugUI() {
		mActivity.updateDebug("Num Objects: " + Game.sGame.getObjectManager().getNumObjects());
	}
	
	public static void updatePlayerHealth() {
		mActivity.updateHealth("Health: " + Game.sGame.getObjectManager().getPlayerObject().getHealth());
	}
	
	public static void showGameOverScreen() {
		mActivity.showGameOver();
	}
	
}

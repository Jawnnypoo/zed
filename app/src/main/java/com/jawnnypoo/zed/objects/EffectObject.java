package com.jawnnypoo.zed.objects;


import java.util.ArrayList;

import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.Texture;

/**
 * Object generated at a X Y position where you want a specific animation to show.
 * Set up to die off after one loop of animation shows
 * @author Jawn
 *
 */
public class EffectObject extends GameObject{

	
	
	public EffectObject(float x, float y, int width, int height, String name,
			ArrayList<Texture> texture, long frameTime) {
		super(x, y, width, height, name, texture, frameTime);
		mLoopAnimation = false;
		mHasAI = true;
		mCanMove = true;
	}
	
	@Override
	protected void processAI() {
		super.processAI();
		//Even though it isnt AI, we want this object to go away after playing through its animation
		if (mHasCompletedAnimation) {
			Game.sGame.getObjectManager().kill(this);
		}
	}

}

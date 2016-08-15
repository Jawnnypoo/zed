package com.jawnnypoo.zed.objects;


import com.jawnnypoo.zed.game.Game;
import com.jawnnypoo.zed.graphics.Texture;

import java.util.ArrayList;

/**
 * Tiles that stay in the background. We draw these first, so that
 * they do not cover up the player
 *
 * @author Jawn
 */
public class BackgroundObject extends GameObject {

    public static int BACKGROUND_X_VELOCITY = -3;

    public BackgroundObject(float x, float y, int width, int height,
                            String name, ArrayList<Texture> texture, long frameTime, int xVelocity, int yVelocity) {
        super(x, y, width, height, name, texture, frameTime);
        mVelocity.x = xVelocity;
        mVelocity.y = yVelocity;
        mCanMove = true;
    }


    @Override
    protected void processPhysics() {
        super.processPhysics();
        //If we get past the player, move back to the edge of the screen.
        if (mPosition.x + mWidth <= 0) {
            mPosition.x = Game.sGame.getGameWidth();
        }
    }


}

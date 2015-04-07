/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jawnnypoo.zed.graphics;

import com.jawnnypoo.zed.objects.GameObject;
import com.jawnnypoo.zed.physics.Vector2;



/**
 * WARNING: NOT BEING USED. THE WAY THE GAME WORKS IT IS NOT NEEDED.
 * YOU WILL NEED TO EXPAND THIS
 * Manages the position of the camera based on a target game object.
 */
public class CameraSystem {
    private GameObject mTarget;
    private Vector2 mCurrentCameraPosition;
    private Vector2 mTargetPosition;
    
    private static Vector2 mTargetOffset;
    
    public CameraSystem() {
        mCurrentCameraPosition = new Vector2();
        mTargetPosition = new Vector2();
    }
    
    public void reset() {
        mTarget = null;
        mCurrentCameraPosition.zero();
        mTargetPosition.zero();
    }
    
    void setTarget(GameObject target) {
    	mCurrentCameraPosition.set(target.getPosition());
        mTarget = target;
    }
    
    public GameObject getTarget() {
		return mTarget;
	}
    
    public void update() {
        
        //Update the camera to follow the object
        if (mTarget != null) {
        	mTargetPosition.set(mTarget.getPosition().x - mTargetOffset.x, mTarget.getPosition().y - mTargetOffset.y);
        }
    }
}

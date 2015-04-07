package com.jawnnypoo.zed.physics;

/**
 * Simple CollisionManager that just compares distances to find collisions,
 * essentially creating a radius hitbox around the object of comparison.
 * I recommend instead using AABB collisions, using bounding boxes
 * http://docs.oracle.com/javafx/2/api/javafx/geometry/BoundingBox.html
 */
public class CollisionManager {

	public static final int DISTANCE_BULLET_TO_ENEMY = 10000;
	public static final int DISTANCE_PLAYER_TO_ENEMY = 8000;
	public static float distance(float x, float y, float x2, float y2) {
		
		float xDist = (float)Math.abs(x - x2); //x distance
		float yDist = (float)Math.abs(y - y2); //y distance

		//return result
		return (float) Math.sqrt(xDist*xDist + yDist*yDist);
	}
	
	//Use this function if we need to find the distance between objects many times, since it 
	//does not apply the square root (expensive operation)
	public static float distanceSquared(float x, float y, float x2, float y2) {
		float xDist = (float)Math.abs(x - x2); //x distance
		float yDist = (float)Math.abs(y - y2); //y distance
		
		return (xDist*xDist + yDist*yDist);
	}
}

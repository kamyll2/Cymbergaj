package com.wygralak.cymbergaj.Engine;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.Vector2;

/**
 * Created by Kamil on 2016-03-10.
 */
public class PlayerEngine implements ICollisionInterpreter {
    public static final float PLAYER_RADIUS = 50f;
    public static final Paint PLAYER_PAINT = new Paint();

    private float pitchWidth;
    private float pitchHeight;

    private float currentX;
    private float currentY;
    private float speedRatio = -1f;

    public PlayerEngine() {
        PLAYER_PAINT.setColor(Color.RED);
    }

    public void updatePosition(float x, float y) {
        updateCurrentSpeedRatio(currentX, currentY, x, y);
        currentX = x;
        currentY = y;
    }

    private void updateCurrentSpeedRatio(float x1, float y1, float x2, float y2) {
        float length = getLengthBetweenPoints(x1, y1, x2, y2);
        if (length > 100f) {
            speedRatio = 3.0f;
        } else {
            speedRatio = 0.023f * (length - 100) + 3.0f;
        }
    }

    public void invalidateSpeedRatio() {
        speedRatio = 0.8f;
    }

    private float getLengthBetweenPoints(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setPitchSize(int width, int height) {
        pitchHeight = height;
        pitchWidth = width;
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        if (isCollision(x, y)) {
            float collisionPointX = ((x * PLAYER_RADIUS) + (currentX * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            float collisionPointY = ((y * PLAYER_RADIUS) + (currentY * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            Vector2 collisionVector = Vector2.createVector2FromTwoPoints(collisionPointX, collisionPointY, x, y).normalize();
            float scaler = (1f / Math.max(Math.abs(collisionVector.x), Math.abs(collisionVector.y))) * 1.1f;
            collisionVector.x *= scaler;
            collisionVector.y *= scaler;

            invoker.updateVector(currentVector.add(collisionVector).normalize());

            float currX;
            float currY;
            float currSpeed = invoker.getCurrentSpeed();
            invoker.setSpeedDirectly(1f);
            do {
                invoker.updatePosition(1d);
                currX = invoker.getCurrentPositionX();
                currY = invoker.getCurrentPositionY();
            } while (isCollision(currX, currY));

            invoker.setSpeedDirectly(currSpeed);
            invoker.updateSpeedWithRatio(speedRatio);
            float minSpeed = computeMinimumSpeed(speedRatio);
            if (invoker.getCurrentSpeed() < minSpeed) {
                invoker.setSpeedDirectly(minSpeed);
            }
            invoker.updatePosition(1d);
        }
        return false;
    }

    private boolean isCollision(float x, float y) {
        float collisionDistance = BallEngine.BALL_RADIUS + PLAYER_RADIUS;
        float actualDistance = (float) Math.sqrt(Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2));
        return actualDistance < collisionDistance;
    }

    private float computeMinimumSpeed(float speedRatio) {
        //ratio=0.7 => minSpeed=0
        //ratio=3.0 => minSpeed=35
        return 15.2173f * speedRatio - 10.6521f;
    }
}

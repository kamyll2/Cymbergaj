package com.wygralak.cymbergaj;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;

import java.util.Vector;

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

    public PlayerEngine() {
        PLAYER_PAINT.setColor(Color.RED);
    }

    public void updatePosition(float x, float y) {
        currentX = x;
        currentY = y;
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
        float collisionDistance = BallEngine.BALL_RADIUS + PLAYER_RADIUS;
        float actualDistance = (float) Math.sqrt(Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2));
        if (actualDistance < collisionDistance) {
            float collisionPointX = ((x * PLAYER_RADIUS) + (currentX * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            float collisionPointY = ((y * PLAYER_RADIUS) + (currentY * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            Vector2 collisionVector = Vector2.createVector2FromTwoPoints(collisionPointX, collisionPointY, x, y).normalize();
            invoker.updateVector(currentVector.add(collisionVector).normalize());
        }
        return false;
    }
}

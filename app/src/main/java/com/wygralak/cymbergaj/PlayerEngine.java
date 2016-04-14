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

    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, float x, float y) {
        return vectorHandle(invoker, x, y);
        /*float minDist = BallEngine.BALL_RADIUS + PLAYER_RADIUS;
        float dist = (float) Math.sqrt(Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2));
        if (dist < minDist) {
            //float newAngle = invoker.getCurrentAngle() == 90f ? 180f : 90f;
            //invoker.updateAngle(invoker.getCurrentAngle()*(-1));
            invoker.updateAngle((180 - invoker.getCurrentAngle()) % 360);
            return true;
        }
        return false;*/
    }

    private boolean handle(ICollisionInvoker invoker, float x, float y) {
        float collisionDistance = BallEngine.BALL_RADIUS + PLAYER_RADIUS;
        float actualDistance = (float) Math.sqrt(Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2));
        if (actualDistance < collisionDistance) {
            float collNormalAngle = (float) Math.atan2(y - currentY, x - currentX);
            float moveDist1 = (collisionDistance - actualDistance) * 1.0f;//1 to wsp. masy
            float moveDist2 = (collisionDistance - actualDistance) * 1.0f;//1 to wsp. masy
            /*invoker.updatePosition(x + moveDist2*Math.cos(collNormalAngle + 180.0f),
                    y + moveDist2*Math.sin(collNormalAngle + 180.0f));*/
            //---
            float nX = (float) Math.cos(collNormalAngle);
            float nY = (float) Math.sin(collNormalAngle);

            float dx = (float) Math.sin(Math.toRadians(invoker.getCurrentAngle()));
            float newDx = dx * nX;
            float newAngle = (float) Math.asin(newDx);
            float degrees = (float) Math.toDegrees(newAngle);
            invoker.updateAngle(degrees);
            return true;
        }
        return false;
    }

    /*private boolean vectorHandle(ICollisionInvoker invoker, float x, float y) {
        float collisionDistance = BallEngine.BALL_RADIUS + PLAYER_RADIUS;
        float actualDistance = (float) Math.sqrt(Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2));
        if (actualDistance < collisionDistance) {
            float collisionPointX = ((x * PLAYER_RADIUS) + (currentX * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            float collisionPointY = ((y * PLAYER_RADIUS) + (currentY * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            Vector2 currentVector = invoker.getCurrentVector();
            float newVelX1 = (currentVector.x * (1.0f - 4.0f) + (2.0f * 4.0f * 0.0f)) / (1.0f + 4.0f);
            float newVelY1 = (currentVector.y * (1.0f - 4.0f) + (2.0f * 4.0f * 0.0f)) / (1.0f + 4.0f);
            invoker.updateVector(new Vector2(newVelX1, newVelY1));
            //newVelX2 = (secondBall.speed.x * (secondBall.mass – firstBall.mass) + (2 * firstBall.mass * firstBall.speed.x)) / (firstBall.mass + secondBall.mass);
            //newVelY2 = (secondBall.speed.y * (secondBall.mass – firstBall.mass) + (2 * firstBall.mass * firstBall.speed.y)) / (firstBall.mass + secondBall.mass);
        }
        return false;
    }*/

    private boolean vectorHandle(ICollisionInvoker invoker, float x, float y) {
        float collisionDistance = BallEngine.BALL_RADIUS + PLAYER_RADIUS;
        float actualDistance = (float) Math.sqrt(Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2));
        if (actualDistance < collisionDistance) {
            float collisionPointX = ((x * PLAYER_RADIUS) + (currentX * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            float collisionPointY = ((y * PLAYER_RADIUS) + (currentY * BallEngine.BALL_RADIUS)) / (BallEngine.BALL_RADIUS + PLAYER_RADIUS);
            Vector2 currentVector = invoker.getCurrentVector();
            Vector2 collisionVector = Vector2.createVector2FromTwoPoints(collisionPointX, collisionPointY, x, y).normalize();
            //float newVelX1 = (currentVector.x * (1.0f - 4.0f) + (2.0f * 4.0f * 0.0f)) / (1.0f + 4.0f);
            //float newVelY1 = (currentVector.y * (1.0f - 4.0f) + (2.0f * 4.0f * 0.0f)) / (1.0f + 4.0f);
            invoker.updateVector(currentVector.add(collisionVector).normalize());
            //newVelX2 = (secondBall.speed.x * (secondBall.mass – firstBall.mass) + (2 * firstBall.mass * firstBall.speed.x)) / (firstBall.mass + secondBall.mass);
            //newVelY2 = (secondBall.speed.y * (secondBall.mass – firstBall.mass) + (2 * firstBall.mass * firstBall.speed.y)) / (firstBall.mass + secondBall.mass);
        }
        return false;
    }

    /*
    collisionPointX =
 ((firstBall.x * secondBall.radius) + (secondBall.x * firstBall.radius))
 / (firstBall.radius + secondBall.radius);

collisionPointY =
 ((firstBall.y * secondBall.radius) + (secondBall.y * firstBall.radius))
 / (firstBall.radius + secondBall.radius);
     */
}

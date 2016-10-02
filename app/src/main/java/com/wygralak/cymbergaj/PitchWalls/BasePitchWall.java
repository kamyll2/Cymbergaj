package com.wygralak.cymbergaj.PitchWalls;

import android.graphics.Paint;
import android.graphics.RectF;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;

/**
 * Created by Kamil on 2016-04-14.
 */
public abstract class BasePitchWall extends RectF implements ICollisionInterpreter{
    public static final float BASE_WALL_THICKNESS = 15f;
    public static final float DEFAULT_SPEED_ABSORB = 0.8f;
    public static final float INCREASED_SPEED_ABSORB = 0.5f;
    public static final float DECREASED_SPEED_ABSORB = 1.4f;
    protected static float defaultGoalSize;

    protected Paint currentPaint;

    public Paint getCurrentPaint(){
        return currentPaint;
    }

    public abstract void updateSize(int pitchWidth, int pitchHeight);

    protected abstract boolean isCollision(float x, float y);

    protected void validateBallOutsideWall(ICollisionInvoker invoker){
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
    }
}

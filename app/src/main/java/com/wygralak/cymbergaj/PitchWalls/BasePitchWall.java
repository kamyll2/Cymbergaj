package com.wygralak.cymbergaj.PitchWalls;

import android.graphics.Paint;
import android.graphics.RectF;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;

/**
 * Created by Kamil on 2016-04-14.
 */
public abstract class BasePitchWall extends RectF implements ICollisionInterpreter{
    public static final float BASE_WALL_THICKNESS = 15f;
    public static final float DEFAULT_SPEED_ABSORB = 0.9f;
    public static final float INCREASED_SPEED_ABSORB = 0.5f;
    public static final float DECREASED_SPEED_ABSORB = 1.4f;
    protected static float defaultGoalSize;

    protected Paint currentPaint;

    public Paint getCurrentPaint(){
        return currentPaint;
    }

    public abstract void updateSize(int pitchWidth, int pitchHeight);
}

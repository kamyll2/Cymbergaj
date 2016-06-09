package com.wygralak.cymbergaj.PitchWalls;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.BallEngine;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class EastDownPitchWall extends BasePitchWall {

    public EastDownPitchWall() {
        currentPaint = new Paint();
        currentPaint.setColor(Color.CYAN);
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        defaultGoalSize = pitchHeight / 2f;
        super.set((float) pitchWidth - BASE_WALL_THICKNESS, (float) pitchHeight / 2f + defaultGoalSize / 2f, (float) pitchWidth, (float) pitchHeight - BASE_WALL_THICKNESS);
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        if (isCollision(x, y)) {
            invoker.updateVector(new Vector2(-Math.abs(currentVector.x), currentVector.y));
            validateBallOutsideWall(invoker);
            invoker.updateSpeedWithRatio(DEFAULT_SPEED_ABSORB);
            return true;
        }
        return false;
    }

    @Override
    protected boolean isCollision(float x, float y) {
        return y + BallEngine.BALL_RADIUS > this.top && x + BallEngine.BALL_RADIUS > this.left;
    }
}

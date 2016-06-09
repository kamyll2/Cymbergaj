package com.wygralak.cymbergaj.PitchWalls;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.BallEngine;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class WestUpPitchWall extends BasePitchWall {

    public WestUpPitchWall() {
        currentPaint = new Paint();
        currentPaint.setColor(Color.BLUE);
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        defaultGoalSize = pitchHeight / 2f;
        super.set(0f, BASE_WALL_THICKNESS, BASE_WALL_THICKNESS, (float) pitchHeight / 2f - defaultGoalSize / 2f);
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        if (y - BallEngine.BALL_RADIUS < this.bottom && x - BallEngine.BALL_RADIUS < this.right) {
            invoker.updateSpeedWithRatio(DEFAULT_SPEED_ABSORB);
            invoker.updateVector(new Vector2(Math.abs(currentVector.x), currentVector.y));
            return true;
        }
        return false;
    }
}

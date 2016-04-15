package com.wygralak.cymbergaj.PitchWalls;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.BallEngine;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class SouthLeftPitchWall extends BasePitchWall {

    public SouthLeftPitchWall() {
        currentPaint = new Paint();
        currentPaint.setColor(Color.BLUE);
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        super.set(0f, pitchHeight - BASE_WALL_THICKNESS, (float) pitchWidth / 2f, pitchHeight);
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        if (y + BallEngine.BALL_RADIUS > this.top) {
            invoker.updateVector(new Vector2(currentVector.x, -Math.abs(currentVector.y)));
            return true;
        }
        return false;
    }
}

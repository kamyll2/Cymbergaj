package com.wygralak.cymbergaj.PitchWalls;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.BallEngine;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class NorthLeftPitchWall extends BasePitchWall {

    public NorthLeftPitchWall() {
        currentPaint = new Paint();
        currentPaint.setColor(Color.BLUE);
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        if (y - BallEngine.BALL_RADIUS < this.bottom ) {
            invoker.updateVector(new Vector2(currentVector.x, Math.abs(currentVector.y)));
            return true;
        }
        return false;
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        super.set(0f, 0f, (float) pitchWidth / 2f, BASE_WALL_THICKNESS);
    }

}

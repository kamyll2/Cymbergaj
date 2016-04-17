package com.wygralak.cymbergaj.PitchWalls;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.BallEngine;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class WestDownPitchWall extends BasePitchWall {

    public WestDownPitchWall() {
        currentPaint = new Paint();
        currentPaint.setColor(Color.BLUE);
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        defaultGoalSize = pitchHeight / 2f;
        super.set(0f, (float) pitchHeight / 2f + defaultGoalSize / 2f, BASE_WALL_THICKNESS, (float) pitchHeight - BASE_WALL_THICKNESS);
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        if(y + BallEngine.BALL_RADIUS > this.top && x - BallEngine.BALL_RADIUS < this.right){
            invoker.updateVector(new Vector2(Math.abs(currentVector.x), currentVector.y));
            return true;
        }
        return false;
    }
}

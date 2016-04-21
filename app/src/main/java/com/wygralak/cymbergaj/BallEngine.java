package com.wygralak.cymbergaj;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.PitchWalls.BasePitchWall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2016-03-10.
 */
public class BallEngine implements ICollisionInvoker {
    public static final float BALL_RADIUS = 20f;
    public static final Paint BALL_PAINT = new Paint();

    protected List<ICollisionInterpreter> collisionables;

    protected float currentX;
    protected float currentY;
    private float pitchWidth;
    private float pitchHeight;
    protected int speed = 6;

    protected MainActivity mainActivity;

    private Vector2 currentVector;


    public BallEngine(MainActivity mainActivity) {
        //TODO delete mainActivity from fields
        this.mainActivity = mainActivity;
        BALL_PAINT.setColor(Color.WHITE);
        collisionables = new ArrayList<>();
        currentVector = new Vector2(0.1f, 0.1f).normalize();
    }

    public void setupDefaultPosition(float pitchWidth, float pitchHeight) {
        this.pitchWidth = pitchWidth;
        this.pitchHeight = pitchHeight;
        currentX = pitchWidth / 2f;
        currentY = pitchHeight / 2f;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void addColissionable(ICollisionInterpreter collisionInterpreter) {
        collisionables.add(collisionInterpreter);
    }

    public void addColissionables(List<BasePitchWall> list) {
        collisionables.addAll(list);
    }

    public void removeColissionable(ICollisionInterpreter collisionInterpreter) {
        collisionables.remove(collisionInterpreter);
    }

    public void updatePosition() {
        currentX = currentX + speed * currentVector.x;
        currentY = currentY + speed * currentVector.y;

        for (int i = 0; i < collisionables.size(); i++) {
            if (collisionables.get(i).checkForCollisionAndHandle(this, currentVector, currentX, currentY)) {
                break;
            }
        }
    }

    @Override
    public void updateVector(Vector2 angle) {
        currentVector = angle;
        mainActivity.setStatusText(currentVector.toString());
    }

    @Override
    public Vector2 getCurrentVector() {
        return currentVector;
    }

    @Override
    public void updatePosition(float newX, float newY) {
        currentY = newY;
        currentX = newX;
    }
}

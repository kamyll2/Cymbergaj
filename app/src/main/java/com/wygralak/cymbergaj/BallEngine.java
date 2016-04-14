package com.wygralak.cymbergaj;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;

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

    private float angle = 15f;

    public BallEngine(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        BALL_PAINT.setColor(Color.WHITE);
        collisionables = new ArrayList<>();
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

    public void updatePosition() {
        currentX = currentX + (float) Math.sin(Math.toRadians(angle)) * speed;
        currentY = currentY + (float) Math.cos(Math.toRadians(angle)) * speed;

        for (int i = 0; i < collisionables.size(); i++) {
            if (collisionables.get(i).checkForCollisionAndHandle(this, currentX, currentY)) {
                break;
            }
        }
    }

    public void addColissionable(ICollisionInterpreter collisionInterpreter) {
        collisionables.add(collisionInterpreter);
    }

    public void removeColissionable(ICollisionInterpreter collisionInterpreter) {
        collisionables.remove(collisionInterpreter);
    }

    @Override
    public void updateAngle(float angle) {
        int mul = angle > 360 ? -1 :
                (angle < 0 ? 1 : 0);
        this.angle = angle + mul * 360;
        mainActivity.setStatusText(String.valueOf(this.angle));
        //this.angle*=-1;
    }

    @Override
    public float getCurrentAngle() {
        return angle;
    }

    @Override
    public void updateVector(Vector2 angle) {

    }

    @Override
    public Vector2 getCurrentVector() {
        return null;
    }
}

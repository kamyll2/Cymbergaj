package com.wygralak.cymbergaj;

/**
 * Created by Kamil on 2016-04-13.
 */
public class VectorBallEngine extends BallEngine{
    private Vector2 movVector;

    public VectorBallEngine(MainActivity mainActivity) {
        super(mainActivity);
        movVector = new Vector2(0.1f, 0.1f).normalize();
    }

    public void updatePosition() {
        /*currentX = currentX + (float) Math.sin(Math.toRadians(angle)) * speed;
        currentY = currentY + (float) Math.cos(Math.toRadians(angle)) * speed;
        for (int i = 0; i < collisionables.size(); i++) {
            if (collisionables.get(i).checkForCollisionAndHandle(this, currentX, currentY)) {
                break;
            }
        }*/
        currentX = currentX + speed * movVector.x;
        currentY = currentY + speed * movVector.y;

        for (int i = 0; i < collisionables.size(); i++) {
            if (collisionables.get(i).checkForCollisionAndHandle(this, currentX, currentY)) {
                break;
            }
        }
    }

    @Override
    public void updateVector(Vector2 angle) {
        movVector = angle;
        mainActivity.setStatusText(movVector.toString());
    }

    @Override
    public Vector2 getCurrentVector() {
        return movVector;
    }
}

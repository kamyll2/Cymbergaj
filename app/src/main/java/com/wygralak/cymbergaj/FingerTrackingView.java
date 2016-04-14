package com.wygralak.cymbergaj;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;

/**
 * Created by Kamil on 2016-03-09.
 */
public class FingerTrackingView extends View implements ICollisionInterpreter {
    private float center_line;

    private BallEngine ballEngine;
    private PlayerEngine player1Engine;
    private PlayerEngine player2Engine;

    public FingerTrackingView(Context context) {
        super(context);
    }

    public FingerTrackingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        center_line = (float) getWidth() / 2f;
        if (player1Engine != null) {
            float player1_x = (float) getWidth() - PlayerEngine.PLAYER_RADIUS - 10f;
            float player1_y = (float) getHeight() / 2f;
            player1Engine.setPitchSize(getWidth(), getHeight());
            player1Engine.updatePosition(player1_x, player1_y);
        }
        if (player2Engine != null) {
            float player2_x = PlayerEngine.PLAYER_RADIUS + 10f;
            float player2_y = (float) getHeight() / 2f;
            player2Engine.setPitchSize(getWidth(), getHeight());
            player2Engine.updatePosition(player2_x, player2_y);
        }
        if (ballEngine != null) {
            ballEngine.setupDefaultPosition(getWidth(), getHeight());
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if (!ready) return true;
        int pointerCount = event.getPointerCount();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (pointerCount == 2) {
                    multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                } else if (pointerCount == 1) {
                    singleMove(event.getX(), event.getY());
                }
                //invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount == 2) {
                    multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                } else if (pointerCount == 1) {
                    singleMove(event.getX(), event.getY());
                }
                //invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (pointerCount == 2) {
                    multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                } else if (pointerCount == 1) {
                    singleMove(event.getX(), event.getY());
                }
                //invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 2) {
                    multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                } else if (pointerCount == 1) {
                    singleMove(event.getX(), event.getY());
                }
                //invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (pointerCount == 2) {
                    multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                } else if (pointerCount == 1) {
                    singleMove(event.getX(), event.getY());
                }
                //invalidate();
                break;
        }
        return true;
    }

    private void multiMove(float x1, float y1, float x2, float y2) {
        int t1 = x1 > center_line ? 1 : 0;
        int t2 = x2 > center_line ? 1 : 0;

        if (t1 + t2 == 1) {
            if (t1 == 1) {
                setPlayer1Movement(x1, y1);
                setPlayer2Movement(x2, y2);
            } else {
                setPlayer2Movement(x1, y1);
                setPlayer1Movement(x2, y2);
            }
        }
    }

    private void singleMove(float x, float y) {
        if (x > center_line) {
            setPlayer1Movement(x, y);
        } else {
            setPlayer2Movement(x, y);
        }
    }

    private void setPlayer1Movement(float x, float y) {
        player1Engine.updatePosition(x, y);
    }

    private void setPlayer2Movement(float x, float y) {
        player2Engine.updatePosition(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(player1Engine.getCurrentX(), player1Engine.getCurrentY(), PlayerEngine.PLAYER_RADIUS, PlayerEngine.PLAYER_PAINT);
        canvas.drawCircle(player2Engine.getCurrentX(), player2Engine.getCurrentY(), PlayerEngine.PLAYER_RADIUS, PlayerEngine.PLAYER_PAINT);
        if (ballEngine != null) {
            canvas.drawCircle(ballEngine.getCurrentX(), ballEngine.getCurrentY(), BallEngine.BALL_RADIUS, BallEngine.BALL_PAINT);
        }
        //((MainActivity)getContext()).setStatusText("Canvas: "+ canvas.isHardwareAccelerated() + " View: " + this.isHardwareAccelerated());
    }

    public void setBallEngine(BallEngine ballEngine) {
        this.ballEngine = ballEngine;
        ballEngine.setupDefaultPosition(getWidth(), getHeight());
    }

    public void setPlayer1Engine(PlayerEngine player1Engine) {
        this.player1Engine = player1Engine;
    }

    public void setPlayer2Engine(PlayerEngine player2Engine) {
        this.player2Engine = player2Engine;
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, float x, float y) {
        return vectorHandle(invoker, x, y);
        /*if (x - BallEngine.BALL_RADIUS < 0 || x + BallEngine.BALL_RADIUS > getWidth()) {
            //float newAngle = invoker.getCurrentAngle() == 90f ? 180f : 90f;
            float newAngle = 180f - (180f + invoker.getCurrentAngle());
            invoker.updateAngle(newAngle);
            return true;
        }
        if (y - BallEngine.BALL_RADIUS < 0 || y + BallEngine.BALL_RADIUS > getHeight()) {
            float newAngle = 270f - (90f + invoker.getCurrentAngle());
            invoker.updateAngle(newAngle);
            return true;
        }
        return false;*/
    }

    private boolean vectorHandle(ICollisionInvoker invoker, float x, float y){
        if(x<BallEngine.BALL_RADIUS || x + BallEngine.BALL_RADIUS > getWidth()){
            Vector2 currentVector = invoker.getCurrentVector();
            invoker.updateVector(new Vector2(-currentVector.x, currentVector.y));
            return true;
        }
        if (y < BallEngine.BALL_RADIUS || y + BallEngine.BALL_RADIUS > getHeight()) {
            Vector2 currentVector = invoker.getCurrentVector();
            invoker.updateVector(new Vector2(currentVector.x, -currentVector.y));
            return true;
        }
        return false;
    }
}

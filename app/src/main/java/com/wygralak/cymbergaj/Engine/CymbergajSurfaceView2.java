package com.wygralak.cymbergaj.Engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.wygralak.cymbergaj.ColissionUtils.ICollisionInterpreter;
import com.wygralak.cymbergaj.ColissionUtils.ICollisionInvoker;
import com.wygralak.cymbergaj.PitchWalls.BasePitchWall;
import com.wygralak.cymbergaj.PitchWalls.EastDownPitchWall;
import com.wygralak.cymbergaj.PitchWalls.EastUpPitchWall;
import com.wygralak.cymbergaj.PitchWalls.NorthLeftPitchWall;
import com.wygralak.cymbergaj.PitchWalls.NorthRightPitchWall;
import com.wygralak.cymbergaj.PitchWalls.SouthLeftPitchWall;
import com.wygralak.cymbergaj.PitchWalls.SouthRightPitchWall;
import com.wygralak.cymbergaj.PitchWalls.WestDownPitchWall;
import com.wygralak.cymbergaj.PitchWalls.WestUpPitchWall;
import com.wygralak.cymbergaj.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2016-10-02.
 */
public class CymbergajSurfaceView2 extends SurfaceView implements SurfaceHolder.Callback {
    public class CymbergajThread extends Thread implements ICollisionInterpreter {

        /*
         * State-tracking constants
         */
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;

        /*
         * Member (state) fields
         */

        /**
         * Current height of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasHeight = 1;

        /**
         * Current width of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasWidth = 1;

        /**
         * Message handler used by thread to interact with TextView
         */
        private Handler mHandler;

        /**
         * Used to figure out elapsed time between frames
         */
        private long mLastTime;

        /**
         * The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN
         */
        private int mMode;

        /**
         * Indicate whether the surface has been created & is ready to draw
         */
        private boolean mRun = false;

        private final Object mRunLock = new Object();

        /**
         * Handle to the surface manager object we interact with
         */
        private SurfaceHolder mSurfaceHolder;

        /**
         * Engine objects
         */
        private List<BasePitchWall> pitchWalls;
        private BallEngine ballEngine;
        private PlayerEngine player2Engine;
        private PlayerEngine player1Engine;

        /**
         * Cymbergaj self objects
         */
        private float center_line;

        public CymbergajThread(SurfaceHolder surfaceHolder, Context context,
                               Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;

            generatePitchWalls();
            ballEngine = new BallEngine();
            player1Engine = new PlayerEngine();
            player2Engine = new PlayerEngine();

            ballEngine.addColissionable(this);
            ballEngine.addColissionable(player1Engine);
            ballEngine.addColissionable(player2Engine);
            ballEngine.addColissionables(pitchWalls);
        }

        private void generatePitchWalls() {
            pitchWalls = new ArrayList<>();
            pitchWalls.add(new NorthLeftPitchWall());
            pitchWalls.add(new NorthRightPitchWall());
            pitchWalls.add(new SouthLeftPitchWall());
            pitchWalls.add(new SouthRightPitchWall());
            pitchWalls.add(new WestUpPitchWall());
            pitchWalls.add(new WestDownPitchWall());
            pitchWalls.add(new EastUpPitchWall());
            pitchWalls.add(new EastDownPitchWall());
        }

        @Override
        public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
            //TODO NOTIFY PLAYER SCORED!
            if (x - BallEngine.BALL_RADIUS > mCanvasWidth) {
                //((MainActivity) getContext()).setStatusText("GOOL LEFT");
                invoker.updatePositionDirectly(mCanvasWidth / 2f, mCanvasHeight / 2f);
                //refree.notifyPlayer2Scored();
                //return true;
            } else if (x + BallEngine.BALL_RADIUS < 0) {
                //((MainActivity) getContext()).setStatusText("GOOL RIGHT");
                invoker.updatePositionDirectly(mCanvasWidth / 2f, mCanvasHeight / 2f);
                //refree.notifyPlayer1Scored();
                //return true;
            }
            return false;
        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
                //TODO inicjalizacja pozycji

                mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
            }
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
                setState(STATE_PAUSE);
                //TODO restore state
            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING) updatePhysics();
                        // Critical section. Do not allow mRun to be set false until
                        // we are sure all canvas draw operations are complete.
                        //
                        // If mRun has been toggled false, inhibit canvas operations.
                        synchronized (mRunLock) {
                            if (mRun) doDraw(c);
                        }
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
                //TODO save state
            }
            return map;
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            // Do not allow mRun to be modified while any canvas operations
            // are potentially in-flight. See doDraw().
            synchronized (mRunLock) {
                mRun = b;
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode one of the STATE_* constants
         * @see #setState(int, CharSequence)
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode    one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             */
            synchronized (mSurfaceHolder) {
                mMode = mode;

                if (mMode == STATE_RUNNING) {
                    Message msg = mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("text", "");
                    b.putInt("viz", View.INVISIBLE);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                } else {
                    //TODO state !running, launch screen
                    /*mRotating = 0;
                    mEngineFiring = false;
                    Resources res = mContext.getResources();
                    CharSequence str = "";
                    if (mMode == STATE_READY)
                        str = res.getText(R.string.mode_ready);
                    else if (mMode == STATE_PAUSE)
                        str = res.getText(R.string.mode_pause);
                    else if (mMode == STATE_LOSE)
                        str = res.getText(R.string.mode_lose);
                    else if (mMode == STATE_WIN)
                        str = res.getString(R.string.mode_win_prefix)
                                + mWinsInARow + " "
                                + res.getString(R.string.mode_win_suffix);

                    if (message != null) {
                        str = message + "\n" + str;
                    }

                    if (mMode == STATE_LOSE) mWinsInARow = 0;

                    Message msg = mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("text", str.toString());
                    b.putInt("viz", View.VISIBLE);
                    msg.setData(b);
                    mHandler.sendMessage(msg);*/
                }
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                center_line = (float) mCanvasWidth / 2f;

                float player1_x = (float) mCanvasWidth - PlayerEngine.PLAYER_RADIUS - 10f;
                float player1_y = (float) mCanvasHeight / 2f;
                player1Engine.setPitchSize(mCanvasWidth, mCanvasHeight);
                player1Engine.updatePosition(player1_x, player1_y);

                float player2_x = PlayerEngine.PLAYER_RADIUS + 10f;
                float player2_y = (float) mCanvasHeight / 2f;
                player2Engine.setPitchSize(mCanvasWidth, mCanvasHeight);
                player2Engine.updatePosition(player2_x, player2_y);

                ballEngine.setupDefaultPosition(mCanvasWidth, mCanvasHeight);

                updatePitchWallSizes(mCanvasWidth, mCanvasHeight);
            }
        }

        private void updatePitchWallSizes(int pitchWidth, int pitchHeight) {
            for (int i = 0; i < pitchWalls.size(); i++) {
                pitchWalls.get(i).updateSize(pitchWidth, pitchHeight);
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
        }

        boolean doTouchEvent(MotionEvent event) {
            boolean handled = true;
            //synchronized (mSurfaceHolder) {
            if (mMode == STATE_RUNNING) {
                int pointerCount = event.getPointerCount();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (pointerCount == 2) {
                            multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                        } else if (pointerCount == 1) {
                            singleMove(event.getX(), event.getY());
                        } else {
                            handled = false;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (pointerCount == 2) {
                            multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                        } else if (pointerCount == 1) {
                            singleMove(event.getX(), event.getY());
                        } else {
                            handled = false;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        if (pointerCount == 2) {
                            multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                        } else if (pointerCount == 1) {
                            singleMove(event.getX(), event.getY());
                        } else {
                            handled = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (pointerCount == 2) {
                            multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                        } else if (pointerCount == 1) {
                            singleMove(event.getX(), event.getY());
                        } else {
                            handled = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pointerCount == 2) {
                            multiMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                        } else if (pointerCount == 1) {
                            singleMove(event.getX(), event.getY());
                        } else {
                            handled = false;
                        }
                        break;
                }
            } else {
                handled = false;
            }
            //}
            return handled;
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

        /**
         * Draws the ship, fuel/speed bars, and background to the provided
         * Canvas.
         */
        private void doDraw(Canvas canvas) {
            canvas.drawColor(Color.GREEN);
            canvas.drawCircle(player1Engine.getCurrentX(), player1Engine.getCurrentY(), PlayerEngine.PLAYER_RADIUS, PlayerEngine.PLAYER_PAINT);
            canvas.drawCircle(player2Engine.getCurrentX(), player2Engine.getCurrentY(), PlayerEngine.PLAYER_RADIUS, PlayerEngine.PLAYER_PAINT);
            if (ballEngine != null) {
                canvas.drawCircle(ballEngine.getCurrentX(), ballEngine.getCurrentY(), BallEngine.BALL_RADIUS, BallEngine.BALL_PAINT);
            }
            drawPitchWalls(canvas);
        }

        private void drawPitchWalls(Canvas canvas) {
            for (int i = 0; i < pitchWalls.size(); i++) {
                canvas.drawRect(pitchWalls.get(i), pitchWalls.get(i).getCurrentPaint());
            }
        }

        /**
         * Figures the lander state (x, y, fuel, ...) based on the passage of
         * realtime. Does not invalidate(). Called at the start of draw().
         * Detects the end-of-game and sets the UI to the next state.
         */
        private void updatePhysics() {
            long now = System.currentTimeMillis();

            // Do nothing if mLastTime is in the future.
            // This allows the game-start to delay the start of the physics
            // by 100ms or whatever.
            if (mLastTime > now) return;

            double elapsed = (now - mLastTime) / 1000.0;

            double ratio = elapsed / 0.015d;
            //TODO uwzględnić elapsed
            ballEngine.updatePosition(ratio);
            ballEngine.considerFriction();
            ballEngine.checkForCollisions();

            mLastTime = now;

        }
    }

    /**
     * Handle to the application context, used to e.g. fetch Drawables.
     */
    private Context mContext;

    /**
     * Pointer to the text view to display "Paused.." etc.
     */
    private TextView mStatusText;

    /**
     * The thread that actually draws the animation
     */
    private CymbergajThread thread;

    public CymbergajSurfaceView2(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new CymbergajThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public CymbergajThread getThread() {
        return thread;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.doTouchEvent(event);
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    /**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
        thread.setState(CymbergajThread.STATE_RUNNING);
        thread.doStart();
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();

    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}

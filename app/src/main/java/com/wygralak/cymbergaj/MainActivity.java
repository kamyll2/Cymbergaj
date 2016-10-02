package com.wygralak.cymbergaj;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wygralak.cymbergaj.Engine.CymbergajSurfaceView2;
import com.wygralak.cymbergaj.Engine.ICymbergajRefree;


public class MainActivity extends ActionBarActivity implements IMessageViewer, ICymbergajRefree {

    RelativeLayout overlayView;
    TextView messageTextView;
    TextView countdownTextView;

    CymbergajSurfaceView2 surfaceView;
    private CymbergajSurfaceView2.CymbergajThread gameThread;
    private boolean countdownRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlayView = (RelativeLayout) findViewById(R.id.overlay);
        messageTextView = (TextView) findViewById(R.id.text);
        countdownTextView = (TextView) findViewById(R.id.countdownHolder);
        surfaceView = (CymbergajSurfaceView2) findViewById(R.id.pitch);
        surfaceView.setRefree(this);
        gameThread = surfaceView.getThread();

        overlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameThread.isGameInStateReady() && !countdownRunning) {
                    countdownRunning = true;
                    new Thread(new CountdownRunnable(new CountdownRunnable.ICountdownNotifier() {
                        @Override
                        public void notifyCountdown(final int step) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (step != 0) {
                                        showCountdown("Game will start in", "" + step);
                                    } else {
                                        hideMessageBox();
                                        gameThread.doStart();
                                        countdownRunning = false;
                                    }
                                }
                            });
                        }
                    })).start();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        surfaceView.getThread().pause(); // pause game when Activity pauses
        super.onPause();
    }

    @Override
    public void showMessage(String message) {
        messageTextView.setText(message);
        messageTextView.setVisibility(View.VISIBLE);
        countdownTextView.setVisibility(View.GONE);
        overlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCountdown(String step) {
        countdownTextView.setText(step);
        messageTextView.setVisibility(View.GONE);
        countdownTextView.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCountdown(String message, String step) {
        messageTextView.setText(message);
        countdownTextView.setText(step);
        messageTextView.setVisibility(View.VISIBLE);
        countdownTextView.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMessageBox() {
        overlayView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void notifyPlayer1Scored() {
        if (!countdownRunning) {
            countdownRunning = true;
            new Thread(new CountdownRunnable(new CountdownRunnable.ICountdownNotifier() {
                @Override
                public void notifyCountdown(final int step) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (step != 0) {
                                showCountdown("Player1 scored a goal!", "" + step);
                            } else {
                                hideMessageBox();
                                gameThread.doStart();
                                countdownRunning = false;
                            }
                        }
                    });
                }
            })).start();
        }
    }

    @Override
    public void notifyPlayer2Scored() {
        if (!countdownRunning) {
            countdownRunning = true;
            new Thread(new CountdownRunnable(new CountdownRunnable.ICountdownNotifier() {
                @Override
                public void notifyCountdown(final int step) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (step != 0) {
                                showCountdown("Player2 scored a goal!", "" + step);
                            } else {
                                hideMessageBox();
                                gameThread.doStart();
                                countdownRunning = false;
                            }
                        }
                    });
                }
            })).start();
        }
    }
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatePitchWalls();
        textView = (TextView) findViewById(R.id.statusText);
        ballEngine = new BallEngine(this);
        player1Engine = new PlayerEngine(this);
        player2Engine = new PlayerEngine(this);
        surfaceView = (FingerTrackingView) findViewById(R.id.surfaceView);
        surfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ballEngine.addColissionable(surfaceView);
                ballEngine.addColissionable(player1Engine);
                ballEngine.addColissionable(player2Engine);
                ballEngine.addColissionables(pitchWalls);
            }
        }, 200);
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                startGameWithDelay();
            }
        });
        surfaceView.setRefree(this);
        surfaceView.setBallEngine(ballEngine);
        surfaceView.setPlayer1Engine(player1Engine);
        surfaceView.setPlayer2Engine(player2Engine);
        surfaceView.setPitchWalls(pitchWalls);
    }

    private void startRefreshingThread() {
        refreshingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(15);
                        ballEngine.updatePosition();
                        ballEngine.considerFriction();
                        ballEngine.checkForCollisions();
                        surfaceView.postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        refreshingThread.start();
    }

    @Override
    protected void onDestroy() {
        refreshingThread.interrupt();
        super.onDestroy();
    }

    public void setStatusText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
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
    public void notifyPlayer1Scored() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshingThread.interrupt();
                getSupportActionBar().setTitle("GOOL PLAYER 1");
                surfaceView.setDefaultPositions();
                ballEngine.setDefaultSpeed();
                startGameWithDelay();
            }
        });
    }

    @Override
    public void notifyPlayer2Scored() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshingThread.interrupt();
                getSupportActionBar().setTitle("GOOL PLAYER 2");
                surfaceView.setDefaultPositions();
                ballEngine.setDefaultSpeed();
                startGameWithDelay();
            }
        });
    }

    private void startGameWithDelay() {
        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.setCancelable(false);
        }
        dialog.setTitle("3");
        dialog.show();
        new Thread(new CountdownRunnable(this)).start();

    }

    @Override
    public void notifyCountdown(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (i != 0) {
                    dialog.setTitle(String.valueOf(i));
                } else {
                    dialog.hide();
                    startRefreshingThread();
                }
            }
        });
    }*/
}

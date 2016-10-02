package com.wygralak.cymbergaj;

import android.app.Dialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wygralak.cymbergaj.Engine.BallEngine;
import com.wygralak.cymbergaj.Engine.FingerTrackingView;
import com.wygralak.cymbergaj.Engine.ICymbergajRefree;
import com.wygralak.cymbergaj.Engine.PlayerEngine;
import com.wygralak.cymbergaj.PitchWalls.BasePitchWall;
import com.wygralak.cymbergaj.PitchWalls.EastDownPitchWall;
import com.wygralak.cymbergaj.PitchWalls.EastUpPitchWall;
import com.wygralak.cymbergaj.PitchWalls.NorthLeftPitchWall;
import com.wygralak.cymbergaj.PitchWalls.NorthRightPitchWall;
import com.wygralak.cymbergaj.PitchWalls.SouthLeftPitchWall;
import com.wygralak.cymbergaj.PitchWalls.SouthRightPitchWall;
import com.wygralak.cymbergaj.PitchWalls.WestDownPitchWall;
import com.wygralak.cymbergaj.PitchWalls.WestUpPitchWall;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ICymbergajRefree {

    private TextView textView;

    FingerTrackingView pitch;
    private Thread refreshingThread;
    private BallEngine ballEngine;
    private PlayerEngine player2Engine;
    private PlayerEngine player1Engine;
    private List<BasePitchWall> pitchWalls;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatePitchWalls();
        textView = (TextView) findViewById(R.id.statusText);
        ballEngine = new BallEngine(this);
        player1Engine = new PlayerEngine(this);
        player2Engine = new PlayerEngine(this);
        pitch = (FingerTrackingView) findViewById(R.id.pitch);
        pitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                ballEngine.addColissionable(pitch);
                ballEngine.addColissionable(player1Engine);
                ballEngine.addColissionable(player2Engine);
                ballEngine.addColissionables(pitchWalls);
            }
        }, 200);
        pitch.post(new Runnable() {
            @Override
            public void run() {
                startGameWithDelay();
            }
        });
        pitch.setRefree(this);
        pitch.setBallEngine(ballEngine);
        pitch.setPlayer1Engine(player1Engine);
        pitch.setPlayer2Engine(player2Engine);
        pitch.setPitchWalls(pitchWalls);
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
                        pitch.postInvalidate();
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
                pitch.setDefaultPositions();
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
                pitch.setDefaultPositions();
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
    }
}

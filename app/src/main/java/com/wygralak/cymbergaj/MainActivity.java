package com.wygralak.cymbergaj;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

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


public class MainActivity extends ActionBarActivity {

    private TextView textView;

    FingerTrackingView pitch;
    private Thread refreshingThread;
    private BallEngine ballEngine;
    private PlayerEngine player2Engine;
    private PlayerEngine player1Engine;
    private List<BasePitchWall> pitchWalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatePitchWalls();
        textView = (TextView) findViewById(R.id.statusText);
        ballEngine = new BallEngine(this);
        player1Engine = new PlayerEngine();
        player2Engine = new PlayerEngine();
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
                startRefreshingThread();
            }
        });
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

    public void setStatusText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    private void generatePitchWalls(){
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
}

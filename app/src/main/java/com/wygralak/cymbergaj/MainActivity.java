package com.wygralak.cymbergaj;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private TextView textView;

    FingerTrackingView pitch;
    private Thread refreshingThread;
    private BallEngine ballEngine;
    private PlayerEngine player2Engine;
    private PlayerEngine player1Engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}

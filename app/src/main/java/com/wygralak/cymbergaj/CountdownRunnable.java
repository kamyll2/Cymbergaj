package com.wygralak.cymbergaj;

import com.wygralak.cymbergaj.Engine.ICymbergajRefree;

/**
 * Created by Kamil on 2016-04-17.
 */
public class CountdownRunnable implements Runnable {

    private ICymbergajRefree refree;

    public CountdownRunnable(ICymbergajRefree refree) {
        this.refree = refree;
    }

    @Override
    public void run() {
        for (int i = 3; i >= 0; i--) {
            refree.notifyCountdown(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                refree.notifyCountdown(0);
            }
        }
    }
}

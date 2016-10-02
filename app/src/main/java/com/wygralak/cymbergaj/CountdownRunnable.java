package com.wygralak.cymbergaj;

/**
 * Created by Kamil on 2016-04-17.
 */
public class CountdownRunnable implements Runnable {

    private ICountdownNotifier notifier;

    public CountdownRunnable(ICountdownNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void run() {
        for (int i = 3; i >= 0; i--) {
            notifier.notifyCountdown(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                notifier.notifyCountdown(0);
            }
        }
    }

    public interface ICountdownNotifier{
        void notifyCountdown(int step);
    }
}

package com.wygralak.cymbergaj;

/**
 * Created by Kamil on 2016-10-02.
 */
public interface IMessageViewer {
    void showMessage(String message);
    void showCountdown(String step);
    void showCountdown(String message, String step);
    void hideMessageBox();
}

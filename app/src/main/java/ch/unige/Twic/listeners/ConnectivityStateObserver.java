package ch.unige.Twic.listeners;

import android.content.Context;


public interface ConnectivityStateObserver {
    public void update(ConnectivityState observable, Boolean isOnline);
    public Context getContext();
}

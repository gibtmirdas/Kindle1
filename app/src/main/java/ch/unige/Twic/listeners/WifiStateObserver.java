package ch.unige.Twic.listeners;

import android.content.Context;


public interface WifiStateObserver {
    public void update(WifiState observable, Boolean isOnline);
    public Context getContext();
}

package ch.unige.Twic.listeners;

import android.content.Context;


public interface ConnectivityStateObserver {
    void update(Boolean isOnline);
    Context getContext();
}

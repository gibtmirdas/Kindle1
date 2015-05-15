package ch.unige.Twic.connectivity;

import android.content.Context;

/**
 * Observer interface for the connectivity state
 */
public interface ConnectivityStateObserver {
    void update(Boolean isOnline);
    Context getContext();
}

package ch.unige.Twic.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * Observe the state of the wifi connexion and notify its observers.
 */
public class ConnectivityState {

    ArrayList<ConnectivityStateObserver> observers;

    /**
     * Singleton instance of the class.
     */
    private static ConnectivityState connectivityState;

    /**
     * Get the singleton instance of the class.
     * @return the singleton instance of the class.
     */
    public static ConnectivityState getConnectivityState() {
        if (connectivityState == null) {
            connectivityState = new ConnectivityState();
        }
        return connectivityState;
    }

    private ConnectivityState() {
        observers = new ArrayList<ConnectivityStateObserver>();
    }

    /**
     * Add an observer to monitor the wifi state.
     * @param listener observer that will be notified when th wifi state change.
     */
    public void addObserver(ConnectivityStateObserver listener) {
        observers.add(listener);
    }


    public void updateObservers() {
        for(ConnectivityStateObserver o : observers) {
            o.update(this, isOnline(o.getContext()));
        }
    }

    public Boolean isOnline(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        Boolean isOnline = netInfo != null && (
                        netInfo.getType() == ConnectivityManager.TYPE_ETHERNET ||
                        netInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                        netInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        return isOnline;
    }

};
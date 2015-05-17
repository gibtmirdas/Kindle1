package ch.unige.Twic.connectivity;

import android.content.Context;
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
        observers = new ArrayList<>();
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
            o.update(isOnline(o.getContext()));
        }
    }

    /**
     * Determine if the device is connected to the web by any service (Wifi, mobile, ethernet,...)
     * @param context The application context
     * @return if the device is connected to the web
     */
    public Boolean isOnline(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        return netInfo != null && netInfo.getState().equals(NetworkInfo.State.CONNECTED);
    }

}

package ch.unige.Twic.listeners;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;


public class WifiState {

    ArrayList<WifiStateObserver> observers;

    private static WifiState wifiState;

    public static WifiState getWifiState() {
        if (wifiState == null) {
            wifiState = new WifiState();
        }
        return wifiState;
    }

    private WifiState() {
        observers = new ArrayList<WifiStateObserver>();
    }

    public void addObserver(WifiStateObserver listener) {
        observers.add(listener);
    }

    public void updateObservers() {
        for(WifiStateObserver o : observers) {
            o.update(this, isOnline(o.getContext()));
        }
    }

    public Boolean isOnline(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        Boolean isOnline = netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
        return isOnline;
    }

};
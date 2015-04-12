package ch.unige.Twic.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class WifiListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiState.getWifiState().updateObservers();
    }


};
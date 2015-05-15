package ch.unige.Twic.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ConnectivityListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityState.getConnectivityState().updateObservers();
    }


};
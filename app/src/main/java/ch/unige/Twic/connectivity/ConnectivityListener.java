package ch.unige.Twic.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listener on the status of the device connectivity
 */
public class ConnectivityListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityState.getConnectivityState().updateObservers();
    }
}
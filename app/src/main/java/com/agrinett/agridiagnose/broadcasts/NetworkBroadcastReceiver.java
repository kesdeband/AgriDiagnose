package com.agrinett.agridiagnose.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.agrinett.agridiagnose.data.Repository;

//import static android.net.ConnectivityManager.TYPE_MOBILE;
//import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.agrinett.agridiagnose.broadcasts.INetworkListener.TYPE_CONNECTED;
import static com.agrinett.agridiagnose.broadcasts.INetworkListener.TYPE_NOT_CONNECTED;
import static com.agrinett.agridiagnose.broadcasts.INetworkListener.TYPE_NOT_SYNC;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private final INetworkListener listener;

    public NetworkBroadcastReceiver(INetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = getConnectivityStatus(context);
        listener.onNetworkStatusChanged(status);
    }

    private int getConnectivityStatus(Context context) {
        Repository repository = new Repository(context);
        int results;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;

        if(repository.QueryIsSynced()) {
            results = TYPE_CONNECTED;
        }
        else {
            results = TYPE_NOT_SYNC;
            if(activeNetwork == null) {
                results = TYPE_NOT_CONNECTED;
            }
        }
        return results;
    }
}

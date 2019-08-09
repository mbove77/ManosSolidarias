package com.bove.martin.manossolidarias.activities.broadcast;

/**
 * Created by Martín Bove on 23/02/2019.
 * E-mail: mbove77@gmail.com
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class NetworkChangeReceiver extends BroadcastReceiver {
    public static final String NETWORK_AVAILABLE_ACTION = "com.bove.martin.manossolidarias.NetworkAvailable";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent networkStateIntent = new Intent(NETWORK_AVAILABLE_ACTION);
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE,  isConnectedToInternet(context));
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

       /*
        boolean isConnected = connectivityManager.getActiveNetworkInfo()!=null;

        if (isConnected) {
            if(conectionLost) {
                Log.e("NET", "Connectado: " + isConnected);
                Toast.makeText(context, "De nuevo en linea", Toast.LENGTH_SHORT).show();
                //Snackbar.make(context. findViewById(R.id.createAccountActivity), getText(R.string.error_create_account), Snackbar.LENGTH_LONG).show();
                conectionLost = false;
            }
        } else {
            Log.e("NET", "Connectado: " + isConnected);
            Toast.makeText(context, "No connectado", Toast.LENGTH_SHORT).show();
            conectionLost = true;
        }
        */
    }

    private boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.e(NetworkChangeReceiver.class.getName(), e.getMessage());
            return false;
        }
    }
}


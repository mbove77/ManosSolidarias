package com.bove.martin.manossolidarias.activities.broadcast;

/**
 * Created by Martín Bove on 23/02/2019.
 * E-mail: mbove77@gmail.com
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bove.martin.manossolidarias.activities.utils.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatus(context);

        if(status == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toast.makeText(context, "Sin conexión a internet", Toast.LENGTH_LONG).show();
        }
    }
}
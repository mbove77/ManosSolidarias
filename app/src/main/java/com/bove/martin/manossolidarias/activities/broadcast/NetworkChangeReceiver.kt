package com.bove.martin.manossolidarias.activities.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Created by Martín Bove on 23/02/2019.
 * E-mail: mbove77@gmail.com
 */
class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val networkStateIntent = Intent(NETWORK_AVAILABLE_ACTION)
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE, isConnectedToInternet(context))
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent)
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

    private fun isConnectedToInternet(context: Context?): Boolean {
        return try {
            if (context != null) {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
            false
        } catch (e: Exception) {
            Log.e(NetworkChangeReceiver::class.java.name, e.message)
            false
        }
    }

    companion object {
        const val NETWORK_AVAILABLE_ACTION = "com.bove.martin.manossolidarias.NetworkAvailable"
        const val IS_NETWORK_AVAILABLE = "isNetworkAvailable"
    }
}
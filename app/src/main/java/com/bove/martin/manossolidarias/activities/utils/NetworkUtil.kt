package com.bove.martin.manossolidarias.activities.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Martín Bove on 23/02/2019.
 * E-mail: mbove77@gmail.com
 */
object NetworkUtil {
    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0
    fun getConnectivityStatus(context: Context): Int {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): Boolean {
        val conn = getConnectivityStatus(context)
        var status = false
        if (conn == TYPE_WIFI) {
            status = true
        } else if (conn == TYPE_MOBILE) {
            status = true
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = false
        }
        return status
    }
}
package com.bove.martin.manossolidarias.presentation.base

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.widget.ImageView
import androidx.multidex.MultiDex
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.data.broadcast.NetworkChangeReceiver
import com.bumptech.glide.Glide
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

/**
 * Created by Martín Bove on 20/07/2018.
 * E-mail: mbove77@gmail.com
 */
// TODO implementar un observador de conexión
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Para android 7 o superior, registramos el reciber del estado de la conexión.
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(NetworkChangeReceiver(), intentFilter)
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String) {
                Glide.with(imageView.context)
                        .load(uri)
                        .into(imageView)
            }

            override fun placeholder(ctx: Context, tag: String): Drawable {
                return IconicsDrawable(ctx, MaterialDrawerFont.Icon.mdf_person).colorRes(R.color.colorFacebookLight).backgroundColorRes(R.color.colorBackground).sizeDp(56).paddingDp(16)
            }
        })
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
package com.bove.martin.manossolidarias.activities.base;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import androidx.multidex.MultiDex;
import android.widget.ImageView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.broadcast.NetworkChangeReceiver;
import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

/**
 * Created by Martín Bove on 20/07/2018.
 * E-mail: mbove77@gmail.com
 */

// TODO implementar un observador de conexión
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Para android 7 o superior, registramos el reciber del estado de la conexión.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), intentFilter);


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext())
                        .load(uri)
                        .into(imageView);
            }
            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return  new IconicsDrawable(ctx, MaterialDrawerFont.Icon.mdf_person).colorRes(R.color.colorFacebookLight).backgroundColorRes(R.color.colorBackground).sizeDp(56).paddingDp(16);
            }
        });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

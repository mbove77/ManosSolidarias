package com.bove.martin.manossolidarias.activities.base;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.widget.ImageView;

import com.bove.martin.manossolidarias.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Martín Bove on 20/07/2018.
 * E-mail: mbove77@gmail.com
 */

// TODO implementar un observador de conexión
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
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

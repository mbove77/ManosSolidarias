package com.bove.martin.manossolidarias.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.adapters.PagerAdapter;
import com.bove.martin.manossolidarias.model.Institucion;
import com.google.gson.Gson;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

public class OngInfoActivity extends BaseActivity implements FragmentComunication {

    private Institucion ong;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    public static final int INFO_FRAGMENT = 0;
    public static final int MAP_FRAGMENT = 1;
    public static final int MENSAJE_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ong_info);

        // Obtenemos el objeto guardado previamente en las pref
        Gson gson = new Gson();
        String json = getPreferences().getString("institucion", "");
        ong = gson.fromJson(json, Institucion.class);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(ong.getNombre());
        setSupportActionBar(myToolbar);

        // Cargamos el NavDrawer
        DrawerUtil.getDrawer(this, myToolbar);

        // Ocultamos el cuadro de cargando
        hideProgressDialog();

        tabLayout = findViewById(R.id.tabLayout);
        int iconsize = 30;
        Drawable iconInfo = new IconicsDrawable(this, FontAwesome.Icon.faw_info_circle).color(Color.WHITE).sizeDp(iconsize);
        Drawable iconMap = new IconicsDrawable(this, FontAwesome.Icon.faw_map2).color(Color.WHITE).sizeDp(iconsize);
        Drawable iconSms = new IconicsDrawable(this, FontAwesome.Icon.faw_comments2).color(Color.WHITE).sizeDp(iconsize);

        tabLayout.addTab(createTab(getString(R.string.info), iconInfo));
        tabLayout.addTab(createTab(getString(R.string.map), iconMap));
        tabLayout.addTab(createTab(getString(R.string.sms), iconSms));

        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // TODO podemos sacar el ong de este adapter por que se recupera en el los fragments de la sharedPref.
        viewPager = findViewById(R.id.viewPager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), ong);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public void enviarONG(Institucion institucion) {

    }

    // Custom Tab con icono al lado izquierdo
    private TabLayout.Tab createTab(String text, Drawable icon){
        TabLayout.Tab tab = tabLayout.newTab().setText(text).setIcon(icon).setCustomView(R.layout.custom_tab);

        // remove imageView bottom margin
        if (tab.getCustomView() != null){
            ImageView imageView = (ImageView) tab.getCustomView().findViewById(android.R.id.icon);
            ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) imageView.getLayoutParams());
            lp.bottomMargin = 0;
            imageView.requestLayout();
        }

        return tab;
    }

}

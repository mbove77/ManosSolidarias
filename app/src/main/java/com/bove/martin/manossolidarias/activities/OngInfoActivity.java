package com.bove.martin.manossolidarias.activities;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.adapters.PagerAdapter;
import com.bove.martin.manossolidarias.model.Institucion;
import com.google.gson.Gson;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.readystatesoftware.viewbadger.BadgeView;

public class OngInfoActivity extends BaseActivity implements FragmentComunication {

    private Institucion ong;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    private BadgeView badge;

    // Fragment order
    public static final int INFO_FRAGMENT = 0;
    public static final int MENSAJE_FRAGMENT = 1;
    public static final int MAP_FRAGMENT = 2;

    private TabLayout.Tab mensajeTab;
    private TabLayout.Tab infoTab;
    private TabLayout.Tab mapTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ong_info);

        // Obtenemos el objeto guardado previamente en las pref
        Gson gson = new Gson();
        String json = getSharedPreferences().getString("institucion", "");
        ong = gson.fromJson(json, Institucion.class);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(ong.getNombre());
        setSupportActionBar(myToolbar);

        // Cargamos el NavDrawer
        DrawerUtil.getDrawer(this, myToolbar, 0);

        // Ocultamos el cuadro de cargando
        hideProgressDialog();

        tabLayout = findViewById(R.id.tabLayout);
        int iconsize = 30;
        Drawable iconInfo = new IconicsDrawable(this, FontAwesome.Icon.faw_info_circle).color(Color.WHITE).sizeDp(iconsize);
        Drawable iconMap = new IconicsDrawable(this, FontAwesome.Icon.faw_map2).color(Color.WHITE).sizeDp(iconsize);
        Drawable iconSms = new IconicsDrawable(this, FontAwesome.Icon.faw_comments2).color(Color.WHITE).sizeDp(iconsize);

        mensajeTab = createTab(getString(R.string.sms), iconSms);
        infoTab = createTab(getString(R.string.info), iconInfo);

        tabLayout.addTab(infoTab);
        tabLayout.addTab(mensajeTab);

        // Si no tiene distancia no agregamos el tab de mapa
        if(ong.getDistancia() != BaseActivity.NO_DISTANCIA) {
            mapTab = createTab(getString(R.string.map), iconMap);
            tabLayout.addTab(mapTab);
        }

        // TODO Mejorar esta implementacion del badger
        //badge = new BadgeView(this, mensajeTab.getCustomView());
        //badge.setText("25");
        // badge.show();

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

        viewPager = findViewById(R.id.viewPager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public void enviarONG(Institucion institucion) {  }

    //TODO Revisar por que no cambia al tab adecuado.
    @Override
    public void changeTab(int tabName) {
        switch (tabName) {
            case INFO_FRAGMENT: {
                viewPager.setCurrentItem(INFO_FRAGMENT);
            }
            case MAP_FRAGMENT: {
                viewPager.setCurrentItem(MAP_FRAGMENT);
            }
            case MENSAJE_FRAGMENT: {
                viewPager.setCurrentItem(MENSAJE_FRAGMENT);
            }
        }

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

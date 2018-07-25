package com.bove.martin.manossolidarias.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.adapters.PagerAdapter;
import com.bove.martin.manossolidarias.model.Institucion;
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

        // Obtenemos la institución
        // Si la referencia no existe volvemos al home
        if(BaseActivity.currentONG  != null) {
            ong = BaseActivity.currentONG;
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(ong.getNombre());
        setSupportActionBar(myToolbar);

        // Cargamos el NavDrawer
        DrawerUtil.getDrawer(this, myToolbar);

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.info).setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_info_circle).color(Color.WHITE).sizeDp(20)));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map).setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_map2).color(Color.WHITE).sizeDp(20)));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.sms).setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_comments2).color(Color.WHITE).sizeDp(20)));

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
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), ong);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public void enviarONG(Institucion institucion) {

    }
}

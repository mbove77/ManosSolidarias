package com.bove.martin.manossolidarias.activities

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil.getDrawer
import com.bove.martin.manossolidarias.adapters.PagerAdapter
import com.bove.martin.manossolidarias.model.Institucion
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.Gson
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import com.readystatesoftware.viewbadger.BadgeView
import kotlinx.android.synthetic.main.activity_ong_info.*

class OngInfoActivity : BaseActivity(), FragmentComunication {
    private lateinit var ong: Institucion

    private lateinit var adapter: PagerAdapter
    private lateinit var badge: BadgeView

    private lateinit var mensajeTab: TabLayout.Tab
    private lateinit var infoTab: TabLayout.Tab
    private lateinit var mapTab: TabLayout.Tab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ong_info)

        // Obtenemos el objeto guardado previamente en las pref
        val gson = Gson()
        val json = getSharedPreferences()!!.getString("institucion", "")
        ong = gson.fromJson(json, Institucion::class.java)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        myToolbar.title = ong.nombre
        setSupportActionBar(myToolbar)

        // Cargamos el NavDrawer
        getDrawer(this, myToolbar, 0)

        // Ocultamos el cuadro de cargando
        hideProgressDialog()


        val iconsize = 30
        val iconInfo: Drawable = IconicsDrawable(this, FontAwesome.Icon.faw_info_circle).color(Color.WHITE).sizeDp(iconsize)
        val iconMap: Drawable = IconicsDrawable(this, FontAwesome.Icon.faw_map2).color(Color.WHITE).sizeDp(iconsize)
        val iconSms: Drawable = IconicsDrawable(this, FontAwesome.Icon.faw_comments2).color(Color.WHITE).sizeDp(iconsize)

        mensajeTab = createTab(getString(R.string.sms), iconSms)
        infoTab = createTab(getString(R.string.info), iconInfo)
        tabLayout.addTab(infoTab)
        tabLayout.addTab(mensajeTab)

        // Si no tiene distancia no agregamos el tab de mapa
        if (ong.distancia != NO_DISTANCIA) {
            mapTab = createTab(getString(R.string.map), iconMap)
            tabLayout.addTab(mapTab)
        }

        // TODO Mejorar esta implementacion del badger
        //badge = new BadgeView(this, mensajeTab.getCustomView());
        //badge.setText("25");
        // badge.show();
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        //viewPager = findViewById(R.id.viewPager)
        adapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
    }

    override fun enviarONG(institucion: Institucion) {}

    //TODO Revisar por que no cambia al tab adecuado.
    override fun changeTab(tabName: Int) {
        when (tabName) {
            INFO_FRAGMENT -> {
                run { viewPager.currentItem = INFO_FRAGMENT }
                run { viewPager.currentItem = MAP_FRAGMENT }
                run { viewPager.currentItem = MENSAJE_FRAGMENT }
            }
            MAP_FRAGMENT -> {
                run { viewPager.currentItem = MAP_FRAGMENT }
                run { viewPager.currentItem = MENSAJE_FRAGMENT }
            }
            MENSAJE_FRAGMENT -> {
                viewPager.currentItem = MENSAJE_FRAGMENT
            }
        }
    }

    // Custom Tab con icono al lado izquierdo
    private fun createTab(text: String, icon: Drawable): TabLayout.Tab {
        val tab = tabLayout!!.newTab().setText(text).setIcon(icon).setCustomView(R.layout.custom_tab)

        // remove imageView bottom margin
        if (tab.customView != null) {
            val imageView = tab.customView!!.findViewById<View>(android.R.id.icon) as ImageView
            val lp = imageView.layoutParams as MarginLayoutParams
            lp.bottomMargin = 0
            imageView.requestLayout()
        }
        return tab
    }

    companion object {
        // Fragment order
        const val INFO_FRAGMENT = 0
        const val MENSAJE_FRAGMENT = 1
        const val MAP_FRAGMENT = 2
    }
}
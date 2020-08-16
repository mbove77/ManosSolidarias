package com.bove.martin.manossolidarias.activities.utils

import android.content.Intent
import android.net.Uri
import androidx.appcompat.widget.Toolbar
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.AboutActivity
import com.bove.martin.manossolidarias.activities.DonationActivity
import com.bove.martin.manossolidarias.activities.HomeActivity
import com.bove.martin.manossolidarias.activities.OngListActivity
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem

/**
 * Created by Martín Bove on 20/07/2018.
 * E-mail: mbove77@gmail.com
 */
object DrawerUtil {
    private var headerResult: AccountHeader? = null
    private var result: Drawer? = null
    @JvmStatic
    fun getDrawer(activity: BaseActivity, toolbar: Toolbar?, selectIndex: Long) {
        val activityName = activity.localClassName
        val nombre = activity.user!!.displayName
        val email = activity.user!!.email
        var foto = activity.user!!.photoUrl
        if (foto == null) foto = Uri.parse("http://placeholder")
        headerResult = AccountHeaderBuilder()
                .withActivity(activity)
                .withCompactStyle(false)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .withAlternativeProfileHeaderSwitching(false)
                .addProfiles(ProfileDrawerItem().withName(nombre).withEmail(email).withIcon(foto))
                .build()

        // Create the drawer
        result = DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar!!)
                .withAccountHeader(headerResult as AccountHeader)
                .addDrawerItems(
                        PrimaryDrawerItem().withName(R.string.home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        PrimaryDrawerItem().withName(R.string.list_ong).withIcon(FontAwesome.Icon.faw_list_alt).withIdentifier(2),
                        PrimaryDrawerItem().withName(R.string.list_donations).withIcon(FontAwesome.Icon.faw_hand_holding_heart).withIdentifier(3),
                        PrimaryDrawerItem().withName(R.string.about).withIcon(FontAwesome.Icon.faw_question_circle).withIdentifier(4)
                )
                .addStickyDrawerItems(
                        PrimaryDrawerItem().withName(R.string.Logout).withIcon(FontAwesome.Icon.faw_sign_out_alt).withIdentifier(5)
                )
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    if (drawerItem != null) {
                        var intent: Intent? = null
                        when (drawerItem.identifier.toInt()) {
                            1 -> intent = Intent(activity, HomeActivity::class.java)
                            2 -> intent = Intent(activity, OngListActivity::class.java)
                            3 -> intent = Intent(activity, DonationActivity::class.java)
                            4 -> intent = Intent(activity, AboutActivity::class.java)
                            5 -> activity.logout()
                        }
                        if (intent != null) {
                            activity.startActivity(intent)
                        }
                    }
                    false
                }
                .withShowDrawerOnFirstLaunch(false)
                .withSelectedItem(selectIndex)
                .build()
    }

    fun updateDrawer(item: IDrawerItem<*, *>?) {
        result!!.updateItem(item!!)
    }
}
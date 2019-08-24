package com.bove.martin.manossolidarias.activities.utils;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.DonationActivity;
import com.bove.martin.manossolidarias.activities.HomeActivity;
import com.bove.martin.manossolidarias.activities.OngListActivity;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Created by Martín Bove on 20/07/2018.
 * E-mail: mbove77@gmail.com
 */
public class DrawerUtil {

    private static AccountHeader headerResult;
    private static Drawer result;

    public static void getDrawer(final BaseActivity activity, Toolbar toolbar, long selectIndex) {
        final String activityName = activity.getLocalClassName();
        String nombre = activity.getUser().getDisplayName();
        String email = activity.getUser().getEmail();
        Uri foto = activity.getUser().getPhotoUrl();
        if(foto == null) foto = Uri.parse("http://placeholder");

        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withCompactStyle(false)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .withAlternativeProfileHeaderSwitching(false)
                .addProfiles(new ProfileDrawerItem().withName(nombre).withEmail(email).withIcon(foto))
                .build();

        // Create the drawer
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.list_ong).withIcon(FontAwesome.Icon.faw_list_alt).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.list_donations).withIcon(FontAwesome.Icon.faw_hand_holding_heart).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.config).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4)
                )
                .addStickyDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.Logout).withIcon(FontAwesome.Icon.faw_sign_out_alt).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            Intent intent = null;
                            // Home item
                            switch (((int) drawerItem.getIdentifier())) {
                                case 1:
                                    intent = new Intent(activity, HomeActivity.class);
                                    break;
                                case 2:
                                    intent = new Intent(activity, OngListActivity.class);
                                    break;
                                case 3:
                                    intent = new Intent(activity, DonationActivity.class);
                                    break;
                                case 5:
                                    activity.logout();
                                    break;
                            }

                            if (intent != null) {
                                activity.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withShowDrawerOnFirstLaunch(false)
                .withSelectedItem(selectIndex)
                .build();

    }

    public static void updateDrawer(IDrawerItem item) {
        result.updateItem(item);
    }
}

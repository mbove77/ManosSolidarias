package com.bove.martin.manossolidarias.activities.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.HomeActivity;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


/**
 * Created by Martín Bove on 20/07/2018.
 * E-mail: mbove77@gmail.com
 */
public class DrawerUtil {

    private static AccountHeader headerResult;
    private static Drawer result;

    //TODO Obtener el email de la cuenta

    public static void getDrawer(final BaseActivity activity, Toolbar toolbar) {
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
                        new PrimaryDrawerItem().withName(R.string.home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1)
                )
                .addStickyDrawerItems(new PrimaryDrawerItem().withName(R.string.Logout).withIcon(FontAwesome.Icon.faw_sign_out_alt).withIdentifier(3))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            Intent intent = null;
                            // Home item
                            if (drawerItem.getIdentifier() == 1) {
                                if(!activityName.equals("activities.HomeActivity")) {
                                    intent = new Intent(activity, HomeActivity.class);
                                    activity.startActivity(intent);
                                }
                            }
                            // Logout item
                            else if (drawerItem.getIdentifier() == 3) {
                                activity.logout();
                            }
                            if (intent != null) {
                                activity.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withShowDrawerOnFirstLaunch(false)
                .build();

            // Add ong list item
            if(activityName.equals("activities.OngListActivity")) {
                PrimaryDrawerItem donation =  new PrimaryDrawerItem().withName(R.string.list_ong).withIcon(FontAwesome.Icon.faw_list_alt).withIdentifier(2);
                result.addItemAtPosition(donation, 2);
                result.setSelection(2);
            }

    }

    public static void updateDrawer(IDrawerItem item) {
        result.updateItem(item);
    }
}

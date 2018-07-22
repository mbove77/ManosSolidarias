package com.bove.martin.manossolidarias.activities.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.bove.martin.manossolidarias.R;
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
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

/**
 * Created by Martín Bove on 20/07/2018.
 * E-mail: mbove77@gmail.com
 */
public class DrawerUtil {

    private static AccountHeader headerResult;
    private static Drawer result;

    //TODO Obtener el email de la cuenta

    public static void getDrawer(final BaseActivity activity, Toolbar toolbar, FirebaseUser user) {
         String nombre = user.getDisplayName();
         String email = user.getEmail();
         Uri foto = user.getPhotoUrl();
         if(foto == null) foto = Uri.parse("http://placeholder");

        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .withAlternativeProfileHeaderSwitching(false)
                .addProfiles(new ProfileDrawerItem().withName(nombre).withEmail(email).withIcon(foto))
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new SectionDrawerItem().withName(R.string.configs),
                        activity.helpDrawerItem = new SecondarySwitchDrawerItem().withName(R.string.showHelp).withChecked(activity.showHelp).withIcon(FontAwesome.Icon.faw_question_circle2).withOnCheckedChangeListener(new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                                activity.resetAyuda(isChecked);
                            }
                        })
                )
                .addStickyDrawerItems(new SecondaryDrawerItem().withName(R.string.Logout).withIcon(FontAwesome.Icon.faw_sign_out_alt).withIdentifier(3))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            Intent intent = null;
                            // Home item
                            if (drawerItem.getIdentifier() == 1) {
                                Toast.makeText(activity, "Opcion 1", Toast.LENGTH_SHORT).show();
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
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    public static void updateDrawer(IDrawerItem item) {
        result.updateItem(item);
    }
}

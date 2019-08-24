package com.bove.martin.manossolidarias.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.bove.martin.manossolidarias.activities.fragments.OngInfoFragment;
import com.bove.martin.manossolidarias.activities.fragments.OngMapFragment;
import com.bove.martin.manossolidarias.activities.fragments.OngMensajesFragment;
import com.bove.martin.manossolidarias.model.Institucion;

import java.util.HashMap;

import static com.bove.martin.manossolidarias.activities.OngInfoActivity.INFO_FRAGMENT;
import static com.bove.martin.manossolidarias.activities.OngInfoActivity.MAP_FRAGMENT;
import static com.bove.martin.manossolidarias.activities.OngInfoActivity.MENSAJE_FRAGMENT;

/**
 * Created by Martín Bove on 23/07/2018.
 * E-mail: mbove77@gmail.com
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numberOfTabs;
    private HashMap mPageReferenceMap;
    private Institucion ong;

    public PagerAdapter(FragmentManager fm, int tabs, Institucion institucion) {
        super(fm);
        this.numberOfTabs = tabs;
        mPageReferenceMap = new HashMap();
        this.ong = institucion;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case INFO_FRAGMENT:
                OngInfoFragment infoFragment = new OngInfoFragment();
                mPageReferenceMap.put(position, infoFragment);
                return infoFragment;
            case MAP_FRAGMENT:
                OngMapFragment mapFragment = new OngMapFragment();
                mPageReferenceMap.put(position, mapFragment);
                return mapFragment;
            case MENSAJE_FRAGMENT:
                OngMensajesFragment smsFragment = new OngMensajesFragment();
                mPageReferenceMap.put(position, smsFragment);
                return smsFragment;
            default:
                return null;
        }
    }

    public Fragment getFragment(int key) {
        return (Fragment) mPageReferenceMap.get(key);
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }
}

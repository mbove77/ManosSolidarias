package com.bove.martin.manossolidarias.adapters

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bove.martin.manossolidarias.activities.OngInfoActivity
import com.bove.martin.manossolidarias.activities.fragments.OngInfoFragment
import com.bove.martin.manossolidarias.activities.fragments.OngMapFragment
import com.bove.martin.manossolidarias.activities.fragments.OngMensajesFragment
import java.util.*

/**
 * Created by Martín Bove on 23/07/2018.
 * E-mail: mbove77@gmail.com
 */
class PagerAdapter(fm: FragmentManager?, private val numberOfTabs: Int) : FragmentStatePagerAdapter(fm!!) {
    private val mPageReferenceMap: HashMap<Int, Fragment> = HashMap()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            OngInfoActivity.INFO_FRAGMENT -> {
                val infoFragment = OngInfoFragment()
                mPageReferenceMap[position] = infoFragment
                infoFragment
            }
            OngInfoActivity.MAP_FRAGMENT -> {
                val mapFragment = OngMapFragment()
                mPageReferenceMap[position] = mapFragment
                mapFragment
            }
            OngInfoActivity.MENSAJE_FRAGMENT -> {
                val smsFragment = OngMensajesFragment()
                mPageReferenceMap[position] = smsFragment
                smsFragment
            }
            else -> Fragment()
        }
    }

    fun getFragment(key: Int): Fragment? = mPageReferenceMap[key]

    override fun getCount(): Int = numberOfTabs

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        mPageReferenceMap.remove(position)
    }
}
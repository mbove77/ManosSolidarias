package com.bove.martin.manossolidarias.activities.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.OngInfoActivity
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.activities.base.BaseActivity.Companion.setVectorForPreLollipop
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication
import com.bove.martin.manossolidarias.activities.utils.truncate
import com.bove.martin.manossolidarias.model.Institucion
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_ong_map.*

class OngMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var rootView: View
    private var callback: FragmentComunication? = null
    private lateinit var ong: Institucion
    private lateinit var gMap: GoogleMap
    private lateinit var preferences: SharedPreferences
    private lateinit var mMapView:MapView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as FragmentComunication
        } catch (e: Exception) {
            throw ClassCastException("$context Debes implementar Fragament Comunication.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_ong_map, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = view.findViewById(R.id.mapView)
        preferences = (activity as OngInfoActivity).getSharedPreferences()!!
        //Fix drawables pre-lolipop
        setVectorForPreLollipop(navButton, R.drawable.ic_directions, context!!, BaseActivity.DRAWABLE_RIGHT)

        loadOng()

        if (mMapView != null) {
            mMapView.onCreate(null)
            mMapView.onResume()
            mMapView.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.uiSettings.isMapToolbarEnabled = false
        val place = LatLng(ong.localizacion!!.latitude, ong.localizacion!!.longitude)
        val marker = gMap.addMarker(MarkerOptions()
                .position(LatLng(ong.localizacion!!.latitude, ong.localizacion!!.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .snippet(String().truncate(ong.direccion!!, 25))
                .title(ong.nombre))
        marker.showInfoWindow()
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15f))

        navButton!!.setOnClickListener {
            val mTitle = ong.nombre
            val geoUri = "http://maps.google.com/maps?q=loc:" + ong.localizacion!!.latitude + "," + ong.localizacion!!.longitude + " (" + mTitle + ")"
            val gmmIntentUri = Uri.parse(geoUri)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
        loadOng()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    // Load current ONG
    private fun loadOng() {
        val gson = Gson()
        val json = preferences.getString("institucion", "")
        ong =  gson.fromJson(json, Institucion::class.java)
    }
}
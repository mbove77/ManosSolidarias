package com.bove.martin.manossolidarias.activities.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication;
import com.bove.martin.manossolidarias.model.Institucion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class OngMapFragment extends Fragment implements OnMapReadyCallback {
    private View rootView;

    private Button navButton;
    private FragmentComunication callback;
    private Institucion ong;

    private GoogleMap gMap;
    private MapView mMapView;

    public OngMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (FragmentComunication) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " Debes implementar Fragament Comunication.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ong_map, container, false);
        navButton = rootView.findViewById(R.id.navButton);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = rootView.findViewById(R.id.mapView);
        if(mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    public Institucion getOng() {
        return ong;
    }

    public void setOng(Institucion ong) {
        this.ong = ong;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng place = new LatLng(ong.getLocalizacion().getLatitude(), ong.getLocalizacion().getLongitude());

        Marker marker = gMap.addMarker(new MarkerOptions()
                .position(new LatLng(ong.getLocalizacion().getLatitude(),ong.getLocalizacion().getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .snippet(truncate(ong.getDireccion(), 25))
                .title(ong.getNombre()));
        marker.showInfoWindow();

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTitle = ong.getNombre();
                String geoUri = "http://maps.google.com/maps?q=loc:" + ong.getLocalizacion().getLatitude() + "," + ong.getLocalizacion().getLongitude() + " (" + mTitle + ")";
                Uri gmmIntentUri = Uri.parse(geoUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            return str;
        }}
}

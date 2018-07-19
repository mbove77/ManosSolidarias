package com.bove.martin.manossolidarias.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.adapters.InstitucionesAdapter;
import com.bove.martin.manossolidarias.model.Institucion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OngListActivity extends BaseActivity implements InstitucionesAdapter.OnItemClickListener {
    private final String TAG = "ONG_LIST";
    private FirebaseFirestore db;
    private String donacion;
    private List<Institucion> instituciones = new ArrayList<Institucion>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout errorLay;
    private Location userLoc;

    private boolean gpsPermiso = false;
    private FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ong_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            donacion = extras.getString("donacion");
        }

        // Mostramos el cargando hasta que lleguen los datos
        showProgressDialog();

        // Instanciamos los elementos
        errorLay = findViewById(R.id.errorMensajeLay);
        recyclerView = findViewById(R.id.recyclerViewOngs);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new InstitucionesAdapter(instituciones, R.layout.ong_item, this, this);

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider));
        recyclerView.addItemDecoration(divider);

        recyclerView.setAdapter(adapter);

        // Chequeo del permiso de localización
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        gpsPermiso = true;
                        Toast.makeText(OngListActivity.this, "Permiso Grantizado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        gpsPermiso = false;
                        Toast.makeText(OngListActivity.this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        // Pedimos la localización del usuario
        if(gpsPermiso) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                userLoc = location;
                                // si ya se cargaron las instituciones entonces actualizamos las distancias.
                                if(instituciones.size() > 0) {
                                    updateDistancias();
                                }
                            }
                        }
                    });
        }

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        db.collection(DB_ONGS)
                .whereGreaterThan("donaciones" + "." + donacion, 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Institucion institucion = document.toObject(Institucion.class);
                                institucion.setKey(document.getId());

                                // si tenemos localización
                                if (userLoc != null) {
                                    float[] distance = new float[2];
                                    Location.distanceBetween(userLoc.getLatitude(), userLoc.getLongitude(), institucion.getLocalizacion().getLatitude(), institucion.getLocalizacion().getLongitude(), distance);
                                    institucion.setDistancia(distance[0]);
                                }
                                instituciones.add(institucion);
                            }
                            if(instituciones.size() > 0) {
                                errorLay.setVisibility(LinearLayout.GONE);
                                hideProgressDialog();
                                if (userLoc != null) {
                                    Collections.sort(instituciones);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                hideProgressDialog();
                                errorLay.setVisibility(LinearLayout.VISIBLE);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());

                        }
                    }
                });
    }

    @Override
    public void onItemClick(Institucion institucion, int posicion) {

    }

    // Update distancias después de cargadas
    private void updateDistancias() {
        for (Institucion institucion : instituciones) {
            float[] distance = new float[2];
            Location.distanceBetween(userLoc.getLatitude(), userLoc.getLongitude(), institucion.getLocalizacion().getLatitude(), institucion.getLocalizacion().getLongitude(), distance);
            institucion.setDistancia(distance[0]);
        }
        Collections.sort(instituciones);
        adapter.notifyDataSetChanged();
    }

    public void algo() {

    }
}

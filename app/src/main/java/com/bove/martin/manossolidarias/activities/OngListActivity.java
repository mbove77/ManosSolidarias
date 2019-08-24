package com.bove.martin.manossolidarias.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.activities.utils.PlayGifView;
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
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OngListActivity extends BaseActivity implements InstitucionesAdapter.OnItemClickListener {
    private final String TAG = "ONG_LIST";
    private FirebaseFirestore db;
    private String donacion;
    private List<Institucion> instituciones = new ArrayList<Institucion>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout errorLay;
    private PlayGifView pGif;
    private IconicsButton backButton;
    private Location userLoc;

    private boolean updateDistance = false;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ong_list);

        // Instanciamos los elementos
        errorLay = findViewById(R.id.errorMensajeLay);
        recyclerView = findViewById(R.id.recyclerViewOngs);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        pGif = findViewById(R.id.viewGif);
        pGif.setImageResource(R.drawable.nofound_error);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Cargamos el NavDrawer
        DrawerUtil.getDrawer(this, myToolbar,2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            donacion = extras.getString("donacion");
        } else {
            // Si no viene una donación mostramos la lista de todas las ong.
            donacion = null;
        }

        // Not found back button
        backButton = findViewById(R.id.notFoundBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(donacion != null) {
                    Intent intentDonations = new Intent(getBaseContext(), DonationActivity.class);
                    startActivity(intentDonations);
                } else {
                    goToHome();
                }
            }
        });

        // Mostramos el cargando hasta que lleguen los datos
        showProgressDialog();

        // Arrancamos el recyclerView
        adapter = new InstitucionesAdapter(instituciones, R.layout.ong_item, this, this);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider)));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);

        // pedimos el permiso de GPS en tiempo de ejecución.
        checkPermission();

        // Pedimos la localización del usuario
        getUserLocation();

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        if(donacion != null) {
            // Lista filtrada por donación
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

                                    // Si la ong esta aprobada
                                    if(institucion.isAprobado()) {
                                        // Si tenemos localización
                                        geoLocateResults(institucion);
                                    }
                                }
                                if (instituciones.size() > 0) {
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
                                // Agregamos la opción de sugerir una nueva ong
                                Institucion addOng = new Institucion(getString(R.string.nueva_ong), true);
                                instituciones.add(addOng);
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                                hideProgressDialog();
                            }
                        }
                    });
        }else {
            // Lista completa
            db.collection(DB_ONGS)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Institucion institucion = document.toObject(Institucion.class);
                                    institucion.setKey(document.getId());

                                    // Si la ong esta aprobada
                                    if(institucion.isAprobado()) {
                                        // Si tenemos localización
                                        geoLocateResults(institucion);
                                    }
                                }
                                if (instituciones.size() > 0) {
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
                                // Agregamos la opción de sugerir una nueva ong
                                Institucion addOng = new Institucion(getString(R.string.nueva_ong), true);
                                instituciones.add(addOng);
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                                hideProgressDialog();
                            }
                        }
                    });

        }
    }

    private void geoLocateResults(Institucion institucion) {
        // Si tenemos localización
        if (userLoc != null && institucion.getLocalizacion().getLatitude() != 0.0) {
            float[] distance = new float[2];
            Location.distanceBetween(userLoc.getLatitude(), userLoc.getLongitude(), institucion.getLocalizacion().getLatitude(), institucion.getLocalizacion().getLongitude(), distance);
            institucion.setDistancia(distance[0]);
        }
        if(institucion.getLocalizacion().getLatitude() == 0.0) {
            institucion.setDistancia(BaseActivity.NO_DISTANCIA);
        }
        instituciones.add(institucion);
    }

    @Override
    public void onItemClick(Institucion institucion, int posicion) {
        if(!institucion.isEspecial()) {
            // Mostramos el cargando por si demora mas de lo habitual
            showProgressDialog();
            Intent intent = new Intent(this, OngInfoActivity.class);

            // Guardamos el objeto en las prefs
            SharedPreferences.Editor prefsEditor = getPreferences().edit();
            Gson gson = new Gson();
            String json = gson.toJson(institucion);
            prefsEditor.putString("institucion", json);
            prefsEditor.commit();

            startActivity(intent);
        } else  {
            // Si es la opción de sugerir ong
            Intent suggestInent = new Intent(this,  SuggestOngActivity.class);
            startActivity(suggestInent);
        }
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            userLoc = location;
                            updateDistancias();
                        }
                    }
                });
    }

    private void checkPermission() {
        // Chequeo del permiso de localización on runtime
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        getUserLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    // TODO Tratar de evitar la segunda llamada y también al método getUserLocation
    // Update distancias después de cargadas las ongs
    private void updateDistancias() {
        if(instituciones.size() > 0 & userLoc != null) {
            for (Institucion institucion : instituciones) {
                if(!institucion.isEspecial() && institucion.getLocalizacion().getLatitude() != 0.0) {
                    float[] distance = new float[2];
                    Location.distanceBetween(userLoc.getLatitude(), userLoc.getLongitude(), institucion.getLocalizacion().getLatitude(), institucion.getLocalizacion().getLongitude(), distance);
                    institucion.setDistancia(distance[0]);
                }
            }
            Collections.sort(instituciones);
            adapter.notifyDataSetChanged();
        }
    }

}

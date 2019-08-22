package com.bove.martin.manossolidarias.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.adapters.DonationAdapter;
import com.bove.martin.manossolidarias.model.Donacion;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DonationActivity extends BaseActivity implements DonationAdapter.OnItemClickListener, DonationAdapter.OnLongClickListener {
    private final String TAG = "Donation Activity";
    private final String SHOW_HELP_KEY = "showHelp";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Donacion> donaciones = new ArrayList<Donacion>();

    private SharedPreferences preferences;
    private Boolean showHelp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.donaciones);
        setSupportActionBar(myToolbar);

        // load preference para mostrar la ayuda 1 sola vez
        preferences = getPreferences();
        showHelp = preferences.getBoolean(SHOW_HELP_KEY, true);

        // load NavDrawer
        DrawerUtil.getDrawer(this, myToolbar,3);

        // Mostramos el cargando hasta que lleguen los datos
        showProgressDialog();

        // Instanciamos los elementos
        recyclerView = findViewById(R.id.recyclerViewDonation);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new DonationAdapter(donaciones, R.layout.donation_item, this, this, this);
        recyclerView.setAdapter(adapter);

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(DB_DONATIONS)
        .orderBy("order", Query.Direction.ASCENDING)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Donacion donacion = document.toObject(Donacion.class);
                        donacion.setKey(document.getId());
                        donaciones.add(donacion);
                    }
                    hideProgressDialog();
                    if(showHelp) {
                        showHelp();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    hideProgressDialog();
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menulogout:
                logout();
                break;
            case R.id.menuAdd:
                Intent i = new Intent(this, AddOngActivity.class);
                startActivity(i);
                break;
            case R.id.menuShowHelp:
                preferences.edit().clear().apply();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    // Muestra la ayuda
    private void showHelp() {
        preferences.edit().putBoolean(SHOW_HELP_KEY, false).apply();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(findViewById(R.id.homeLayout), getString(R.string.donation_help), Snackbar.LENGTH_LONG).show();
            }
        }, 2500);
    }

    @Override
    public void onItemClick(Donacion donacion, int posicion) {
        if(donacion.getEspecial()) {
            Intent intentNewDon = new Intent(this, SuggestDonationActivity.class);
            startActivity(intentNewDon);
            // Test a crash
            //Crashlytics.getInstance().crash();
        } else {
            Intent intent = new Intent(this, OngListActivity.class);
            intent.putExtra("donacion", donacion.getKey());
            startActivity(intent);
        }
    }

    @Override
    public void onLongClick(Donacion donacion, int posicion) {
        Snackbar.make(findViewById(R.id.homeLayout), donacion.getDesc(), Snackbar.LENGTH_LONG).show();
    }

}

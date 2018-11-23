package com.bove.martin.manossolidarias.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.model.SugerenciaDonacion;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NewDonationActivity extends BaseActivity {
    private final String TAG = "NEW_DONATION";
    private final String POST_NUMBER = "postnumber";
    private final String LAST_POST_DATE = "lastpostdate";

    private EditText editTextDonName;
    private EditText editTextDonDesc;

    private FirebaseFirestore db;

    private SharedPreferences preferences;

    private Intent homeInten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_donation);

        editTextDonName = findViewById(R.id.editTextDonName);
        editTextDonDesc = findViewById(R.id.editTextDonDesc);
        Button buttonDonSend = findViewById(R.id.buttonSaveDon);

        homeInten = new Intent(getBaseContext(), HomeActivity.class);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.donation_tittle);
        setSupportActionBar(myToolbar);

        // load NavDrawer
        DrawerUtil.getDrawer(this, myToolbar);

        // load preference
        preferences = getPreferences();

        buttonDonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDonationSuggest();
            }
        });

    }

    private void sendDonationSuggest() {
        String donName = editTextDonName.getText().toString().toLowerCase().trim();

        if(donName.isEmpty()) {
            editTextDonName.setError(getText(R.string.error_req));
        } else {

            if(spamProtec()) {
                showProgressDialog();
                db.collection(DB_SUGGEST_DONATIONS)
                        .whereEqualTo("nombre", donName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int regCount = 0;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        regCount++;
                                    }
                                    // hay registros
                                    if (regCount > 0) {
                                        hideProgressDialog();
                                        showAlertDialog(INFO_ALERT, getString(R.string.donation_already_suggest), "ok", null, null, null);
                                    } else {
                                        saveDonation();
                                    }

                                } else {
                                    hideProgressDialog();
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }

                        });
            } else {
                showAlertDialog(ERROR_ALERT, getString(R.string.donation_suggest_spam), "ok", homeInten, null, null);
            }
        }
    }

    // Si el usuario posteo 2 veces en los últimos 5 minutos return false
    private boolean spamProtec() {
        int minTimeToPostInSec = 300;
        int postNumber = preferences.getInt(POST_NUMBER, 0);
        long lastPostDate = preferences.getLong(LAST_POST_DATE, 0);
        long nowDate = Timestamp.now().getSeconds();

        return (nowDate - lastPostDate < minTimeToPostInSec) & (postNumber >= 2) ? false : true;
    }

    private void saveDonation() {
        String nombre = editTextDonName.getText().toString().toLowerCase().trim();
        String desc = editTextDonDesc.getText().toString().trim();

        SugerenciaDonacion sugerenciaDonacion = new SugerenciaDonacion();
        sugerenciaDonacion.setNombre(nombre);
        sugerenciaDonacion.setDesc(desc);
        sugerenciaDonacion.setUserKey(getUser().getUid());

        // Agregamos 1 al numero de publicaciones del mismo usarlo, para controlar que no haga spam.
        // Y agregamos la fecha del ultimo posteo.
        int postNumber = preferences.getInt(POST_NUMBER, 0);
        preferences.edit().putInt(POST_NUMBER, ++postNumber).apply();
        preferences.edit().putLong(LAST_POST_DATE, Timestamp.now().getSeconds()).apply();

        db.collection(DB_SUGGEST_DONATIONS)
                .add(sugerenciaDonacion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        hideProgressDialog();
                        showAlertDialog(SUCCESS_ALERT, getString(R.string.donation_suggest_success), "ok", homeInten, null, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        hideProgressDialog();
                        showAlertDialog(ERROR_ALERT, getString(R.string.donation_suggest_error), "ok", homeInten, null, null);
                    }
                });

    }

}

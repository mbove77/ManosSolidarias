package com.bove.martin.manossolidarias.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.model.SugerenciaOng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SuggestOngActivity extends BaseActivity {
    private final String TAG = "SUGGEST_ONG";
    private final String POST_NUMBER = "postnumberONG";
    private final String LAST_POST_DATE = "lastpostdateONG";

    private EditText editTextSuguetsOngName;
    private EditText editTextSuguetsOngDesc;
    private EditText editTextSuguetsMisc;

    private FirebaseFirestore db;
    private SharedPreferences preferences;
    private Intent homeInten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_ong);

        editTextSuguetsOngName = findViewById(R.id.editTextSuguetsOngName);
        editTextSuguetsOngDesc = findViewById(R.id.editTextSuguetsOngDesc);
        editTextSuguetsMisc = findViewById(R.id.editTextSuguetsMisc);
        Button buttonOngSend = findViewById(R.id.buttonSaveOng);

        homeInten = new Intent(getBaseContext(), DonationActivity.class);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.new_ong_tittle);
        setSupportActionBar(myToolbar);

        // load NavDrawer
        DrawerUtil.getDrawer(this, myToolbar,0);

        // load preference
        preferences = getPreferences();

        buttonOngSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOngSuggest();
            }
        });
    }

    private void sendOngSuggest() {
        String ongName = editTextSuguetsOngName.getText().toString().toLowerCase().trim();
        String ongDesc = editTextSuguetsOngDesc.getText().toString().toLowerCase().trim();

        if(ongName.isEmpty()) {
            editTextSuguetsOngName.setError(getText(R.string.error_req));
        } else if(ongDesc.isEmpty()) {
            editTextSuguetsOngDesc.setError(getText(R.string.error_req));
        } else {

            if(spamProtec()) {
                showProgressDialog();
                db.collection(DB_SUGGEST_ONG)
                        .whereEqualTo("nombre", ongName)
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
                                        showAlertDialog(INFO_ALERT, getString(R.string.ong_already_suggest), "ok", null, null, null);
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
        String nombre = editTextSuguetsOngName.getText().toString().toLowerCase().trim();
        String desc = editTextSuguetsOngDesc.getText().toString().toLowerCase().trim();
        String misc = editTextSuguetsMisc.getText().toString().toLowerCase().trim();

        SugerenciaOng sugerenciaOng = new SugerenciaOng();
        sugerenciaOng.setNombre(nombre);
        sugerenciaOng.setDesc(desc);
        sugerenciaOng.setMisc(misc);
        sugerenciaOng.setUserKey(getUser().getUid());

        // Agregamos 1 al numero de publicaciones del mismo usarlo, para controlar que no haga spam.
        // Y agregamos la fecha del ultimo posteo.
        int postNumber = preferences.getInt(POST_NUMBER, 0);
        preferences.edit().putInt(POST_NUMBER, ++postNumber).apply();
        preferences.edit().putLong(LAST_POST_DATE, Timestamp.now().getSeconds()).apply();

        db.collection(DB_SUGGEST_ONG)
                .add(sugerenciaOng)
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

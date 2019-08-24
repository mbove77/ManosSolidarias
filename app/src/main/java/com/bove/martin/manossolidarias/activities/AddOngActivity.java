package com.bove.martin.manossolidarias.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.model.Institucion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class AddOngActivity extends AppCompatActivity {
    private final String TAG = "ADD_ONG";
    private FirebaseFirestore db;
    private Institucion institucion;
    private TextView textViewMsj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ong);

        textViewMsj = findViewById(R.id.textViewInfoAddOng);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        institucion = new Institucion();
        institucion.setNombre("Fundación Camino");
        institucion.setDescripcion("Erradicar la desnutrición infantil en la ciudad de Rosario trabajando bajo los postulados de CONIN, basando sus acciones en la educación y la asistencia integral a las familias.");
        institucion.setMisc("");
        institucion.setDireccion("Bv. Oroño 281 piso 7 B, Rosario");
        institucion.setDonaciones(getDonaciones());
        institucion.setEmail("info@fundacioncamino.org");
        institucion.setFacebook("https://www.facebook.com/CaminoCONIN/");
        institucion.setHeader_img_url("https://manos-solidarias.firebaseapp.com/ongs/R002/header.jpg");
        institucion.setHorario("");
        institucion.setInstagram("https://www.instagram.com/coninrosario/");
        institucion.setLocalizacion(new GeoPoint(-32.9377923,-60.6536386));
        institucion.setLogo_url("https://manos-solidarias.firebaseapp.com/ongs/R002/logo.png");
        institucion.setTelefono("(341) 4257371");
        institucion.setWhatsapp("+54 9 341-3218258");
        institucion.setWeb("https://www.fundacioncamino.org");
        institucion.setTwitter("");
        institucion.setYoutube("");

        db.collection("instituciones")
                .add(institucion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        textViewMsj.setText("Institución agregada correctamente!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textViewMsj.setText("Error agregadno institución");
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public Map<String, Long> getDonaciones() {
        // [START example_map_post_advanced]
        Map<String, Long> categories = new HashMap<>();
        categories.put("0xTxWNaHKnk5kaNkqYUo", 1502144665L);
        categories.put("L7TfFTmqL5YA6QtSFHOk", 1502144665L);
        return categories;
        // [END example_map_post_advanced]
    }

    public void addDonaciones(String documentId) {
        db.collection("instituciones").document(documentId)
                .update("donaciones", getDonaciones())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


}

package com.bove.martin.manossolidarias.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.model.Institucion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_add_ong.*
import java.util.*

class AddOngActivity : AppCompatActivity() {
    private val TAG = "ADD_ONG"
    private var db: FirebaseFirestore? = null
    private var institucion: Institucion? = null
    private var textViewMsj: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ong)

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()
        institucion = Institucion()
        institucion!!.nombre = "Fundación Camino"
        institucion!!.descripcion = "Erradicar la desnutrición infantil en la ciudad de Rosario trabajando bajo los postulados de CONIN, basando sus acciones en la educación y la asistencia integral a las familias."
        institucion!!.misc = ""
        institucion!!.direccion = "Bv. Oroño 281 piso 7 B, Rosario"
        institucion!!.donaciones = donaciones
        institucion!!.email = "info@fundacioncamino.org"
        institucion!!.facebook = "https://www.facebook.com/CaminoCONIN/"
        institucion!!.header_img_url = "https://manos-solidarias.firebaseapp.com/ongs/R002/header.jpg"
        institucion!!.horario = ""
        institucion!!.instagram = "https://www.instagram.com/coninrosario/"
        institucion!!.localizacion = GeoPoint(-32.9377923, -60.6536386)
        institucion!!.logo_url = "https://manos-solidarias.firebaseapp.com/ongs/R002/logo.png"
        institucion!!.telefono = "(341) 4257371"
        institucion!!.whatsapp = "+54 9 341-3218258"
        institucion!!.web = "https://www.fundacioncamino.org"
        institucion!!.twitter = ""
        institucion!!.youtube = ""
        db!!.collection("instituciones")
                .add(institucion!!)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                    textViewInfoAddOng.text = "Institución agregada correctamente!"
                }
                .addOnFailureListener { e ->
                    textViewInfoAddOng.text = "Error agregadno institución"
                    Log.w(TAG, "Error adding document", e)
                }
    }

    // [START example_map_post_advanced]
    val donaciones: Map<String, Long>
        get() {
            // [START example_map_post_advanced]
            val categories: MutableMap<String, Long> = HashMap()
            categories["0xTxWNaHKnk5kaNkqYUo"] = 1502144665L
            categories["L7TfFTmqL5YA6QtSFHOk"] = 1502144665L
            return categories
            // [END example_map_post_advanced]
        }

    fun addDonaciones(documentId: String?) {
        db!!.collection("instituciones").document(documentId!!)
                .update("donaciones", donaciones)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
}
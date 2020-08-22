package com.bove.martin.manossolidarias.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil.getDrawer
import com.bove.martin.manossolidarias.model.SugerenciaOng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_suggest_ong.*

class SuggestOngActivity : BaseActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var homeInten: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_ong)

        homeInten = Intent(baseContext, OngListActivity::class.java)

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        myToolbar.setTitle(R.string.new_ong_tittle)
        setSupportActionBar(myToolbar)

        // load NavDrawer
        getDrawer(this, myToolbar, 0)

        buttonSaveOng.setOnClickListener { sendOngSuggest() }
    }

    private fun sendOngSuggest() {
        val ongName = editTextSuguetsOngName!!.text.toString().toLowerCase().trim()
        val ongDesc = editTextSuguetsOngDesc!!.text.toString().toLowerCase().trim()
        if (ongName.isEmpty()) {
            editTextSuguetsOngName.error = getText(R.string.error_req)
        } else if (ongDesc.isEmpty()) {
            editTextSuguetsOngDesc.error = getText(R.string.error_req)
        } else {
            if (spamProtec()) {
                showProgressDialog()
                db.collection(DB_SUGGEST_ONG)
                        .whereEqualTo("nombre", ongName)
                        .get()
                        .addOnCompleteListener { task ->
                            var regCount = 0
                            if (task.isSuccessful) {
                                for (document in task.result!!) {
                                    regCount++
                                }
                                // hay registros
                                if (regCount > 0) {
                                    hideProgressDialog()
                                    showAlertDialog(INFO_ALERT, getString(R.string.ong_already_suggest), "ok", null, null, null)
                                } else {
                                    saveDonation()
                                }
                            } else {
                                hideProgressDialog()
                                Log.w(TAG, "Error getting documents.", task.exception)
                            }
                        }
            } else {
                showAlertDialog(ERROR_ALERT, getString(R.string.donation_suggest_spam), "ok", homeInten, null, null)
            }
        }
    }

    // Si el usuario posteo 2 veces en los últimos 5 minutos return false
    private fun spamProtec(): Boolean {
        val minTimeToPostInSec = 300
        val postNumber = preferences!!.getInt(POST_NUMBER, 0)
        val lastPostDate = preferences!!.getLong(LAST_POST_DATE, 0)
        val nowDate = Timestamp.now().seconds
        return !((nowDate - lastPostDate < minTimeToPostInSec) and (postNumber >= 2))
    }

    private fun saveDonation() {
        val nombre = editTextSuguetsOngName.text.toString().toLowerCase().trim()
        val desc = editTextSuguetsOngDesc.text.toString().toLowerCase().trim()
        val misc = editTextSuguetsMisc.text.toString().toLowerCase().trim()
        val sugerenciaOng = SugerenciaOng()
        sugerenciaOng.nombre = nombre
        sugerenciaOng.desc = desc
        sugerenciaOng.misc = misc
        sugerenciaOng.userKey = user!!.uid

        // Agregamos 1 al numero de publicaciones del mismo usarlo, para controlar que no haga spam.
        // Y agregamos la fecha del ultimo posteo.
        var postNumber = preferences!!.getInt(POST_NUMBER, 0)
        preferences!!.edit().putInt(POST_NUMBER, ++postNumber).apply()
        preferences!!.edit().putLong(LAST_POST_DATE, Timestamp.now().seconds).apply()

        db.collection(DB_SUGGEST_ONG)
                .add(sugerenciaOng)
                .addOnSuccessListener { //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    hideProgressDialog()
                    showAlertDialog(SUCCESS_ALERT, getString(R.string.donation_suggest_success), "ok", homeInten, null, null)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    hideProgressDialog()
                    showAlertDialog(ERROR_ALERT, getString(R.string.donation_suggest_error), "ok", homeInten, null, null)
                }
    }

    companion object {
        private const val TAG = "SUGGEST_ONG"
        private const val POST_NUMBER = "postnumberONG"
        private const val LAST_POST_DATE = "lastpostdateONG"
    }
}
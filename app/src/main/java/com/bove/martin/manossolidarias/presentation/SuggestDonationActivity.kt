package com.bove.martin.manossolidarias.presentation

 import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.presentation.base.BaseActivity
import com.bove.martin.manossolidarias.data.utils.DrawerUtil.getDrawer
import com.bove.martin.manossolidarias.domain.model.SugerenciaDonacion
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_suggest_donation.*

class SuggestDonationActivity : BaseActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var homeInten: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_donation)

        homeInten = Intent(baseContext, DonationActivity::class.java)

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        myToolbar.setTitle(R.string.donation_tittle)
        setSupportActionBar(myToolbar)

        // load NavDrawer
        getDrawer(this, myToolbar, 0)

        // load preference
        //preferences = preferences
        buttonSaveDon.setOnClickListener { sendDonationSuggest() }
    }

    private fun sendDonationSuggest() {
        val donName = editTextDonName.text.toString().toLowerCase().trim()
        if (donName.isEmpty()) {
            editTextDonName.error = getText(R.string.error_req)
        } else {
            if (spamProtec()) {
                showProgressDialog()
                db.collection(DB_SUGGEST_DONATIONS)
                        .whereEqualTo("nombre", donName)
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
                                    showAlertDialog(INFO_ALERT, getString(R.string.donation_already_suggest), "ok", null, null, null)
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
        val nombre = editTextDonName!!.text.toString().toLowerCase().trim()
        val desc = editTextDonDesc!!.text.toString().trim()
        val sugerenciaDonacion = SugerenciaDonacion()
        sugerenciaDonacion.nombre = nombre
        sugerenciaDonacion.desc = desc
        sugerenciaDonacion.userKey = user!!.uid

        // Agregamos 1 al numero de publicaciones del mismo usarlo, para controlar que no haga spam.
        // Y agregamos la fecha del ultimo posteo.
        var postNumber = preferences!!.getInt(POST_NUMBER, 0)
        preferences!!.edit().putInt(POST_NUMBER, ++postNumber).apply()
        preferences!!.edit().putLong(LAST_POST_DATE, Timestamp.now().seconds).apply()
        db.collection(DB_SUGGEST_DONATIONS)
                .add(sugerenciaDonacion)
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
        const val TAG = "NEW_DONATION"
        const val POST_NUMBER = "postnumber"
        const val LAST_POST_DATE = "lastpostdate"
    }
}
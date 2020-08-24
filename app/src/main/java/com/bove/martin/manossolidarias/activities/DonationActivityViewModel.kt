package com.bove.martin.manossolidarias.activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bove.martin.manossolidarias.activities.base.BaseActivity.Companion.DB_DONATIONS
import com.bove.martin.manossolidarias.model.Donacion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.ArrayList

/**
 * Created by Martín Bove on 24-Aug-20.
 * E-mail: mbove77@gmail.com
 */
class DonationActivityViewModel: ViewModel() {
    private val _dontations = MutableLiveData<List<Donacion>>()
    val dontations: LiveData<List<Donacion>> get() = _dontations

    init {
        getDonations()
    }

    fun getDonations() {
        val db = FirebaseFirestore.getInstance()
        db.collection(DB_DONATIONS)
        .orderBy("order", Query.Direction.ASCENDING)
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var donacitionList = ArrayList<Donacion>()
                for (document in task.result!!) {
                    val donacion = document.toObject(Donacion::class.java)
                    donacion.key = document.id
                    donacitionList.add(donacion)
                }
                _dontations.value = donacitionList
            } else {
                Log.w("DonationViewModel", "Error getting documents.", task.exception)
            }
        }

    }

}
package com.bove.martin.manossolidarias.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bove.martin.manossolidarias.presentation.base.BaseActivity.Companion.DB_DONATIONS
import com.bove.martin.manossolidarias.domain.model.Donacion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

        GlobalScope.launch(Dispatchers.IO) {

            val result = getAllDonations()

            withContext(Dispatchers.Main) {
                if (result != null) {

                    var donacitionList = ArrayList<Donacion>()

                    for (document in result.documents) {
                        val donacion = document.toObject(Donacion::class.java)
                        donacion?.key = document.id
                        donacitionList.add(donacion!!)
                    }
                    _dontations.value = donacitionList
                } else {
                    Log.w("DonationViewModel", "Error getting documents.")
                }
            }
        }

    }

    suspend fun getAllDonations(): QuerySnapshot?{
        val db = FirebaseFirestore.getInstance()
        return try{
            val data = db
                    .collection(DB_DONATIONS)
                    .orderBy("order", Query.Direction.ASCENDING)
                    .get()
                    .await()
            data
        }catch (e : Exception){
            null
        }
    }

}
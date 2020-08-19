package com.bove.martin.manossolidarias.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil.getDrawer
import com.bove.martin.manossolidarias.activities.utils.goToActivity
import com.bove.martin.manossolidarias.activities.utils.goToActivityForResult
import com.bove.martin.manossolidarias.adapters.DonationAdapter
import com.bove.martin.manossolidarias.model.Donacion
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_donation.*
import java.util.*

class DonationActivity : BaseActivity(), DonationAdapter.OnItemClickListener, DonationAdapter.OnLongClickListener {
    private val TAG = "Donation Activity"
    private val SHOW_HELP_KEY = "showHelp"

    private lateinit var adapter: DonationAdapter
    private val donaciones: MutableList<Donacion> = ArrayList()

    private var showHelp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        myToolbar.setTitle(R.string.donaciones)
        setSupportActionBar(myToolbar)

        // load preference para mostrar la ayuda 1 sola vez
        //preferences = preferences;
        showHelp = getSharedPreferences()!!.getBoolean(SHOW_HELP_KEY, true)

        // load NavDrawer
        getDrawer(this, myToolbar, 3)

        // Mostramos el cargando hasta que lleguen los datos
        showProgressDialog()

        // Instanciamos los elementos
        val layoutManager = GridLayoutManager(this, 3)
        recyclerViewDonation.setLayoutManager(layoutManager)
        recyclerViewDonation.setItemAnimator(DefaultItemAnimator())
        adapter = DonationAdapter(donaciones, R.layout.donation_item, this, this, this)
        recyclerViewDonation.setAdapter(adapter)

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        db.collection(DB_DONATIONS)
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val donacion = document.toObject(Donacion::class.java)
                            donacion.key = document.id
                            donaciones.add(donacion)
                        }
                        hideProgressDialog()
                        if (showHelp) {
                            showHelp()
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        hideProgressDialog()
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
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
    private fun showHelp() {
        preferences!!.edit().putBoolean(SHOW_HELP_KEY, false).apply()
        val handler = Handler()
        handler.postDelayed({ Snackbar.make(findViewById(R.id.homeLayout), getString(R.string.donation_help), Snackbar.LENGTH_LONG).show() }, 2500)
    }

    override fun onItemClick(donacion: Donacion?, posicion: Int) {
        if (donacion!!.especial) {
            goToActivity<SuggestDonationActivity>()
        } else {
           goToActivity<OngListActivity>() {
               intent.putExtra("donacion", donacion.key)
           }
        }
    }

    override fun onLongClick(donacion: Donacion?, posicion: Int) {
        Snackbar.make(findViewById(R.id.homeLayout), donacion!!.desc!!, Snackbar.LENGTH_LONG).show()
    }
}
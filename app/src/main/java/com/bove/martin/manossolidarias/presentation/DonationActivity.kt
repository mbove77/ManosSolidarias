package com.bove.martin.manossolidarias.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.utils.goToActivity
import com.bove.martin.manossolidarias.adapters.DonationAdapter
import com.bove.martin.manossolidarias.presentation.base.BaseActivity
import com.bove.martin.manossolidarias.data.utils.DrawerUtil.getDrawer

import com.bove.martin.manossolidarias.domain.model.Donacion
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_donation.*

import java.util.*

class DonationActivity : BaseActivity(), DonationAdapter.OnItemClickListener, DonationAdapter.OnLongClickListener {
    private val TAG = "Donation Activity"
    private val SHOW_HELP_KEY = "showHelp"

    private lateinit var adapter: DonationAdapter
    private var donaciones: List<Donacion> = ArrayList()
    private lateinit var donactionActivityViewModel: DonationActivityViewModel

    private var showHelp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        donactionActivityViewModel = ViewModelProvider(this).get(DonationActivityViewModel::class.java)

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
        recyclerViewDonation.layoutManager = layoutManager
        recyclerViewDonation.itemAnimator = DefaultItemAnimator()
        adapter = DonationAdapter(donaciones, R.layout.donation_item, this, this, this)
        recyclerViewDonation.adapter = adapter

        donactionActivityViewModel.dontations.observe(this, androidx.lifecycle.Observer {
            if (it.size > 0) {
                hideProgressDialog()
                if (showHelp) {
                    showHelp()
                }
               adapter.setData(it)
            }
        })

    }

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
            val intent = Intent(this, OngListActivity::class.java)
            intent.putExtra("donacion", donacion.key)
            startActivity(intent)
        }
    }

    override fun onLongClick(donacion: Donacion?, posicion: Int) {
        Snackbar.make(findViewById(R.id.homeLayout), donacion!!.desc!!, Snackbar.LENGTH_LONG).show()
    }

}
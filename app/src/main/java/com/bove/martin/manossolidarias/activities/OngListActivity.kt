package com.bove.martin.manossolidarias.activities

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil.getDrawer
import com.bove.martin.manossolidarias.activities.utils.goToActivity
import com.bove.martin.manossolidarias.adapters.InstitucionesAdapter
import com.bove.martin.manossolidarias.model.Institucion
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_ong_list.*
import java.util.*

class OngListActivity : BaseActivity(), InstitucionesAdapter.OnItemClickListener {
    private val TAG = "ONG_LIST"

    private lateinit var db: FirebaseFirestore
    private var donacion: String? = null
    private val instituciones: MutableList<Institucion> = ArrayList()

    private lateinit var adapter: InstitucionesAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var userLoc: Location? = null
    private val updateDistance = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ong_list)

        layoutManager = LinearLayoutManager(this)
        recyclerViewOngs.layoutManager = layoutManager
        recyclerViewOngs.itemAnimator = DefaultItemAnimator()

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(myToolbar)

        // Cargamos el NavDrawer
        getDrawer(this, myToolbar, 2)

        val extras = intent.extras
        donacion = extras?.getString("donacion")



        notFoundBackButton.setOnClickListener {
            if (donacion != null) {
                goToActivity<DonationActivity>()
            } else {
                goToActivity<HomeActivity>()
            }
        }

        // Mostramos el cargando hasta que lleguen los datos
        showProgressDialog()

        // Arrancamos el recyclerView
        adapter = InstitucionesAdapter(instituciones, R.layout.ong_item, this, this)
        val divider = DividerItemDecoration(recyclerViewOngs.context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(baseContext, R.drawable.line_divider))!!)
        recyclerViewOngs.addItemDecoration(divider)
        recyclerViewOngs.adapter = adapter

        // pedimos el permiso de GPS en tiempo de ejecución.
        checkPermission()

        // Pedimos la localización del usuario
        userLocation

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()
        if (donacion != null) {
            // Lista filtrada por donación
            db.collection(DB_ONGS)
                    .whereGreaterThan("donaciones.$donacion", 0)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val institucion = document.toObject(Institucion::class.java)
                                institucion.key = document.id

                                // Si la ong esta aprobada
                                if (institucion.isAprobado) {
                                    // Si tenemos localización
                                    geoLocateResults(institucion)
                                }
                            }
                            if (instituciones.size > 0) {
                                errorMensajeLay.visibility = LinearLayout.GONE
                                hideProgressDialog()
                                if (userLoc != null) {
                                    Collections.sort(instituciones)
                                }
                                adapter.setInstitucionesFull(instituciones)
                                adapter.notifyDataSetChanged()
                            } else {
                                hideProgressDialog()
                                errorMensajeLay.visibility = LinearLayout.VISIBLE
                            }
                            // Agregamos la opción de sugerir una nueva ong
                            val addOng = Institucion(getString(R.string.nueva_ong), true)
                            instituciones.add(addOng)
                        } else {
                            Log.w(TAG, "Error getting documents.", task.exception)
                            hideProgressDialog()
                        }
                    }
        } else {
            // Lista completa
            db.collection(DB_ONGS)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val institucion = document.toObject(Institucion::class.java)
                                institucion.key = document.id

                                // Si la ong esta aprobada
                                if (institucion.isAprobado) {
                                    // Si tenemos localización
                                    geoLocateResults(institucion)
                                }
                            }
                            if (instituciones.size > 0) {
                                errorMensajeLay.visibility = LinearLayout.GONE
                                hideProgressDialog()
                                if (userLoc != null) {
                                    Collections.sort(instituciones)
                                }
                                adapter.setInstitucionesFull(instituciones)
                                adapter.notifyDataSetChanged()
                            } else {
                                hideProgressDialog()
                                errorMensajeLay.visibility = LinearLayout.VISIBLE
                            }
                            // Agregamos la opción de sugerir una nueva ong
                            val addOng = Institucion(getString(R.string.nueva_ong), true)
                            instituciones.add(addOng)
                        } else {
                            Log.w(TAG, "Error getting documents.", task.exception)
                            hideProgressDialog()
                        }
                    }
        }
    }

    private fun geoLocateResults(institucion: Institucion) {
        // Si tenemos localización
        if (userLoc != null && institucion.localizacion!!.latitude != 0.0) {
            val distance = FloatArray(2)
            Location.distanceBetween(userLoc!!.latitude, userLoc!!.longitude, institucion.localizacion!!.latitude, institucion.localizacion!!.longitude, distance)
            institucion.distancia = distance[0]
        }
        if (institucion.localizacion!!.latitude == 0.0) {
            institucion.distancia = NO_DISTANCIA
        }
        instituciones.add(institucion)
    }

    override fun onItemClick(institucion: Institucion?, posicion: Int) {
        if (!institucion!!.isEspecial) {
            // Mostramos el cargando por si demora mas de lo habitual
            showProgressDialog()
            val intent = Intent(this, OngInfoActivity::class.java)

            // Guardamos el objeto en las prefs
            val prefsEditor = getSharedPreferences()!!.edit()
            val gson = Gson()
            val json = gson.toJson(institucion)
            prefsEditor.putString("institucion", json)
            prefsEditor.commit()
            startActivity(intent)
        } else {
            // Si es la opción de sugerir ong
            val suggestInent = Intent(this, SuggestOngActivity::class.java)
            startActivity(suggestInent)
        }
    }

    // Got last known location. In some rare situations this can be null.
   // @get:SuppressLint("MissingPermission")
    private val userLocation: Unit
        private get() {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this) { location -> // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            userLoc = location
                            updateDistancias()
                        }
                    }
        }

    private fun checkPermission() {
        // Chequeo del permiso de localización on runtime
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        userLocation
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    // TODO Tratar de evitar la segunda llamada y también al método getUserLocation
    // Update distancias después de cargadas las ongs
    private fun updateDistancias() {
        if (instituciones.size > 0 && userLoc != null) {
            for (institucion in instituciones) {
                if (!institucion.isEspecial && institucion.localizacion!!.latitude != 0.0) {
                    val distance = FloatArray(2)
                    Location.distanceBetween(userLoc!!.latitude, userLoc!!.longitude, institucion.localizacion!!.latitude, institucion.localizacion!!.longitude, distance)
                    institucion.distancia = distance[0]
                }
            }
            Collections.sort(instituciones)
            adapter.notifyDataSetChanged()
        }
    }

    // Serach menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_ong_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        return true
    }
}
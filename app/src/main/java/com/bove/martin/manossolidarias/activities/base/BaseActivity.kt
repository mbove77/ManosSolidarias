package com.bove.martin.manossolidarias.activities.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.HomeActivity
import com.bove.martin.manossolidarias.activities.MainActivity
import com.bove.martin.manossolidarias.activities.broadcast.NetworkChangeReceiver
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId

open class BaseActivity : AppCompatActivity() {
    // TODO reemplazar todos los string con variables declaradas aquí.
    // TODO Crear popUp con icono de desconexión
    // TODO Implementar funcionalidad para denunciar datos errados en los perfiles
    // Posibles nuevos fetures
    // TODO Opciones para ordenar y buscar en la lista de ongs
    // TODO Agregar a fav ongs
    // TODO Recibir notificaciones de la app y de ongs especificas
    // TODO Actualizar theme a Theme.MaterialComponents.DayNight.DarkActionBar
    // TODO Incluir animaciones con la lib lottie
    // TODO Filtrar los resultados de las ong
    // TODO Reaccionar a mensaje entrante con notificación
    // TODO Ir migrando a kotlin y empezar a usar jetpack

    var preferences: SharedPreferences? = null
    var mProgressDialog: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null

    // Set Firebase User
    // Get current Firebase User
    var user: FirebaseUser? = null
        get() = if (field != null) {
            field
        } else {
            FirebaseAuth.getInstance().currentUser.also { field = it }
        }

    companion object {

        // Constantes
        const val DB_DONATIONS = "donaciones"
        const val DB_SUGGEST_DONATIONS = "sugerencias_donaciones"
        const val DB_SUGGEST_ONG = "sugerencias_ong"
        const val DB_ONGS = "instituciones"
        const val DB_NEWS = "noticias"
        const val DB_APPS = "otras_apps"
        const val SHARED_PREF = "pref"
        const val INFO_ALERT = "info"
        const val WARN_ALERT = "warning"
        const val ERROR_ALERT = "error"
        const val SUCCESS_ALERT = "exito"
        const val DRAWABLE_LEFT = 1
        const val DRAWABLE_RIGHT = 2
        const val DRAWABLE_TOP = 3
        const val DRAWABLE_BOTTOM = 4
        const val NO_DISTANCIA = 100000000f

        //region Helper method for PreLollipop TextView & Buttons Vector Images
        fun setVectorForPreLollipop(resourceId: Int, activity: Context): Drawable? {
            val icon: Drawable?
            icon = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                VectorDrawableCompat.create(activity.resources, resourceId, activity.theme)
            } else {
                activity.resources.getDrawable(resourceId, activity.theme)
            }
            return icon
        }

        // Para solucionar el error en los drawables en los dispositivos pre-Lolipop
        @JvmStatic
        fun setVectorForPreLollipop(textView: TextView, resourceId: Int, activity: Context, position: Int) {
            val icon: Drawable?
            icon = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                VectorDrawableCompat.create(activity.resources, resourceId,
                        activity.theme)
            } else {
                activity.resources.getDrawable(resourceId, activity.theme)
            }
            when (position) {
                DRAWABLE_LEFT -> textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
                DRAWABLE_RIGHT -> textView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon,null)
                DRAWABLE_TOP -> textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null,null)
                DRAWABLE_BOTTOM -> textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon)
            }
        }

        fun getPrelolipotDrawable(resourceId: Int, activity: Context): Drawable? {
            val icon: Drawable?
            icon = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                VectorDrawableCompat.create(activity.resources, resourceId,
                        activity.theme)
            } else {
                activity.resources.getDrawable(resourceId, activity.theme)
            }
            return icon
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargamos el FCM token
        //getFCMToken();

        // Cargamos las shared para mostrar la ayuda una sola vez.
        preferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)

        // Mostramos mensaje si no hay conexión
        val intentFilter = IntentFilter(NetworkChangeReceiver.NETWORK_AVAILABLE_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val isNetworkAvailable = intent.getBooleanExtra(NetworkChangeReceiver.IS_NETWORK_AVAILABLE, false)
                val networkStatus = if (isNetworkAvailable) getString(R.string.online_msj) else getString(R.string.offline_msj)
                val snackbar = Snackbar.make(window.decorView.rootView, networkStatus, Snackbar.LENGTH_LONG)
                val sbView = snackbar.view
                if (isNetworkAvailable) sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary)) else sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGoogleLight))
                snackbar.show()
            }
        }, intentFilter)
    }

    fun getSharedPreferences(): SharedPreferences? {
        return preferences;
    }

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }
        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    // Logout
    fun logout() {
        if (mAuth != null) {
            mAuth!!.signOut()
        } else {
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()
        }
        LoginManager.getInstance().logOut()
        goToMain()
    }

    // Intent para ir a la Home (Donaciones)
    fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    // Intent para ir a la página de login
    fun goToMain() {
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        startActivity(i)
    }

    // Muestra mensaje de dialogo uniforme en todos los activities
    fun showAlertDialog(tipo: String?, mensaje: String?, positiveButton: String?, positiveIntent: Intent?, negativeButton: String?, negativeIntent: Intent?) {
        val titulo: String
        val builder = AlertDialog.Builder(this)
        val inflateView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        builder.setView(inflateView)
        val tituloTextView = inflateView.findViewById<TextView>(R.id.alertTitle)
        val mensajeTextView = inflateView.findViewById<TextView>(R.id.alertMensaje)
        val exitoImage = inflateView.findViewById<ImageView>(R.id.imageViewOK)
        val infoImage = inflateView.findViewById<ImageView>(R.id.imageViewINFO)
        val warnImage = inflateView.findViewById<ImageView>(R.id.imageViewWARN)
        val errorImage = inflateView.findViewById<ImageView>(R.id.imageViewERROR)
        when (tipo) {
            SUCCESS_ALERT -> {
                exitoImage.visibility = View.VISIBLE
                titulo = "Exito!"
                tituloTextView.text = titulo
            }
            WARN_ALERT -> {
                warnImage.visibility = View.VISIBLE
                titulo = "Aviso!"
                tituloTextView.text = titulo
            }
            ERROR_ALERT -> {
                errorImage.visibility = View.VISIBLE
                titulo = "Error!"
                tituloTextView.text = titulo
            }
            INFO_ALERT -> {
                infoImage.visibility = View.VISIBLE
                titulo = "Información"
                tituloTextView.text = titulo
            }
        }
        mensajeTextView.text = mensaje
        if (positiveButton != null) {
            builder.setPositiveButton(positiveButton) { dialog, which -> positiveIntent?.let { startActivity(it) } }
        }
        if (negativeButton != null) {
            builder.setNegativeButton(negativeButton) { dialog, which -> negativeIntent?.let { startActivity(it) } }
        }

        // Creamos y mostramos
        builder.setCancelable(false)
        builder.create().show()
    }// Get new Instance ID token

    // Log and toast
    // Trae el token del dispositivo para FCM Firebase Cloud Menssages
    private val fCMToken: Unit
        private get() {
            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("FCM_TOKEN", "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new Instance ID token
                        val token = task.result!!.token

                        // Log and toast
                        val msg = getString(R.string.fcm_token, token)
                        Log.d("FCM_TOKEN", msg)
                    })
        }
}
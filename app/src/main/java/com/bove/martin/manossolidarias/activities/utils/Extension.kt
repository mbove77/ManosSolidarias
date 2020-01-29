package com.bove.martin.manossolidarias.activities.utils

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bove.martin.manossolidarias.R

import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern


/**
 * Created by Martín Bove on 07/09/2019.
 * E-mail: mbove77@gmail.com
 */
// Devuelve true si el numero es natural
fun Int.isNatural() = this >= 0

// Agregamos Toast a Activity
fun Activity.toast(mensaje: CharSequence, duracion: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, mensaje, duracion).show()
}

// Agregamos SnackBar a Activity
fun Activity.snackBar(mensaje: CharSequence,
                      view: View? = findViewById(R.id.container),
                      duracion: Int = Snackbar.LENGTH_SHORT,
                      accion: String? = null,
                      actionEvent:(v: View) -> Unit = {}) {

    if(view != null) {
        val snack = Snackbar.make(view, mensaje, duracion)
        if(!accion.isNullOrEmpty()) {
            snack.setAction(accion, actionEvent)
        }
        snack.show()
    }
}

// Agreganos layout inflate a viewGrup
fun ViewGroup.inflate(layoutId: Int) = LayoutInflater.from(context).inflate(layoutId,this, false)

// Agreganos Load by url a ImageView
fun ImageView.loadFromUrl(url: Any) = Glide.with(this).load(url).into(this)


// Agreganos goto a Activity
inline fun <reified T: Activity>Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

// Agreganos gotoActivityForresult a Activity
fun Activity.goToActivityForResult(action: String, resquetCode: Int, init: Intent.() -> Unit = {}) {
    val intent = Intent(action)
    intent.init()
    startActivityForResult(intent, resquetCode)
}

// Validación de campos en tiempo real
fun EditText.validate(validation:(String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
           validation(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })

}

fun Activity.isValidateEmail(email: String) : Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

fun Activity.isValidatePassword(password: String): Boolean {
    // Necesita Contener -->    1 Num / 1 Minuscula / 1 Mayuscula / 1 Special / Min Caracteres 4
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    val patterns = Pattern.compile(passwordPattern)
    return patterns.matcher(password).matches()
}

fun Activity.isValidateConfirmPassword(password: String, confirmPassword: String) :Boolean {
    return password == confirmPassword
}


package com.bove.martin.manossolidarias.presentation.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.utils.goToActivity
import com.bove.martin.manossolidarias.presentation.MainActivity
import com.bove.martin.manossolidarias.presentation.base.BaseActivity

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.*

class CreateAccountActivity : BaseActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Fix drawables
        setVectorForPreLollipop(editTextEmail, R.drawable.ic_person, this, DRAWABLE_LEFT)
        setVectorForPreLollipop(editTextRegPass, R.drawable.ic_lock, this, DRAWABLE_LEFT)
        setVectorForPreLollipop(editTextRegRePass, R.drawable.ic_lock, this, DRAWABLE_LEFT)
        mAuth = FirebaseAuth.getInstance()

        // back button
        imageViewBack.setOnClickListener(View.OnClickListener {
            goToActivity<MainActivity>()
        })

        // register button
        buttonRestorePass.setOnClickListener(View.OnClickListener {
            if (validateMailPass()) {
                createAccount(editTextEmail.text.toString() + "", editTextRegPass.text.toString() + "")
            }
        })

        // ayuda
        textViewHelp.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.app_mail)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_mail_subject))
            startActivity(Intent.createChooser(intent, "Email via..."))
        })
    }

    private fun validateMailPass(): Boolean {
        val email = editTextEmail!!.text.toString()
        val pass = editTextRegPass!!.text.toString()
        val rePass = editTextRegRePass!!.text.toString()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = getText(R.string.error_email)
            return false
        }
        if (pass.isEmpty()) {
            editTextRegPass!!.error = getText(R.string.error_pass)
            return false
        }
        if (pass.length <= 5) {
            editTextRegPass!!.error = getText(R.string.error_pass_short)
            return false
        }
        if (pass != rePass) {
            editTextRegRePass!!.error = getText(R.string.error_pass_confirm)
            return false
        }
        return true
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        showProgressDialog()

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        user = mAuth!!.currentUser
                        // Enviamos el email de verificación
                        Objects.requireNonNull(mAuth!!.currentUser)!!.sendEmailVerification()
                        gotoMain(email, password)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        hideKeyboard(this)
                        Snackbar.make(findViewById(R.id.createAccountActivity), getText(R.string.error_create_account), Snackbar.LENGTH_LONG).show()
                    }
                    hideProgressDialog()
                }
    }

    private fun gotoMain(email: String, pass: String) {
        val i = Intent(this, MainActivity::class.java)
        i.putExtra("email", email)
        i.putExtra("pass", pass)
        startActivity(i)
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}
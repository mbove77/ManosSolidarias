package com.bove.martin.manossolidarias.presentation.account

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.utils.goToActivity
import com.bove.martin.manossolidarias.presentation.MainActivity
import com.bove.martin.manossolidarias.presentation.base.BaseActivity

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_restore_password.*

class RestorePasswordActivity : BaseActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_password)

        mAuth = FirebaseAuth.getInstance()

        // Fix drawables
        setVectorForPreLollipop(editTextEmail, R.drawable.ic_person, this, DRAWABLE_LEFT)
        imageViewBack.setOnClickListener(View.OnClickListener {
           goToActivity<MainActivity>()
        })
        buttonRestorePass.setOnClickListener(View.OnClickListener { restablecerPass() })
    }

    private fun restablecerPass() {
        val emailAddress = editTextEmail!!.text.toString()
        if (TextUtils.isEmpty(emailAddress) || !Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            editTextEmail!!.error = getString(R.string.error_email_forgot)
            return
        }
        mAuth!!.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG_EMAIL, "Email sent.")
                        hideKeyboard(this)
                        val snackbar = Snackbar
                                .make(findViewById(R.id.restorePassLayout), getText(R.string.email_forgot_send), Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK") {
                                   goToActivity<MainActivity>()
                                }
                        snackbar.show()
                    }
                }
    }

    companion object {
        private const val TAG_EMAIL = "EmailActivity"
    }
}
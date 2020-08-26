package com.bove.martin.manossolidarias.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.utils.goToActivity
import com.bove.martin.manossolidarias.presentation.account.CreateAccountActivity
import com.bove.martin.manossolidarias.presentation.account.RestorePasswordActivity
import com.bove.martin.manossolidarias.presentation.base.BaseActivity

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCallbackManager: CallbackManager
    private var firebaseUser: FirebaseUser? = null

    private lateinit var fbloginButton: LoginButton
    private lateinit var gso: GoogleSignInOptions
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var createAccountMensaje = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fix drawables
        setVectorForPreLollipop(editTextEmail, R.drawable.ic_person, this, DRAWABLE_LEFT)
        setVectorForPreLollipop(editTextPass, R.drawable.ic_lock, this, DRAWABLE_LEFT)

        // Si se llega desde createAccount escribir los datos en el login
        val extras = intent.extras
        if (extras != null) {
            if (extras.getString("email") != null) {
                editTextEmail.setText(extras.getString("email"))
            }
            if (extras.getString("pass") != null) {
                editTextPass.setText(extras.getString("pass"))
            }
        }
        mAuth = FirebaseAuth.getInstance()
        firebaseUser = user

        // Initialize Facebook Login button
        fbloginButton = LoginButton(this)
        mCallbackManager = CallbackManager.Factory.create()
        fbloginButton.setReadPermissions("email", "public_profile")
        fbloginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG_FACEBOOK, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG_FACEBOOK, "facebook:onCancel")
                updateUI(null)
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG_FACEBOOK, "facebook:onError", error)
                updateUI(null)
            }
        })


        // Facebook Login Button
        buttonFacebook.setOnClickListener { fbloginButton.performClick() }

        // Google Login Button
        buttonGoogle.setOnClickListener { signInGoogle() }

        // Email Login Button
        buttonLogin.setOnClickListener { signInEmail(editTextEmail.text.toString(), editTextPass.text.toString()) }

        // Perdí el password action
        textViewLostPass.setOnClickListener { goToActivity<RestorePasswordActivity>() }

        // Create account
        textViewCreateAccount.setOnClickListener {goToActivity<CreateAccountActivity>() }

        // Configure Google Sign In
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    public override fun onStart() {
        super.onStart()
        // Si el usuario esta logeado pasamos al home
        if (firebaseUser != null) {
            // Name, email address, and profile photo Url
            val name = firebaseUser?.displayName
            val email = firebaseUser?.email
            val photoUrl = firebaseUser?.photoUrl
            val uid = firebaseUser?.uid
            val emailVerified = firebaseUser!!.isEmailVerified
            //Todo revisar que funcione este fix
            val provider = firebaseUser!!.providerData[1].providerId

            // Si el email no esta verificado
            if (provider == "password" && !emailVerified) {
                emailVerifyerror()
                return
            }
            goToActivity<HomeActivity>()
        }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // START onactivityResult Google
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG_GOOGLE, "Google sign in failed", e)
                updateUI(null)
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    // START auth_with_google
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        Log.d(TAG_GOOGLE, "firebaseAuthWithGoogle:" + acct!!.id)
        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_GOOGLE, "signInWithCredential:success")
                        firebaseUser = mAuth.currentUser
                        this.user = firebaseUser
                        updateUI(firebaseUser)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_GOOGLE, "signInWithCredential:failure", task.exception)
                        Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_sigin), Snackbar.LENGTH_LONG).show()
                        updateUI(null)
                    }
                    hideProgressDialog()
                }
    }

    // START sign_in_with_email
    private fun signInEmail(email: String, password: String) {
        Log.d(TAG_EMAIL, "signIn:$email")
        if (!validateForm()) {
            return
        }
        showProgressDialog()
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_EMAIL, "signInWithEmail:success")
                        firebaseUser = mAuth.currentUser
                        this.user = firebaseUser

                        // Revisamos si el email esta verificado
                        if (!firebaseUser!!.isEmailVerified) {
                            emailVerifyerror()
                        } else {
                            updateUI(firebaseUser)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_EMAIL, "signInWithEmail:failure", task.exception)
                        Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_sigin), Snackbar.LENGTH_LONG).show()
                        updateUI(null)
                    }
                    hideProgressDialog()
                }
    }

    // START auth_with_facebook
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG_FACEBOOK, "handleFacebookAccessToken:$token")
        showProgressDialog()
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_FACEBOOK, "signInWithCredential:success")
                        firebaseUser = mAuth.currentUser
                        this.user = firebaseUser
                        updateUI(firebaseUser)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_FACEBOOK, "signInWithCredential:failure", task.exception)
                        Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_sigin), Snackbar.LENGTH_LONG).show()
                        updateUI(null)
                    }
                    hideProgressDialog()
                }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            hideProgressDialog()
            goToActivity<HomeActivity>()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = editTextEmail!!.text.toString()
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = getString(R.string.error_email)
            valid = false
        } else {
            editTextEmail!!.error = null
        }
        val password = editTextPass!!.text.toString()
        if (TextUtils.isEmpty(password)) {
            editTextPass!!.error = getString(R.string.error_pass)
            valid = false
        } else {
            editTextPass!!.error = null
        }
        return valid
    }

    private fun emailVerifyerror() {
        // Si ya mostramos el mensaje ofrecer re-enviar la verificación
        if (createAccountMensaje) {
            Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_email_verfy), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.resend_verfy) { firebaseUser!!.sendEmailVerification() }.setDuration(8000)
                    .show()
        } else {
            // Si no mostrar el mensaje inicial y ofrecer abrir el correo.
            createAccountMensaje = true
            Snackbar.make(findViewById(R.id.main_layout), getText(R.string.email_verfy), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.open_mail) { openEmailClient() }.setDuration(8000)
                    .show()
        }
    }

    private fun openEmailClient() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        try {
            startActivity(intent)
            startActivity(Intent.createChooser(intent, getString(R.string.email_clients)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.error_email_client), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG_GOOGLE = "GoogleActivity"
        private const val TAG_FACEBOOK = "FacebookActivity"
        private const val TAG_EMAIL = "EmailActivity"
        private const val RC_SIGN_IN = 9001
    }
}
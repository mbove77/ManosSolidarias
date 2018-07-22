package com.bove.martin.manossolidarias.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.account.CreateAccountActivity;
import com.bove.martin.manossolidarias.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private FirebaseUser user;

    private Button buttonFacebook;
    private Button buttonGoogle;
    private LoginButton fbloginButton;

    private EditText editTextEmail;
    private EditText editTextPass;
    private Button buttonLoginEmail;

    private TextView textViewlostPass;
    private TextView textViewNewAccount;

    private static final String TAG_GOOGLE = "GoogleActivity";
    private static final String TAG_FACEBOOK = "FacebookActivity";
    private static final String TAG_EMAIL = "EmailActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    // TODO Cuando se crea una cuenta con email, hacer un intent para abrir el correo, y luego si ingresa son registrar ofreecer reenviar el correo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonFacebook = findViewById(R.id.buttonFacebook);
        buttonGoogle = findViewById(R.id.buttonGoogle);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        buttonLoginEmail = findViewById(R.id.buttonLogin);
        textViewlostPass = findViewById(R.id.textViewLostPass);
        textViewNewAccount = findViewById(R.id.textViewCreateAccount);

        mAuth = FirebaseAuth.getInstance();
        user = getUser();

        // TODO Hacer el key de release cuando se publique la app
        // Initialize Facebook Login button
        fbloginButton = new LoginButton(this);
        mCallbackManager = CallbackManager.Factory.create();
        fbloginButton.setReadPermissions("email", "public_profile");
        fbloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG_FACEBOOK, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG_FACEBOOK, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG_FACEBOOK, "facebook:onError", error);
                updateUI(null);
            }
        });


        // Facebook Login Button
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbloginButton.performClick();
            }
        });

        // Google Login Button
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        // Email Login Button
        buttonLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInEmail(editTextEmail.getText()+"", editTextPass.getText()+"");
            }});

        // Perdí el password action
        textViewlostPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restablecerPass();
            }
        });

        // Create account
        textViewNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Si el usuario esta logeado pasamos al home

        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();
            boolean emailVerified = user.isEmailVerified();
            String provider = user.getProviders().get(0);

            // Si el email no esta verificado
            if(provider.equals("password")  && !emailVerified) {
                emailVerifyerror();
                return;
            }

            goToHome();
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // START onactivityResult Google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG_GOOGLE, "Google sign in failed", e);
                updateUI(null);
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // START auth_with_google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG_GOOGLE, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_GOOGLE, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            setUser(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_GOOGLE, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_sigin), Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    // START sign_in_with_email
    private void signInEmail(String email, String password) {
        Log.d(TAG_EMAIL, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_EMAIL, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            setUser(user);

                            // Vericamos si el email esta verificado
                            if(!user.isEmailVerified()) {
                               emailVerifyerror();
                            } else {
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_EMAIL, "signInWithEmail:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_sigin), Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    // START auth_with_facebook
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG_FACEBOOK, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_FACEBOOK, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            setUser(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_FACEBOOK, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_sigin), Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            hideProgressDialog();
            goToHome();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editTextEmail.getText().toString();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.error_email));
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        String password = editTextPass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPass.setError(getString(R.string.error_pass));
            valid = false;
        } else {
            editTextPass.setError(null);
        }
        return valid;
    }

    private void restablecerPass() {

        String emailAddress = editTextEmail.getText().toString();
        if (TextUtils.isEmpty(emailAddress) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            editTextEmail.setError(getString(R.string.error_email_forgot));
            return;
        }

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG_EMAIL, "Email sent.");
                            Snackbar.make(findViewById(R.id.main_layout), getText(R.string.email_forgot_send), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void emailVerifyerror() {
        Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_email_verfy), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.resend_verfy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification();
            }
        }).setDuration(7000)
          .show();
    }
}

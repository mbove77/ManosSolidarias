package com.bove.martin.manossolidarias.activities.account;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CreateAccountActivity extends BaseActivity {
    private ImageView imageViewBack;
    private EditText editTextEmail;
    private EditText editTextPass;
    private EditText editTextRePass;
    private Button buttonRegister;
    private Button textViewAyuda;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        imageViewBack = findViewById(R.id.imageViewBack);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPass  = findViewById(R.id.editTextRegPass);
        editTextRePass = findViewById(R.id.editTextRegRePass);
        buttonRegister = findViewById(R.id.buttonRestorePass);
        textViewAyuda = findViewById(R.id.textViewHelp);

        // Fix drawables
        BaseActivity.setVectorForPreLollipop(editTextEmail, R.drawable.ic_person, this, BaseActivity.DRAWABLE_LEFT );
        BaseActivity.setVectorForPreLollipop(editTextPass, R.drawable.ic_lock, this, BaseActivity.DRAWABLE_LEFT );
        BaseActivity.setVectorForPreLollipop(editTextRePass, R.drawable.ic_lock, this, BaseActivity.DRAWABLE_LEFT );

        mAuth = FirebaseAuth.getInstance();

        // back button
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateMailPass()) {
                    createAccount(editTextEmail.getText()+"" , editTextPass.getText()+"");
                }
            }
        });

        // ayuda
        textViewAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { getString(R.string.app_mail) });
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_mail_subject));

                startActivity(Intent.createChooser(intent, "Email via..."));
            }
        });

    }

    private boolean validateMailPass() {
        String email = editTextEmail.getText().toString();
        String pass  = editTextPass.getText().toString();
        String rePass = editTextRePass.getText().toString();

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getText(R.string.error_email));
            return false;
        }
        if(pass.isEmpty()) {
            editTextPass.setError(getText(R.string.error_pass));
            return false;
        }
        if(pass.length() <= 5 ) {
            editTextPass.setError(getText(R.string.error_pass_short));
            return false;
        }
        if(!pass.equals(rePass)) {
            editTextRePass.setError(getText(R.string.error_pass_confirm));
            return false;
        }
        return true;
    }


    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            setUser(mAuth.getCurrentUser());
                            // Enviamos el email de verificación
                            Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification();
                            gotoMain(email, password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(findViewById(R.id.createAccountActivity), getText(R.string.error_create_account), Snackbar.LENGTH_LONG).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void gotoMain(String email, String pass) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("email", email);
        i.putExtra("pass", pass);
        startActivity(i);
    }
}

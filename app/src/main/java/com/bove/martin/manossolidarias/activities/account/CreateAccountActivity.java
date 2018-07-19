package com.bove.martin.manossolidarias.activities.account;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends BaseActivity {
    private ImageView imageViewBack;
    private EditText editTextEmail;
    private EditText editTextPass;
    private EditText editTextRePass;
    private Button buttonRegister;
    private TextView textViewAyuda;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        imageViewBack = findViewById(R.id.imageViewBack);
        editTextEmail = findViewById(R.id.editTextRegEmail);
        editTextPass  = findViewById(R.id.editTextRegPass);
        editTextRePass = findViewById(R.id.editTextRegRePass);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewAyuda = findViewById(R.id.textViewHelp);

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
    // TODO implementar la opción ver password
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


    private void createAccount(String email, String password) {
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
                            mAuth.getCurrentUser().sendEmailVerification();
                            gotoMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), getText(R.string.error_sigin), Snackbar.LENGTH_LONG).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void gotoMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}

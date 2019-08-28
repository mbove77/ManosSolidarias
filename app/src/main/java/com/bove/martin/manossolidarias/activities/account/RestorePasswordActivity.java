package com.bove.martin.manossolidarias.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class RestorePasswordActivity extends BaseActivity {
    private static final String TAG_EMAIL = "EmailActivity";
    private FirebaseAuth mAuth;

    private ImageView imageViewback;
    private EditText editTextEmail;
    private Button buttonRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonRestore = findViewById(R.id.buttonRestorePass);
        imageViewback = findViewById(R.id.imageViewBack);

        mAuth = FirebaseAuth.getInstance();

        // Fix drawables
        BaseActivity.setVectorForPreLollipop(editTextEmail, R.drawable.ic_person, this, BaseActivity.DRAWABLE_LEFT );

        imageViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RestorePasswordActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restablecerPass();
            }
        });
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

                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.restorePassLayout), getText(R.string.email_forgot_send), Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent i = new Intent(RestorePasswordActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    });
                            hideKeyboard(RestorePasswordActivity.this);
                            snackbar.show();
                        }
                    }
                });
    }
}

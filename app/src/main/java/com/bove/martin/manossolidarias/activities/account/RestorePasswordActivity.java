package com.bove.martin.manossolidarias.activities.account;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;

public class RestorePasswordActivity extends BaseActivity {

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

        // Fix drawables
        BaseActivity.setVectorForPreLollipop(editTextEmail, R.drawable.ic_person, this, BaseActivity.DRAWABLE_LEFT );

        imageViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RestorePasswordActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}

package com.bove.martin.manossolidarias.activities.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bove.martin.manossolidarias.activities.HomeActivity;
import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.bove.martin.manossolidarias.model.Institucion;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;

public class BaseActivity extends AppCompatActivity {
   // TODO reamplazar todos los string con variables declaradas aqui

    // Constantes
    public static final String DB_DONATIONS = "donaciones";
    public static final String DB_ONGS = "instituciones";
    public static final String DB_MENSAJES = "mensajes";
    public static final String SHARED_PREF = "pref";

    // Shared Preferences
    public SharedPreferences preferences;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargamos las shared para mostrar la ayuda una sola vez.
        preferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    // Logout
    public  void logout() {
        if(mAuth != null) {
            mAuth.signOut();
        } else {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
        }
        LoginManager.getInstance().logOut();
        goToMain();
    }

    // Intent para ir a la Home (Donaciones)
    public void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Intent para ir a la página de login
    public  void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    // Get current Firebase User
    public FirebaseUser getUser() {
       if(user != null) { return user; } else { return user = FirebaseAuth.getInstance().getCurrentUser(); }
    }

    // Set Firebase User
    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    // return sharedPef
    public SharedPreferences getPreferences() {
        return preferences;
    }
}

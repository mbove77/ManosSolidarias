package com.bove.martin.manossolidarias.activities.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bove.martin.manossolidarias.activities.HomeActivity;
import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {
    public final String DB_DONATIONS = "donaciones";
    public final String DB_ONGS = "instituciones";
    public final String SHARED_PREF = "pref";


    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

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

    public void logout() {
        // Firebase sign out
        if(mAuth != null) {
            mAuth.signOut();
        } else {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
        }
        LoginManager.getInstance().logOut();
        goToMain();
    }

    public FirebaseUser getUser() {
       if(user != null) {
           return user;
       } else {
           return user = FirebaseAuth.getInstance().getCurrentUser();
       }
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public  void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }
}

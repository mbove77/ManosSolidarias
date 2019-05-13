package com.bove.martin.manossolidarias.activities.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bove.martin.manossolidarias.activities.DonationActivity;
import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.HomeActivity;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {
    // TODO reemplazar todos los string con variables declaradas aquí.
    // TODO mejorar comunicación visual de falta de conexión y ver de bloquear la ui.

    // Constantes
    public static final String DB_DONATIONS = "donaciones";
    public static final String DB_SUGGEST_DONATIONS = "sugerencias_donaciones";
    public static final String DB_SUGGEST_ONG = "sugerencias_ong";
    public static final String DB_ONGS = "instituciones";
    public static final String DB_NEWS = "noticias";

    public static final String SHARED_PREF = "pref";

    public static final String INFO_ALERT = "info";
    public static final String WARN_ALERT = "warning";
    public static final String ERROR_ALERT = "error";
    public static final String SUCCESS_ALERT = "exito";

    public static int NO_DISTANCIA = 100000000;

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

    // Muestra mensaje de dialogo uniforme en todos los activities
    public void showAlertDialog(String tipo, String mensaje, String positiveButton, final Intent positiveIntent, String negativeButton, final Intent negativeIntent) {
        String titulo;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflateView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        builder.setView(inflateView);

        final TextView tituloTextView = inflateView.findViewById(R.id.alertTitle);
        final TextView mensajeTextView = inflateView.findViewById(R.id.alertMensaje);

        final ImageView exitoImage = inflateView.findViewById(R.id.imageViewOK);
        final ImageView infoImage = inflateView.findViewById(R.id.imageViewINFO);
        final ImageView warnImage = inflateView.findViewById(R.id.imageViewWARN);
        final ImageView errorImage = inflateView.findViewById(R.id.imageViewERROR);

        switch (tipo) {
            case SUCCESS_ALERT:
                exitoImage.setVisibility(View.VISIBLE);
                titulo = "Exito!";
                tituloTextView.setText(titulo);
                break;
            case WARN_ALERT:
                warnImage.setVisibility(View.VISIBLE);
                titulo = "Aviso!";
                tituloTextView.setText(titulo);
                break;
            case ERROR_ALERT:
                errorImage.setVisibility(View.VISIBLE);
                titulo = "Error!";
                tituloTextView.setText(titulo);
                break;
            case INFO_ALERT:
                infoImage.setVisibility(View.VISIBLE);
                titulo = "Información";
                tituloTextView.setText(titulo);
                break;
        }

        mensajeTextView.setText(mensaje);

        if(positiveButton != null) {
            builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(positiveIntent != null)
                        startActivity(positiveIntent);
                }
            });
        }

        if(negativeButton != null) {
            builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(negativeIntent != null)
                        startActivity(negativeIntent);
                }
            });
        }

        // Creamos y mostramos
        builder.setCancelable(false);
        builder.create().show();
    }
}

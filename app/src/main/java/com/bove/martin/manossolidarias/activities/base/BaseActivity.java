package com.bove.martin.manossolidarias.activities.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.HomeActivity;
import com.bove.martin.manossolidarias.activities.MainActivity;
import com.bove.martin.manossolidarias.activities.broadcast.NetworkChangeReceiver;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static com.google.android.material.snackbar.Snackbar.make;
import static com.bove.martin.manossolidarias.activities.broadcast.NetworkChangeReceiver.IS_NETWORK_AVAILABLE;

public class BaseActivity extends AppCompatActivity {
    // TODO reemplazar todos los string con variables declaradas aquí.
    // TODO Crear popUp con icono de desconexión
    // TODO Implementar funcionalidad para denunciar datos errados en los perfiles
    // TODO Remover icono de location de la lista de ongs si no tiene dirección
    // Posibles nuevos fetures
    // TODO Opciones para ordenar y buscar en la lista de ongs
    // TODO Agregar a fav ongs
    // TODO Recibir notificaciones de la app y de ongs especificas
    // TODO Actualizar theme a Theme.MaterialComponents.DayNight.DarkActionBar
    // TODO Incluir animaciones con la lib lottie
    // TODO Filtrar los resultados de las ong
    // TODO Reaccionar a mensaje entrante con notificación
    // TODO Ir migrando a kotlin y empezar a usar jetpack

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

    public static final int DRAWABLE_LEFT = 1;
    public static final int DRAWABLE_RIGHT = 2;
    public static final int DRAWABLE_TOP = 3;
    public static final int DRAWABLE_BOTTOM = 4;

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

        // Cargamos el FCM token
        //getFCMToken();

        // Cargamos las shared para mostrar la ayuda una sola vez.
        preferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        // Mostramos mensaje si no hay conexión
        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? getString(R.string.online_msj) : getString(R.string.offline_msj);

                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(),  networkStatus, Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();

                if(isNetworkAvailable)
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                else
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGoogleLight));

                snackbar.show();
            }
        }, intentFilter);

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

    public void hideKeyboard(Activity activity) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    // Trae el token del dispositivo para FCM Firebase Cloud Menssages
    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM_TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.fcm_token, token);
                        Log.d("FCM_TOKEN", msg);
                    }
                });
    }


    //region Helper method for PreLollipop TextView & Buttons Vector Images
    public static Drawable setVectorForPreLollipop(int resourceId, Context activity) {
        Drawable icon;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon = VectorDrawableCompat.create(activity.getResources(), resourceId, activity.getTheme());
        } else {
            icon = activity.getResources().getDrawable(resourceId, activity.getTheme());
        }

        return icon;
    }

    // Para solucionar el error en los drawables en los dispositivos pre-Lolipop
    public static void setVectorForPreLollipop(TextView textView, int resourceId, Context activity, int position) {
        Drawable icon;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon = VectorDrawableCompat.create(activity.getResources(), resourceId,
                    activity.getTheme());
        } else {
            icon = activity.getResources().getDrawable(resourceId, activity.getTheme());
        }
        switch (position) {
            case DRAWABLE_LEFT:
                textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null,
                        null);
                break;

            case DRAWABLE_RIGHT:
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon,
                        null);
                break;

            case DRAWABLE_TOP:
                textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null,
                        null);
                break;

            case DRAWABLE_BOTTOM:
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                        icon);
                break;
        }
    }

    public static Drawable getPrelolipotDrawable(int resourceId, Context activity) {
        Drawable icon;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon = VectorDrawableCompat.create(activity.getResources(), resourceId,
                    activity.getTheme());
        } else {
            icon = activity.getResources().getDrawable(resourceId, activity.getTheme());
        }
        return icon;
    }

}

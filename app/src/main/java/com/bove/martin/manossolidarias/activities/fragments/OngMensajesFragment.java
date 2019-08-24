package com.bove.martin.manossolidarias.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication;
import com.bove.martin.manossolidarias.activities.utils.DateFormat;
import com.bove.martin.manossolidarias.model.ChatUser;
import com.bove.martin.manossolidarias.model.Institucion;
import com.bove.martin.manossolidarias.model.Mensaje;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.MessageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OngMensajesFragment extends Fragment {
    private final String TAG = "ONG_MSJ";

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private List<Mensaje> mensajes;

    private Map<String, Object> mensajeMap = new HashMap<>();

    private Institucion ong;
    private FirebaseUser user;
    private ChatUser ongChat;
    private ChatUser userChat;

    public SharedPreferences preferences;

    private View rootView;
    private MessageView messageView;

    private TextView mensajeField;
    private ImageView sendButton;

    private FragmentComunication callback;

    public OngMensajesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (FragmentComunication) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " Debes implementar Fragament Comunication.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ong_mensajes, container, false);

        messageView =  rootView.findViewById(R.id.message_view);
        messageView.setRightBubbleColor(ContextCompat.getColor(getContext(), R.color.chatColorAdmin));
        messageView.setRightMessageTextColor(ContextCompat.getColor(getContext(), R.color.colorListText));
        messageView.setLeftBubbleColor(ContextCompat.getColor(getContext(), R.color.chatColorUser));
        messageView.setLeftMessageTextColor(ContextCompat.getColor(getContext(), R.color.colorListText));

        sendButton = rootView.findViewById(R.id.sendMensaje);
        mensajeField = rootView.findViewById(R.id.editTextMsj);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMensaje();
            }
        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chat");

        getCurrentONG();
        user = getCurrentUser();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((BaseActivity) getActivity()).showProgressDialog();
        loadUsers();
        loadMensajes();
    }

    // Load users
    private  void loadUsers() {
        ongChat = new ChatUser(0, ong.getNombre());
        userChat = new ChatUser(1, (user.getDisplayName().isEmpty()) ? user.getEmail() : user.getDisplayName());
    }

    // Load mensajes from db
    private void loadMensajes() {
        final String ongid = ong.getKey();
        final String userid = user.getUid();

        // Read from the database first time
        myRef.child(ongid).child(userid).child("mensajes").orderByChild("timeSpam").limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            // First time read
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        GenericTypeIndicator<Mensaje> t = new GenericTypeIndicator<Mensaje>() {};
                        Mensaje mensaje =  messageSnapshot.getValue(t);

                        if(!mensajeMap.containsKey(String.valueOf(mensaje.getTimeSpam()))) {
                            mensajeMap.put(String.valueOf(mensaje.getTimeSpam()), mensaje);
                        }
                    }
                } else {
                    // Si no hay resultados creamos el primer mensaje
                    Mensaje nuevoMensaje = new Mensaje(ong.getNombre(), getString(R.string.welcome_chat), true);
                    printMensaje(nuevoMensaje);
                    //mensajeMap.put(String.valueOf(nuevoMensaje.getTimeSpam()),nuevoMensaje);
                    //myRef.child(ongid).child(userid).child("mensajes").updateChildren(mensajeMap);
                }
                myRef.removeEventListener(this);
                regChildListener();
                ((BaseActivity) getActivity()).hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                ((BaseActivity) getActivity()).hideProgressDialog();
            }
        });

    }

    // Imprimimos el mensaje en pantalla
    private void printMensaje(Mensaje mensaje) {
        messageView.setMessage(buildChatMensaje(mensaje));
        messageView.scrollToEnd();
    }

    // Crea un mensaje de chat a partir de un mensaje de db
    private Message buildChatMensaje(Mensaje mensaje) {
        // Seteando la fecha
        Date d = new Date(mensaje.getTimeSpam());
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        Message message = new Message.Builder()
                .setUser((mensaje.isAdmin()) ? ongChat : userChat)
                .hideIcon(true)
                .setRight(mensaje.isAdmin())
                .setText(mensaje.getMensaje())
                .setSendTime(c)
                .setDateFormatter(new DateFormat())
                .build();

        return message;
    }

    // remueve un mensaje del chat view
    private void removeMensaje(Mensaje mensaje) {
        messageView.remove(buildChatMensaje(mensaje));
        messageView.scrollToEnd();
    }

    // Recibimos el mensaje del usuario
    private void sendMensaje() {
        if(mensajeField.getText().length() > 0) {
            Mensaje mensaje = new Mensaje(userChat.getName(), mensajeField.getText().toString(), false);
            saveMensaje(mensaje);
            mensajeField.setText("");
        }
    }

    // Guardamos el mensaje en la base de datos
    private void saveMensaje(Mensaje mensaje) {
        mensajeMap.put(String.valueOf(mensaje.getTimeSpam()), mensaje);
        myRef.child(ong.getKey()).child(user.getUid()).child("mensajes").updateChildren(mensajeMap);
    }

    // Registramos cambios en la base de datos
    private void regChildListener() {
        myRef.child(ong.getKey()).child(user.getUid()).child("mensajes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<Mensaje> t = new GenericTypeIndicator<Mensaje>() {};
                Mensaje mensaje =  dataSnapshot.getValue(t);

                printMensaje(mensaje);

                if(!mensajeMap.containsKey(String.valueOf(mensaje.getTimeSpam()))) {
                    mensajeMap.put(String.valueOf(mensaje.getTimeSpam()),mensaje );
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                dataSnapshot.getValue();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Todo para remover un mensaje hay que almacenar todos los message del chat view en un mapa para buscarlos por id
                // GenericTypeIndicator<Mensaje> t = new GenericTypeIndicator<Mensaje>() {};
                // Mensaje mensaje =  dataSnapshot.getValue(t);
                //removeMensaje(mensaje);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private FirebaseUser getCurrentUser() {
        if(user != null) {
            return user;
        } else {
            return user = FirebaseAuth.getInstance().getCurrentUser();
        }
    }

    private void getCurrentONG() {
        if (ong == null ) {
            if (preferences == null) {
                // Obtenemos el objeto guardado previamente en las pref
                preferences = this.getActivity().getSharedPreferences(BaseActivity.SHARED_PREF, Context.MODE_PRIVATE);
            }
            Gson gson = new Gson();
            String json = preferences.getString("institucion", "");
            ong = gson.fromJson(json, Institucion.class);
        }
    }

}

package com.bove.martin.manossolidarias.activities.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.OngInfoActivity
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication
import com.bove.martin.manossolidarias.activities.utils.DateFormat
import com.bove.martin.manossolidarias.model.ChatUser
import com.bove.martin.manossolidarias.model.Institucion
import com.bove.martin.manossolidarias.model.Mensaje
import com.github.bassaer.chatmessageview.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_ong_mensajes.*

import java.util.*

class OngMensajesFragment : Fragment() {
    private val TAG = "ONG_MSJ"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private val mensajes: List<Mensaje>? = null
    private val mensajeMap: MutableMap<String, Any?> = HashMap()
    private lateinit var ong: Institucion
    private lateinit var user: FirebaseUser
    private var ongChat: ChatUser? = null
    private var userChat: ChatUser? = null
    private lateinit var preferences: SharedPreferences
    private var rootView: View? = null
    private var callback: FragmentComunication? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as FragmentComunication
        } catch (e: Exception) {
            throw ClassCastException("$context Debes implementar Fragament Comunication.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_ong_mensajes, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = (activity as OngInfoActivity).getSharedPreferences()!!
        (activity as BaseActivity?)!!.showProgressDialog()
        message_view.setRightBubbleColor(ContextCompat.getColor(context!!, R.color.chatColorAdmin))
        message_view.setRightMessageTextColor(ContextCompat.getColor(context!!, R.color.colorListText))
        message_view.setLeftBubbleColor(ContextCompat.getColor(context!!, R.color.chatColorUser))
        message_view.setLeftMessageTextColor(ContextCompat.getColor(context!!, R.color.colorListText))
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("chat")
        loadOng()
        laodCurrentUser()
        loadUsers()
        loadMensajes()
    }

    // TODO Revisar user.getDisplayName() si viene nullo tira un NPE cuando llamamos a isEmpty()
    // Load users
    private fun loadUsers() {
        ongChat = ChatUser(0, ong!!.nombre!!)
        userChat = ChatUser(1, if (user!!.displayName!!.isEmpty()) user.email!! else user!!.displayName!!)
    }

    // Load mensajes from db
    private fun loadMensajes() {
        val ongid = ong!!.key
        val userid = user!!.uid

        // Read from the database first time
        if (ongid != null) {
            myRef.child(ongid).child(userid).child("mensajes").orderByChild("timeSpam").limitToFirst(50).addListenerForSingleValueEvent(object : ValueEventListener {
                // First time read
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (messageSnapshot in dataSnapshot.children) {
                            val t: GenericTypeIndicator<Mensaje?> = object : GenericTypeIndicator<Mensaje?>() {}
                            val mensaje = messageSnapshot.getValue(t)
                            if (!mensajeMap.containsKey(mensaje!!.timeSpam.toString())) {
                                mensajeMap[mensaje.timeSpam.toString()] = mensaje
                            }
                        }
                    } else {
                        // Si no hay resultados creamos el primer mensaje
                        val nuevoMensaje = Mensaje(ong.nombre, getString(R.string.welcome_chat), true)
                        printMensaje(nuevoMensaje)
                        //mensajeMap.put(String.valueOf(nuevoMensaje.getTimeSpam()),nuevoMensaje);
                        //myRef.child(ongid).child(userid).child("mensajes").updateChildren(mensajeMap);
                    }
                    myRef.removeEventListener(this)
                    regChildListener()
                    (activity as BaseActivity?)!!.hideProgressDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                    (activity as BaseActivity?)!!.hideProgressDialog()
                }
            })
        }
    }

    // Imprimimos el mensaje en pantalla
    private fun printMensaje(mensaje: Mensaje?) {
        message_view.setMessage(buildChatMensaje(mensaje))
        message_view.scrollToEnd()
    }

    // Crea un mensaje de chat a partir de un mensaje de db
    private fun buildChatMensaje(mensaje: Mensaje?): Message {
        // Seteando la fecha
        val d = Date(mensaje!!.timeSpam)
        val c = Calendar.getInstance()
        c.time = d
        return Message.Builder()
                .setUser(if (mensaje.isAdmin) ongChat!! else userChat!!)
                .hideIcon(true)
                .setRight(mensaje.isAdmin)
                .setText(mensaje.mensaje!!)
                .setSendTime(c)
                .setDateFormatter(DateFormat())
                .build()
    }

    // remueve un mensaje del chat view
    private fun removeMensaje(mensaje: Mensaje) {
        message_view.remove(buildChatMensaje(mensaje))
        message_view.scrollToEnd()
    }

    // Recibimos el mensaje del usuario
    private fun sendMensaje() {
        if (editTextMsj.text.length > 0) {
            val mensaje = Mensaje(userChat?.getName(), editTextMsj.text.toString(), false)
            saveMensaje(mensaje)
            editTextMsj.setText("")
        }
    }

    // Guardamos el mensaje en la base de datos
    private fun saveMensaje(mensaje: Mensaje) {
        mensajeMap[mensaje.timeSpam.toString()] = mensaje
        myRef.child(ong.key!!).child(user.uid).child("mensajes").updateChildren(mensajeMap)
    }

    // Registramos cambios en la base de datos
    private fun regChildListener() {
        myRef.child(ong.key!!).child(user.uid).child("mensajes").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val t: GenericTypeIndicator<Mensaje?> = object : GenericTypeIndicator<Mensaje?>() {}
                val mensaje = dataSnapshot.getValue(t)
                printMensaje(mensaje)
                if (!mensajeMap.containsKey(mensaje!!.timeSpam.toString())) {
                    mensajeMap[mensaje.timeSpam.toString()] = mensaje
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                dataSnapshot.value
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Todo para remover un mensaje hay que almacenar todos los message del chat view en un mapa para buscarlos por id
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                dataSnapshot.value
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun laodCurrentUser(): Unit {
        user = FirebaseAuth.getInstance().currentUser!!
    }


    // Obtenemos el objeto guardado previamente en las pref
    private fun loadOng(): Unit{
        val gson = Gson()
        val json = preferences.getString("institucion", "")
        ong =  gson.fromJson(json, Institucion::class.java)
    }

}
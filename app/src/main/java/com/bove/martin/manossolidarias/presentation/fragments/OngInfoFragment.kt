package com.bove.martin.manossolidarias.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bove.martin.manossolidarias.R

import com.bove.martin.manossolidarias.databinding.FragmentOngInfoBinding
import com.bove.martin.manossolidarias.domain.model.Institucion
import com.bove.martin.manossolidarias.presentation.OngInfoActivity

import com.bumptech.glide.Glide
import com.google.gson.Gson

class OngInfoFragment : Fragment() {
    private lateinit var ong: Institucion
    private lateinit var preferences: SharedPreferences

    private var _binding: FragmentOngInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOngInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = (activity as OngInfoActivity).getSharedPreferences()!!
        loadOng()

        // Cargamos los elementos
        Glide.with(this)
                .load(ong.header_img_url)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(binding.infoOngHeader)

        Glide.with(this)
                .load(ong.logo_url)
                .circleCrop()
                .placeholder(R.drawable.oval_place_holder)
                .into(binding.infoOngLogo)

        binding.infoMision.text = ong.descripcion
        if (TextUtils.isEmpty(ong.horario)) {
            binding.infoHorarioTittle.visibility = View.GONE
            binding.infoHorario.visibility = View.GONE
        } else {
            binding.infoHorario.text = ong.horario
        }
        if (TextUtils.isEmpty(ong.direccion)) {
            binding.infoDireccionTittle.visibility = View.GONE
            binding.infoDireccion.visibility = View.GONE
        } else {
            binding.infoDireccion.text = ong.direccion
            //fixme solucionar la comunicacion con el activity usando viewmodels
           // binding.infoDireccion.setOnClickListener { callback!!.changeTab(OngInfoActivity.MAP_FRAGMENT) }
        }
        if (TextUtils.isEmpty(ong.misc)) {
            binding.infoMiscTittle.visibility = View.GONE
            binding.infoMisc.visibility = View.GONE
        } else {
            binding.infoMisc.text = ong.misc
        }
        if (TextUtils.isEmpty(ong.telefono)) {
            binding.OngTeleiIcon.visibility = View.GONE
            binding.ongInfoTel.visibility = View.GONE
            binding.divider1.visibility = View.GONE
        } else {
            binding.ongInfoTel.text = ong.telefono
            binding.ongInfoTel.setOnClickListener { makeCall(ong.telefono) }
        }
        if (TextUtils.isEmpty(ong.email)) {
            binding.OngMailiIcon.visibility = View.GONE
            binding.ongInfoEmail.visibility = View.GONE
            binding.divider2.visibility = View.GONE
        } else {
            binding.ongInfoEmail.text = ong.email
            binding.ongInfoEmail.setOnClickListener { openMail(ong.email) }
        }
        if (TextUtils.isEmpty(ong.web)) {
            binding.OngWebiIcon.visibility = View.GONE
            binding.ongInfoWeb.visibility = View.GONE
            binding.divider3.visibility = View.GONE
        } else {
            binding.ongInfoWeb.text = ong.web
            binding.ongInfoWeb.setOnClickListener { openWeb(ong.web) }
        }

        // Social section
        if (TextUtils.isEmpty(ong.facebook)) {
            binding.iconicsImageFB.visibility = View.GONE
        } else {
            binding.iconicsImageFB.setOnClickListener { openLink(ong.facebook, "facebook") }
        }
        if (TextUtils.isEmpty(ong.instagram)) {
            binding.iconicsImageInstagram.visibility = View.GONE
        } else {
            binding.iconicsImageInstagram.setOnClickListener { openLink(ong.instagram, "instagram") }
        }
        if (TextUtils.isEmpty(ong.twitter)) {
            binding.iconicsImageTwitter.visibility = View.GONE
        } else {
            binding.iconicsImageTwitter.setOnClickListener { openLink(ong.twitter, "twitter") }
        }
        if (TextUtils.isEmpty(ong.youtube)) {
            binding.iconicsImageYoutube.visibility = View.GONE
        } else {
            binding.iconicsImageYoutube.setOnClickListener { openLink(ong.youtube, "youtube") }
        }
        if (TextUtils.isEmpty(ong.whatsapp)) {
            binding.iconicsImageWhatsapp.visibility = View.GONE
        } else {
            binding.iconicsImageWhatsapp.setOnClickListener { openLink(ong.whatsapp, "whastapp") }
        }
    }

    // Obtenemos el objeto guardado previamente en las pref
    private fun loadOng() {
        val gson = Gson()
        val json = preferences.getString("institucion", "")
        ong =  gson.fromJson(json, Institucion::class.java)
    }

    private fun makeCall(telefono: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$telefono")
        startActivity(intent)
    }

    private fun openMail(emial: String?) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:$emial")
        startActivity(emailIntent)
    }

    private fun openWeb(web: String?) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(web)
        try {
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, getString(R.string.error_no_intent), Toast.LENGTH_SHORT).show()
        }
    }

    // Open links in native apps
    private fun openLink(link: String?, service: String) {
        var base_url = ""
        var service_url = ""
        var paquete = ""
        var customIntent = false
        when (service) {
            "facebook" -> {
                base_url = "https://m.facebook.com/"
                paquete = ""
                service_url = ""
            }
            "twitter" -> {
                base_url = "https://twitter.com/"
                paquete = "com.twitter.android"
                service_url = "http://twitter.com/"
            }
            "instagram" -> {
                base_url = "http://instagram.com/"
                paquete = "com.instagram.android"
                service_url = "http://instagram.com/_u/"
            }
            "youtube" -> {
                base_url = "https://www.youtube.com/user/"
                paquete = "com.google.android.youtube"
                service_url = "https://www.youtube.com/user/"
            }
            "whastapp" -> {
                customIntent = true
                try {
                    val text = "Hola " + ong.nombre
                    // Formato 543416709663
                    val toNumber = ong.whatsapp
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$toNumber&text=$text")
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        if (!customIntent) {
            val uri = Uri.parse(service_url + link)
            val likeIng = Intent(Intent.ACTION_VIEW, uri)
            likeIng.setPackage(paquete)
            try {
                startActivity(likeIng)
            } catch (e: ActivityNotFoundException) {
                openWeb(base_url + link)
            }
        }
    }

    // Si se resume debe traer nuevamente el la ong del sharedPref.
    override fun onResume() {
        super.onResume()
        loadOng()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
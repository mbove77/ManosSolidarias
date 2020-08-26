package com.bove.martin.manossolidarias.domain.model

import com.bove.martin.manossolidarias.presentation.base.BaseActivity
import com.google.firebase.firestore.GeoPoint

/**
 * Created by Martín Bove on 05/07/2018.
 * E-mail: mbove77@gmail.com
 */
class Institucion : Comparable<Institucion> {
    var key: String? = null
    var nombre: String? = null
    var descripcion: String? = null
    var donaciones: Map<String, Long>? = null
    var email: String? = null
    var facebook: String? = null
    var header_img_url: String? = null
    var horario: String? = null
    var instagram: String? = null
    var localizacion: GeoPoint? = null
    var logo_url: String? = null
    var misc: String? = null
    var telefono: String? = null
    var twitter: String? = null
    var web: String? = null
    var whatsapp: String? = null
    var youtube: String? = null
    var direccion: String? = null
    var distancia = 0f
    var isEspecial = false
    var isAprobado = false

    constructor() {}

    constructor(key: String?, nombre: String?, descripcion: String?, donaciones: Map<String, Long>?, email: String?, facebook: String?, header_img_url: String?, horario: String?, instagram: String?, localizacion: GeoPoint?, logo_url: String?, misc: String?, telefono: String?, twitter: String?, web: String?, whatsapp: String?, youtube: String?, direccion: String?, especial: Boolean, aprobado: Boolean) {
        this.key = key
        this.nombre = nombre
        this.descripcion = descripcion
        this.donaciones = donaciones
        this.email = email
        this.facebook = facebook
        this.header_img_url = header_img_url
        this.horario = horario
        this.instagram = instagram
        this.localizacion = localizacion
        this.logo_url = logo_url
        this.misc = misc
        this.telefono = telefono
        this.twitter = twitter
        this.web = web
        this.whatsapp = whatsapp
        this.youtube = youtube
        this.direccion = direccion
        isEspecial = especial
        isAprobado = aprobado
    }

    // Constructor para el agregar nueva ong
    constructor(nombre: String?, especial: Boolean) {
        this.nombre = nombre
        isEspecial = especial
        logo_url = "https://manos-solidarias.firebaseapp.com/icons/icon-new-ong.png"
        distancia = BaseActivity.NO_DISTANCIA.toFloat()
    }

    override fun compareTo(o: Institucion): Int {
        return if (distancia < o.distancia) -1 else if (distancia > o.distancia) 1 else 0
    }
}
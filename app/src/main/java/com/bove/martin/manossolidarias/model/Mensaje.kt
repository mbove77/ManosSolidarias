package com.bove.martin.manossolidarias.model

/**
 * Created by Martín Bove on 31/08/2018.
 * E-mail: mbove77@gmail.com
 */
class Mensaje {
    var userName: String? = null
    var mensaje: String? = null
    var isAdmin = false
    var timeSpam: Long = 0

    constructor() {}

    constructor(userName: String?, mensaje: String?, admin: Boolean) {
        this.userName = userName
        this.mensaje = mensaje
        isAdmin = admin
        timeSpam = createTiemSpam()
    }

    private fun createTiemSpam(): Long {
        return System.currentTimeMillis()
    }
}
package com.bove.martin.manossolidarias.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by Martín Bove on 21/11/2018.
 * E-mail: mbove77@gmail.com
 */
data class SugerenciaDonacion (var nombre: String? = null, var desc: String? = null, var userKey: String? = null, @ServerTimestamp var timestamp: Date? = null)
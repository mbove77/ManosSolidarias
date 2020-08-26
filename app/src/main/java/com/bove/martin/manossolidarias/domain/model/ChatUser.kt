package com.bove.martin.manossolidarias.domain.model

import android.graphics.Bitmap
import com.github.bassaer.chatmessageview.model.IChatUser

/**
 * Created by Martín Bove on 27/08/2018.
 * E-mail: mbove77@gmail.com
 */
class ChatUser(private var id: Int, name: String) : IChatUser {
    private lateinit var icon: Bitmap
    private var name: String

    init {
      //  icon = icon
        this.name = name
    }

    fun setId(id: Int) {
        this.id = id
    }
    override fun getIcon(): Bitmap? {
        return icon
    }

    override fun setIcon(icon: Bitmap) {
        this.icon = icon
    }

    override fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    override fun getId(): String {
        return id.toString()
    }
}
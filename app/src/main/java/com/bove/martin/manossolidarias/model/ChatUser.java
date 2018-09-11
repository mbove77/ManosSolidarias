package com.bove.martin.manossolidarias.model;


import android.graphics.Bitmap;

import com.github.bassaer.chatmessageview.model.IChatUser;



/**
 * Created by Martín Bove on 27/08/2018.
 * E-mail: mbove77@gmail.com
 */
public class ChatUser  implements IChatUser{
    private int id;
    private Bitmap icon;
    private String name;

    public ChatUser(int id, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }
}

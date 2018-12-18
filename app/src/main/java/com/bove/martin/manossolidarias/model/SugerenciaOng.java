package com.bove.martin.manossolidarias.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Martín Bove on 21/11/2018.
 * E-mail: mbove77@gmail.com
 */
public class SugerenciaOng {
    private String nombre;
    private String desc;
    private String misc;
    private String userKey;

    @ServerTimestamp
    private Date timestamp;

    public SugerenciaOng() { }

    public SugerenciaOng(String nombre, String desc,String misc, String userKey, Date timestamp) {
        this.nombre = nombre;
        this.desc = desc;
        this.misc = misc;
        this.userKey = userKey;
        this.timestamp = timestamp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
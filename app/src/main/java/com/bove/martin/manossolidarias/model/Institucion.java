package com.bove.martin.manossolidarias.model;

import android.support.annotation.NonNull;

import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Martín Bove on 05/07/2018.
 * E-mail: mbove77@gmail.com
 */
public class Institucion implements Comparable<Institucion> {
    private String key;
    private String nombre;
    private String descripcion;
    private Map<String, Long> donaciones;
    private String email;
    private String facebook;
    private String header_img_url;
    private String horario;
    private String instagram;
    private GeoPoint localizacion;
    private String logo_url;
    private String misc;
    private String telefono;
    private String twitter;
    private String web;
    private String whatsapp;
    private String youtube;
    private String direccion;
    private float distancia;
    private boolean especial;

    public Institucion() {}

    public Institucion(String key, String nombre, String descripcion, Map<String, Long> donaciones, String email, String facebook, String header_img_url, String horario, String instagram, GeoPoint localizacion, String logo_url, String misc, String telefono, String twitter, String web, String whatsapp, String youtube, String direccion, boolean especial) {
        this.key = key;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.donaciones = donaciones;
        this.email = email;
        this.facebook = facebook;
        this.header_img_url = header_img_url;
        this.horario = horario;
        this.instagram = instagram;
        this.localizacion = localizacion;
        this.logo_url = logo_url;
        this.misc = misc;
        this.telefono = telefono;
        this.twitter = twitter;
        this.web = web;
        this.whatsapp = whatsapp;
        this.youtube = youtube;
        this.direccion = direccion;
        this.especial = especial;
    }

    // Constructor para el agregar nueva ong
    public Institucion(String nombre, boolean especial) {
        this.nombre = nombre;
        this.especial = especial;
        this.logo_url = "https://manos-solidarias.firebaseapp.com/icons/icon-new-ong.png";
        this.distancia = BaseActivity.NO_DISTANCIA;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map<String, Long> getDonaciones() {
        return donaciones;
    }

    public void setDonaciones(Map<String, Long> donaciones) {
        this.donaciones = donaciones;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getHeader_img_url() {
        return header_img_url;
    }

    public void setHeader_img_url(String header_img_url) {
        this.header_img_url = header_img_url;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public GeoPoint getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(GeoPoint localizacion) {
        this.localizacion = localizacion;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public boolean isEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    @Override
    public int compareTo(@NonNull Institucion o) {
        if (this.distancia < o.distancia)
            return -1;
        else if (this.distancia > o.distancia)
            return 1;
        else
            return 0;
    }
}

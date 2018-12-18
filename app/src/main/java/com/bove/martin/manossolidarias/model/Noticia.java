package com.bove.martin.manossolidarias.model;

/**
 * Created by Martín Bove on 28/06/2018.
 * E-mail: mbove77@gmail.com
 */
public class Noticia {
    private String titulo;
    private String desc;
    private String foto_url;
    private String link;
    private String ong_id;

    public Noticia() { }

    public Noticia(String titulo, String desc, String foto_url, String link, String ong_id) {
        this.titulo = titulo;
        this.desc = desc;
        this.foto_url = foto_url;
        this.link = link;
        this.ong_id = ong_id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOng_id() {
        return ong_id;
    }

    public void setOng_id(String ong_id) {
        this.ong_id = ong_id;
    }
}

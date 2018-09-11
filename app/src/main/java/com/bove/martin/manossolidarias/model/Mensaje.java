package com.bove.martin.manossolidarias.model;

/**
 * Created by Martín Bove on 31/08/2018.
 * E-mail: mbove77@gmail.com
 */
public class Mensaje {
    private String userName;
    private String mensaje;
    private boolean admin;
    private long timeSpam;

    public Mensaje() { }

    public Mensaje(String userName, String mensaje, boolean admin) {
        this.userName = userName;
        this.mensaje = mensaje;
        this.admin = admin;
        this.timeSpam = createTiemSpam();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public long getTimeSpam() {
        return timeSpam;
    }

    public void setTimeSpam(long timeSpam) {
        this.timeSpam = timeSpam;
    }

    private long createTiemSpam() {
        return System.currentTimeMillis();
    }
}

package com.darwindeveloper.nearby.search_devices;

/**
 * Created by DARWIN on 22/12/2016.
 */

public class Dispositivo {
    private String nombre;
    private String adress;
    private boolean vinculado;
    boolean header;

    public Dispositivo(boolean header, String nombre) {
        this.header = header;
        this.nombre = nombre;
    }

    public Dispositivo(String nombre, String adress, boolean vinculado) {
        this.nombre = nombre;
        this.adress = adress;
        this.vinculado = vinculado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public boolean isVinculado() {
        return vinculado;
    }

    public void setVinculado(boolean vinculado) {
        this.vinculado = vinculado;
    }

    public boolean isHeader() {
        return header;
    }
}

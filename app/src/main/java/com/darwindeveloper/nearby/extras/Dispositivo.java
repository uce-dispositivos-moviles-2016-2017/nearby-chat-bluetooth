package com.darwindeveloper.nearby.extras;

/**
 * Created by DARWIN on 2/1/2017.
 */

public class Dispositivo {



    private String MAC, NOMBRE;

    /**
     * @param MAC    direccion mac del bluetooth
     * @param NOMBRE nombre del dispositivo bluetooh
     */
    public Dispositivo(String MAC, String NOMBRE) {
        this.MAC = MAC;
        this.NOMBRE = NOMBRE;
    }


    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }
}

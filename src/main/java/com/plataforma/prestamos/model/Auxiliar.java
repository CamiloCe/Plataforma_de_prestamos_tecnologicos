package com.plataforma.prestamos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AUXILIAR")
public class Auxiliar extends Usuario {

    private String sedeAsignada;

    public Auxiliar() {}

    public Auxiliar(String nombre, String correo, String password, String sedeAsignada) {
        super(nombre, correo, password);
        this.sedeAsignada = sedeAsignada;
    }

    public String getSedeAsignada() { return sedeAsignada; }
    public void setSedeAsignada(String sedeAsignada) { this.sedeAsignada = sedeAsignada; }
}
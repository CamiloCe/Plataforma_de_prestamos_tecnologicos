package com.plataforma.prestamos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SOLICITANTE")
public class Solicitante extends Usuario {

    private boolean bloqueado; 

    public Solicitante() {}

    public Solicitante(String nombre, String correo, String password, boolean bloqueado) {
        super(nombre, correo, password);
        this.bloqueado = bloqueado;
    }

    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
}
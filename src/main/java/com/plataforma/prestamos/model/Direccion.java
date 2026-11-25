// Direccion.java
package com.plataforma.prestamos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DIRECCION")
public class Direccion extends Usuario {
    public Direccion() {}
    public Direccion(String nombre, String correo, String password) {
        super(nombre, correo, password);
    }
}
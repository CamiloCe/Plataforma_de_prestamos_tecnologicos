// Mantenimiento.java
package com.plataforma.prestamos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANTENIMIENTO")
public class Mantenimiento extends Usuario {
    public Mantenimiento() {}
    public Mantenimiento(String nombre, String correo, String password) {
        super(nombre, correo, password);
    }
}
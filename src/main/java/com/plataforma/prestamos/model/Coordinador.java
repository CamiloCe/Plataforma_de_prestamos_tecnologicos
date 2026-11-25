// Coordinador.java
package com.plataforma.prestamos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COORDINADOR")
public class Coordinador extends Usuario {
    public Coordinador() {}
    public Coordinador(String nombre, String correo, String password) {
        super(nombre, correo, password);
    }
}
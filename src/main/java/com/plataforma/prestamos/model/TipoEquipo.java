package com.plataforma.prestamos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_equipo")
public class TipoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipo;

    private String nombre;
    private String descripcion;
    private boolean especializado; // false = Estándar, true = Especializado (RN-06)

    public TipoEquipo() {}

    public TipoEquipo(String nombre, String descripcion, boolean especializado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.especializado = especializado;
    }

    public Integer getIdTipo() { return idTipo; }
    public void setIdTipo(Integer idTipo) { this.idTipo = idTipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isEspecializado() { return especializado; }
    public void setEspecializado(boolean especializado) { this.especializado = especializado; }
}
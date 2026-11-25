package com.plataforma.prestamos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidentes")
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idIncidente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_prestamo")
    private Prestamo prestamo;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private boolean resuelto = false;

    public Incidente() {
    }

    // Getters y Setters
    public Integer getIdIncidente() { return idIncidente; }
    public void setIdIncidente(Integer idIncidente) { this.idIncidente = idIncidente; }
    public Prestamo getPrestamo() { return prestamo; }
    public void setPrestamo(Prestamo prestamo) { this.prestamo = prestamo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isResuelto() { return resuelto; }
    public void setResuelto(boolean resuelto) { this.resuelto = resuelto; }
}
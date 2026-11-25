// Aviso.java
package com.plataforma.prestamos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "avisos")
public class Aviso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAviso;

    private String tipo; 
    private String estadoEnvio;
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    public void registrarEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
        this.fechaHora = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getIdAviso() { return idAviso; }
    public void setIdAviso(Integer idAviso) { this.idAviso = idAviso; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstadoEnvio() { return estadoEnvio; }
    public void setEstadoEnvio(String estadoEnvio) { this.estadoEnvio = estadoEnvio; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
}
package com.plataforma.prestamos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @Column(unique = true, nullable = false, length = 12)
    private String codigoRetiro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_solicitante")
    private Solicitante solicitante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_unidad")
    private UnidadEquipo unidadEquipo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false, length = 20)
    private String tipoSolicitud; // ESTANDAR o ESPECIALIZADA

    @Column(nullable = false, length = 25)
    private String estado; // PENDIENTE, CONFIRMADA, EN_PRESTAMO, FINALIZADA, RECHAZADA, NO_CONFIRMADA, CANCELADA, VENCIDA

    @Column(length = 255)
    private String justificacion;

    public Reserva() {
        this.codigoRetiro = "RES-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public void confirmar() {
        this.estado = "CONFIRMADA";
    }

    public void cancelar() {
        this.estado = "CANCELADA";
    }

    public void vencer() {
        this.estado = "VENCIDA";
    }

    // Getters y Setters
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }
    public String getCodigoRetiro() { return codigoRetiro; }
    public void setCodigoRetiro(String codigoRetiro) { this.codigoRetiro = codigoRetiro; }
    public Solicitante getSolicitante() { return solicitante; }
    public void setSolicitante(Solicitante solicitante) { this.solicitante = solicitante; }
    public UnidadEquipo getUnidadEquipo() { return unidadEquipo; }
    public void setUnidadEquipo(UnidadEquipo unidadEquipo) { this.unidadEquipo = unidadEquipo; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public String getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(String tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }
}
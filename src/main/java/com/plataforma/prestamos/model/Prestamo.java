package com.plataforma.prestamos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPrestamo;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_reserva", unique = true)
    private Reserva reserva;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_auxiliar_entrega")
    private Auxiliar auxiliarEntrega;

    @ManyToOne
    @JoinColumn(name = "id_auxiliar_recepcion")
    private Auxiliar auxiliarRecepcion;

    @Column(nullable = false)
    private LocalDateTime fechaHoraRetiro;

    private LocalDateTime fechaHoraDevolucion;

    @Column(nullable = false, length = 30)
    private String estadoPrestamo; // ACTIVO, DEVUELTO_A_TIEMPO, DEVUELTO_CON_RETRASO

    @Column(nullable = false)
    private boolean tardio = false;

    @Column(length = 255)
    private String observacionesDevolucion;

    public Prestamo() {
    }

    // Getters y Setters
    public Integer getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(Integer idPrestamo) { this.idPrestamo = idPrestamo; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Auxiliar getAuxiliarEntrega() { return auxiliarEntrega; }
    public void setAuxiliarEntrega(Auxiliar auxiliarEntrega) { this.auxiliarEntrega = auxiliarEntrega; }

    public Auxiliar getAuxiliarRecepcion() { return auxiliarRecepcion; }
    public void setAuxiliarRecepcion(Auxiliar auxiliarRecepcion) { this.auxiliarRecepcion = auxiliarRecepcion; }

    public LocalDateTime getFechaHoraRetiro() { return fechaHoraRetiro; }
    public void setFechaHoraRetiro(LocalDateTime fechaHoraRetiro) { this.fechaHoraRetiro = fechaHoraRetiro; }

    public LocalDateTime getFechaHoraDevolucion() { return fechaHoraDevolucion; }
    public void setFechaHoraDevolucion(LocalDateTime fechaHoraDevolucion) { this.fechaHoraDevolucion = fechaHoraDevolucion; }

    public String getEstadoPrestamo() { return estadoPrestamo; }
    public void setEstadoPrestamo(String estadoPrestamo) { this.estadoPrestamo = estadoPrestamo; }

    public boolean isTardio() { return tardio; }
    public void setTardio(boolean tardio) { this.tardio = tardio; }

    public String getObservacionesDevolucion() { return observacionesDevolucion; }
    public void setObservacionesDevolucion(String observacionesDevolucion) { this.observacionesDevolucion = observacionesDevolucion; }
}
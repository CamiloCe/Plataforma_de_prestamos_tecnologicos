package com.plataforma.prestamos.service;

import com.plataforma.prestamos.model.*;
import com.plataforma.prestamos.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoService {

    private final ReservaRepository reservaRepo;
    private final PrestamoRepository prestamoRepo;
    private final UsuarioRepository usuarioRepo;
    private final UnidadEquipoRepository unidadEquipoRepo;
    private final IncidenteRepository incidenteRepo;
    private final AuditoriaYAvisosService auditoriaYAvisos;

    public PrestamoService(ReservaRepository reservaRepo,
                           PrestamoRepository prestamoRepo,
                           UsuarioRepository usuarioRepo,
                           UnidadEquipoRepository unidadEquipoRepo,
                           IncidenteRepository incidenteRepo,
                           AuditoriaYAvisosService auditoriaYAvisos) {
        this.reservaRepo = reservaRepo;
        this.prestamoRepo = prestamoRepo;
        this.usuarioRepo = usuarioRepo;
        this.unidadEquipoRepo = unidadEquipoRepo;
        this.incidenteRepo = incidenteRepo;
        this.auditoriaYAvisos = auditoriaYAvisos;
    }

    //  Registrar entrega física del equipo ingresando el código único del estudiante
    @Transactional
    public Prestamo registrarRetiroPorCodigo(Auxiliar auxiliar, String codigoRetiro, LocalDateTime relojOperativo) {
        String codigoLimpio = codigoRetiro != null ? codigoRetiro.trim().toUpperCase() : "";
        Reserva reserva = reservaRepo.findByCodigoRetiroIgnoreCase(codigoLimpio)
                .orElseThrow(() -> new IllegalArgumentException("Código de reserva no encontrado: " + codigoLimpio));

        if (!reserva.getUnidadEquipo().getSede().equalsIgnoreCase(auxiliar.getSedeAsignada())) {
            throw new IllegalStateException("Esta reserva corresponde a " + reserva.getUnidadEquipo().getSede() +
                    ", pero estás operando en " + auxiliar.getSedeAsignada() + ".");
        }

        if (!"CONFIRMADA".equals(reserva.getEstado())) {
            throw new IllegalStateException("La reserva " + reserva.getCodigoRetiro() +
                    " no está lista para retiro (Estado actual: " + reserva.getEstado() + ").");
        }

        // Validar ventana de 30 minutos desde la hora de inicio (RN-10)
        LocalDateTime limiteRetiro = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio()).plusMinutes(30);
        if (relojOperativo.isAfter(limiteRetiro)) {
            reserva.vencer();
            reservaRepo.save(reserva);
            throw new IllegalStateException("La reserva " + reserva.getCodigoRetiro() +
                    " superó los 30 minutos de tolerancia y pasó automáticamente a VENCIDA.");
        }

        reserva.setEstado("EN_PRESTAMO");
        reservaRepo.save(reserva);

        Prestamo prestamo = new Prestamo();
        prestamo.setReserva(reserva);
        prestamo.setAuxiliarEntrega(auxiliar);
        prestamo.setFechaHoraRetiro(relojOperativo);
        prestamo.setEstadoPrestamo("ACTIVO");

        Prestamo guardado = prestamoRepo.save(prestamo);

        auditoriaYAvisos.registrarAuditoria(auxiliar, "ENTREGA_EQUIPO",
                "Auxiliar entregó unidad " + reserva.getUnidadEquipo().getCodigo() +
                " con código " + reserva.getCodigoRetiro());

        return guardado;
    }

    //Registrar devolución del equipo, verificar retraso y reportar incidente opcional
    @Transactional
    public Prestamo registrarDevolucion(Auxiliar auxiliar, Integer idPrestamo, LocalDateTime horaDevolucion,
                                        String observaciones, boolean reportarIncidente, String descripcionIncidente) {
        Prestamo prestamo = prestamoRepo.findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado."));

        if (!"ACTIVO".equals(prestamo.getEstadoPrestamo())) {
            throw new IllegalStateException("Este préstamo ya fue cerrado anteriormente.");
        }

        Reserva reserva = prestamo.getReserva();
        if (!reserva.getUnidadEquipo().getSede().equalsIgnoreCase(auxiliar.getSedeAsignada())) {
            throw new IllegalStateException("Solo un auxiliar de " + reserva.getUnidadEquipo().getSede() + " puede recibir este equipo.");
        }

        LocalDateTime fechaHoraLimite = LocalDateTime.of(reserva.getFecha(), reserva.getHoraFin());
        boolean conRetraso = horaDevolucion.isAfter(fechaHoraLimite);

        prestamo.setAuxiliarRecepcion(auxiliar);
        prestamo.setFechaHoraDevolucion(horaDevolucion);
        prestamo.setObservacionesDevolucion(observaciones);

        if (conRetraso) {
            prestamo.setEstadoPrestamo("DEVUELTO_CON_RETRASO");
            // Bloquear automáticamente al solicitante por devolución tardía
            Solicitante sol = reserva.getSolicitante();
            sol.setBloqueado(true);
            usuarioRepo.save(sol);
            auditoriaYAvisos.emitirAviso(reserva, "DEVOLUCION_TARDIA", false);
        } else {
            prestamo.setEstadoPrestamo("DEVUELTO_A_TIEMPO");
        }

        reserva.setEstado("FINALIZADA");
        reservaRepo.save(reserva);

        //  Si se reporta daño o incidente, bloquear la unidad para mantenimiento
        if (reportarIncidente) {
            UnidadEquipo unidad = reserva.getUnidadEquipo();
            unidad.setEstado(false);
            unidadEquipoRepo.save(unidad);

            Incidente inc = new Incidente();
            inc.setPrestamo(prestamo);
            inc.setFechaHora(horaDevolucion);
            inc.setDescripcion((descripcionIncidente != null && !descripcionIncidente.isBlank())
                    ? descripcionIncidente : "Daño reportado en devolución por auxiliar.");
            inc.setResuelto(false);
            incidenteRepo.save(inc);
        }

        Prestamo cerrado = prestamoRepo.save(prestamo);

        auditoriaYAvisos.registrarAuditoria(auxiliar, "DEVOLUCION_EQUIPO",
                "Devolución de reserva " + reserva.getCodigoRetiro() + " (" + cerrado.getEstadoPrestamo() + ")" +
                (reportarIncidente ? " + INCIDENTE REPORTADO" : ""));

        return cerrado;
    }

    public List<Prestamo> listarPrestamosActivosPorSede(String sede) {
        return prestamoRepo.findByEstadoPrestamoOrderByFechaHoraRetiroDesc("ACTIVO").stream()
                .filter(p -> p.getReserva().getUnidadEquipo().getSede().equalsIgnoreCase(sede))
                .toList();
    }

    public List<Prestamo> listarHistorialPrestamosPorSede(String sede) {
        return prestamoRepo.findAllByOrderByFechaHoraRetiroDesc().stream()
                .filter(p -> p.getReserva().getUnidadEquipo().getSede().equalsIgnoreCase(sede))
                .toList();
    }
}
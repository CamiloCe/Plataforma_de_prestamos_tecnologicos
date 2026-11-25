package com.plataforma.prestamos.service;

import com.plataforma.prestamos.model.*;
import com.plataforma.prestamos.repository.ReservaRepository;
import com.plataforma.prestamos.repository.UnidadEquipoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepo;
    private final UnidadEquipoRepository unidadEquipoRepo;
    private final CatalogoDisponibilidadService catalogoService;
    private final AuditoriaYAvisosService auditoriaYAvisos;

    public ReservaService(ReservaRepository reservaRepo,
                          UnidadEquipoRepository unidadEquipoRepo,
                          CatalogoDisponibilidadService catalogoService,
                          AuditoriaYAvisosService auditoriaYAvisos) {
        this.reservaRepo = reservaRepo;
        this.unidadEquipoRepo = unidadEquipoRepo;
        this.catalogoService = catalogoService;
        this.auditoriaYAvisos = auditoriaYAvisos;
    }

    @Transactional
    public synchronized Reserva crearReserva(Solicitante solicitante, Integer idUnidad, LocalDate fecha,
                                             LocalTime horaInicio, LocalTime horaFin, boolean simularFalloAviso) {
        if (solicitante.isBloqueado()) {
            throw new IllegalStateException("Tu cuenta se encuentra bloqueada por retraso o sanción y no puedes realizar reservas.");
        }

        // Validar fecha >= hoy, horas enteras y máximo 4 horas
        catalogoService.validarFranjaHoraria(fecha, horaInicio, horaFin);

        UnidadEquipo unidad = unidadEquipoRepo.findById(idUnidad)
                .orElseThrow(() -> new IllegalArgumentException("La unidad de equipo seleccionada no existe."));
        if (!unidad.isEstado()) {
            throw new IllegalStateException("La unidad " + unidad.getCodigo() + " está fuera de servicio.");
        }

        List<Reserva> conflictos = reservaRepo.findReservasSolapadas(unidad, fecha, horaInicio, horaFin);
        if (!conflictos.isEmpty()) {
            throw new IllegalStateException("Conflicto de disponibilidad: la unidad " + unidad.getCodigo() +
                    " ya fue reservada en esa franja horaria.");
        }

        Reserva reserva = new Reserva();
        reserva.setSolicitante(solicitante);
        reserva.setUnidadEquipo(unidad);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);

        boolean esEspecializado = unidad.getTipoEquipo().isEspecializado();
        if (esEspecializado) {
            reserva.setTipoSolicitud("ESPECIALIZADA");
            reserva.setEstado("PENDIENTE");
        } else {
            reserva.setTipoSolicitud("ESTANDAR");
            reserva.confirmar();
        }

        Reserva guardada = reservaRepo.save(reserva);

        auditoriaYAvisos.registrarAuditoria(solicitante, "CREAR_RESERVA",
                "Reserva " + guardada.getCodigoRetiro() + " (" + guardada.getTipoSolicitud() +
                ") para unidad " + unidad.getCodigo() + " en estado " + guardada.getEstado());

        if (!esEspecializado) {
            auditoriaYAvisos.emitirAviso(guardada, "CONFIRMACION", simularFalloAviso);
        }

        return guardada;
    }

    @Transactional
    public synchronized Reserva gestionarSolicitudEspecializada(Coordinador coordinador, Integer idReserva,
                                                                boolean aprobar, String justificacion,
                                                                boolean simularFalloAviso) {
        Reserva reserva = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada."));

        if (!"PENDIENTE".equals(reserva.getEstado())) {
            throw new IllegalStateException("Solo se pueden gestionar reservas en estado PENDIENTE.");
        }

        if (aprobar) {
            UnidadEquipo unidad = reserva.getUnidadEquipo();
            List<Reserva> conflictos = reservaRepo.findReservasSolapadas(
                    unidad, reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin());
            conflictos.removeIf(r -> r.getIdReserva().equals(reserva.getIdReserva()));

            if (!unidad.isEstado() || !conflictos.isEmpty()) {
                reserva.setEstado("NO_CONFIRMADA");
                reserva.setJustificacion("Unidad no disponible al momento de la revisión.");
                Reserva resNoConf = reservaRepo.save(reserva);
                auditoriaYAvisos.registrarAuditoria(coordinador, "NO_CONFIRMADA_RESERVA",
                        "Reserva " + resNoConf.getCodigoRetiro() + " marcada como NO_CONFIRMADA.");
                auditoriaYAvisos.emitirAviso(resNoConf, "RECHAZO", simularFalloAviso);
                return resNoConf;
            }

            reserva.confirmar();
            reserva.setJustificacion((justificacion != null && !justificacion.isBlank()) ? justificacion : "Aprobada por Coordinación.");
            Reserva aprobada = reservaRepo.save(reserva);
            auditoriaYAvisos.registrarAuditoria(coordinador, "APROBAR_RESERVA",
                    "Reserva " + aprobada.getCodigoRetiro() + " aprobada por Coordinador.");
            auditoriaYAvisos.emitirAviso(aprobada, "CONFIRMACION", simularFalloAviso);
            return aprobada;
        } else {
            if (justificacion == null || justificacion.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar una justificación obligatoria para rechazar la solicitud.");
            }
            reserva.setEstado("RECHAZADA");
            reserva.setJustificacion(justificacion.trim());
            Reserva rechazada = reservaRepo.save(reserva);
            auditoriaYAvisos.registrarAuditoria(coordinador, "RECHAZAR_RESERVA",
                    "Reserva " + rechazada.getCodigoRetiro() + " rechazada. Motivo: " + rechazada.getJustificacion());
            auditoriaYAvisos.emitirAviso(rechazada, "RECHAZO", simularFalloAviso);
            return rechazada;
        }
    }

    @Transactional
    public Reserva cancelarReserva(Solicitante solicitante, Integer idReserva, boolean simularFalloAviso) {
        Reserva reserva = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada."));

        if (!reserva.getSolicitante().getIdUsuario().equals(solicitante.getIdUsuario())) {
            throw new SecurityException("Solo el titular puede cancelar esta reserva.");
        }

        if (!"PENDIENTE".equals(reserva.getEstado()) && !"CONFIRMADA".equals(reserva.getEstado())) {
            throw new IllegalStateException("No se puede cancelar una reserva en estado " + reserva.getEstado());
        }

        reserva.cancelar();
        Reserva cancelada = reservaRepo.save(reserva);

        auditoriaYAvisos.registrarAuditoria(solicitante, "CANCELAR_RESERVA",
                "Reserva " + cancelada.getCodigoRetiro() + " cancelada por el titular.");
        auditoriaYAvisos.emitirAviso(cancelada, "CANCELACION", simularFalloAviso);
        return cancelada;
    }

    @Transactional
    public int procesarVencimientos(Usuario ejecutor, LocalDateTime relojActual, boolean simularFalloAviso) {
        List<Reserva> confirmadas = reservaRepo.findByEstadoOrderByFechaAscHoraInicioAsc("CONFIRMADA");
        int vencidasCount = 0;

        for (Reserva r : confirmadas) {
            LocalDateTime limiteRetiro = LocalDateTime.of(r.getFecha(), r.getHoraInicio()).plusMinutes(30);
            if (relojActual.isAfter(limiteRetiro)) {
                r.vencer();
                reservaRepo.save(r);
                vencidasCount++;
                auditoriaYAvisos.registrarAuditoria(ejecutor, "VENCIMIENTO_RESERVA",
                        "Reserva " + r.getCodigoRetiro() + " marcada como VENCIDA (superó 30 min sin retiro).");
                auditoriaYAvisos.emitirAviso(r, "VENCIMIENTO", simularFalloAviso);
            }
        }
        return vencidasCount;
    }

    public List<Reserva> listarReservasPorSolicitante(Solicitante solicitante) {
        return reservaRepo.findBySolicitanteOrderByFechaDescHoraInicioDesc(solicitante);
    }

    public List<Reserva> listarReservasPendientes() {
        return reservaRepo.findByEstadoOrderByFechaAscHoraInicioAsc("PENDIENTE");
    }

    public List<Reserva> listarTodas() {
        return reservaRepo.findAll();
    }
}
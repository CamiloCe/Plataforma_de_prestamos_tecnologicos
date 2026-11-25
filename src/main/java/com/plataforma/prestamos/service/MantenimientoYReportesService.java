package com.plataforma.prestamos.service;

import com.plataforma.prestamos.model.*;
import com.plataforma.prestamos.repository.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MantenimientoYReportesService {

    private final IncidenteRepository incidenteRepo;
    private final UnidadEquipoRepository unidadEquipoRepo;
    private final ReservaRepository reservaRepo;
    private final PrestamoRepository prestamoRepo;
    private final AuditoriaYAvisosService auditoriaService;

    public MantenimientoYReportesService(IncidenteRepository incidenteRepo,
                                         UnidadEquipoRepository unidadEquipoRepo,
                                         ReservaRepository reservaRepo,
                                         PrestamoRepository prestamoRepo,
                                         AuditoriaYAvisosService auditoriaService) {
        this.incidenteRepo = incidenteRepo;
        this.unidadEquipoRepo = unidadEquipoRepo;
        this.reservaRepo = reservaRepo;
        this.prestamoRepo = prestamoRepo;
        this.auditoriaService = auditoriaService;
    }

    // Listar incidentes abiertos y unidades fuera de servicio
    public List<Incidente> listarIncidentesPendientes() {
        return incidenteRepo.findByResueltoOrderByFechaHoraDesc(false);
    }

    public List<Incidente> listarHistorialIncidentes() {
        return incidenteRepo.findAllByOrderByFechaHoraDesc();
    }

    public List<UnidadEquipo> listarUnidadesInactivas() {
        return unidadEquipoRepo.findByEstado(false);
    }

    // Resolver incidente y reactivar la unidad para el inventario disponible
    @Transactional
    public void resolverIncidenteYReactivarUnidad(Mantenimiento tecnico, Integer idIncidente, String solucionAplicada) {
        Incidente incidente = incidenteRepo.findById(idIncidente)
                .orElseThrow(() -> new IllegalArgumentException("Incidente no encontrado."));

        if (incidente.isResuelto()) {
            throw new IllegalStateException("Este incidente ya fue cerrado.");
        }

        incidente.setResuelto(true);
        if (solucionAplicada != null && !solucionAplicada.isBlank()) {
            incidente.setDescripcion(incidente.getDescripcion() + " | Resolución: " + solucionAplicada.trim());
        }
        incidenteRepo.save(incidente);

        UnidadEquipo unidad = incidente.getPrestamo().getReserva().getUnidadEquipo();
        unidad.setEstado(true);
        unidadEquipoRepo.save(unidad);

        auditoriaService.registrarAuditoria(tecnico, "REACTIVAR_UNIDAD",
                "Incidente #" + idIncidente + " cerrado. Unidad " + unidad.getCodigo() + " reactivada.");
    }

    // Consolidado de indicadores operativos por sede para Dirección
    public Map<String, Map<String, Long>> generarInformeConsolidadoPorSede() {
        List<String> sedes = List.of("Sede Norte", "Sede Sur");
        List<Reserva> todasReservas = reservaRepo.findAll();
        List<Prestamo> todosPrestamos = prestamoRepo.findAll();
        List<UnidadEquipo> todasUnidades = unidadEquipoRepo.findAll();
        List<Incidente> todosIncidentes = incidenteRepo.findAll();

        Map<String, Map<String, Long>> reporte = new LinkedHashMap<>();

        for (String sede : sedes) {
            Map<String, Long> metricas = new LinkedHashMap<>();

            long totalReservas = todasReservas.stream()
                    .filter(r -> r.getUnidadEquipo().getSede().equalsIgnoreCase(sede))
                    .count();

            long reservasVencidas = todasReservas.stream()
                    .filter(r -> r.getUnidadEquipo().getSede().equalsIgnoreCase(sede) && "VENCIDA".equals(r.getEstado()))
                    .count();

            long prestamosRealizados = todosPrestamos.stream()
                    .filter(p -> p.getReserva().getUnidadEquipo().getSede().equalsIgnoreCase(sede))
                    .count();

            long devolucionesTardias = todosPrestamos.stream()
                    .filter(p -> p.getReserva().getUnidadEquipo().getSede().equalsIgnoreCase(sede)
                            && "DEVUELTO_CON_RETRASO".equals(p.getEstadoPrestamo()))
                    .count();

            long unidadesOperativas = todasUnidades.stream()
                    .filter(u -> u.getSede().equalsIgnoreCase(sede) && u.isEstado())
                    .count();

            long unidadesEnMantenimiento = todasUnidades.stream()
                    .filter(u -> u.getSede().equalsIgnoreCase(sede) && !u.isEstado())
                    .count();

            long incidentesSede = todosIncidentes.stream()
                    .filter(i -> i.getPrestamo().getReserva().getUnidadEquipo().getSede().equalsIgnoreCase(sede))
                    .count();

            metricas.put("totalReservas", totalReservas);
            metricas.put("reservasVencidas", reservasVencidas);
            metricas.put("prestamosRealizados", prestamosRealizados);
            metricas.put("devolucionesTardias", devolucionesTardias);
            metricas.put("unidadesOperativas", unidadesOperativas);
            metricas.put("unidadesEnMantenimiento", unidadesEnMantenimiento);
            metricas.put("incidentesSede", incidentesSede);

            reporte.put(sede, metricas);
        }

        return reporte;
    }
}
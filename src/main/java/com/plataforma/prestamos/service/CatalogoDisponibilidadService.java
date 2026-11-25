package com.plataforma.prestamos.service;

import com.plataforma.prestamos.model.TipoEquipo;
import com.plataforma.prestamos.model.UnidadEquipo;
import com.plataforma.prestamos.repository.ReservaRepository;
import com.plataforma.prestamos.repository.TipoEquipoRepository;
import com.plataforma.prestamos.repository.UnidadEquipoRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CatalogoDisponibilidadService {

    public static final int MAX_HORAS_RESERVA = 4;

    private final TipoEquipoRepository tipoEquipoRepo;
    private final UnidadEquipoRepository unidadEquipoRepo;
    private final ReservaRepository reservaRepo;

    public CatalogoDisponibilidadService(TipoEquipoRepository tipoEquipoRepo,
                                         UnidadEquipoRepository unidadEquipoRepo,
                                         ReservaRepository reservaRepo) {
        this.tipoEquipoRepo = tipoEquipoRepo;
        this.unidadEquipoRepo = unidadEquipoRepo;
        this.reservaRepo = reservaRepo;
    }

    public List<TipoEquipo> listarCatalogo() {
        return tipoEquipoRepo.findAll();
    }

    public void validarFranjaHoraria(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se permiten fechas anteriores a hoy (" + LocalDate.now() + ").");
        }
        if (horaInicio == null || horaFin == null || !horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        if (horaInicio.getMinute() != 0 || horaFin.getMinute() != 0) {
            throw new IllegalArgumentException("Las reservas solo se permiten en bloques de horas enteras (ej: 08:00 a 10:00).");
        }
        long horas = Duration.between(horaInicio, horaFin).toHours();
        if (horas > MAX_HORAS_RESERVA) {
            throw new IllegalArgumentException("Regla de negocio: el tiempo máximo de reserva es de " + MAX_HORAS_RESERVA + " horas continuas.");
        }
    }

    public List<UnidadEquipo> consultarUnidadesDisponibles(String sede, Integer idTipo, LocalDate fecha,
                                                           LocalTime horaInicio, LocalTime horaFin) {
        validarFranjaHoraria(fecha, horaInicio, horaFin);

        List<UnidadEquipo> candidatas;
        boolean filtrarSede = (sede != null && !sede.isBlank() && !"TODAS".equalsIgnoreCase(sede));

        if (filtrarSede && idTipo != null) {
            TipoEquipo tipo = tipoEquipoRepo.findById(idTipo)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de equipo no encontrado."));
            candidatas = unidadEquipoRepo.findBySedeAndTipoEquipoAndEstadoTrue(sede, tipo);
        } else if (filtrarSede) {
            candidatas = unidadEquipoRepo.findBySedeAndEstadoTrue(sede);
        } else if (idTipo != null) {
            TipoEquipo tipo = tipoEquipoRepo.findById(idTipo)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de equipo no encontrado."));
            candidatas = unidadEquipoRepo.findAll().stream()
                    .filter(u -> u.isEstado() && u.getTipoEquipo().getIdTipo().equals(tipo.getIdTipo()))
                    .toList();
        } else {
            candidatas = unidadEquipoRepo.findAll().stream()
                    .filter(UnidadEquipo::isEstado)
                    .toList();
        }

        List<UnidadEquipo> disponibles = new ArrayList<>();
        for (UnidadEquipo unidad : candidatas) {
            boolean ocupada = !reservaRepo.findReservasSolapadas(unidad, fecha, horaInicio, horaFin).isEmpty();
            if (!ocupada) {
                disponibles.add(unidad);
            }
        }
        return disponibles;
    }

    public Map<String, List<UnidadEquipo>> agruparDisponiblesPorSede(List<UnidadEquipo> disponibles) {
        Map<String, List<UnidadEquipo>> porSede = new LinkedHashMap<>();
        for (UnidadEquipo u : disponibles) {
            porSede.computeIfAbsent(u.getSede(), k -> new ArrayList<>()).add(u);
        }
        return porSede;
    }
}
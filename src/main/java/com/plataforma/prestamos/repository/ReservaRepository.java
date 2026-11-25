package com.plataforma.prestamos.repository;

import com.plataforma.prestamos.model.Reserva;
import com.plataforma.prestamos.model.Solicitante;
import com.plataforma.prestamos.model.UnidadEquipo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByCodigoRetiroIgnoreCase(String codigoRetiro);

    List<Reserva> findBySolicitanteOrderByFechaDescHoraInicioDesc(Solicitante solicitante);

    List<Reserva> findByEstadoOrderByFechaAscHoraInicioAsc(String estado);

    @Query("SELECT r FROM Reserva r WHERE r.unidadEquipo = :unidad " +
           "AND r.fecha = :fecha " +
           "AND r.estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_PRESTAMO') " +
           "AND (:horaInicio < r.horaFin AND :horaFin > r.horaInicio)")
    List<Reserva> findReservasSolapadas(
            @Param("unidad") UnidadEquipo unidad,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);
}
package com.plataforma.prestamos.repository;

import com.plataforma.prestamos.model.Incidente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidenteRepository extends JpaRepository<Incidente, Integer> {
    List<Incidente> findByResueltoOrderByFechaHoraDesc(boolean resuelto);
    List<Incidente> findAllByOrderByFechaHoraDesc();
}
package com.plataforma.prestamos.repository;

import com.plataforma.prestamos.model.Prestamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {
    List<Prestamo> findByEstadoPrestamoOrderByFechaHoraRetiroDesc(String estadoPrestamo);
    List<Prestamo> findAllByOrderByFechaHoraRetiroDesc();
}
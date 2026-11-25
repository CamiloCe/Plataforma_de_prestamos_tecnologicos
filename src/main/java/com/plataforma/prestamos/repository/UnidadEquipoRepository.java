package com.plataforma.prestamos.repository;

import com.plataforma.prestamos.model.TipoEquipo;
import com.plataforma.prestamos.model.UnidadEquipo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadEquipoRepository extends JpaRepository<UnidadEquipo, Integer> {
    List<UnidadEquipo> findBySedeAndEstadoTrue(String sede);
    List<UnidadEquipo> findBySedeAndTipoEquipoAndEstadoTrue(String sede, TipoEquipo tipoEquipo);
    List<UnidadEquipo> findByEstado(boolean estado);
    boolean existsByCodigo(String codigo);
}
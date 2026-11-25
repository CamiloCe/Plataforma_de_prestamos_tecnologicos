package com.plataforma.prestamos.repository;

import com.plataforma.prestamos.model.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEquipoRepository extends JpaRepository<TipoEquipo, Integer> {
}
// AvisoRepository.java
package com.plataforma.prestamos.repository;

import com.plataforma.prestamos.model.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoRepository extends JpaRepository<Aviso, Integer> {
}
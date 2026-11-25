// RegistroAuditoriaRepository.java
package com.plataforma.prestamos.repository;

import com.plataforma.prestamos.model.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Integer> {
}
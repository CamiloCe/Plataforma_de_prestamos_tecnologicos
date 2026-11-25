package com.plataforma.prestamos.service;

import com.plataforma.prestamos.model.Aviso;
import com.plataforma.prestamos.model.RegistroAuditoria;
import com.plataforma.prestamos.model.Reserva;
import com.plataforma.prestamos.model.Usuario;
import com.plataforma.prestamos.repository.AvisoRepository;
import com.plataforma.prestamos.repository.RegistroAuditoriaRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaYAvisosService {

    private final RegistroAuditoriaRepository auditoriaRepo;
    private final AvisoRepository avisoRepo;

    public AuditoriaYAvisosService(RegistroAuditoriaRepository auditoriaRepo, AvisoRepository avisoRepo) {
        this.auditoriaRepo = auditoriaRepo;
        this.avisoRepo = avisoRepo;
    }

   
    public void registrarAuditoria(Usuario usuario, String accion, String detalle) {
        RegistroAuditoria reg = new RegistroAuditoria();
        reg.setFechaHora(LocalDateTime.now());
        reg.setUsuario(usuario);
        reg.setAccion(accion);
        reg.setDetalle(detalle);
        auditoriaRepo.save(reg);
    }

  
    public void emitirAviso(Reserva reserva, String tipoAviso, boolean simularFalloEnvio) {
        Aviso aviso = new Aviso();
        aviso.setReserva(reserva);
        aviso.setTipo(tipoAviso);
        if (simularFalloEnvio) {
            aviso.registrarEstadoEnvio("FALLIDO");
        } else {
            aviso.registrarEstadoEnvio("ENVIADO");
        }
        avisoRepo.save(aviso);
    }
}
package com.plataforma.prestamos.service;

import com.plataforma.prestamos.model.Usuario;
import com.plataforma.prestamos.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

    private final UsuarioRepository usuarioRepo;
    private final AuditoriaYAvisosService auditoriaService;

    public AutenticacionService(UsuarioRepository usuarioRepo, AuditoriaYAvisosService auditoriaService) {
        this.usuarioRepo = usuarioRepo;
        this.auditoriaService = auditoriaService;
    }

    public Usuario autenticar(String correo, String password) {
        Optional<Usuario> opt = usuarioRepo.findByCorreo(correo);
        if (opt.isPresent() && opt.get().iniciarSesion(password)) {
            Usuario usuario = opt.get();
            auditoriaService.registrarAuditoria(usuario, "INICIO_SESION",
                    "Acceso exitoso al sistema con rol " + usuario.getRolNombre());
            return usuario;
        }
        return null;
    }
}
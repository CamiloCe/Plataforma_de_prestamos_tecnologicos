package com.plataforma.prestamos.controller;

import com.plataforma.prestamos.model.Usuario;
import com.plataforma.prestamos.service.AutenticacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AutenticacionService authService;

    public AuthController(AutenticacionService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String raiz(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u != null) {
            return redirigirSegunRol(u);
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        Usuario usuario = authService.autenticar(correo, password);
        if (usuario == null) {
            model.addAttribute("error", "Credenciales inválidas. Verifique correo y contraseña.");
            return "login";
        }
        session.setAttribute("usuarioLogueado", usuario);
        return redirigirSegunRol(usuario);
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String redirigirSegunRol(Usuario usuario) {
        return switch (usuario.getRolNombre()) {
            case "SOLICITANTE" -> "redirect:/solicitante/panel";
            case "COORDINADOR" -> "redirect:/coordinador/panel";
            case "AUXILIAR" -> "redirect:/auxiliar/panel";
            case "MANTENIMIENTO" -> "redirect:/mantenimiento/panel";
            case "DIRECCION" -> "redirect:/direccion/panel";
            default -> "redirect:/login";
        };
    }
}
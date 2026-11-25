package com.plataforma.prestamos.controller;

import com.plataforma.prestamos.model.Reserva;
import com.plataforma.prestamos.model.Solicitante;
import com.plataforma.prestamos.model.UnidadEquipo;
import com.plataforma.prestamos.model.Usuario;
import com.plataforma.prestamos.service.CatalogoDisponibilidadService;
import com.plataforma.prestamos.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/solicitante")
public class SolicitanteController {

    private final CatalogoDisponibilidadService catalogoService;
    private final ReservaService reservaService;

    public SolicitanteController(CatalogoDisponibilidadService catalogoService, ReservaService reservaService) {
        this.catalogoService = catalogoService;
        this.reservaService = reservaService;
    }

    @GetMapping("/panel")
    public String panelSolicitante(
            @RequestParam(required = false, defaultValue = "TODAS") String sede,
            @RequestParam(required = false) Integer idTipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            HttpSession session,
            Model model) {

        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Solicitante solicitante)) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", solicitante);
        model.addAttribute("catalogo", catalogoService.listarCatalogo());
        model.addAttribute("misReservas", reservaService.listarReservasPorSolicitante(solicitante));

        if (fecha != null && horaInicio != null && horaFin != null) {
            try {
                List<UnidadEquipo> disponibles = catalogoService.consultarUnidadesDisponibles(
                        sede, idTipo, fecha, horaInicio, horaFin);
                model.addAttribute("unidadesPorSede", catalogoService.agruparDisponiblesPorSede(disponibles));
                model.addAttribute("totalDisponibles", disponibles.size());
                model.addAttribute("sedeSel", sede);
                model.addAttribute("idTipoSel", idTipo);
                model.addAttribute("fechaSel", fecha);
                model.addAttribute("horaInicioSel", horaInicio);
                model.addAttribute("horaFinSel", horaFin);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        return "solicitante_panel";
    }

    @PostMapping("/reservar")
    public String crearReserva(@RequestParam Integer idUnidad,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
                               HttpSession session,
                               RedirectAttributes flash) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Solicitante solicitante)) {
            return "redirect:/login";
        }

        try {
            Reserva r = reservaService.crearReserva(solicitante, idUnidad, fecha, horaInicio, horaFin, false);
            flash.addFlashAttribute("exito", "Reserva #" + r.getIdReserva() +
                    " registrada exitosamente en estado: " + r.getEstado());
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/solicitante/panel";
    }

    @PostMapping("/cancelar/{idReserva}")
    public String cancelarReserva(@PathVariable Integer idReserva,
                                  HttpSession session,
                                  RedirectAttributes flash) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Solicitante solicitante)) {
            return "redirect:/login";
        }

        try {
            reservaService.cancelarReserva(solicitante, idReserva, false);
            flash.addFlashAttribute("exito", "Reserva #" + idReserva + " cancelada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/solicitante/panel";
    }
}
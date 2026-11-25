package com.plataforma.prestamos.controller;

import com.plataforma.prestamos.model.*;
import com.plataforma.prestamos.repository.AvisoRepository;
import com.plataforma.prestamos.repository.RegistroAuditoriaRepository;
import com.plataforma.prestamos.service.MantenimientoYReportesService;
import com.plataforma.prestamos.service.PrestamoService;
import com.plataforma.prestamos.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CoordinadorYRolesController {

    private final ReservaService reservaService;
    private final PrestamoService prestamoService;
    private final MantenimientoYReportesService mantReportesService;
    private final AvisoRepository avisoRepo;
    private final RegistroAuditoriaRepository auditoriaRepo;

    public CoordinadorYRolesController(ReservaService reservaService,
                                       PrestamoService prestamoService,
                                       MantenimientoYReportesService mantReportesService,
                                       AvisoRepository avisoRepo,
                                       RegistroAuditoriaRepository auditoriaRepo) {
        this.reservaService = reservaService;
        this.prestamoService = prestamoService;
        this.mantReportesService = mantReportesService;
        this.avisoRepo = avisoRepo;
        this.auditoriaRepo = auditoriaRepo;
    }

    // --- COORDINADOR ---
    @GetMapping("/coordinador/panel")
    public String panelCoordinador(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Coordinador coordinador)) return "redirect:/login";
        model.addAttribute("usuario", coordinador);
        model.addAttribute("pendientes", reservaService.listarReservasPendientes());
        model.addAttribute("todasReservas", reservaService.listarTodas());
        return "coordinador_panel";
    }

    @GetMapping("/coordinador/auditoria")
    public String auditoriaCoordinador(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Coordinador coordinador)) return "redirect:/login";
        model.addAttribute("usuario", coordinador);
        model.addAttribute("avisos", avisoRepo.findAll());
        model.addAttribute("auditoria", auditoriaRepo.findAll());
        return "coordinador_auditoria";
    }

    @PostMapping("/coordinador/gestionar/{idReserva}")
    public String gestionarEspecializada(@PathVariable Integer idReserva,
                                         @RequestParam boolean aprobar,
                                         @RequestParam(required = false) String justificacion,
                                         HttpSession session,
                                         RedirectAttributes flash) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Coordinador coordinador)) return "redirect:/login";
        try {
            Reserva r = reservaService.gestionarSolicitudEspecializada(coordinador, idReserva, aprobar, justificacion, false);
            flash.addFlashAttribute("exito", "Solicitud " + r.getCodigoRetiro() + " actualizada a: " + r.getEstado());
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/panel";
    }

    // --- AUXILIAR DE SEDE (Retiro por código + Devoluciones + Reloj Operativo) ---
    @GetMapping("/auxiliar/panel")
    public String panelAuxiliar(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Auxiliar aux)) return "redirect:/login";
        model.addAttribute("usuario", aux);
        model.addAttribute("reservasSede", reservaService.listarTodas().stream()
                .filter(r -> r.getUnidadEquipo().getSede().equalsIgnoreCase(aux.getSedeAsignada()))
                .toList());
        model.addAttribute("prestamosActivos", prestamoService.listarPrestamosActivosPorSede(aux.getSedeAsignada()));
        model.addAttribute("historialPrestamos", prestamoService.listarHistorialPrestamosPorSede(aux.getSedeAsignada()));
        return "auxiliar_panel";
    }

    @PostMapping("/auxiliar/registrar-retiro")
    public String registrarRetiro(@RequestParam String codigoRetiro,
                                  @RequestParam(defaultValue = "0") int desfaseMinutos,
                                  HttpSession session,
                                  RedirectAttributes flash) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Auxiliar aux)) return "redirect:/login";
        try {
            LocalDateTime relojOperativo = LocalDateTime.now().plusMinutes(desfaseMinutos);
            Prestamo p = prestamoService.registrarRetiroPorCodigo(aux, codigoRetiro, relojOperativo);
            flash.addFlashAttribute("exito", "Equipo entregado correctamente. Préstamo #" + p.getIdPrestamo() +
                    " iniciado para el código " + p.getReserva().getCodigoRetiro() + ".");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/auxiliar/panel";
    }

    @PostMapping("/auxiliar/registrar-devolucion/{idPrestamo}")
    public String registrarDevolucion(@PathVariable Integer idPrestamo,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDev,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaDev,
                                      @RequestParam(required = false) String observaciones,
                                      @RequestParam(defaultValue = "false") boolean reportarIncidente,
                                      @RequestParam(required = false) String descripcionIncidente,
                                      HttpSession session,
                                      RedirectAttributes flash) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Auxiliar aux)) return "redirect:/login";
        try {
            LocalDateTime fechaHoraDev = LocalDateTime.of(fechaDev, horaDev);
            Prestamo p = prestamoService.registrarDevolucion(
                    aux, idPrestamo, fechaHoraDev, observaciones, reportarIncidente, descripcionIncidente);
            if ("DEVUELTO_CON_RETRASO".equals(p.getEstadoPrestamo())) {
                flash.addFlashAttribute("error", "Devolución registrada CON RETRASO. El solicitante " +
                        p.getReserva().getSolicitante().getNombre() + " ha sido BLOQUEADO automáticamente.");
            } else {
                flash.addFlashAttribute("exito", "Devolución registrada a tiempo para la reserva " +
                        p.getReserva().getCodigoRetiro() + ".");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/auxiliar/panel";
    }

    @PostMapping("/auxiliar/adelantar-reloj")
    public String adelantarReloj(@RequestParam(defaultValue = "35") int minutosAdelanto,
                                 HttpSession session,
                                 RedirectAttributes flash) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Auxiliar aux)) return "redirect:/login";
        LocalDateTime relojAdelantado = LocalDateTime.now().plusMinutes(minutosAdelanto);
        int vencidas = reservaService.procesarVencimientos(aux, relojAdelantado, false);
        flash.addFlashAttribute("exito", "Verificación ejecutada (+ " + minutosAdelanto +
                " min): " + vencidas + " reserva(s) vencida(s) por no retiro.");
        return "redirect:/auxiliar/panel";
    }

    // --- PANEL DE MANTENIMIENTO (RF-14) ---
    @GetMapping("/mantenimiento/panel")
    public String panelMantenimiento(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Mantenimiento mant)) return "redirect:/login";
        model.addAttribute("usuario", mant);
        model.addAttribute("incidentesPendientes", mantReportesService.listarIncidentesPendientes());
        model.addAttribute("historialIncidentes", mantReportesService.listarHistorialIncidentes());
        model.addAttribute("unidadesInactivas", mantReportesService.listarUnidadesInactivas());
        return "mantenimiento_panel";
    }

    @PostMapping("/mantenimiento/resolver/{idIncidente}")
    public String resolverIncidente(@PathVariable Integer idIncidente,
                                    @RequestParam(required = false) String solucionAplicada,
                                    HttpSession session,
                                    RedirectAttributes flash) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Mantenimiento mant)) return "redirect:/login";
        try {
            mantReportesService.resolverIncidenteYReactivarUnidad(mant, idIncidente, solucionAplicada);
            flash.addFlashAttribute("exito", "Incidente #" + idIncidente + " resuelto y unidad reactivada en el inventario.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mantenimiento/panel";
    }

    // --- PANEL DE DIRECCIÓN (RF-15: Informes consolidados por sede) ---
    @GetMapping("/direccion/panel")
    public String panelDireccion(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (!(u instanceof Direccion dir)) return "redirect:/login";
        model.addAttribute("usuario", dir);
        model.addAttribute("informeSedes", mantReportesService.generarInformeConsolidadoPorSede());
        model.addAttribute("reservas", reservaService.listarTodas());
        return "direccion_panel";
    }
}
package com.plataforma.prestamos.config;

import com.plataforma.prestamos.model.*;
import com.plataforma.prestamos.repository.TipoEquipoRepository;
import com.plataforma.prestamos.repository.UnidadEquipoRepository;
import com.plataforma.prestamos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarDatosBase(UsuarioRepository usuarioRepo,
                                      TipoEquipoRepository tipoEquipoRepo,
                                      UnidadEquipoRepository unidadEquipoRepo) {
        return args -> {
            // 1. Usuarios base del sistema (si aún no existen)
            if (usuarioRepo.count() == 0) {
                Solicitante sol1 = new Solicitante();
                sol1.setNombre("Carlos Mendoza");
                sol1.setCorreo("solicitante@uni.edu");
                sol1.setPassword("1234");
                sol1.setBloqueado(false);
                usuarioRepo.save(sol1);

                Solicitante solBloq = new Solicitante();
                solBloq.setNombre("Laura Ríos (Sancionada)");
                solBloq.setCorreo("bloqueado@uni.edu");
                solBloq.setPassword("1234");
                solBloq.setBloqueado(true);
                usuarioRepo.save(solBloq);

                Coordinador coord = new Coordinador();
                coord.setNombre("Dra. Elena Torres");
                coord.setCorreo("coordinador@uni.edu");
                coord.setPassword("1234");
                usuarioRepo.save(coord);

                Auxiliar auxNorte = new Auxiliar();
                auxNorte.setNombre("Andrés Gómez");
                auxNorte.setCorreo("auxiliar@uni.edu");
                auxNorte.setPassword("1234");
                auxNorte.setSedeAsignada("Sede Norte");
                usuarioRepo.save(auxNorte);

                Auxiliar auxSur = new Auxiliar();
                auxSur.setNombre("Marta Salazar");
                auxSur.setCorreo("auxiliar.sur@uni.edu");
                auxSur.setPassword("1234");
                auxSur.setSedeAsignada("Sede Sur");
                usuarioRepo.save(auxSur);

                Mantenimiento mant = new Mantenimiento();
                mant.setNombre("Ing. Jorge Pardo");
                mant.setCorreo("mantenimiento@uni.edu");
                mant.setPassword("1234");
                usuarioRepo.save(mant);

                Direccion dir = new Direccion();
                dir.setNombre("Dir. Roberto Silva");
                dir.setCorreo("direccion@uni.edu");
                dir.setPassword("1234");
                usuarioRepo.save(dir);
            }

            // 2. Catálogo de Tipos de Equipo
            TipoEquipo portatil = obtenerOCrearTipo(tipoEquipoRepo,
                    "Portátil Académico",
                    "Laptop Core i7, 16GB RAM, ideal para desarrollo y ofimática.",
                    false);

            TipoEquipo videobeam = obtenerOCrearTipo(tipoEquipoRepo,
                    "Proyector / VideoBeam HD",
                    "Proyector HDMI de 3800 lúmenes para presentaciones en aula.",
                    false);

            TipoEquipo camara = obtenerOCrearTipo(tipoEquipoRepo,
                    "Cámara Profesional 4K",
                    "Cámara mirrorless con lente 24-70mm y trípode para producción audiovisual.",
                    true);

            TipoEquipo dron = obtenerOCrearTipo(tipoEquipoRepo,
                    "Dron Topográfico / Fotogrametría",
                    "Aeronave no tripulada con sensor multiespectral (requiere aval de Coordinación).",
                    true);

            // 3. CONFIGURACIÓN DE STOCK POR TIPO DE EQUIPO Y SEDE
            // Cambia el último número para definir cuántas unidades físicas existen de cada equipo:
            registrarStock(unidadEquipoRepo, portatil, "Sede Norte", "POR-N", 10); // 10 portátiles en Sede Norte
            registrarStock(unidadEquipoRepo, portatil, "Sede Sur",   "POR-S", 8);  // 8 portátiles en Sede Sur

            registrarStock(unidadEquipoRepo, videobeam, "Sede Norte", "VB-N", 5);  // 5 proyectores en Sede Norte
            registrarStock(unidadEquipoRepo, videobeam, "Sede Sur",   "VB-S", 4);  // 4 proyectores en Sede Sur

            registrarStock(unidadEquipoRepo, camara, "Sede Norte", "CAM-N", 4);    // 4 cámaras 4K en Sede Norte
            registrarStock(unidadEquipoRepo, camara, "Sede Sur",   "CAM-S", 3);    // 3 cámaras 4K en Sede Sur

            registrarStock(unidadEquipoRepo, dron, "Sede Norte", "DRN-N", 2);      // 2 drones en Sede Norte
            registrarStock(unidadEquipoRepo, dron, "Sede Sur",   "DRN-S", 2);      // 2 drones en Sede Sur
        };
    }

    private TipoEquipo obtenerOCrearTipo(TipoEquipoRepository repo, String nombre, String descripcion, boolean especializado) {
        return repo.findAll().stream()
                .filter(t -> t.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(() -> {
                    TipoEquipo nuevo = new TipoEquipo();
                    nuevo.setNombre(nombre);
                    nuevo.setDescripcion(descripcion);
                    nuevo.setEspecializado(especializado);
                    return repo.save(nuevo);
                });
    }

    private void registrarStock(UnidadEquipoRepository repo, TipoEquipo tipo, String sede, String prefijoCodigo, int cantidadStock) {
        for (int i = 1; i <= cantidadStock; i++) {
            String codigoUnidad = String.format("%s%02d", prefijoCodigo, i); 
            if (!repo.existsByCodigo(codigoUnidad)) {
                UnidadEquipo unidad = new UnidadEquipo();
                unidad.setCodigo(codigoUnidad);
                unidad.setSede(sede);
                unidad.setEstado(true);
                unidad.setTipoEquipo(tipo);
                repo.save(unidad);
            }
        }
    }
}
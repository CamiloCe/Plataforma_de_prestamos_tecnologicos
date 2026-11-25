document.addEventListener("DOMContentLoaded", () => {
    // 1. Selección rápida desde tarjeta de catálogo hacia el buscador
    const botonesSeleccionar = document.querySelectorAll(".btn-seleccionar-tipo");
    const selectTipo = document.getElementById("selectTipoEquipo");
    const seccionBuscador = document.getElementById("seccionBuscador");

    botonesSeleccionar.forEach(btn => {
        btn.addEventListener("click", () => {
            const idTipo = btn.getAttribute("data-id-tipo");
            if (selectTipo) {
                selectTipo.value = idTipo;
                selectTipo.classList.add("border-primary", "shadow-sm");
                setTimeout(() => selectTipo.classList.remove("border-primary", "shadow-sm"), 1200);
            }
            if (seccionBuscador) {
                seccionBuscador.scrollIntoView({ behavior: "smooth", block: "start" });
            }
        });
    });

    // 2. Selector amigable de horas enteras (máximo 4 horas continuas)
    const selectInicio = document.getElementById("selectHoraInicio");
    const selectFin = document.getElementById("selectHoraFin");
    const resumenDuracion = document.getElementById("resumenDuracion");

    const actualizarFranjaHoraria = () => {
        if (!selectInicio || !selectFin) return;
        const hIni = parseInt(selectInicio.value.split(":")[0], 10);
        let hFin = parseInt(selectFin.value.split(":")[0], 10);

        Array.from(selectFin.options).forEach(opt => {
            const hOpt = parseInt(opt.value.split(":")[0], 10);
            const valida = hOpt > hIni && (hOpt - hIni) <= 4;
            opt.disabled = !valida;
        });

        if (hFin <= hIni || (hFin - hIni) > 4) {
            const nuevaFin = Math.min(hIni + 1, 21);
            selectFin.value = (nuevaFin < 10 ? "0" : "") + nuevaFin + ":00";
            hFin = nuevaFin;
        }

        if (resumenDuracion) {
            const duracion = hFin - hIni;
            resumenDuracion.textContent = `Duración seleccionada: ${duracion} hora(s) continua(s) (Máximo permitido: 4h)`;
        }
    };

    if (selectInicio && selectFin) {
        selectInicio.addEventListener("change", actualizarFranjaHoraria);
        selectFin.addEventListener("change", actualizarFranjaHoraria);
        actualizarFranjaHoraria();
    }

    // 3. Filtro instantáneo en Auditoría
    const filtroAuditoria = document.getElementById("filtroAuditoriaInput");
    if (filtroAuditoria) {
        filtroAuditoria.addEventListener("keyup", () => {
            const texto = filtroAuditoria.value.toLowerCase();
            document.querySelectorAll("#tablaAuditoria tbody tr").forEach(fila => {
                fila.style.display = fila.innerText.toLowerCase().includes(texto) ? "" : "none";
            });
        });
    }

    // 4. Reloj en vivo para el panel del Auxiliar
    const relojDisplay = document.getElementById("relojSistemaLive");
    if (relojDisplay) {
        const actualizarReloj = () => {
            const ahora = new Date();
            relojDisplay.textContent = ahora.toLocaleTimeString('es-CO', { hour12: false });
        };
        actualizarReloj();
        setInterval(actualizarReloj, 1000);
    }
});
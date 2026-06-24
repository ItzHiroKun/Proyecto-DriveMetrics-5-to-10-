package cl.duoc.reportesMS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.reportesMS.model.Reporte;
import cl.duoc.reportesMS.service.ReporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Generación y consulta de reportes: citas, facturación, clientes e historial")
public class ReporteController {

    @Autowired
    private ReporteService service;

    // GET: Citas de hoy
    @GetMapping("/citas-hoy")
    @Operation(summary = "Reporte de citas del día",
                description = "Obtiene un resumen de las citas agendadas para el día actual, incluyendo cantidad, estados y detalles relevantes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Object>> citasHoy() {
        return ResponseEntity.ok(service.reporteCitasHoy());
    }

    // GET: Facturación mensual
    @GetMapping("/facturacion-mensual")
    @Operation(summary = "Reporte de facturación mensual",
                description = "Genera un reporte con el resumen de facturación para un mes y año específicos. Incluye total facturado, cantidad de facturas y desglose por tipo de servicio.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte de facturación generado",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Object>> facturacionMensual(
            @Parameter(description = "Año del reporte (ej. 2025)", required = true, example = "2025")
            @RequestParam int año,
            @Parameter(description = "Mes del reporte (1=Enero, 12=Diciembre)", required = true, example = "6")
            @RequestParam int mes) {
        return ResponseEntity.ok(service.reporteFacturacionMensual(año, mes));
    }

    // GET: Top clientes
    @GetMapping("/top-clientes")
    @Operation(summary = "Reporte de clientes frecuentes",
                description = "Lista los clientes con mayor actividad (citas o facturación) en el período actual, ordenados de mayor a menor.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de top clientes",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<Map<String, Object>>> topClientes() {
        return ResponseEntity.ok(service.reporteTopClientes());
    }

    // GET: Historial de reportes
    @GetMapping("/historial")
    @Operation(summary = "Historial de reportes generados",
                description = "Consulta el historial de reportes guardados. Se puede filtrar por tipo (VENTAS_MENSUALES, CITAS_POR_ESTADO, etc.). Si no se especifica tipo, devuelve todos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reporte.class))),
        @ApiResponse(responseCode = "204", description = "No hay reportes que coincidan con el filtro", content = @Content)
    })
    public ResponseEntity<List<Reporte>> historial(
            @Parameter(description = "Tipo de reporte para filtrar (opcional). Ej: VENTAS_MENSUALES, CITAS_POR_ESTADO, PAGOS_POR_METODO")
            @RequestParam(required = false) String tipo) {
        List<Reporte> historial = service.listarHistorial(tipo);
        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
    }
}
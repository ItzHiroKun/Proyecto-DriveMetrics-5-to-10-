package cl.duoc.reportesMS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.reportesMS.model.Reporte;
import cl.duoc.reportesMS.service.ReporteService;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService service;

    //GET: Citas de hoy
    @GetMapping("/citas-hoy")
    public ResponseEntity<Map<String, Object>> citasHoy() {
        return ResponseEntity.ok(service.reporteCitasHoy());
    }

    //GET: Facturación mensual
    @GetMapping("/facturacion-mensual")
    public ResponseEntity<Map<String, Object>> facturacionMensual(
            @RequestParam int año,
            @RequestParam int mes) {
        return ResponseEntity.ok(service.reporteFacturacionMensual(año, mes));
    }

    //GET: Top clientes
    @GetMapping("/top-clientes")
    public ResponseEntity<List<Map<String, Object>>> topClientes() {
        return ResponseEntity.ok(service.reporteTopClientes());
    }

    //GET: Historial de reportes
    @GetMapping("/historial")
    public ResponseEntity<List<Reporte>> historial(
            @RequestParam(required = false) String tipo) {
        List<Reporte> historial = service.listarHistorial(tipo);
        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
    }
}
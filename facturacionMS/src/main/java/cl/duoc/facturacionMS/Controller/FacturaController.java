package cl.duoc.facturacionMS.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.facturacionMS.DTO.FacturaDetalleDTO;
import cl.duoc.facturacionMS.Model.Factura;
import cl.duoc.facturacionMS.Model.Pago;
import cl.duoc.facturacionMS.Service.FacturaService;

@RestController
@RequestMapping("/api/v1/facturas")

public class FacturaController {

    @Autowired
    private FacturaService service;

    //Facturas

    @GetMapping
    public ResponseEntity<List<Factura>> listarFacturas() {
        List<Factura> lista = service.listarFacturas();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> buscarFactura(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<FacturaDetalleDTO> detalleFactura(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerDetalle(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Factura> generarFactura(@RequestParam Long ordenId) {
        try {
            Factura factura = service.generarFactura(ordenId);
            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //Pagos

    @PostMapping ("/{id}/pagos")
    public ResponseEntity<Pago> registrarPago(@PathVariable Long id, @RequestBody Pago pago) {
        try {
            Pago nuevoPago = service.registrarPago(id, pago);
            return ResponseEntity.ok(nuevoPago);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/pagos")
    public ResponseEntity<List<Pago>> listarPagos(@PathVariable Long id) {
        List<Pago> pagos = service.listarPagos(id);
        if (pagos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pagos);
    }
}
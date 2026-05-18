package cl.duoc.notificacionesMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.notificacionesMS.dto.NotificacionDetalleDTO;
import cl.duoc.notificacionesMS.model.Notificacion;
import cl.duoc.notificacionesMS.service.NotificacionService;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    //POST: crear notificación (usado por otros MS)
    @PostMapping
    public ResponseEntity<Notificacion> crear(@RequestBody Notificacion notificacion) {
        return ResponseEntity.ok(service.crear(notificacion));
    }

    //GET: listar todas
    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        List<Notificacion> lista = service.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    //GET: ver una notificación simple
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //GET: detalle completo (incluye datos del destinatario)
    @GetMapping("/{id}/detalle")
    public ResponseEntity<NotificacionDetalleDTO> detalle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerDetalle(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //PUT: simular envío (o reenviar)
    @PutMapping("/{id}/enviar")
    public ResponseEntity<Notificacion> enviar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.enviar(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
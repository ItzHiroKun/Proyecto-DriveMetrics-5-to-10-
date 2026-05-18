package cl.duoc.citasMS.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.citasMS.DTO.CitaDetalleDTO;
import cl.duoc.citasMS.Model.Cita;
import cl.duoc.citasMS.Service.CitaService;


@RestController
@RequestMapping("/api/v1/citas")

public class CitaController {


    @Autowired
    private CitaService service;

    //Get: Listar todas las citas
    @GetMapping
    public ResponseEntity<List<Cita>> listar() {
        List<Cita> lista = service.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    //Get: Bucar cita por id (Simple)
    @GetMapping("/{id}")
    public ResponseEntity<Cita> buscar(@PathVariable Long id) {
        try {
            Cita cita = service.buscarPorId(id);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Get: Detalle completo
    @GetMapping("{id}/detalle")
    public ResponseEntity<CitaDetalleDTO> detalle(@PathVariable Long id) {
        try {
            CitaDetalleDTO dto = service.obtenerDetalle(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Post: Crear nueva cita
    @PostMapping
    public ResponseEntity<Cita> guardar(@RequestBody Cita cita) {
        try {
            Cita nueva = service.guardar(cita);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Put: Actualizar estado de cita
    @PutMapping("/{id}/estado")
    public ResponseEntity<Cita> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            Cita actualizada = service.actualizarEstado(id, estado);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Delete: Cancelar/Eliminar cita
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

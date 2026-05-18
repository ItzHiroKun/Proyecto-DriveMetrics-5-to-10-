package cl.duoc.proveedoresMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.proveedoresMS.dto.ProveedorDetalleDTO;
import cl.duoc.proveedoresMS.model.Proveedor;
import cl.duoc.proveedoresMS.service.ProveedorService;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService service;

    //GET listar todos
    @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {
        List<Proveedor> lista = service.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    //GET listar activos
    @GetMapping("/activos")
    public ResponseEntity<List<Proveedor>> listarActivos() {
        return ResponseEntity.ok(service.listarActivos());
    }

    //GET buscar por ID (simple)
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //GET detalle completo (con repuestos)
    @GetMapping("/{id}/detalle")
    public ResponseEntity<ProveedorDetalleDTO> detalle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerDetalle(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //POST crear
    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(service.guardar(proveedor));
    }

    //PUT actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        try {
            return ResponseEntity.ok(service.actualizar(id, proveedor));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //DELETE lógico (desactivar)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        try {
            service.desactivar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
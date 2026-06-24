package cl.duoc.proveedoresMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.proveedoresMS.dto.ProveedorDetalleDTO;
import cl.duoc.proveedoresMS.model.Proveedor;
import cl.duoc.proveedoresMS.service.ProveedorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/proveedores")
@Tag(name = "Proveedores", description = "Gestión de proveedores: listar, buscar, crear, actualizar y desactivar")
public class ProveedorController {

    @Autowired
    private ProveedorService service;

    // GET listar todos
    @GetMapping
    @Operation(summary = "Listar todos los proveedores", description = "Obtiene la lista completa de proveedores registrados, tanto activos como inactivos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de proveedores obtenido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Proveedor.class))),
        @ApiResponse(responseCode = "204", description = "No hay proveedores registrados", content = @Content)
    })
    public ResponseEntity<List<Proveedor>> listar() {
        List<Proveedor> lista = service.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // GET listar activos
    @GetMapping("/activos")
    @Operation(summary = "Listar proveedores activos", description = "Obtiene únicamente los proveedores que están activos (campo activo = true)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de proveedores activos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Proveedor.class)))
    })
    public ResponseEntity<List<Proveedor>> listarActivos() {
        return ResponseEntity.ok(service.listarActivos());
    }

    // GET buscar por ID (simple)
    @GetMapping("/{id}")
    @Operation(summary = "Buscar proveedor por ID", description = "Retorna los datos básicos de un proveedor por su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado",
                    content = @Content(schema = @Schema(implementation = Proveedor.class))),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    })
    public ResponseEntity<Proveedor> buscar(
            @Parameter(description = "ID del proveedor a consultar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET detalle completo (con repuestos)
    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtener detalle de proveedor",
                description = "Devuelve un DTO con los datos del proveedor y el listado de repuestos que suministra")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = ProveedorDetalleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    })
    public ResponseEntity<ProveedorDetalleDTO> detalle(
            @Parameter(description = "ID del proveedor para obtener el detalle", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerDetalle(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST crear
    @PostMapping
    @Operation(summary = "Registrar nuevo proveedor", description = "Crea un proveedor con los datos proporcionados. El campo 'activo' se inicializa en true por defecto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proveedor creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Proveedor.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o RUN duplicado", content = @Content)
    })
    public ResponseEntity<Proveedor> crear(
            @Parameter(description = "Objeto proveedor con todos los campos obligatorios (excepto id)", required = true)
            @RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(service.guardar(proveedor));
    }

    // PUT actualizar
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor", description = "Modifica los datos de un proveedor existente. No se puede modificar el RUN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proveedor actualizado",
                    content = @Content(schema = @Schema(implementation = Proveedor.class))),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<Proveedor> actualizar(
            @Parameter(description = "ID del proveedor a modificar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Objeto con los campos a actualizar", required = true)
            @RequestBody Proveedor proveedor) {
        try {
            return ResponseEntity.ok(service.actualizar(id, proveedor));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE lógico (desactivar)
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar proveedor", description = "Realiza una baja lógica del proveedor (cambia el campo 'activo' a false). No se elimina físicamente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Proveedor desactivado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    })
    public ResponseEntity<Void> desactivar(
            @Parameter(description = "ID del proveedor a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            service.desactivar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
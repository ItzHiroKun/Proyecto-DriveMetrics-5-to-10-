package cl.duoc.citasMS.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.citasMS.DTO.CitaDetalleDTO;
import cl.duoc.citasMS.Model.Cita;
import cl.duoc.citasMS.Service.CitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas", description = "Gestión de citas mecánicas: agendar, consultar, modificar estado y cancelar")
public class CitaController {

    @Autowired
    private CitaService service;

    // GET: Listar todas las citas
    @GetMapping
    @Operation(summary = "Listar todas las citas", description = "Obtiene un listado completo de todas las citas registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de citas obtenido exitosamente", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cita.class))),
        @ApiResponse(responseCode = "204", description = "No hay citas registradas", content = @Content)
    })
    public ResponseEntity<List<Cita>> listar() {
        List<Cita> lista = service.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar cita por ID (simple)
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cita por ID", description = "Retorna los datos básicos de una cita específica mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita encontrada",
                    content = @Content(schema = @Schema(implementation = Cita.class))),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content)
    })
    public ResponseEntity<Cita> buscar(
            @Parameter(description = "ID de la cita que se desea consultar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Cita cita = service.buscarPorId(id);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET: Detalle completo de la cita (incluye datos del cliente y vehículo)
    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtener detalle completo de cita",
                description = "Devuelve un DTO con los datos de la cita junto con información resumida del cliente y vehículo asociados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle de la cita obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = CitaDetalleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content)
    })
    public ResponseEntity<CitaDetalleDTO> detalle(
            @Parameter(description = "ID de la cita para obtener el detalle", required = true, example = "1")
            @PathVariable Long id) {
        try {
            CitaDetalleDTO dto = service.obtenerDetalle(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST: Crear nueva cita
    @PostMapping
    @Operation(summary = "Agendar nueva cita", description = "Crea una cita con los datos proporcionados. Los IDs de cliente y vehículo deben existir en sus respectivos microservicios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita creada exitosamente",
                    content = @Content(schema = @Schema(implementation = Cita.class))),
        @ApiResponse(responseCode = "400", description = "Datos de cita inválidos o faltan referencias", content = @Content)
    })
    public ResponseEntity<Cita> guardar(
            @Parameter(description = "Objeto cita con todos los campos necesarios (excepto id)", required = true)
            @RequestBody Cita cita) {
        try {
            Cita nueva = service.guardar(cita);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT: Actualizar estado de la cita
    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de una cita", description = "Modifica el estado de una cita (ej. 'Agendada', 'Completada', 'Cancelada') sin necesidad de enviar todos los datos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Cita.class))),
        @ApiResponse(responseCode = "400", description = "Estado no válido o solicitud incorrecta", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content)
    })
    public ResponseEntity<Cita> actualizarEstado(
            @Parameter(description = "ID de la cita a modificar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado de la cita (Agendada, Completada, Cancelada)", required = true, example = "Completada")
            @RequestParam String estado) {
        try {
            Cita actualizada = service.actualizarEstado(id, estado);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE: Cancelar / eliminar cita
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar (eliminar) cita", description = "Elimina lógicamente una cita. Solo se pueden cancelar citas que no estén completadas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cita cancelada exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la cita a cancelar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
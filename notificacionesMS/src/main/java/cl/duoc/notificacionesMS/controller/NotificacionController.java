package cl.duoc.notificacionesMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.notificacionesMS.dto.NotificacionDetalleDTO;
import cl.duoc.notificacionesMS.model.Notificacion;
import cl.duoc.notificacionesMS.service.NotificacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/notificaciones")
@Tag(name = "Notificaciones", description = "Gestión de notificaciones: creación, consulta, detalle y envío")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    // POST: crear notificación (usado por otros MS)
    @PostMapping
    @Operation(summary = "Crear una nueva notificación",
                description = "Registra una notificación en estado PENDIENTE. Usado internamente por otros microservicios (citas, facturación, etc.) para notificar eventos a clientes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación creada exitosamente",
                    content = @Content(schema = @Schema(implementation = Notificacion.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o faltan campos obligatorios", content = @Content)
    })
    public ResponseEntity<Notificacion> crear(
            @Parameter(description = "Objeto notificación con los datos del mensaje, tipo, destinatario y referencia", required = true)
            @RequestBody Notificacion notificacion) {
        return ResponseEntity.ok(service.crear(notificacion));
    }

    // GET: listar todas
    @GetMapping
    @Operation(summary = "Listar todas las notificaciones", description = "Obtiene la lista completa de notificaciones registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de notificaciones obtenido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notificacion.class))),
        @ApiResponse(responseCode = "204", description = "No hay notificaciones registradas", content = @Content)
    })
    public ResponseEntity<List<Notificacion>> listar() {
        List<Notificacion> lista = service.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // GET: ver una notificación simple
    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificación por ID", description = "Retorna los datos básicos de una notificación específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación encontrada",
                    content = @Content(schema = @Schema(implementation = Notificacion.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    public ResponseEntity<Notificacion> buscar(
            @Parameter(description = "ID de la notificación a consultar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET: detalle completo (incluye datos del destinatario)
    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtener detalle completo de la notificación",
                description = "Devuelve un DTO con los datos de la notificación e información resumida del destinatario (cliente o mecánico)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = NotificacionDetalleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    public ResponseEntity<NotificacionDetalleDTO> detalle(
            @Parameter(description = "ID de la notificación para obtener el detalle", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerDetalle(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT: simular envío (o reenviar)
    @PutMapping("/{id}/enviar")
    @Operation(summary = "Enviar (o reenviar) una notificación",
                description = "Cambia el estado de la notificación a ENVIADA y registra la fecha de envío. Si ya fue enviada, la reenvía.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación enviada/reenviada correctamente",
                    content = @Content(schema = @Schema(implementation = Notificacion.class))),
        @ApiResponse(responseCode = "400", description = "Error al enviar (estado no válido o notificación ya fallida)", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    public ResponseEntity<Notificacion> enviar(
            @Parameter(description = "ID de la notificación a enviar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.enviar(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
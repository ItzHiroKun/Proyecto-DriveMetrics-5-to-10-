package cl.duoc.notificacionesMS.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDetalleDTO {

    private Long id;
    private String mensaje;
    private String tipo;
    private String destinatarioTipo;
    private Long destinatarioId;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
    private String referencia;

    // Datos del destinatario obtenidos de otros MS
    private String nombreDestinatario;
    private String contactoDestinatario;
}
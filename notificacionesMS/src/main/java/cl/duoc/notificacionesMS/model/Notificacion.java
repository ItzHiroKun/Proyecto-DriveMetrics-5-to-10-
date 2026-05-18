package cl.duoc.notificacionesMS.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(length = 50)
    private String tipo;                // CITA, ORDEN, PROMOCION, etc.

    @Column(name = "destinatario_tipo", nullable = false, length = 20)
    private String destinatarioTipo;    // CLIENTE o MECANICO

    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId;

    @Column(length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, ENVIADA, FALLIDA

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(length = 100)
    private String referencia;          // ID de la cita/orden relacionada
}
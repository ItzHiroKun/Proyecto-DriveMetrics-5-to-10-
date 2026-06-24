package cl.duoc.citasMS.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
@Schema(description = "Representa una cita en el sistema de gestión de citas.")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la cita.")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Fecha de la cita.")
    private LocalDate fecha;

    @Column(nullable = false)
    @Schema(description = "Hora de la cita.")
    private LocalDateTime hora;

    @Column
    @Schema(description = "Lugar de la cita.")
    private String lugar;

    @Column(length = 20)
    @Schema(description = "Estado de la cita. Puede ser 'Agendada', 'Completada' o 'Cancelada'.")
    private String estado; //Agendada, Completada, Cancelada

    //Solo se guarda el id del vehículo
    //El vehículo vive en otro MS

    @Column(name = "vehiculo_id", nullable = false)
    @Schema(description = "Identificador único del vehículo asociado a la cita.")
    private Long vehiculoId;

    // Se añade la columna cliente_id para asosicar la cita con el cliente
    @Column(name = "cliente_id", nullable = false)
    @Schema(description = "Identificador único del cliente asociado a la cita.")
    private Long clientId;
    
}

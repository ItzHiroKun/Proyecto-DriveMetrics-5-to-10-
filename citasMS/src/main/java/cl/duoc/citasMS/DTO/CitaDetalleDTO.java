package cl.duoc.citasMS.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO con el detalle completo de una cita, incluyendo los datos resumidos del vehículo y del cliente")
public class CitaDetalleDTO {

    @Schema(description = "ID de la cita", example = "1")
    private Long id;

    @Schema(description = "Fecha de la cita", example = "2025-06-20")
    private LocalDate fecha;

    @Schema(description = "Hora de la cita", example = "2025-06-20T10:00:00")
    private LocalDateTime hora;

    @Schema(description = "Lugar donde se realizará la cita", example = "Taller Central")
    private String lugar;

    @Schema(description = "Estado actual de la cita (Agendada, Completada, Cancelada)", example = "Agendada")
    private String estado;

    // Datos externos (otros MS)

    @Schema(description = "Datos resumidos del vehículo asociado a la cita (proviene del microservicio de vehículos)")
    private VehiculoDTO vehiculo;

    @Schema(description = "Datos resumidos del cliente que agendó la cita (proviene del microservicio de clientes)")
    private ClienteDTO cliente;
}
package cl.duoc.citasMS.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos resumidos de un vehículo, utilizados en el detalle de cita")
public class VehiculoDTO {

    @Schema(description = "ID del vehículo en el microservicio de vehículos", example = "1")
    private Long id;

    @Schema(description = "Patente del vehículo", example = "HF3456")
    private String patente;

    @Schema(description = "Marca del vehículo", example = "Toyota")
    private String marca;

    @Schema(description = "Modelo del vehículo", example = "Corolla")
    private String modelo;

    @Schema(description = "Número de chasis del vehículo", example = "JTDBR32E302123456")
    private String chasis;

    @Schema(description = "ID del cliente dueño del vehículo (referencia al microservicio de clientes)", example = "1")
    private Long clienteId;
}
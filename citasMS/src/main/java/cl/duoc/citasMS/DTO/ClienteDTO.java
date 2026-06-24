package cl.duoc.citasMS.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos resumidos de un cliente, utilizados en el detalle de cita")
public class ClienteDTO {

    @Schema(description = "ID del cliente en el microservicio de clientes", example = "1")
    private Long id;

    @Schema(description = "RUN del cliente (sin dígito verificador)", example = "12345678")
    private Long run;

    @Schema(description = "Dígito verificador del RUN", example = "9")
    private String dv;

    @Schema(description = "Nombre completo del cliente (nombre + apellido)", example = "Juan Pérez")
    private String nombreCompleto;

    @Schema(description = "Teléfono o email de contacto", example = "987654321")
    private String contacto;

    @Schema(description = "Dirección del cliente", example = "Av. Matta 123")
    private String direccion;
}
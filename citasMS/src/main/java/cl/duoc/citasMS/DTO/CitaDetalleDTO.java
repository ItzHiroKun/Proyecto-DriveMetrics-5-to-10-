package cl.duoc.citasMS.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CitaDetalleDTO {

    private Long id;
    private LocalDate fecha;
    private LocalDateTime hora;
    private String lugar;
    private String estado;

    //Datos externos (Otro MS)

    private VehiculoDTO vehiculo;
    private ClienteDTO cliente;
}

package cl.duoc.reportesMS.dto;

import java.time.LocalDate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {
    private Long id;
    private LocalDate fecha;
    private String estado;
    private Long vehiculoId;
}
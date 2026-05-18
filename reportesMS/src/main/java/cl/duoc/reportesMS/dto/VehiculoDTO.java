package cl.duoc.reportesMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {
    private Long id;
    private String patente;
    private Long clienteId;
}
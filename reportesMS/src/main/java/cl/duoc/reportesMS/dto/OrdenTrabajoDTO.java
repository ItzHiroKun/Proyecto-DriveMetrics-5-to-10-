package cl.duoc.reportesMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoDTO {
    private Long id;
    private String estado;
    private Long mecanicoId;
    private Long vehiculoId;
}
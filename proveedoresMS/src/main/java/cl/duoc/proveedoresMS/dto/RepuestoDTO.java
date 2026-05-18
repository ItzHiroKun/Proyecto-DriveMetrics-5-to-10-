package cl.duoc.proveedoresMS.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepuestoDTO {

    private Long id;
    private String nombre;
    private Integer stockActual;
    private Integer stockMinimo;
    private BigDecimal precioVentaActual;
    private Long proveedorId;       // para filtrar
}
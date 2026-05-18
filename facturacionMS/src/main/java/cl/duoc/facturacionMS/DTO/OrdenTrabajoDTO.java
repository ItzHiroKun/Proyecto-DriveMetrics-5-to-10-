package cl.duoc.facturacionMS.DTO;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrdenTrabajoDTO {

    private Long id; 
    private String numeroFolio;
    private String estado;
    private BigDecimal subtotalManoObra;
    private BigDecimal subtotalRepuestos;
    private BigDecimal iva;
    private BigDecimal totalFinal;
    private Long vehiculoId;
    private Long clienteId;
}

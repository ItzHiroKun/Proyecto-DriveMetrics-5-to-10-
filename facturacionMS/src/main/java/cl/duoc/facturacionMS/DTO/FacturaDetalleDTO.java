package cl.duoc.facturacionMS.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDetalleDTO {

    private Long id;
    private Long numeroBoleta;
    private LocalDateTime fechaEmision;
    private BigDecimal subtotalManoObra;
    private BigDecimal subtotalRepuestos;
    private BigDecimal iva;
    private BigDecimal totalFinal;

    // Datos externos desde órdenes MS
    private OrdenTrabajoDTO ordenTrabajo;

    // Pagos realizados a esta factura
    private List<PagoDTO> pagos;
}
package cl.duoc.facturacionMS.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PagoDTO {

    private Long id;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String referencia;
}

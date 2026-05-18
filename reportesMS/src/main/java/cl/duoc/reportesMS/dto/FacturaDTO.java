package cl.duoc.reportesMS.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Long id;
    private LocalDateTime fechaEmision;
    private BigDecimal totalFinal;
    private Long ordenId;
}
package cl.duoc.facturacionMS.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_boleta", nullable = false, unique = true)
    private Long numeroBoleta;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "orden_id", nullable = false, unique = true)
    private Long ordenId;

    @Column(name = "subtotal_mano_obra", precision = 12, scale = 2)
    private BigDecimal subtotalManoObra;

    @Column(name = "subtotal_repuestos", precision = 12, scale = 2)
    private BigDecimal subtotalRepuestos;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(name = "total_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFinal;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
}
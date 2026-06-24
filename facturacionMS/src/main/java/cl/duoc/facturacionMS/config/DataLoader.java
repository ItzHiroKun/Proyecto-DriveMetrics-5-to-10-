package cl.duoc.facturacionMS.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.facturacionMS.Model.Factura;
import cl.duoc.facturacionMS.Model.Pago;
import cl.duoc.facturacionMS.Repository.FacturaRepository;
import cl.duoc.facturacionMS.Repository.PagoRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initFacturacion(FacturaRepository facturaRepo, PagoRepository pagoRepo) {
        return args -> {
            if (facturaRepo.count() > 0) {
                System.out.println("Las facturas ya existen, no se cargan nuevamente...");
                return;
            }

            // IDs de clientes (referencia del MS de clientes)
            Long clienteParticular1 = 1L;   // Juan Pérez
            Long clienteParticular2 = 2L;   // María Gómez
            Long clienteEmpresa    = 3L;     // Marcopolo SPA

            Factura fac1 = new Factura(null, 1001L,
                    LocalDateTime.of(2025, 6, 20, 15, 0), 101L,
                    new BigDecimal("150.000"), new BigDecimal("50.000"),
                    new BigDecimal("38.000"), new BigDecimal("230.000"),
                    clienteParticular1);

            Factura fac2 = new Factura(null, 1002L,
                    LocalDateTime.of(2025, 6, 22, 17, 0), 102L,
                    new BigDecimal("200.000"), new BigDecimal("100.000"),
                    new BigDecimal("57.000"), new BigDecimal("357.000"),
                    clienteParticular2);

            Factura fac3 = new Factura(null, 1003L,
                    LocalDateTime.of(2025, 6, 25, 10, 0), 103L,
                    new BigDecimal("300.000"), new BigDecimal("150.000"),
                    new BigDecimal("85.500"), new BigDecimal("535.500"),
                    clienteEmpresa);

            facturaRepo.save(fac1);
            facturaRepo.save(fac2);
            facturaRepo.save(fac3);

            Pago pago1 = new Pago(null, fac1, fac1.getTotalFinal(),
                    LocalDateTime.of(2025, 6, 21, 9, 0),
                    "Transferencia", "Pago factura 1001");

            Pago pago2 = new Pago(null, fac2, fac2.getTotalFinal(),
                    LocalDateTime.of(2025, 6, 23, 11, 0),
                    "Efectivo", "Pago factura 1002");

            Pago pago3 = new Pago(null, fac3, fac3.getTotalFinal(),
                    LocalDateTime.of(2025, 6, 26, 14, 0),
                    "Efectivo", "Pago factura 1003");   // Corregido: 1003 en vez de 1002

            pagoRepo.save(pago1);
            pagoRepo.save(pago2);
            pagoRepo.save(pago3);

            System.out.println("Facturas y pagos cargados con éxito...");
        };
    }
}

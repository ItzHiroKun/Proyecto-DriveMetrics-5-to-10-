package cl.duoc.reportesMS.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.reportesMS.model.Reporte;
import cl.duoc.reportesMS.repository.ReporteRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initReportes(ReporteRepository reporteRepo) {
        return args -> {
            if (reporteRepo.count() > 0) {
                System.out.println("Los reportes ya existen, no se cargan nuevamente...");
                return;
            }

            // 1. Reporte de ventas mensuales (junio 2025)
            Reporte r1 = new Reporte(
                    null,
                    "VENTAS_MENSUALES",
                    LocalDateTime.of(2025, 6, 30, 18, 0),
                    "{\"mes\":6,\"anio\":2025}",
                    "{\"total_ventas\":1122500,\"cantidad_facturas\":3,\"desglose\":["
                    + "{\"numero_boleta\":1001,\"cliente\":\"Juan Pérez\",\"total\":230000},"
                    + "{\"numero_boleta\":1002,\"cliente\":\"María Gómez\",\"total\":357000},"
                    + "{\"numero_boleta\":1003,\"cliente\":\"Marcopolo SPA\",\"total\":535500}"
                    + "]}"
            );

            // 2. Reporte de reparaciones por tipo de vehículo
            Reporte r2 = new Reporte(
                    null,
                    "REPARACIONES_POR_TIPO",
                    LocalDateTime.of(2025, 6, 30, 18, 5),
                    "{\"fecha_inicio\":\"2025-06-01\",\"fecha_fin\":\"2025-06-30\"}",
                    "{\"total_reparaciones\":4,\"detalle\":["
                    + "{\"tipo\":\"Auto\",\"cantidad\":1,\"modelo\":\"Corolla\"},"
                    + "{\"tipo\":\"Moto\",\"cantidad\":1,\"modelo\":\"R15\"},"
                    + "{\"tipo\":\"Camioneta\",\"cantidad\":1,\"modelo\":\"Montana\"},"
                    + "{\"tipo\":\"Camion\",\"cantidad\":1,\"modelo\":\"Porter\"}"
                    + "]}"
            );

            // 3. Reporte de clientes atendidos
            Reporte r3 = new Reporte(
                    null,
                    "CLIENTES_ATENDIDOS",
                    LocalDateTime.of(2025, 6, 30, 18, 10),
                    "{\"periodo\":\"Junio 2025\"}",
                    "{\"total_clientes\":3,\"clientes\":["
                    + "{\"nombre\":\"Juan Pérez\",\"citas\":2,\"vehiculos\":[\"Corolla\",\"Montana\"]},"
                    + "{\"nombre\":\"María Gómez\",\"citas\":1,\"vehiculos\":[\"R15\"]},"
                    + "{\"nombre\":\"Marcopolo SPA\",\"citas\":1,\"vehiculos\":[\"Porter\"]}"
                    + "]}"
            );

            // 4. Reporte de citas por estado
            Reporte r4 = new Reporte(
                    null,
                    "CITAS_POR_ESTADO",
                    LocalDateTime.of(2025, 6, 30, 18, 15),
                    "{\"estado\":\"TODOS\"}",
                    "{\"total_citas\":4,\"estados\":["
                    + "{\"estado\":\"Agendada\",\"cantidad\":2},"
                    + "{\"estado\":\"Completada\",\"cantidad\":1},"
                    + "{\"estado\":\"Cancelada\",\"cantidad\":1}"
                    + "]}"
            );

            // 5. Reporte de pagos por método
            Reporte r5 = new Reporte(
                    null,
                    "PAGOS_POR_METODO",
                    LocalDateTime.of(2025, 6, 30, 18, 20),
                    "{\"fecha_desde\":\"2025-06-01\",\"fecha_hasta\":\"2025-06-30\"}",
                    "{\"total_pagos\":1122500,\"metodos\":["
                    + "{\"metodo\":\"Transferencia\",\"monto\":230000,\"cantidad\":1},"
                    + "{\"metodo\":\"Efectivo\",\"monto\":892500,\"cantidad\":2}"
                    + "]}"
            );

            reporteRepo.save(r1);
            reporteRepo.save(r2);
            reporteRepo.save(r3);
            reporteRepo.save(r4);
            reporteRepo.save(r5);

            System.out.println("Reportes cargados con éxito...");
        };
    }
}
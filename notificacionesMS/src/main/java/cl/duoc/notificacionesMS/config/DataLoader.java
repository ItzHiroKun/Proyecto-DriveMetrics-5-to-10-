package cl.duoc.notificacionesMS.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.notificacionesMS.model.Notificacion;
import cl.duoc.notificacionesMS.repository.NotificacionRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initNotificaciones(NotificacionRepository notiRepo) {
        return args -> {
            if (notiRepo.count() > 0) {
                System.out.println("Las notificaciones ya existen, no se cargan nuevamente...");
                return;
            }

            // IDs de clientes (referencia del microservicio clientesMS)
            Long cliente1    = 1L;   // Juan Pérez
            Long cliente2   = 2L;   // María Gómez
            Long cliente3 = 3L;   // Marcopolo SPA

            // ---- Notificaciones para Juan Pérez (cliente 1) ----
            // Recordatorio de cita (CITA-101)
            Notificacion noti1 = new Notificacion(
                    null,
                    "Estimado Juan, le recordamos su cita de mantención programada para el 20/06/2025 a las 10:00 hrs en Taller Central.",
                    "CITA",
                    "CLIENTE",
                    cliente1,
                    "PENDIENTE",
                    LocalDateTime.of(2025, 6, 19, 9, 0),
                    null,
                    "CITA-101"
            );

            // Confirmación de pago (FACT-1001)
            Notificacion noti2 = new Notificacion(
                    null,
                    "Estimado Juan, su pago por $230.000 correspondiente a la factura N° 1001 ha sido recibido exitosamente.",
                    "PAGO",
                    "CLIENTE",
                    cliente1,
                    "ENVIADA",
                    LocalDateTime.of(2025, 6, 21, 10, 0),
                    LocalDateTime.of(2025, 6, 21, 10, 1),
                    "FACT-1001"
            );

            // Promoción especial
            Notificacion noti3 = new Notificacion(
                    null,
                    "¡Hola Juan! Por ser cliente frecuente, obtén un 15% de descuento en tu próxima revisión general. Válido hasta el 31/07/2025.",
                    "PROMOCION",
                    "CLIENTE",
                    cliente1,
                    "PENDIENTE",
                    LocalDateTime.of(2025, 6, 23, 12, 0),
                    null,
                    "PROMO-002"
            );

            // ---- Notificaciones para María Gómez (cliente 2) ----
            // Recordatorio de cita (CITA-102)
            Notificacion noti4 = new Notificacion(
                    null,
                    "Estimada María, le recordamos su cita del 22/06/2025 a las 14:30 hrs en Sucursal Norte.",
                    "CITA",
                    "CLIENTE",
                    cliente2,
                    "PENDIENTE",
                    LocalDateTime.of(2025, 6, 21, 9, 0),
                    null,
                    "CITA-102"
            );

            // Confirmación de pago (FACT-1002)
            Notificacion noti5 = new Notificacion(
                    null,
                    "Estimada María, su pago por $357.000 correspondiente a la factura N° 1002 ha sido recibido exitosamente.",
                    "PAGO",
                    "CLIENTE",
                    cliente2,
                    "ENVIADA",
                    LocalDateTime.of(2025, 6, 23, 15, 0),
                    LocalDateTime.of(2025, 6, 23, 15, 1),
                    "FACT-1002"
            );

            // Promoción especial
            Notificacion noti6 = new Notificacion(
                    null,
                    "¡Hola María! Aprovecha un 20% de descuento en cambio de aceite hasta el 30/06/2025.",
                    "PROMOCION",
                    "CLIENTE",
                    cliente2,
                    "ENVIADA",
                    LocalDateTime.of(2025, 6, 18, 12, 0),
                    LocalDateTime.of(2025, 6, 18, 12, 1),
                    "PROMO-001"
            );

            // ---- Notificaciones para Marcopolo SPA (cliente 3) ----
            // Confirmación de orden completada (ORDEN-103)
            Notificacion noti7 = new Notificacion(
                    null,
                    "Su orden de trabajo N° 103 ha sido completada. Puede retirar su vehículo en Sucursal Sur.",
                    "ORDEN",
                    "CLIENTE",
                    cliente3,
                    "PENDIENTE",
                    LocalDateTime.of(2025, 6, 26, 8, 0),
                    null,
                    "ORDEN-103"
            );

            // Confirmación de pago (FACT-1003)
            Notificacion noti8 = new Notificacion(
                    null,
                    "El pago por $535.500 de la factura N° 1003 ha sido recibido exitosamente. Gracias por su preferencia.",
                    "PAGO",
                    "CLIENTE",
                    cliente3,
                    "ENVIADA",
                    LocalDateTime.of(2025, 6, 26, 17, 0),
                    LocalDateTime.of(2025, 6, 26, 17, 1),
                    "FACT-1003"
            );

            // Guardar todas las notificaciones
            notiRepo.save(noti1);
            notiRepo.save(noti2);
            notiRepo.save(noti3);
            notiRepo.save(noti4);
            notiRepo.save(noti5);
            notiRepo.save(noti6);
            notiRepo.save(noti7);
            notiRepo.save(noti8);

            System.out.println("Notificaciones cargadas con éxito...");
        };
    }
}
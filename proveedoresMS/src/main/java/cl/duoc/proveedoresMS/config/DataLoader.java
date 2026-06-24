package cl.duoc.proveedoresMS.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.proveedoresMS.model.Proveedor;
import cl.duoc.proveedoresMS.repository.ProveedorRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initProveedores(ProveedorRepository proveedorRepo) {
        return args -> {
            if (proveedorRepo.count() > 0) {
                System.out.println("Los proveedores ya existen, no se cargan nuevamente...");
                return;
            }

            Proveedor prov1 = new Proveedor(
                    null,
                    76543210L,          // run
                    "K",                // dv
                    "Importadora Motor Chile Ltda.",
                    "Carlos Fuentes",
                    "+56223456789",
                    "carlos.fuentes@importmotor.cl",
                    "Av. Las Industrias 456, Santiago",
                    true
            );

            Proveedor prov2 = new Proveedor(
                    null,
                    12345678L,
                    "5",
                    "Repuestos Automotrices del Sur S.A.",
                    "Ana Martínez",
                    "+56412345678",
                    "ana.martinez@repuestosur.cl",
                    "Calle Comercio 1234, Concepción",
                    true
            );

            Proveedor prov3 = new Proveedor(
                    null,
                    87654321L,
                    "3",
                    "Lubricantes y Filtros Nacionales E.I.R.L.",
                    "Pedro Rojas",
                    "+56987654321",
                    "pedro.rojas@lubrifiltros.cl",
                    "Pasaje Los Talleres 78, Valparaíso",
                    true
            );

            Proveedor prov4 = new Proveedor(
                    null,
                    23456789L,
                    "0",
                    "Neumáticos Globales SpA",
                    "Laura Soto",
                    "+56229876543",
                    "laura.soto@neumaticosglobal.cl",
                    "Av. Circunvalación 890, Renca",
                    false   // proveedor inactivo
            );

            proveedorRepo.save(prov1);
            proveedorRepo.save(prov2);
            proveedorRepo.save(prov3);
            proveedorRepo.save(prov4);

            System.out.println("Proveedores cargados con éxito...");
        };
    }
}
package cl.duoc.facturacionMS.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.facturacionMS.DTO.OrdenTrabajoDTO;

@FeignClient(name = "ordenesTrabajoMS", url = "http://localhost:8087")
public interface OrdenesTrabajoClient {

    @GetMapping("/api/v1/ordenes/dto/{id}")
    OrdenTrabajoDTO obtenerOrden(@PathVariable("id") Long id);
}
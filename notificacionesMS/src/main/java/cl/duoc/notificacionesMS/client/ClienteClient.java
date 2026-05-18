package cl.duoc.notificacionesMS.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.notificacionesMS.dto.ClienteDTO;

@FeignClient(name = "clientesMS", url = "http://localhost:8085")
public interface ClienteClient {

    @GetMapping("/api/v1/clientes/dto/{id}")
    ClienteDTO obtenerCliente(@PathVariable("id") Long id);
}
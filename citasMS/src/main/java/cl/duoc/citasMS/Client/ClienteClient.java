package cl.duoc.citasMS.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.citasMS.DTO.ClienteDTO;

@FeignClient(name = "ClientesMS", url = "http://localhost:8085")

public interface ClienteClient {
    
    @GetMapping("/api/v1/clientes/DTO/{id}")
    ClienteDTO obtenerCliente(@PathVariable("id") Long id);
}

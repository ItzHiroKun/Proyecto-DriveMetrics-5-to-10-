package cl.duoc.notificacionesMS.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.notificacionesMS.dto.MecanicoDTO;

@FeignClient(name = "mecanicosMS", url = "http://localhost:8090")
public interface MecanicoClient {

    @GetMapping("/api/v1/mecanicos/dto/{id}")
    MecanicoDTO obtenerMecanico(@PathVariable("id") Long id);
}
package cl.duoc.citasMS.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.citasMS.DTO.VehiculoDTO;

@FeignClient(name = "vehiculosMS", url = "http://localhost:8084")

public interface VehiculoClient {

    @GetMapping("/api/v1/vehiculos/DTO/{id}")
    VehiculoDTO obtenerVehiculo(@PathVariable("id") Long id);
}

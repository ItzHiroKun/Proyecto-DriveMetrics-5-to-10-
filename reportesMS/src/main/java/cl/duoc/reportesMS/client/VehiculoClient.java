package cl.duoc.reportesMS.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.reportesMS.dto.VehiculoDTO;

@FeignClient(name = "vehiculosMS", url = "http://localhost:8084")
public interface VehiculoClient {

    @GetMapping("/api/v1/vehiculos/dto/{id}")
    VehiculoDTO obtenerVehiculo(@PathVariable("id") Long id);
}
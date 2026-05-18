package cl.duoc.proveedoresMS.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.proveedoresMS.dto.RepuestoDTO;

@FeignClient(name = "inventarioRepuestosMS", url = "http://localhost:8091")
public interface RepuestoClient {

    @GetMapping("/api/v1/repuestos/dto/proveedor/{proveedorId}")
    List<RepuestoDTO> obtenerRepuestosPorProveedor(@PathVariable("proveedorId") Long proveedorId);
}
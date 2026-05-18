package cl.duoc.reportesMS.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.reportesMS.dto.OrdenTrabajoDTO;

@FeignClient(name = "ordenesTrabajoMS", url = "http://localhost:8087")
public interface OrdenTrabajoClient {

    @GetMapping("/api/v1/ordenes")
    List<OrdenTrabajoDTO> listarOrdenes();
}
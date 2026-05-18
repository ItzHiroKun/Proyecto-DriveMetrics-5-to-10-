package cl.duoc.reportesMS.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.reportesMS.dto.FacturaDTO;

@FeignClient(name = "facturacionMS", url = "http://localhost:8088")
public interface FacturaClient {

    @GetMapping("/api/v1/facturas")
    List<FacturaDTO> listarFacturas();
}
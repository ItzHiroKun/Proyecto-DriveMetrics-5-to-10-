package cl.duoc.reportesMS.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import cl.duoc.reportesMS.dto.CitaDTO;

@FeignClient(name = "citasMS", url = "http://localhost:8086")
public interface CitaClient {

    @GetMapping("/api/v1/citas")
    List<CitaDTO> listarCitas();
}
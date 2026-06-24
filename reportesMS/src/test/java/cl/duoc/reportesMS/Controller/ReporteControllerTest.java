package cl.duoc.reportesMS.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.duoc.reportesMS.controller.ReporteController;
import cl.duoc.reportesMS.dto.CitaDTO;
import cl.duoc.reportesMS.dto.ClienteDTO;
import cl.duoc.reportesMS.model.Reporte;
import cl.duoc.reportesMS.service.ReporteService;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReporteService reporteService;

    // ─────────────── CITAS DE HOY ───────────────
    @Test
    void citasHoy_cuandoHayCitas_retorna200() throws Exception {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("total_citas", 2);
        respuesta.put("citas", List.of(new CitaDTO()));
        when(reporteService.reporteCitasHoy()).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/reportes/citas-hoy"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total_citas").value(2));
    }

    @Test
    void citasHoy_cuandoNoHayCitas_retorna200ConCero() throws Exception {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("total_citas", 0);
        when(reporteService.reporteCitasHoy()).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/reportes/citas-hoy"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total_citas").value(0));
    }

    // ─────────────── FACTURACIÓN MENSUAL ───────────────
    @Test
    void facturacionMensual_cuandoHayFacturas_retorna200() throws Exception {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("total_facturado", new BigDecimal("500.000"));
        respuesta.put("cantidad_facturas", 3);
        when(reporteService.reporteFacturacionMensual(2025, 6)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/reportes/facturacion-mensual")
               .param("año", "2025")
               .param("mes", "6"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total_facturado").value(500000))
               .andExpect(jsonPath("$.cantidad_facturas").value(3));
    }

    @Test
    void facturacionMensual_cuandoNoHayFacturas_retorna200ConCero() throws Exception {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("total_facturado", BigDecimal.ZERO);
        respuesta.put("cantidad_facturas", 0);
        when(reporteService.reporteFacturacionMensual(2025, 6)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/reportes/facturacion-mensual")
               .param("año", "2025")
               .param("mes", "6"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.cantidad_facturas").value(0));
    }

    // ─────────────── TOP CLIENTES ───────────────
    @Test
    void topClientes_cuandoExisten_retorna200() throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        Map<String, Object> clienteMap = new HashMap<>();
        clienteMap.put("cliente", new ClienteDTO(1L, "Juan Pérez"));
        clienteMap.put("total_citas", 5);
        lista.add(clienteMap);
        when(reporteService.reporteTopClientes()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/reportes/top-clientes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].total_citas").value(5));
    }

    @Test
    void topClientes_cuandoNoExisten_retorna200ConListaVacia() throws Exception {
        when(reporteService.reporteTopClientes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reportes/top-clientes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isEmpty());
    }

    // ─────────────── HISTORIAL ───────────────
    @Test
    void historial_conFiltro_retorna200() throws Exception {
        Reporte reporte = new Reporte();
        reporte.setId(1L);
        reporte.setTipo("VENTAS_MENSUALES");
        when(reporteService.listarHistorial("VENTAS_MENSUALES")).thenReturn(List.of(reporte));

        mockMvc.perform(get("/api/v1/reportes/historial")
               .param("tipo", "VENTAS_MENSUALES"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].tipo").value("VENTAS_MENSUALES"));
    }

    @Test
    void historial_sinFiltro_retornaTodos() throws Exception {
        Reporte reporte = new Reporte();
        reporte.setId(1L);
        reporte.setTipo("GENERAL");
        when(reporteService.listarHistorial(null)).thenReturn(List.of(reporte));

        mockMvc.perform(get("/api/v1/reportes/historial"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].tipo").value("GENERAL"));
    }

    @Test
    void historial_cuandoVacio_retorna204() throws Exception {
        when(reporteService.listarHistorial("INEXISTENTE")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reportes/historial")
               .param("tipo", "INEXISTENTE"))
               .andExpect(status().isNoContent());
    }
}
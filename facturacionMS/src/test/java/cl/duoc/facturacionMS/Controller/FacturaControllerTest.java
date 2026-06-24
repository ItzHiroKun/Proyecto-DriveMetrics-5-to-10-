package cl.duoc.facturacionMS.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.facturacionMS.Controller.FacturaController;
import cl.duoc.facturacionMS.Model.Factura;
import cl.duoc.facturacionMS.Model.Pago;
import cl.duoc.facturacionMS.Service.FacturaService;
import cl.duoc.facturacionMS.DTO.FacturaDetalleDTO;
import cl.duoc.facturacionMS.DTO.OrdenTrabajoDTO;
import cl.duoc.facturacionMS.DTO.PagoDTO;

@WebMvcTest(FacturaController.class)
class FacturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacturaService facturaService;

    private ObjectMapper objectMapper;
    private Factura facturaEjemplo;
    private Pago pagoEjemplo;
    private FacturaDetalleDTO detalleEjemplo;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Se registra módulo para fechas (si tenés LocalDateTime, necesitás jackson-datatype-jsr310 en el pom)
        objectMapper.findAndRegisterModules();

        // FEjemplo de factura
        facturaEjemplo = new Factura();
        facturaEjemplo.setId(1L);
        facturaEjemplo.setNumeroBoleta(1001L);
        facturaEjemplo.setFechaEmision(LocalDateTime.of(2025, 6, 20, 15, 0));
        facturaEjemplo.setSubtotalManoObra(new BigDecimal("150.000"));
        facturaEjemplo.setSubtotalRepuestos(new BigDecimal("50.000"));
        facturaEjemplo.setIva(new BigDecimal("38.000"));
        facturaEjemplo.setTotalFinal(new BigDecimal("238.000"));
        facturaEjemplo.setOrdenId(101L);

        // Pago de ejemplo
        pagoEjemplo = new Pago();
        pagoEjemplo.setId(1L);
        pagoEjemplo.setMonto(new BigDecimal("100.000"));
        pagoEjemplo.setFechaPago(LocalDateTime.of(2025, 6, 21, 9, 0));
        pagoEjemplo.setMetodoPago("Transferencia");
        pagoEjemplo.setReferencia("Pago factura 1001");
        pagoEjemplo.setFactura(facturaEjemplo);

        // Detalle de ejemplo
        detalleEjemplo = new FacturaDetalleDTO();
        detalleEjemplo.setId(1L);
        detalleEjemplo.setNumeroBoleta(1001L);
        detalleEjemplo.setFechaEmision(facturaEjemplo.getFechaEmision());
        detalleEjemplo.setSubtotalManoObra(facturaEjemplo.getSubtotalManoObra());
        detalleEjemplo.setSubtotalRepuestos(facturaEjemplo.getSubtotalRepuestos());
        detalleEjemplo.setIva(facturaEjemplo.getIva());
        detalleEjemplo.setTotalFinal(facturaEjemplo.getTotalFinal());

        OrdenTrabajoDTO orden = new OrdenTrabajoDTO();
        orden.setId(101L);
        orden.setNumeroFolio("OT-101");
        detalleEjemplo.setOrdenTrabajo(orden);

        PagoDTO pagoDTO = new PagoDTO();
        pagoDTO.setId(1L);
        pagoDTO.setMonto(new BigDecimal("100.000"));
        pagoDTO.setMetodoPago("Transferencia");
        detalleEjemplo.setPagos(List.of(pagoDTO));
    }

    // ─────────────── LISTAR FACTURAS ───────────────
    @Test
    void listarFacturas_cuandoHayDatos_retorna200() throws Exception {
        when(facturaService.listarFacturas()).thenReturn(List.of(facturaEjemplo));

        mockMvc.perform(get("/api/v1/facturas"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].numeroBoleta").value(1001));
    }

    @Test
    void listarFacturas_cuandoVacio_retorna204() throws Exception {
        when(facturaService.listarFacturas()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/facturas"))
               .andExpect(status().isNoContent());
    }

    // ─────────────── BUSCAR FACTURA POR ID ───────────────
    @Test
    void buscarFactura_cuandoExiste_retorna200() throws Exception {
        when(facturaService.buscarPorId(1L)).thenReturn(facturaEjemplo);
        mockMvc.perform(get("/api/v1/facturas/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void buscarFactura_cuandoNoExiste_retorna404() throws Exception {
        when(facturaService.buscarPorId(99L)).thenThrow(new RuntimeException("Factura no encontrada"));
        mockMvc.perform(get("/api/v1/facturas/99"))
               .andExpect(status().isNotFound());
    }

    // ─────────────── DETALLE FACTURA ───────────────
    @Test
    void detalleFactura_cuandoExiste_retorna200() throws Exception {
        when(facturaService.obtenerDetalle(1L)).thenReturn(detalleEjemplo);
        mockMvc.perform(get("/api/v1/facturas/1/detalle"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.totalFinal").value(238000)); // BigDecimal se serializa sin punto, cuidado
    }

    @Test
    void detalleFactura_cuandoNoExiste_retorna404() throws Exception {
        when(facturaService.obtenerDetalle(99L)).thenThrow(new RuntimeException("Factura no encontrada"));
        mockMvc.perform(get("/api/v1/facturas/99/detalle"))
               .andExpect(status().isNotFound());
    }

    // ─────────────── GENERAR FACTURA ───────────────
    @Test
    void generarFactura_cuandoOrdenValida_retorna200() throws Exception {
        when(facturaService.generarFactura(101L)).thenReturn(facturaEjemplo);

        mockMvc.perform(post("/api/v1/facturas")
               .param("ordenId", "101"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.numeroBoleta").value(1001));
    }

    @Test
    void generarFactura_cuandoOrdenInvalida_retorna400() throws Exception {
        when(facturaService.generarFactura(99L)).thenThrow(new RuntimeException("Orden no facturable"));
        mockMvc.perform(post("/api/v1/facturas")
               .param("ordenId", "99"))
               .andExpect(status().isBadRequest());
    }

    // ─────────────── REGISTRAR PAGO ───────────────
    @Test
    void registrarPago_cuandoValido_retorna200() throws Exception {
        when(facturaService.registrarPago(eq(1L), any(Pago.class))).thenReturn(pagoEjemplo);

        mockMvc.perform(post("/api/v1/facturas/1/pagos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(pagoEjemplo)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.monto").value(100000));
    }

    @Test
    void registrarPago_cuandoInvalido_retorna400() throws Exception {
        when(facturaService.registrarPago(eq(1L), any(Pago.class)))
                .thenThrow(new RuntimeException("El pago supera el total"));

        mockMvc.perform(post("/api/v1/facturas/1/pagos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(pagoEjemplo)))
               .andExpect(status().isBadRequest());
    }

    // ─────────────── LISTAR PAGOS (endpoint corregido a GET) ───────────────
    @Test
    void listarPagos_cuandoExisten_retorna200() throws Exception {
        when(facturaService.listarPagos(1L)).thenReturn(List.of(pagoEjemplo));

        mockMvc.perform(get("/api/v1/facturas/1/pagos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void listarPagos_cuandoVacio_retorna204() throws Exception {
        when(facturaService.listarPagos(1L)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/facturas/1/pagos"))
               .andExpect(status().isNoContent());
    }
}
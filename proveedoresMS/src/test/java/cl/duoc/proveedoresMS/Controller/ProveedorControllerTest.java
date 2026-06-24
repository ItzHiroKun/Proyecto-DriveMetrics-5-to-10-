package cl.duoc.proveedoresMS.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

import cl.duoc.proveedoresMS.controller.ProveedorController;
import cl.duoc.proveedoresMS.dto.ProveedorDetalleDTO;
import cl.duoc.proveedoresMS.dto.RepuestoDTO;
import cl.duoc.proveedoresMS.model.Proveedor;
import cl.duoc.proveedoresMS.service.ProveedorService;

@WebMvcTest(ProveedorController.class)
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProveedorService proveedorService;

    private ObjectMapper objectMapper;
    private Proveedor proveedorEjemplo;
    private ProveedorDetalleDTO detalleEjemplo;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // No se necesita módulo de tiempo porque Proveedor no tiene fechas

        // Proveedor de ejemplo
        proveedorEjemplo = new Proveedor();
        proveedorEjemplo.setId(1L);
        proveedorEjemplo.setRun(76543210L);
        proveedorEjemplo.setDv("K");
        proveedorEjemplo.setRazonSocial("Importadora Motor Chile Ltda.");
        proveedorEjemplo.setContacto("Carlos Fuentes");
        proveedorEjemplo.setTelefono("+56223456789");
        proveedorEjemplo.setEmail("carlos.fuentes@importmotor.cl");
        proveedorEjemplo.setDireccion("Av. Las Industrias 456, Santiago");
        proveedorEjemplo.setActivo(true);

        // Detalle de ejemplo (con repuestos vacíos)
        detalleEjemplo = new ProveedorDetalleDTO();
        detalleEjemplo.setId(1L);
        detalleEjemplo.setRun(76543210L);
        detalleEjemplo.setDv("K");
        detalleEjemplo.setRazonSocial("Importadora Motor Chile Ltda.");
        detalleEjemplo.setContacto("Carlos Fuentes");
        detalleEjemplo.setTelefono("+56223456789");
        detalleEjemplo.setEmail("carlos.fuentes@importmotor.cl");
        detalleEjemplo.setDireccion("Av. Las Industrias 456, Santiago");
        detalleEjemplo.setActivo(true);
        detalleEjemplo.setRepuestosSuministrados(new ArrayList<>());
    }

    // ─────────────── LISTAR TODOS ───────────────
    @Test
    void listar_cuandoHayDatos_retorna200() throws Exception {
        when(proveedorService.listar()).thenReturn(List.of(proveedorEjemplo));

        mockMvc.perform(get("/api/v1/proveedores"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].razonSocial").value("Importadora Motor Chile Ltda."));
    }

    @Test
    void listar_cuandoVacio_retorna204() throws Exception {
        when(proveedorService.listar()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/proveedores"))
               .andExpect(status().isNoContent());
    }

    // ─────────────── LISTAR ACTIVOS ───────────────
    @Test
    void listarActivos_cuandoHayActivos_retorna200() throws Exception {
        when(proveedorService.listarActivos()).thenReturn(List.of(proveedorEjemplo));

        mockMvc.perform(get("/api/v1/proveedores/activos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L));
    }

    // ─────────────── BUSCAR POR ID ───────────────
    @Test
    void buscar_cuandoExiste_retorna200() throws Exception {
        when(proveedorService.buscarPorId(1L)).thenReturn(proveedorEjemplo);

        mockMvc.perform(get("/api/v1/proveedores/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.razonSocial").value("Importadora Motor Chile Ltda."));
    }

    @Test
    void buscar_cuandoNoExiste_retorna404() throws Exception {
        when(proveedorService.buscarPorId(99L)).thenThrow(new RuntimeException("Proveedor no encontrado"));

        mockMvc.perform(get("/api/v1/proveedores/99"))
               .andExpect(status().isNotFound());
    }

    // ─────────────── DETALLE ───────────────
    @Test
    void detalle_cuandoExiste_retorna200() throws Exception {
        when(proveedorService.obtenerDetalle(1L)).thenReturn(detalleEjemplo);

        mockMvc.perform(get("/api/v1/proveedores/1/detalle"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.razonSocial").value("Importadora Motor Chile Ltda."));
    }

    @Test
    void detalle_cuandoNoExiste_retorna404() throws Exception {
        when(proveedorService.obtenerDetalle(99L)).thenThrow(new RuntimeException("Proveedor no encontrado"));

        mockMvc.perform(get("/api/v1/proveedores/99/detalle"))
               .andExpect(status().isNotFound());
    }

    // ─────────────── CREAR ───────────────
    @Test
    void crear_cuandoDatosValidos_retorna200() throws Exception {
        when(proveedorService.guardar(any(Proveedor.class))).thenReturn(proveedorEjemplo);

        mockMvc.perform(post("/api/v1/proveedores")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(proveedorEjemplo)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void crear_cuandoDatosInvalidos_retorna400() throws Exception {
        when(proveedorService.guardar(any(Proveedor.class)))
                .thenThrow(new RuntimeException("RUN duplicado"));

        mockMvc.perform(post("/api/v1/proveedores")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{}"))
               .andExpect(status().isBadRequest());
    }

    // ─────────────── ACTUALIZAR ───────────────
    @Test
    void actualizar_cuandoExiste_retorna200() throws Exception {
        when(proveedorService.actualizar(eq(1L), any(Proveedor.class))).thenReturn(proveedorEjemplo);

        mockMvc.perform(put("/api/v1/proveedores/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(proveedorEjemplo)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void actualizar_cuandoNoExiste_retorna404() throws Exception {
        when(proveedorService.actualizar(eq(99L), any(Proveedor.class)))
                .thenThrow(new RuntimeException("Proveedor no encontrado"));

        mockMvc.perform(put("/api/v1/proveedores/99")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(proveedorEjemplo)))
               .andExpect(status().isNotFound());
    }

    // ─────────────── DESACTIVAR (DELETE lógico) ───────────────
    @Test
    void desactivar_cuandoExiste_retorna204() throws Exception {
        doNothing().when(proveedorService).desactivar(1L);

        mockMvc.perform(delete("/api/v1/proveedores/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    void desactivar_cuandoNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Proveedor no encontrado")).when(proveedorService).desactivar(99L);

        mockMvc.perform(delete("/api/v1/proveedores/99"))
               .andExpect(status().isNotFound());
    }
}
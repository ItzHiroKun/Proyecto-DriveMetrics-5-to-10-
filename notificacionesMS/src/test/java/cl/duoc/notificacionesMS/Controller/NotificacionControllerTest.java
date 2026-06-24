package cl.duoc.notificacionesMS.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cl.duoc.notificacionesMS.controller.NotificacionController;
import cl.duoc.notificacionesMS.dto.NotificacionDetalleDTO;
import cl.duoc.notificacionesMS.model.Notificacion;
import cl.duoc.notificacionesMS.service.NotificacionService;

@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacionService notificacionService;

    private ObjectMapper objectMapper;
    private Notificacion notificacionEjemplo;
    private NotificacionDetalleDTO detalleEjemplo;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Notificación simple
        notificacionEjemplo = new Notificacion();
        notificacionEjemplo.setId(1L);
        notificacionEjemplo.setMensaje("Mensaje de prueba");
        notificacionEjemplo.setTipo("CITA");
        notificacionEjemplo.setDestinatarioTipo("CLIENTE");
        notificacionEjemplo.setDestinatarioId(1L);
        notificacionEjemplo.setEstado("PENDIENTE");
        notificacionEjemplo.setFechaCreacion(LocalDateTime.of(2025, 6, 19, 9, 0));
        notificacionEjemplo.setReferencia("CITA-101");

        // Detalle
        detalleEjemplo = new NotificacionDetalleDTO();
        detalleEjemplo.setId(1L);
        detalleEjemplo.setMensaje("Mensaje detalle");
        detalleEjemplo.setTipo("CITA");
        detalleEjemplo.setEstado("PENDIENTE");
        detalleEjemplo.setDestinatarioTipo("CLIENTE");
        detalleEjemplo.setDestinatarioId(1L);
        detalleEjemplo.setNombreDestinatario("Juan Pérez");
        detalleEjemplo.setContactoDestinatario("987654321");
        detalleEjemplo.setReferencia("CITA-101");
        detalleEjemplo.setFechaCreacion(LocalDateTime.of(2025, 6, 19, 9, 0));
    }

    // ─────────────── CREAR (POST) ───────────────
    @Test
    void crear_cuandoDatosValidos_retorna200() throws Exception {
        when(notificacionService.crear(any(Notificacion.class))).thenReturn(notificacionEjemplo);

        mockMvc.perform(post("/api/v1/notificaciones")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(notificacionEjemplo)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void crear_cuandoDatosInvalidos_retorna400() throws Exception {
        when(notificacionService.crear(any(Notificacion.class)))
                .thenThrow(new RuntimeException("Datos inválidos"));

        mockMvc.perform(post("/api/v1/notificaciones")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{}"))
               .andExpect(status().isBadRequest());
    }

    // ─────────────── LISTAR (GET) ───────────────
    @Test
    void listar_cuandoHayDatos_retorna200() throws Exception {
        when(notificacionService.listar()).thenReturn(List.of(notificacionEjemplo));

        mockMvc.perform(get("/api/v1/notificaciones"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void listar_cuandoVacio_retorna204() throws Exception {
        when(notificacionService.listar()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/notificaciones"))
               .andExpect(status().isNoContent());
    }

    // ─────────────── BUSCAR POR ID ───────────────
    @Test
    void buscar_cuandoExiste_retorna200() throws Exception {
        when(notificacionService.buscarPorId(1L)).thenReturn(notificacionEjemplo);

        mockMvc.perform(get("/api/v1/notificaciones/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void buscar_cuandoNoExiste_retorna404() throws Exception {
        when(notificacionService.buscarPorId(99L)).thenThrow(new RuntimeException("Notificación no encontrada"));

        mockMvc.perform(get("/api/v1/notificaciones/99"))
               .andExpect(status().isNotFound());
    }

    // ─────────────── DETALLE ───────────────
    @Test
    void detalle_cuandoExiste_retorna200() throws Exception {
        when(notificacionService.obtenerDetalle(1L)).thenReturn(detalleEjemplo);

        mockMvc.perform(get("/api/v1/notificaciones/1/detalle"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.nombreDestinatario").value("Juan Pérez"));
    }

    @Test
    void detalle_cuandoNoExiste_retorna404() throws Exception {
        when(notificacionService.obtenerDetalle(99L)).thenThrow(new RuntimeException("Notificación no encontrada"));

        mockMvc.perform(get("/api/v1/notificaciones/99/detalle"))
               .andExpect(status().isNotFound());
    }

    // ─────────────── ENVIAR (PUT) ───────────────
    @Test
    void enviar_cuandoExiste_retorna200() throws Exception {
        notificacionEjemplo.setEstado("ENVIADA");
        notificacionEjemplo.setFechaEnvio(LocalDateTime.now());
        when(notificacionService.enviar(1L)).thenReturn(notificacionEjemplo);

        mockMvc.perform(put("/api/v1/notificaciones/1/enviar"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.estado").value("ENVIADA"));
    }

    @Test
    void enviar_cuandoNoExiste_retorna400() throws Exception {
        when(notificacionService.enviar(99L)).thenThrow(new RuntimeException("No se puede enviar"));

        mockMvc.perform(put("/api/v1/notificaciones/99/enviar"))
               .andExpect(status().isBadRequest());
    }
}
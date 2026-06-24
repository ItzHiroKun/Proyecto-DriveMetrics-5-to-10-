package cl.duoc.citasMS.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import cl.duoc.citasMS.Controller.CitaController;
import cl.duoc.citasMS.DTO.CitaDetalleDTO;
import cl.duoc.citasMS.DTO.ClienteDTO;
import cl.duoc.citasMS.DTO.VehiculoDTO;
import cl.duoc.citasMS.Model.Cita;
import cl.duoc.citasMS.Service.CitaService;

@WebMvcTest(CitaController.class) // Carga solo el controlador de citas y las dependencias web mínimas
class CitaControllerTest {

    @MockitoBean
    private CitaService citaService; // Simula el servicio, así no se llama al servicio real

    @Autowired
    private MockMvc mockMvc; // Permite simular peticiones HTTP

    private ObjectMapper objectMapper;
    private Cita citaEjemplo;
    private CitaDetalleDTO detalleEjemplo;

    @BeforeEach
    void setUp() {
        // Configuración común a todas las pruebas
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para serializar LocalDate/LocalDateTime

        // Vehículo y cliente de ejemplo (DTOs)
        VehiculoDTO vehiculo = new VehiculoDTO();
        vehiculo.setId(10L);
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Corolla");
        vehiculo.setClienteId(5L);

        ClienteDTO cliente = new ClienteDTO();
        cliente.setId(5L);
        cliente.setNombreCompleto("Juan Pérez");

        // Cita de ejemplo
        citaEjemplo = new Cita();
        citaEjemplo.setId(1L);
        citaEjemplo.setFecha(LocalDate.of(2025, 6, 20));
        citaEjemplo.setHora(LocalDateTime.of(2025, 6, 20, 10, 30));
        citaEjemplo.setLugar("Taller Central");
        citaEjemplo.setEstado("PENDIENTE");
        citaEjemplo.setVehiculoId(10L);

        // DTO de detalle de ejemplo
        detalleEjemplo = new CitaDetalleDTO();
        detalleEjemplo.setId(1L);
        detalleEjemplo.setFecha(citaEjemplo.getFecha());
        detalleEjemplo.setHora(citaEjemplo.getHora());
        detalleEjemplo.setLugar(citaEjemplo.getLugar());
        detalleEjemplo.setEstado(citaEjemplo.getEstado());
        detalleEjemplo.setVehiculo(vehiculo);
        detalleEjemplo.setCliente(cliente);
    }

    // ──────────────── LISTAR CITAS ────────────────
    @Test
    void listarCitas_cuandoHayDatos_retorna200() throws Exception {
        // ARRANGE
        List<Cita> lista = new ArrayList<>();
        lista.add(citaEjemplo);
        when(citaService.listar()).thenReturn(lista);

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/citas"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].lugar").value("Taller Central"))
               .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void listarCitas_cuandoVacio_retorna204() throws Exception {
        // ARRANGE
        when(citaService.listar()).thenReturn(new ArrayList<>());

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/citas"))
               .andExpect(status().isNoContent());
    }

    // ──────────────── BUSCAR POR ID ────────────────
    @Test
    void buscarPorId_cuandoExiste_retorna200() throws Exception {
        // ARRANGE
        when(citaService.buscarPorId(1L)).thenReturn(citaEjemplo);

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/citas/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.lugar").value("Taller Central"));
    }

    @Test
    void buscarPorId_cuandoNoExiste_retorna404() throws Exception {
        // ARRANGE
        when(citaService.buscarPorId(99L)).thenThrow(new RuntimeException("Cita no encontrada"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/citas/99"))
               .andExpect(status().isNotFound());
    }

    // ──────────────── DETALLE ────────────────
    @Test
    void detalle_cuandoExiste_retorna200() throws Exception {
        // ARRANGE
        when(citaService.obtenerDetalle(1L)).thenReturn(detalleEjemplo);

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/citas/1/detalle"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.lugar").value("Taller Central"))
               .andExpect(jsonPath("$.vehiculo.marca").value("Toyota"))
               .andExpect(jsonPath("$.cliente.nombreCompleto").value("Juan Pérez"));
    }

    @Test
    void detalle_cuandoNoExiste_retorna404() throws Exception {
        // ARRANGE
        when(citaService.obtenerDetalle(99L)).thenThrow(new RuntimeException("Cita no encontrada"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/v1/citas/99/detalle"))
               .andExpect(status().isNotFound());
    }

    // ──────────────── GUARDAR ────────────────
    @Test
    void guardar_cuandoVehiculoValido_retorna200() throws Exception {
        // ARRANGE
        when(citaService.guardar(any(Cita.class))).thenReturn(citaEjemplo);

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/citas")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(citaEjemplo)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void guardar_cuandoVehiculoNoExiste_retorna400() throws Exception {
        // ARRANGE
        when(citaService.guardar(any(Cita.class)))
                .thenThrow(new RuntimeException("El Vehiculo no existe"));

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/citas")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(citaEjemplo)))
               .andExpect(status().isBadRequest());
    }

    // ──────────────── ACTUALIZAR ESTADO ────────────────
    @Test
    void actualizarEstado_cuandoExiste_retorna200() throws Exception {
        // ARRANGE
        citaEjemplo.setEstado("COMPLETADA");
        when(citaService.actualizarEstado(eq(1L), eq("COMPLETADA"))).thenReturn(citaEjemplo);

        // ACT & ASSERT
        mockMvc.perform(put("/api/v1/citas/1/estado")
               .param("estado", "COMPLETADA"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void actualizarEstado_cuandoNoExiste_retorna400() throws Exception {
        // ARRANGE
        when(citaService.actualizarEstado(eq(99L), any()))
                .thenThrow(new RuntimeException("Cita no encontrada"));

        // ACT & ASSERT
        mockMvc.perform(put("/api/v1/citas/99/estado")
               .param("estado", "COMPLETADA"))
               .andExpect(status().isBadRequest());
    }

    // ──────────────── ELIMINAR ────────────────
    @Test
    void eliminar_cuandoExiste_retorna204() throws Exception {
        // ARRANGE
        doNothing().when(citaService).eliminar(1L);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/v1/citas/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_retorna404() throws Exception {
        // ARRANGE
        doThrow(new RuntimeException("Cita no encontrada")).when(citaService).eliminar(99L);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/v1/citas/99"))
               .andExpect(status().isNotFound());
    }
}
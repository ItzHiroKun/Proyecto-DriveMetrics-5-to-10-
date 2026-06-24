package cl.duoc.citasMS.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.citasMS.Client.ClienteClient;
import cl.duoc.citasMS.Client.VehiculoClient;
import cl.duoc.citasMS.DTO.CitaDetalleDTO;
import cl.duoc.citasMS.DTO.ClienteDTO;
import cl.duoc.citasMS.DTO.VehiculoDTO;
import cl.duoc.citasMS.Model.Cita;
import cl.duoc.citasMS.Repository.CitaRepository;
import cl.duoc.citasMS.Service.CitaService;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    // Dependencias simuladas (mocks)
    @Mock
    private CitaRepository citaRepository;

    @Mock
    private VehiculoClient vehiculoClient;

    @Mock
    private ClienteClient clienteClient;

    // Instancia del servicio donde se inyectan los mocks
    @InjectMocks
    private CitaService citaService;

    // Datos de prueba
    private Cita citaEjemplo;
    private VehiculoDTO vehiculoDTO;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        // Configuración de la cita
        citaEjemplo = new Cita();
        citaEjemplo.setId(1L);
        citaEjemplo.setFecha(LocalDate.of(2025, 6, 20));
        citaEjemplo.setHora(LocalDateTime.of(2025, 6, 20, 10, 0));
        citaEjemplo.setLugar("Taller Central");
        citaEjemplo.setEstado("PENDIENTE");
        citaEjemplo.setVehiculoId(10L);

        // Vehículo asociado
        vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setId(10L);
        vehiculoDTO.setClienteId(5L);
        vehiculoDTO.setMarca("Toyota");
        vehiculoDTO.setModelo("Corolla");

        // Cliente dueño del vehículo
        clienteDTO = new ClienteDTO();
        clienteDTO.setId(5L);
        clienteDTO.setNombreCompleto("Juan Pérez");
    }

    // ─────────────── LISTAR ───────────────
    @Test
    void listar_debeRetornarTodasLasCitas() {
        // ARRANGE
        List<Cita> listaFalsa = new ArrayList<>();
        listaFalsa.add(citaEjemplo);
        when(citaRepository.findAll()).thenReturn(listaFalsa);

        // ACT
        List<Cita> resultado = citaService.listar();

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals("Taller Central", resultado.get(0).getLugar());
        verify(citaRepository, times(1)).findAll();
    }

    @Test
    void listar_cuandoNoHayCitas_retornaListaVacia() {
        // ARRANGE
        when(citaRepository.findAll()).thenReturn(new ArrayList<>());

        // ACT
        List<Cita> resultado = citaService.listar();

        // ASSERT
        assertTrue(resultado.isEmpty());
    }

    // ─────────────── GUARDAR ───────────────
    @Test
    void guardar_cuandoVehiculoExiste_guardaCorrectamente() {
        // ARRANGE
        when(vehiculoClient.obtenerVehiculo(citaEjemplo.getVehiculoId())).thenReturn(vehiculoDTO);
        when(citaRepository.save(citaEjemplo)).thenReturn(citaEjemplo);

        // ACT
        Cita resultado = citaService.guardar(citaEjemplo);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(vehiculoClient).obtenerVehiculo(citaEjemplo.getVehiculoId());
        verify(citaRepository).save(citaEjemplo);
    }

    @Test
    void guardar_cuandoVehiculoNoExiste_lanzaExcepcion() {
        // ARRANGE
        when(vehiculoClient.obtenerVehiculo(citaEjemplo.getVehiculoId())).thenReturn(null);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.guardar(citaEjemplo));

        assertEquals("El Vehiculo no existe", ex.getMessage());
        verify(citaRepository, never()).save(any());
    }

    // ─────────────── BUSCAR POR ID ───────────────
    @Test
    void buscarPorId_cuandoExiste_retornaCita() {
        // ARRANGE
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));

        // ACT
        Cita resultado = citaService.buscarPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaExcepcion() {
        // ARRANGE
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.buscarPorId(99L));

        assertEquals("La cita no exiiste", ex.getMessage()); // respeta el mensaje exacto de tu servicio
    }

    // ─────────────── ACTUALIZAR ESTADO ───────────────
    @Test
    void actualizarEstado_cuandoExiste_actualizaYGuarda() {
        // ARRANGE
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(citaRepository.save(citaEjemplo)).thenReturn(citaEjemplo);

        // ACT
        Cita resultado = citaService.actualizarEstado(1L, "COMPLETADA");

        // ASSERT
        assertEquals("COMPLETADA", resultado.getEstado());
        verify(citaRepository).save(citaEjemplo);
    }

    @Test
    void actualizarEstado_cuandoNoExiste_lanzaExcepcion() {
        // ARRANGE
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
                () -> citaService.actualizarEstado(99L, "COMPLETADA"));

        verify(citaRepository, never()).save(any());
    }

    // ─────────────── ELIMINAR ───────────────
    @Test
    void eliminar_cuandoExiste_eliminaCorrectamente() {
        // ARRANGE
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        doNothing().when(citaRepository).delete(citaEjemplo);

        // ACT
        assertDoesNotThrow(() -> citaService.eliminar(1L));

        // ASSERT
        verify(citaRepository).delete(citaEjemplo);
    }

    @Test
    void eliminar_cuandoNoExiste_lanzaExcepcion() {
        // ARRANGE
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> citaService.eliminar(99L));
        verify(citaRepository, never()).delete(any());
    }

    // ─────────────── OBTENER DETALLE ───────────────
    @Test
    void obtenerDetalle_cuandoTodoExiste_retornaDTOCompleto() {
        // ARRANGE
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(vehiculoClient.obtenerVehiculo(citaEjemplo.getVehiculoId())).thenReturn(vehiculoDTO);
        when(clienteClient.obtenerCliente(vehiculoDTO.getClienteId())).thenReturn(clienteDTO);

        // ACT
        CitaDetalleDTO resultado = citaService.obtenerDetalle(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Taller Central", resultado.getLugar());
        assertEquals(vehiculoDTO, resultado.getVehiculo());
        assertEquals(clienteDTO, resultado.getCliente());
    }

    @Test
    void obtenerDetalle_cuandoCitaNoExiste_lanzaExcepcion() {
        // ARRANGE
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.obtenerDetalle(99L));

        assertEquals("Cita no encontrada", ex.getMessage());
        verifyNoInteractions(vehiculoClient, clienteClient);
    }

    @Test
    void obtenerDetalle_cuandoVehiculoNoExiste_lanzaExcepcion() {
        // ARRANGE
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(vehiculoClient.obtenerVehiculo(citaEjemplo.getVehiculoId())).thenReturn(null);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.obtenerDetalle(1L));

        assertEquals("Vehiculo no encontrado en el sistema", ex.getMessage());
        verifyNoInteractions(clienteClient);
    }

    @Test
    void obtenerDetalle_cuandoClienteNoExiste_lanzaExcepcion() {
        // ARRANGE
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(vehiculoClient.obtenerVehiculo(citaEjemplo.getVehiculoId())).thenReturn(vehiculoDTO);
        when(clienteClient.obtenerCliente(vehiculoDTO.getClienteId())).thenReturn(null);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> citaService.obtenerDetalle(1L));

        assertEquals("Cliente no encontrado en el sistema", ex.getMessage());
    }
}
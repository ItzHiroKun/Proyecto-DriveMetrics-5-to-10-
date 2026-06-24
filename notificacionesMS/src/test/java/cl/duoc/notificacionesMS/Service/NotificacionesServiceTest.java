package cl.duoc.notificacionesMS.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

import cl.duoc.notificacionesMS.dto.NotificacionDetalleDTO;
import cl.duoc.notificacionesMS.model.Notificacion;
import cl.duoc.notificacionesMS.repository.NotificacionRepository;
import cl.duoc.notificacionesMS.service.NotificacionService;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    // Si en el futuro el servicio necesita clientes Feign para obtener datos del destinatario,
    // agregalos como @Mock aquí y luego @InjectMocks

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacionEjemplo;
    private NotificacionDetalleDTO detalleEjemplo;

    @BeforeEach
    void setUp() {
        // Notificación de ejemplo (PENDIENTE)
        notificacionEjemplo = new Notificacion();
        notificacionEjemplo.setId(1L);
        notificacionEjemplo.setMensaje("Estimado Juan, su cita ha sido agendada.");
        notificacionEjemplo.setTipo("CITA");
        notificacionEjemplo.setDestinatarioTipo("CLIENTE");
        notificacionEjemplo.setDestinatarioId(1L);
        notificacionEjemplo.setEstado("PENDIENTE");
        notificacionEjemplo.setFechaCreacion(LocalDateTime.of(2025, 6, 19, 9, 0));
        notificacionEjemplo.setFechaEnvio(null);
        notificacionEjemplo.setReferencia("CITA-101");

        // Detalle de ejemplo (incluye datos del destinatario)
        detalleEjemplo = new NotificacionDetalleDTO();
        detalleEjemplo.setId(1L);
        detalleEjemplo.setMensaje(notificacionEjemplo.getMensaje());
        detalleEjemplo.setTipo(notificacionEjemplo.getTipo());
        detalleEjemplo.setDestinatarioTipo(notificacionEjemplo.getDestinatarioTipo());
        detalleEjemplo.setDestinatarioId(notificacionEjemplo.getDestinatarioId());
        detalleEjemplo.setEstado(notificacionEjemplo.getEstado());
        detalleEjemplo.setFechaCreacion(notificacionEjemplo.getFechaCreacion());
        detalleEjemplo.setFechaEnvio(null);
        detalleEjemplo.setReferencia(notificacionEjemplo.getReferencia());
        detalleEjemplo.setNombreDestinatario("Juan Pérez");
        detalleEjemplo.setContactoDestinatario("987654321");
    }

    // ─────────────── LISTAR ───────────────
    @Test
    void listar_cuandoHayNotificaciones_retornaLista() {
        List<Notificacion> lista = new ArrayList<>();
        lista.add(notificacionEjemplo);
        when(notificacionRepository.findAll()).thenReturn(lista);

        List<Notificacion> resultado = notificacionService.listar();

        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
        verify(notificacionRepository).findAll();
    }

    @Test
    void listar_cuandoNoHayNotificaciones_retornaListaVacia() {
        when(notificacionRepository.findAll()).thenReturn(new ArrayList<>());
        assertTrue(notificacionService.listar().isEmpty());
    }

    // ─────────────── CREAR ───────────────
    @Test
    void crear_debeGuardarNotificacionConEstadoPendiente() {
        // Simulamos que el repositorio devuelve la notificación guardada
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionEjemplo);

        Notificacion nueva = new Notificacion();
        nueva.setMensaje("Mensaje nuevo");
        // no seteamos estado, el servicio debería asignar "PENDIENTE" por defecto

        Notificacion resultado = notificacionService.crear(nueva);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado()); // o el valor que tu servicio asigne
        verify(notificacionRepository).save(nueva);
    }

    // ─────────────── BUSCAR POR ID ───────────────
    @Test
    void buscarPorId_cuandoExiste_retornaNotificacion() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));

        Notificacion resultado = notificacionService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("CITA", resultado.getTipo());
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificacionService.buscarPorId(99L));
    }

    // ─────────────── ENVIAR ───────────────
    @Test
    void enviar_cuandoExiste_actualizaEstadoYFechaEnvio() {
        // Simula que se encontra la notificación y que al guardar devuelve la misma instancia
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));
        when(notificacionRepository.save(notificacionEjemplo)).thenReturn(notificacionEjemplo);

        Notificacion resultado = notificacionService.enviar(1L);

        assertEquals("ENVIADA", resultado.getEstado());
        assertNotNull(resultado.getFechaEnvio());
        verify(notificacionRepository).save(notificacionEjemplo);
    }

    @Test
    void enviar_cuandoNoExiste_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificacionService.enviar(99L));
        verify(notificacionRepository, never()).save(any());
    }


    @Test
    void obtenerDetalle_cuandoExiste_retornaDTO() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));


        NotificacionDetalleDTO resultado = notificacionService.obtenerDetalle(1L);

        assertNotNull(resultado);
        assertEquals(notificacionEjemplo.getId(), resultado.getId());
        assertEquals(notificacionEjemplo.getMensaje(), resultado.getMensaje());
    }

    @Test
    void obtenerDetalle_cuandoNoExiste_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificacionService.obtenerDetalle(99L));
    }
}
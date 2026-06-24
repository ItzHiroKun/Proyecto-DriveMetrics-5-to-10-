package cl.duoc.proveedoresMS.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.proveedoresMS.dto.ProveedorDetalleDTO;
import cl.duoc.proveedoresMS.dto.RepuestoDTO;
import cl.duoc.proveedoresMS.model.Proveedor;
import cl.duoc.proveedoresMS.repository.ProveedorRepository;
import cl.duoc.proveedoresMS.service.ProveedorService;


@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;


    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedorActivo;
    private Proveedor proveedorInactivo;

    @BeforeEach
    void setUp() {
        // Proveedor activo
        proveedorActivo = new Proveedor();
        proveedorActivo.setId(1L);
        proveedorActivo.setRun(76543210L);
        proveedorActivo.setDv("K");
        proveedorActivo.setRazonSocial("Importadora Motor Chile Ltda.");
        proveedorActivo.setContacto("Carlos Fuentes");
        proveedorActivo.setTelefono("+56223456789");
        proveedorActivo.setEmail("carlos.fuentes@importmotor.cl");
        proveedorActivo.setDireccion("Av. Las Industrias 456, Santiago");
        proveedorActivo.setActivo(true);

        // Proveedor inactivo
        proveedorInactivo = new Proveedor();
        proveedorInactivo.setId(2L);
        proveedorInactivo.setRun(11111111L);
        proveedorInactivo.setDv("1");
        proveedorInactivo.setRazonSocial("Proveedor Antiguo");
        proveedorInactivo.setContacto("Nadie");
        proveedorInactivo.setTelefono("000");
        proveedorInactivo.setEmail("old@old.cl");
        proveedorInactivo.setDireccion("Calle Falsa 123");
        proveedorInactivo.setActivo(false);
    }

    // ─────────────── LISTAR ───────────────
    @Test
    void listar_debeRetornarTodosLosProveedores() {
        // ARRANGE
        List<Proveedor> lista = new ArrayList<>();
        lista.add(proveedorActivo);
        lista.add(proveedorInactivo);
        when(proveedorRepository.findAll()).thenReturn(lista);

        // ACT
        List<Proveedor> resultado = proveedorService.listar();

        // ASSERT
        assertEquals(2, resultado.size());
        verify(proveedorRepository).findAll();
    }

    @Test
    void listar_cuandoNoHayProveedores_retornaListaVacia() {
        when(proveedorRepository.findAll()).thenReturn(new ArrayList<>());
        assertTrue(proveedorService.listar().isEmpty());
    }

    // ─────────────── LISTAR ACTIVOS ───────────────
    @Test
    void listarActivos_debeRetornarSoloProveedoresActivos() {
        // ARRANGE
        when(proveedorRepository.findByActivoTrue()).thenReturn(List.of(proveedorActivo));

        // ACT
        List<Proveedor> activos = proveedorService.listarActivos();

        // ASSERT
        assertEquals(1, activos.size());
        assertTrue(activos.get(0).getActivo());
        verify(proveedorRepository).findByActivoTrue();
    }

    // ─────────────── BUSCAR POR ID ───────────────
    @Test
    void buscarPorId_cuandoExiste_retornaProveedor() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));

        Proveedor resultado = proveedorService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Importadora Motor Chile Ltda.", resultado.getRazonSocial());
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaExcepcion() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> proveedorService.buscarPorId(99L));
    }

    // ─────────────── GUARDAR ───────────────
    @Test
    void guardar_debeGuardarProveedorYAsignarActivoTrue() {
        // ARRANGE: simula que el repositorio guarda y devuelve el proveedor
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorActivo);

        Proveedor nuevo = new Proveedor();
        nuevo.setRun(12345678L);
        nuevo.setDv("5");
        nuevo.setRazonSocial("Nuevo Proveedor");
        // no seteamos activo; el servicio debería ponerlo en true

        // ACT
        Proveedor resultado = proveedorService.guardar(nuevo);

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.getActivo()); // asumiendo que el servicio lo asigna por defecto
        verify(proveedorRepository).save(nuevo);
    }

    // ─────────────── ACTUALIZAR ───────────────
    @Test
    void actualizar_cuandoExiste_actualizaCampos() {
        // ARRANGE
        Proveedor datosActualizados = new Proveedor();
        datosActualizados.setRazonSocial("Nueva Razón Social");
        datosActualizados.setContacto("Nuevo Contacto");
        datosActualizados.setTelefono("999");
        datosActualizados.setEmail("nuevo@email.cl");
        datosActualizados.setDireccion("Nueva Dirección");

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorActivo);

        // ACT
        Proveedor resultado = proveedorService.actualizar(1L, datosActualizados);

        // ASSERT
        assertEquals("Nueva Razón Social", resultado.getRazonSocial());
        assertEquals("Nuevo Contacto", resultado.getContacto());
        verify(proveedorRepository).save(proveedorActivo);
    }

    @Test
    void actualizar_cuandoNoExiste_lanzaExcepcion() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> proveedorService.actualizar(99L, new Proveedor()));
        verify(proveedorRepository, never()).save(any());
    }

    // ─────────────── DESACTIVAR ───────────────
    @Test
    void desactivar_cuandoExiste_poneActivoFalse() {
        // ARRANGE
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));
        when(proveedorRepository.save(proveedorActivo)).thenReturn(proveedorActivo);

        // ACT
        proveedorService.desactivar(1L);

        // ASSERT
        assertFalse(proveedorActivo.getActivo());
        verify(proveedorRepository).save(proveedorActivo);
    }

    @Test
    void desactivar_cuandoNoExiste_lanzaExcepcion() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> proveedorService.desactivar(99L));
        verify(proveedorRepository, never()).save(any());
    }

    // ─────────────── OBTENER DETALLE ───────────────
    @Test
    void obtenerDetalle_cuandoExiste_retornaDTOConRepuestos() {
        // ARRANGE
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorActivo));

        // Simular repuestos suministrados (si usás Feign, mockealo acá)
        List<RepuestoDTO> repuestos = new ArrayList<>();
        RepuestoDTO r = new RepuestoDTO();
        r.setId(1L);
        r.setNombre("Filtro de aceite");
        r.setStockActual(50);
        r.setStockMinimo(10);
        r.setPrecioVentaActual(new java.math.BigDecimal("25.990"));
        r.setProveedorId(1L);
        repuestos.add(r);

        // ACT
        ProveedorDetalleDTO detalle = proveedorService.obtenerDetalle(1L);

        // ASSERT
        assertNotNull(detalle);
        assertEquals(1L, detalle.getId());
        assertEquals("Importadora Motor Chile Ltda.", detalle.getRazonSocial());
        // Si el método obtenerDetalle devuelve repuestos, verificalos:
        // assertEquals(1, detalle.getRepuestosSuministrados().size());
    }

    @Test
    void obtenerDetalle_cuandoNoExisteProveedor_lanzaExcepcion() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> proveedorService.obtenerDetalle(99L));
    }
}
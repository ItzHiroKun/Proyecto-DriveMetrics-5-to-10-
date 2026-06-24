package cl.duoc.facturacionMS.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.facturacionMS.Client.OrdenesTrabajoClient;
import cl.duoc.facturacionMS.DTO.OrdenTrabajoDTO;
import cl.duoc.facturacionMS.Model.Factura;
import cl.duoc.facturacionMS.Model.Pago;
import cl.duoc.facturacionMS.Repository.FacturaRepository;
import cl.duoc.facturacionMS.Repository.PagoRepository;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private OrdenesTrabajoClient ordenClient;

    @InjectMocks
    private FacturaService facturaService;

    private OrdenTrabajoDTO ordenValida;
    private Factura facturaEjemplo;
    private Pago pagoEjemplo;

    @BeforeEach
    void setUp() {
        // Orden de trabajo válida (sin facturar)
        ordenValida = new OrdenTrabajoDTO();
        ordenValida.setId(101L);
        ordenValida.setSubtotalManoObra(new BigDecimal("150.000"));
        ordenValida.setSubtotalRepuestos(new BigDecimal("50.000"));
        // El servicio ignora iva y totalFinal del DTO; los calcula él mismo

        // Factura generada a partir de la orden (valores calculados)
        facturaEjemplo = new Factura();
        facturaEjemplo.setId(1L);
        facturaEjemplo.setNumeroBoleta(1001L);
        facturaEjemplo.setOrdenId(101L);
        facturaEjemplo.setFechaEmision(LocalDateTime.of(2025, 6, 20, 15, 0));
        facturaEjemplo.setSubtotalManoObra(new BigDecimal("150.000"));
        facturaEjemplo.setSubtotalRepuestos(new BigDecimal("50.000"));
        facturaEjemplo.setIva(new BigDecimal("38.000"));       // 19% de 200.000
        facturaEjemplo.setTotalFinal(new BigDecimal("238.000"));

        // Pago de ejemplo
        pagoEjemplo = new Pago();
        pagoEjemplo.setId(1L);
        pagoEjemplo.setMonto(new BigDecimal("100.000"));
        pagoEjemplo.setFechaPago(LocalDateTime.of(2025, 6, 21, 9, 0));
        pagoEjemplo.setMetodoPago("Transferencia");
        pagoEjemplo.setReferencia("Pago parcial");
    }

    // ────────────────────── LISTAR FACTURAS ──────────────────────
    @Test
    void listarFacturas_cuandoHayDatos_retornaLista() {
        // ARRANGE
        List<Factura> lista = new ArrayList<>();
        lista.add(facturaEjemplo);
        when(facturaRepository.findAll()).thenReturn(lista);

        // ACT
        List<Factura> resultado = facturaService.listarFacturas();

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals(1001L, resultado.get(0).getNumeroBoleta());
        verify(facturaRepository).findAll();
    }

    @Test
    void listarFacturas_cuandoNoHayDatos_retornaListaVacia() {
        when(facturaRepository.findAll()).thenReturn(Collections.emptyList());
        List<Factura> resultado = facturaService.listarFacturas();
        assertTrue(resultado.isEmpty());
    }

    // ────────────────────── BUSCAR POR ID ──────────────────────
    @Test
    void buscarPorId_cuandoExiste_retornaFactura() {
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));
        Factura resultado = facturaService.buscarPorId(1L);
        assertEquals(1L, resultado.getId());
        assertEquals(1001L, resultado.getNumeroBoleta());
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaExcepcion() {
        when(facturaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> facturaService.buscarPorId(99L));
    }

    // ────────────────────── GENERAR FACTURA ──────────────────────
    @Test
    void generarFactura_cuandoOrdenValida_creaFacturaConMontosCalculados() {
        // ARRANGE
        when(ordenClient.obtenerOrden(101L)).thenReturn(ordenValida);
        when(facturaRepository.findByOrdenId(101L)).thenReturn(Collections.emptyList()); // no facturada aún
        // Para el número de boleta, se simula que no hay facturas previas
        when(facturaRepository.findAll()).thenReturn(Collections.emptyList());
        // Al guardar, devolvemos la misma factura que se crea (simulando el save)
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        Factura facturaGenerada = facturaService.generarFactura(101L);

        // ASSERT
        assertNotNull(facturaGenerada);
        assertEquals(101L, facturaGenerada.getOrdenId());
        // Verificar que el número de boleta es 1001 (primer correlativo)
        assertEquals(1001L, facturaGenerada.getNumeroBoleta());
        // Verificar que los cálculos de IVA y total son correctos
        assertEquals(new BigDecimal("38.000"), facturaGenerada.getIva().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("238.000"), facturaGenerada.getTotalFinal().setScale(2, RoundingMode.HALF_UP));
        // Se ignoran los valores del DTO de la orden
        verify(ordenClient).obtenerOrden(101L);
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    void generarFactura_cuandoOrdenNoExiste_lanzaExcepcion() {
        when(ordenClient.obtenerOrden(101L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> facturaService.generarFactura(101L));
        verify(facturaRepository, never()).save(any());
    }

    @Test
    void generarFactura_cuandoOrdenYaFacturada_lanzaExcepcion() {
        when(ordenClient.obtenerOrden(101L)).thenReturn(ordenValida);
        when(facturaRepository.findByOrdenId(101L)).thenReturn(List.of(facturaEjemplo)); // ya existe factura
        assertThrows(RuntimeException.class, () -> facturaService.generarFactura(101L));
        verify(facturaRepository, never()).save(any());
    }

    // ────────────────────── REGISTRAR PAGO ──────────────────────
    @Test
    void registrarPago_cuandoMontoValido_guardaPago() {
        // ARRANGE
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));
        // Sin pagos previos
        when(pagoRepository.findByFacturaId(1L)).thenReturn(Collections.emptyList());
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoEjemplo);

        // ACT
        Pago resultado = facturaService.registrarPago(1L, pagoEjemplo);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(new BigDecimal("100.000"), resultado.getMonto());
        verify(pagoRepository).save(pagoEjemplo);
        // Verifica que el pago tenga asociada la factura
        assertEquals(facturaEjemplo, pagoEjemplo.getFactura());
    }

    @Test
    void registrarPago_cuandoSuperaTotal_lanzaExcepcion() {
        // Simular que ya existe un pago de 200.000 y se intenta agregar 100.000 → superaría total (238.000)
        Pago pagoPrevio = new Pago();
        pagoPrevio.setMonto(new BigDecimal("200.000"));
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));
        when(pagoRepository.findByFacturaId(1L)).thenReturn(List.of(pagoPrevio));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> facturaService.registrarPago(1L, pagoEjemplo));
        assertEquals("El pago supera el total de la factura", ex.getMessage());
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void registrarPago_cuandoFacturaNoExiste_lanzaExcepcion() {
        when(facturaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> facturaService.registrarPago(99L, pagoEjemplo));
        verify(pagoRepository, never()).save(any());
    }

    // ────────────────────── LISTAR PAGOS ──────────────────────
    @Test
    void listarPagos_cuandoExistenPagos_retornaLista() {
        when(pagoRepository.findByFacturaId(1L)).thenReturn(List.of(pagoEjemplo));
        List<Pago> pagos = facturaService.listarPagos(1L);
        assertEquals(1, pagos.size());
        assertEquals(new BigDecimal("100.000"), pagos.get(0).getMonto());
    }

    @Test
    void listarPagos_cuandoNoHayPagos_retornaListaVacia() {
        when(pagoRepository.findByFacturaId(1L)).thenReturn(Collections.emptyList());
        assertTrue(facturaService.listarPagos(1L).isEmpty());
    }

    // ────────────────────── OBTENER DETALLE ──────────────────────
    @Test
    void obtenerDetalle_cuandoTodoCorrecto_retornaDTO() {
        // ARRANGE
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));
        when(ordenClient.obtenerOrden(101L)).thenReturn(ordenValida);
        when(pagoRepository.findByFacturaId(1L)).thenReturn(List.of(pagoEjemplo));

        // ACT
        var detalle = facturaService.obtenerDetalle(1L);

        // ASSERT
        assertNotNull(detalle);
        assertEquals(1L, detalle.getId());
        assertEquals(1001L, detalle.getNumeroBoleta());
        assertEquals(new BigDecimal("238.000"), detalle.getTotalFinal());
        assertEquals(ordenValida, detalle.getOrdenTrabajo());
        assertEquals(1, detalle.getPagos().size());
    }

    @Test
    void obtenerDetalle_cuandoOrdenNoExiste_lanzaExcepcion() {
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));
        when(ordenClient.obtenerOrden(101L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> facturaService.obtenerDetalle(1L));
    }
}
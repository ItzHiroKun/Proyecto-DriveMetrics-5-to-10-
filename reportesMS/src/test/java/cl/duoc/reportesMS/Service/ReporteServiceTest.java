package cl.duoc.reportesMS.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.reportesMS.Client.CitasClient;
import cl.duoc.reportesMS.Client.ClientesClient;
import cl.duoc.reportesMS.Client.FacturasClient;
import cl.duoc.reportesMS.Client.OrdenesClient;
import cl.duoc.reportesMS.Client.VehiculosClient;
import cl.duoc.reportesMS.dto.CitaDTO;
import cl.duoc.reportesMS.dto.ClienteDTO;
import cl.duoc.reportesMS.dto.FacturaDTO;
import cl.duoc.reportesMS.dto.OrdenTrabajoDTO;
import cl.duoc.reportesMS.dto.VehiculoDTO;
import cl.duoc.reportesMS.model.Reporte;
import cl.duoc.reportesMS.repository.ReporteRepository;
import cl.duoc.reportesMS.service.ReporteService;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private CitasClient citasClient;

    @Mock
    private FacturasClient facturasClient;

    @Mock
    private ClientesClient clientesClient;

    @Mock
    private OrdenesClient ordenesClient;

    @Mock
    private VehiculosClient vehiculosClient;

    @InjectMocks
    private ReporteService reporteService;

    private Reporte reporteEjemplo;
    private CitaDTO citaEjemplo;
    private FacturaDTO facturaEjemplo;
    private ClienteDTO clienteEjemplo;

    @BeforeEach
    void setUp() {
        // Reporte de ejemplo guardado en el historial
        reporteEjemplo = new Reporte();
        reporteEjemplo.setId(1L);
        reporteEjemplo.setTipo("VENTAS_MENSUALES");
        reporteEjemplo.setFechaGeneracion(LocalDateTime.of(2025, 6, 30, 18, 0));
        reporteEjemplo.setParametros("{\"mes\":6,\"anio\":2025}");
        reporteEjemplo.setResultado("{\"total_ventas\":1122500}");

        // Cita de ejemplo (para reportes de citas)
        citaEjemplo = new CitaDTO();
        citaEjemplo.setId(1L);
        citaEjemplo.setFecha(LocalDate.of(2025, 6, 20));
        citaEjemplo.setEstado("Agendada");
        citaEjemplo.setVehiculoId(1L);

        // Factura de ejemplo (para reportes de facturación)
        facturaEjemplo = new FacturaDTO();
        facturaEjemplo.setId(1L);
        facturaEjemplo.setFechaEmision(LocalDateTime.of(2025, 6, 20, 15, 0));
        facturaEjemplo.setTotalFinal(new BigDecimal("238.000"));
        facturaEjemplo.setOrdenId(101L);

        // Cliente de ejemplo (para reportes de clientes)
        clienteEjemplo = new ClienteDTO();
        clienteEjemplo.setId(1L);
        clienteEjemplo.setNombreCompleto("Juan Pérez");
    }

    // ────────────────────── REPORTE CITAS DE HOY ──────────────────────
    @Test
    void reporteCitasHoy_debeRetornarResumenConCitas() {
        // ARRANGE: simulamos que hay citas para hoy
        when(citasClient.obtenerCitasHoy()).thenReturn(List.of(citaEjemplo));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        Map<String, Object> resultado = reporteService.reporteCitasHoy();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.get("total_citas"));
        assertTrue(resultado.containsKey("citas"));
        verify(reporteRepository).save(any(Reporte.class)); // Se guarda en el historial
    }

    @Test
    void reporteCitasHoy_cuandoNoHayCitas_retornaTotalCero() {
        when(citasClient.obtenerCitasHoy()).thenReturn(List.of());

        Map<String, Object> resultado = reporteService.reporteCitasHoy();

        assertEquals(0, resultado.get("total_citas"));
    }

    // ────────────────────── FACTURACIÓN MENSUAL ──────────────────────
    @Test
    void reporteFacturacionMensual_debeRetornarResumenConFacturas() {
        // ARRANGE: simular facturas de junio 2025
        when(facturasClient.obtenerFacturasPorMes(2025, 6)).thenReturn(List.of(facturaEjemplo));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteEjemplo);

        // ACT
        Map<String, Object> resultado = reporteService.reporteFacturacionMensual(2025, 6);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.get("cantidad_facturas"));
        assertEquals(new BigDecimal("238.000"), resultado.get("total_facturado"));
        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void reporteFacturacionMensual_cuandoNoHayFacturas_retornaTotalCero() {
        when(facturasClient.obtenerFacturasPorMes(2025, 6)).thenReturn(List.of());

        Map<String, Object> resultado = reporteService.reporteFacturacionMensual(2025, 6);

        assertEquals(0, resultado.get("cantidad_facturas"));
        assertEquals(BigDecimal.ZERO, resultado.get("total_facturado"));
    }

    // ────────────────────── TOP CLIENTES ──────────────────────
    @Test
    void reporteTopClientes_debeRetornarListaDeClientesFrecuentes() {
        // Simulamos que el servicio de clientes devuelve una lista de IDs de clientes frecuentes
        when(clientesClient.obtenerTopClientes()).thenReturn(List.of(1L, 2L));
        when(clientesClient.obtenerCliente(1L)).thenReturn(clienteEjemplo);
        ClienteDTO otroCliente = new ClienteDTO();
        otroCliente.setId(2L);
        otroCliente.setNombreCompleto("María Gómez");
        when(clientesClient.obtenerCliente(2L)).thenReturn(otroCliente);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteEjemplo);

        List<Map<String, Object>> resultado = reporteService.reporteTopClientes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", ((ClienteDTO) resultado.get(0).get("cliente")).getNombreCompleto());
        verify(reporteRepository).save(any(Reporte.class));
    }

    // ────────────────────── HISTORIAL DE REPORTES ──────────────────────
    @Test
    void listarHistorial_sinFiltro_retornaTodosLosReportes() {
        when(reporteRepository.findAll()).thenReturn(List.of(reporteEjemplo));

        List<Reporte> historial = reporteService.listarHistorial(null);

        assertEquals(1, historial.size());
        assertEquals("VENTAS_MENSUALES", historial.get(0).getTipo());
    }

    @Test
    void listarHistorial_conFiltro_retornaReportesDelTipoIndicado() {
        when(reporteRepository.findByTipo("VENTAS_MENSUALES")).thenReturn(List.of(reporteEjemplo));

        List<Reporte> historial = reporteService.listarHistorial("VENTAS_MENSUALES");

        assertEquals(1, historial.size());
        assertEquals("VENTAS_MENSUALES", historial.get(0).getTipo());
    }

    @Test
    void listarHistorial_cuandoNoHayReportes_retornaListaVacia() {
        when(reporteRepository.findAll()).thenReturn(List.of());
        assertTrue(reporteService.listarHistorial(null).isEmpty());
    }
}
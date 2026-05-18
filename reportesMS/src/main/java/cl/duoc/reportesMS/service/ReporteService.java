package cl.duoc.reportesMS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.reportesMS.client.*;
import cl.duoc.reportesMS.dto.*;
import cl.duoc.reportesMS.model.Reporte;
import cl.duoc.reportesMS.repository.ReporteRepository;

@Service
public class ReporteService {

    @Autowired
    private CitaClient citaClient;

    @Autowired
    private FacturaClient facturaClient;

    @Autowired
    private OrdenTrabajoClient ordenClient;

    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private VehiculoClient vehiculoClient;

    @Autowired
    private ReporteRepository repository;

    //Resumen de citas (hoy)
    public Map<String, Object> reporteCitasHoy() {
        List<CitaDTO> todas = citaClient.listarCitas();
        LocalDate hoy = LocalDate.now();
        List<CitaDTO> citasHoy = todas.stream()
                .filter(c -> c.getFecha() != null && c.getFecha().equals(hoy))
                .collect(Collectors.toList());

        Map<String, Long> porEstado = citasHoy.stream()
                .collect(Collectors.groupingBy(CitaDTO::getEstado, Collectors.counting()));

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("fecha", hoy.toString());
        resultado.put("totalCitas", citasHoy.size());
        resultado.put("porEstado", porEstado);

        // Convertir a texto para guardar en BD (sin Jackson)
        String textoResultado = convertirMapATexto(resultado);

        guardarReporte("CITAS_DIARIAS", "fecha=" + hoy, textoResultado);
        return resultado;
    }

    //Facturación mensual
    public Map<String, Object> reporteFacturacionMensual(int año, int mes) {
        List<FacturaDTO> facturas = facturaClient.listarFacturas();
        BigDecimal total = facturas.stream()
                .filter(f -> {
                    if (f.getFechaEmision() == null) return false;
                    return f.getFechaEmision().getYear() == año && f.getFechaEmision().getMonthValue() == mes;
                })
                .map(FacturaDTO::getTotalFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("año", año);
        resultado.put("mes", mes);
        resultado.put("totalFacturado", total);
        resultado.put("cantidadFacturas", facturas.size());

        String textoResultado = convertirMapATexto(resultado);
        guardarReporte("FACTURACION_MENSUAL", "año=" + año + ",mes=" + mes, textoResultado);
        return resultado;
    }

    //Clientes con más órdenes
    public List<Map<String, Object>> reporteTopClientes() {
        List<OrdenTrabajoDTO> ordenes = ordenClient.listarOrdenes();
        Map<Long, Long> clienteCantidad = new HashMap<>();
        for (OrdenTrabajoDTO o : ordenes) {
            try {
                VehiculoDTO v = vehiculoClient.obtenerVehiculo(o.getVehiculoId());
                if (v != null && v.getClienteId() != null) {
                    clienteCantidad.merge(v.getClienteId(), 1L, (a, b) -> a + b);
                }
            } catch (Exception ignored) {}
        }

        List<Map.Entry<Long, Long>> top = clienteCantidad.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : top) {
            Map<String, Object> item = new LinkedHashMap<>();
            try {
                ClienteDTO c = clienteClient.obtenerCliente(entry.getKey());
                item.put("clienteId", c.getId());
                item.put("nombre", c.getNombreCompleto());
            } catch (Exception e) {
                item.put("clienteId", entry.getKey());
                item.put("nombre", "Desconocido");
            }
            item.put("cantidadOrdenes", entry.getValue());
            resultado.add(item);
        }

        //Convertir lista de mapas a texto
        String textoResultado = convertirListaMapATexto(resultado);
        guardarReporte("TOP_CLIENTES", "top=5", textoResultado);
        return resultado;
    }


    //GUARDAR HISTORIAL

    private void guardarReporte(String tipo, String parametros, String resultadoTexto) {
        try {
            Reporte r = new Reporte();
            r.setTipo(tipo);
            r.setFechaGeneracion(LocalDateTime.now());
            r.setParametros(parametros);
            r.setResultado(resultadoTexto);
            repository.save(r);
        } catch (Exception ignored) {}
    }

    //CONSULTAR HISTÓRICO

    public List<Reporte> listarHistorial(String tipo) {
        if (tipo != null && !tipo.isEmpty()) {
            return repository.findByTipoOrderByFechaGeneracionDesc(tipo);
        }
        return repository.findAll();
    }

    //MÉTODOS AUXILIARES PARA TEXTO
    private String convertirMapATexto(Map<String, Object> mapa) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : mapa.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    private String convertirListaMapATexto(List<Map<String, Object>> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append("--- Cliente ").append(i + 1).append(" ---\n");
            Map<String, Object> mapa = lista.get(i);
            for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }
}
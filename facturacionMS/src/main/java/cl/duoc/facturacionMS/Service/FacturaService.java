package cl.duoc.facturacionMS.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.facturacionMS.Client.OrdenesTrabajoClient;
import cl.duoc.facturacionMS.DTO.FacturaDetalleDTO;
import cl.duoc.facturacionMS.DTO.OrdenTrabajoDTO;
import cl.duoc.facturacionMS.DTO.PagoDTO;
import cl.duoc.facturacionMS.Model.Factura;
import cl.duoc.facturacionMS.Model.Pago;
import cl.duoc.facturacionMS.Repository.FacturaRepository;
import cl.duoc.facturacionMS.Repository.PagoRepository;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PagoRepository PagoRepository;   // respetamos el nombre que usas

    @Autowired
    private OrdenesTrabajoClient ordenClient;

    // -------------------------------------------------------------
    // Gestión de facturas
    // -------------------------------------------------------------

    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    public Factura buscarPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    @Transactional
    public Factura generarFactura(Long ordenId) {
        // 1. Verificar que la orden exista
        OrdenTrabajoDTO orden = ordenClient.obtenerOrden(ordenId);
        if (orden == null) {
            throw new RuntimeException("La orden de trabajo no existe");
        }

        // 2. Verificar que la orden no tenga ya una factura
        if (!facturaRepository.findByOrdenId(ordenId).isEmpty()) {
            throw new RuntimeException("Esta orden ya tiene una factura generada");
        }

        // 3. Obtener subtotales desde la orden (asegurando no nulos)
        BigDecimal subtotalManoObra = orden.getSubtotalManoObra() != null
                ? orden.getSubtotalManoObra() : BigDecimal.ZERO;
        BigDecimal subtotalRepuestos = orden.getSubtotalRepuestos() != null
                ? orden.getSubtotalRepuestos() : BigDecimal.ZERO;

        // 4. Calcular IVA (19%) y total final automáticamente
        BigDecimal subtotal = subtotalManoObra.add(subtotalRepuestos);
        BigDecimal iva = subtotal.multiply(new BigDecimal("0.19")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalFinal = subtotal.add(iva);

        // 5. Crear la factura y asignar valores calculados (ignoramos los que trae la orden)
        Factura factura = new Factura();
        factura.setOrdenId(ordenId);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotalManoObra(subtotalManoObra);
        factura.setSubtotalRepuestos(subtotalRepuestos);
        factura.setIva(iva);                // calculado automáticamente
        factura.setTotalFinal(totalFinal);   // calculado automáticamente

        // 6. Generar número de boleta correlativo
        Long maxBoleta = facturaRepository.findAll()
                .stream()
                .mapToLong(Factura::getNumeroBoleta)
                .max()
                .orElse(1000);
        factura.setNumeroBoleta(maxBoleta + 1);

        // Si se desea asignar el clienteId desde la orden, descomentar:
        // factura.setClienteId(orden.getClienteId());

        return facturaRepository.save(factura);
    }

    @Transactional(readOnly = true)
    public FacturaDetalleDTO obtenerDetalle(Long facturaId) {
        Factura factura = buscarPorId(facturaId);

        OrdenTrabajoDTO orden = ordenClient.obtenerOrden(factura.getOrdenId());
        if (orden == null) {
            throw new RuntimeException("No se pudo recuperar la orden asociada");
        }

        List<Pago> pagos = PagoRepository.findByFacturaId(facturaId);
        List<PagoDTO> pagosDTO = pagos.stream().map(p -> {
            PagoDTO dto = new PagoDTO();
            dto.setId(p.getId());
            dto.setMonto(p.getMonto());
            dto.setFechaPago(p.getFechaPago());
            dto.setMetodoPago(p.getMetodoPago());
            dto.setReferencia(p.getReferencia());
            return dto;
        }).collect(Collectors.toList());

        FacturaDetalleDTO detalle = new FacturaDetalleDTO();
        detalle.setId(factura.getId());
        detalle.setNumeroBoleta(factura.getNumeroBoleta());
        detalle.setFechaEmision(factura.getFechaEmision());
        detalle.setSubtotalManoObra(factura.getSubtotalManoObra());
        detalle.setSubtotalRepuestos(factura.getSubtotalRepuestos());
        detalle.setIva(factura.getIva());
        detalle.setTotalFinal(factura.getTotalFinal());
        detalle.setOrdenTrabajo(orden);
        detalle.setPagos(pagosDTO);

        return detalle;
    }

    // -------------------------------------------------------------
    // Gestión de pagos
    // -------------------------------------------------------------

    @Transactional
    public Pago registrarPago(Long facturaId, Pago pago) {
        Factura factura = buscarPorId(facturaId);

        BigDecimal totalPagado = PagoRepository.findByFacturaId(facturaId)
                .stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPagado.add(pago.getMonto()).compareTo(factura.getTotalFinal()) > 0) {
            throw new RuntimeException("El pago supera el total de la factura");
        }

        pago.setFactura(factura);
        pago.setFechaPago(LocalDateTime.now());
        return PagoRepository.save(pago);
    }

    public List<Pago> listarPagos(Long facturaId) {
        return PagoRepository.findByFacturaId(facturaId);
    }
}
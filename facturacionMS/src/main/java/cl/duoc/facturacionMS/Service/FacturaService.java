package cl.duoc.facturacionMS.Service;

import java.math.BigDecimal;
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
    private PagoRepository PagoRepository;

    @Autowired
    private OrdenesTrabajoClient ordenClient;

    //Gestión de facturas

    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    public Factura buscarPorId(Long id) {
        return facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    @Transactional
    public Factura generarFactura(Long ordenId) {
        //Verificar que la orden no tenga ya una factura
        OrdenTrabajoDTO orden = ordenClient.obtenerOrden(ordenId);
        if (orden == null){
            throw new RuntimeException("La orden de trabajo no existe");
        }

        //Verificar que la oden no tenga una factura
        if (!facturaRepository.findByOrdenId(ordenId).isEmpty()) {
            throw new RuntimeException("Esta orden ya tiene una factura generada");
        }

        //Crear la factura tomando montos de la orden
        Factura factura = new Factura();
        factura.setOrdenId(ordenId);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotalManoObra(orden.getSubtotalManoObra() != null ?
            orden.getSubtotalManoObra(): BigDecimal.ZERO);
        factura.setSubtotalRepuestos(orden.getSubtotalRepuestos() != null ?
            orden.getSubtotalRepuestos(): BigDecimal.ZERO);
        factura.setIva(orden.getIva());
        factura.setTotalFinal(orden.getTotalFinal());

        //Generar número de boleta
        Long maxBoleta = facturaRepository.findAll().stream().mapToLong(Factura::getNumeroBoleta)
            .max()
            .orElse(1000);
        factura.setNumeroBoleta(maxBoleta + 1);

        return facturaRepository.save(factura);
    }

    @Transactional(readOnly = true)
    public FacturaDetalleDTO obtenerDetalle(Long facturaId) {
        Factura factura = buscarPorId(facturaId);

        //Obtener orden de trabajo asociada
        OrdenTrabajoDTO orden = ordenClient.obtenerOrden(factura.getOrdenId());
        if  (orden == null) {
            throw new RuntimeException("No se pudo recuperar la orden asociada");
        }

        // Obtener pagos en esta factura
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

    //Gestión de pagos

    @Transactional
    public Pago registrarPago(Long facturaId, Pago pago) {
        Factura factura = buscarPorId(facturaId);

        //Validar que el monto del pago no supere el total

        BigDecimal totalPagado = PagoRepository.findByFacturaId(facturaId).stream()
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPagado.add(pago.getMonto()).compareTo(factura.getTotalFinal()) > 0) {
            throw new RuntimeException("El pago super el total de la factura");
        }

        pago.setFactura(factura);
        pago.setFechaPago(LocalDateTime.now());
        return PagoRepository.save(pago);
    }

    public List<Pago> listarPagos(Long facturaId){ 
        return PagoRepository.findByFacturaId(facturaId);
    }
}

package cl.duoc.facturacionMS.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.facturacionMS.Model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Pagos de una factura
    List<Pago> findByFacturaId(Long facturaId);
}
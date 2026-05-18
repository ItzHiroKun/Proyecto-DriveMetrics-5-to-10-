package cl.duoc.facturacionMS.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.facturacionMS.Model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Buscar factura por número de boleta
    Factura findByNumeroBoleta(Long numeroBoleta);

    // Buscar todas las facturas de una orden (aunque debería ser única)
    List<Factura> findByOrdenId(Long ordenId);
}
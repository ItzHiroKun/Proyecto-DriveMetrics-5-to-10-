package cl.duoc.reportesMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.reportesMS.model.Reporte;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipoOrderByFechaGeneracionDesc(String tipo);
}
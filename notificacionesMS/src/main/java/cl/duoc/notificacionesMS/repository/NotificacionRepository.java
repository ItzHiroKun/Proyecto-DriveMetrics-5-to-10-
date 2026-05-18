package cl.duoc.notificacionesMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.notificacionesMS.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatarioTipoAndDestinatarioId(String tipo, Long id);

    List<Notificacion> findByEstado(String estado);
}
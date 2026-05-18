package cl.duoc.citasMS.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.citasMS.Model.Cita;

@Repository
public interface CitaRepository extends JpaRepository <Cita, Long> {

    //Buscar cita por vehículo
    List<Cita> findByVehiculoId(Long vehiculoId);

    //Buscar cita por fecha
    List<Cita> findByFecha(LocalDate fecha);

    //Buscar cita por estado
    List<Cita> findByEstado(String estado);

    //Buscar cita por vehiculo y estado
    List<Cita> findByVehiculoIdAndEstado(Long vehiculoId, String estado);

    //Buscar cita entre fechas
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);
}

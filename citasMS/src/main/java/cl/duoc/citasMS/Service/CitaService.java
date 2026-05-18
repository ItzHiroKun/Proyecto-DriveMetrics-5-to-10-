package cl.duoc.citasMS.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.citasMS.Client.ClienteClient;
import cl.duoc.citasMS.Client.VehiculoClient;
import cl.duoc.citasMS.DTO.*;
import cl.duoc.citasMS.Model.Cita;
import cl.duoc.citasMS.Repository.CitaRepository;

@Service

public class CitaService {

    @Autowired
    private CitaRepository repository;

    @Autowired
    private VehiculoClient vehiculoClient;

    @Autowired
    private ClienteClient clienteClient;

    // CRUD

    public List<Cita> listar() {
        return repository.findAll();
    }

    public Cita guardar(Cita cita) {
        //Validar la existencia del vehiculo
        VehiculoDTO vehiculo = vehiculoClient.obtenerVehiculo(cita.getVehiculoId());
        if (vehiculo == null) {
            throw new RuntimeException("El Vehiculo no existe");
        }
        return repository.save(cita);
    }

    public Cita buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("La cita no exiiste"));
    }

    public Cita actualizarEstado(Long id, String nuevoEstado) {
        Cita cita = buscarPorId(id);
        cita.setEstado(nuevoEstado);
        return repository.save(cita);
    }

    public void eliminar(Long id) {
        Cita cita = buscarPorId(id);
        repository.delete(cita);
    }

    //Detalle completo

    public CitaDetalleDTO obtenerDetalle(Long id) {

        //BD local: Obtener Cita
        Cita cita = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        VehiculoDTO vehiculo = vehiculoClient.obtenerVehiculo(cita.getVehiculoId());
        if (vehiculo == null) {
            throw new RuntimeException("Vehiculo no encontrado en el sistema");
        }
    
        ClienteDTO cliente = clienteClient.obtenerCliente(vehiculo.getClienteId());
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado en el sistema");
        }

        CitaDetalleDTO dto = new CitaDetalleDTO();

        dto.setId(cita.getId());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setLugar(cita.getLugar());
        dto.setEstado(cita.getEstado());
        dto.setVehiculo(vehiculo);
        dto.setCliente(cliente);

        return dto;
    }
}

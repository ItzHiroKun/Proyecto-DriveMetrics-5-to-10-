package cl.duoc.notificacionesMS.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.notificacionesMS.client.ClienteClient;
import cl.duoc.notificacionesMS.client.MecanicoClient;
import cl.duoc.notificacionesMS.dto.NotificacionDetalleDTO;
import cl.duoc.notificacionesMS.model.Notificacion;
import cl.duoc.notificacionesMS.repository.NotificacionRepository;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository repository;

    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private MecanicoClient mecanicoClient;


    // CRUD


    public Notificacion crear(Notificacion notificacion) {
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setEstado("PENDIENTE");
        return repository.save(notificacion);
    }

    public List<Notificacion> listar() {
        return repository.findAll();
    }

    public Notificacion buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    // Simular envío (cambiar estado a ENVIADA)
    public Notificacion enviar(Long id) {
        Notificacion notif = buscarPorId(id);
        // Aquí iría lógica real de envío (email, SMS)
        notif.setEstado("ENVIADA");
        notif.setFechaEnvio(LocalDateTime.now());
        return repository.save(notif);
    }


    //Detalle de datos


    public NotificacionDetalleDTO obtenerDetalle(Long id) {
        Notificacion notif = buscarPorId(id);
        String nombre = "";
        String contacto = "";

        if ("CLIENTE".equalsIgnoreCase(notif.getDestinatarioTipo())) {
            try {
                var cliente = clienteClient.obtenerCliente(notif.getDestinatarioId());
                if (cliente != null) {
                    nombre = cliente.getNombreCompleto();
                    contacto = cliente.getContacto();
                }
            } catch (Exception e) {
                nombre = "Cliente no disponible";
            }
        } else if ("MECANICO".equalsIgnoreCase(notif.getDestinatarioTipo())) {
            try {
                var mecanico = mecanicoClient.obtenerMecanico(notif.getDestinatarioId());
                if (mecanico != null) {
                    nombre = mecanico.getNombres() + " " + mecanico.getApellidos();
                    contacto = mecanico.getContacto();
                }
            } catch (Exception e) {
                nombre = "Mecánico no disponible";
            }
        }

        NotificacionDetalleDTO dto = new NotificacionDetalleDTO();
        dto.setId(notif.getId());
        dto.setMensaje(notif.getMensaje());
        dto.setTipo(notif.getTipo());
        dto.setDestinatarioTipo(notif.getDestinatarioTipo());
        dto.setDestinatarioId(notif.getDestinatarioId());
        dto.setEstado(notif.getEstado());
        dto.setFechaCreacion(notif.getFechaCreacion());
        dto.setFechaEnvio(notif.getFechaEnvio());
        dto.setReferencia(notif.getReferencia());
        dto.setNombreDestinatario(nombre);
        dto.setContactoDestinatario(contacto);

        return dto;
    }
}
package cl.duoc.proveedoresMS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.proveedoresMS.client.RepuestoClient;
import cl.duoc.proveedoresMS.dto.ProveedorDetalleDTO;
import cl.duoc.proveedoresMS.dto.RepuestoDTO;
import cl.duoc.proveedoresMS.model.Proveedor;
import cl.duoc.proveedoresMS.repository.ProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository repository;

    @Autowired
    private RepuestoClient repuestoClient;


    //CRUD


    public List<Proveedor> listar() {
        return repository.findAll();
    }

    public List<Proveedor> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Proveedor guardar(Proveedor proveedor) {
        return repository.save(proveedor);
    }

    public Proveedor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }

    public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor existente = buscarPorId(id);
        existente.setRazonSocial(datos.getRazonSocial());
        existente.setContacto(datos.getContacto());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());
        existente.setDireccion(datos.getDireccion());
        existente.setActivo(datos.getActivo());
        // RUN y DV no se modifican
        return repository.save(existente);
    }

    public void desactivar(Long id) {
        Proveedor p = buscarPorId(id);
        p.setActivo(false);
        repository.save(p);
    }


    //DETALLE CON REPUESTOS


    public ProveedorDetalleDTO obtenerDetalle(Long id) {
        Proveedor proveedor = buscarPorId(id);

        // Obtener repuestos suministrados por este proveedor
        List<RepuestoDTO> repuestos = repuestoClient.obtenerRepuestosPorProveedor(id);

        ProveedorDetalleDTO dto = new ProveedorDetalleDTO();
        dto.setId(proveedor.getId());
        dto.setRun(proveedor.getRun());
        dto.setDv(proveedor.getDv());
        dto.setRazonSocial(proveedor.getRazonSocial());
        dto.setContacto(proveedor.getContacto());
        dto.setTelefono(proveedor.getTelefono());
        dto.setEmail(proveedor.getEmail());
        dto.setDireccion(proveedor.getDireccion());
        dto.setActivo(proveedor.getActivo());
        dto.setRepuestosSuministrados(repuestos);

        return dto;
    }
}
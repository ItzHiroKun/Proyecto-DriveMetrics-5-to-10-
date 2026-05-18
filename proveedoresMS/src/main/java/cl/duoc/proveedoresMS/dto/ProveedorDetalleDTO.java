package cl.duoc.proveedoresMS.dto;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDetalleDTO {

    private Long id;
    private Long run;
    private String dv;
    private String razonSocial;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activo;

    // Lista de repuestos suministrados (desde inventario)
    private List<RepuestoDTO> repuestosSuministrados;
}
package cl.duoc.notificacionesMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MecanicoDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String contacto;
    private String email;
}
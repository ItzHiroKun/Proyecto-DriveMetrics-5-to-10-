package cl.duoc.notificacionesMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;
    private String nombreCompleto;
    private String contacto;    // teléfono o email
    private String email;       // si existe en el MS real
}
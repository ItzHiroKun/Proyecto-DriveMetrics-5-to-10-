package cl.duoc.citasMS.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClienteDTO {

    private Long id;
    private Long run;
    private String dv;
    private String nombreCompleto;
    private String contacto;
    private String direccion;
}

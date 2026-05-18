package cl.duoc.citasMS.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class VehiculoDTO {

    private Long id;
    private String patente;
    private String marca;
    private String modelo;
    private String chasis;
    private Long clienteId; //Id del dueño para buscar al cliente
}

package cl.duoc.citasMS.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalDateTime hora;

    @Column
    private String lugar;

    @Column(length = 20)
    private String estado; //Agendada, Completada, Cancelada

    //Solo se guarda el id del vehículo
    //El vehículo vive en otro MS

    @Column(name = "vehiculo_id", nullable = false)
    private Long vehiculoId;

    // Se añade la columna cliente_id para asosicar la cita con el cliente
    @Column(name = "cliente_id", nullable = false)
    private Long clientId;
    
}

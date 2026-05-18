package cl.duoc.reportesMS.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(length = 255)
    private String parametros;   // JSON simple con los parámetros de entrada

    @Column(columnDefinition = "TEXT")
    private String resultado;    // JSON con los datos generados
}
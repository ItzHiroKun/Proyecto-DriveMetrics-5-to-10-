package cl.duoc.citasMS.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.citasMS.Model.Cita;
import cl.duoc.citasMS.Repository.CitaRepository;




@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initCitas(CitaRepository citaRepo) {
        return args -> {
            // Evitar cargar los datos si ya están cargados
            if (citaRepo.count() > 0){
                System.out.println("Las citas ya existen, no se cargarán nuevamente...");
                return;
            }

            Cita cita1 = new Cita(null, LocalDate.of(2025, 6, 20), LocalDateTime.of(2025, 6, 20, 10, 0), "Taller central", "Agendada", 1L, 1L);
            Cita cita2 = new Cita(null, LocalDate.of(2025, 6, 22), LocalDateTime.of(2025, 6, 22, 14, 30), "Sucursal Norte", "Agendada", 2L, 2L);
            Cita cita3 = new Cita(null, LocalDate.of(2025, 6, 25), LocalDateTime.of(2025, 6, 25, 9, 0), "Taller Central", "Completada", 3L, 1L);
            Cita cita4 = new Cita(null, LocalDate.of(2025, 7, 1), LocalDateTime.of(2025, 7, 1, 11, 0), "Sucursal Sur", "Cancelada", 4L, 3L);

            citaRepo.save(cita1);
            citaRepo.save(cita2);
            citaRepo.save(cita3);
            citaRepo.save(cita4);

            System.out.println("Citas cargadas con éxito...");
        };
    }
}

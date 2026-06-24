package cl.duoc.notificacionesMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class NotificacionesMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificacionesMsApplication.class, args);
		System.out.println("Running...");
	}

}
package cl.duoc.reportesMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReportesMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportesMsApplication.class, args);
		System.out.println("Running...");
	}

}
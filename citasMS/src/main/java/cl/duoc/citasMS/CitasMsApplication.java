package cl.duoc.citasMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CitasMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitasMsApplication.class, args);
	}

}

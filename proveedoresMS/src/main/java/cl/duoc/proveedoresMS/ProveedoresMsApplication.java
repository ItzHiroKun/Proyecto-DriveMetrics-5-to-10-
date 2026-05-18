package cl.duoc.proveedoresMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ProveedoresMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProveedoresMsApplication.class, args);
	}

}
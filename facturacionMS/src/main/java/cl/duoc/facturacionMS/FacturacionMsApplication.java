package cl.duoc.facturacionMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FacturacionMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacturacionMsApplication.class, args);
	}

}
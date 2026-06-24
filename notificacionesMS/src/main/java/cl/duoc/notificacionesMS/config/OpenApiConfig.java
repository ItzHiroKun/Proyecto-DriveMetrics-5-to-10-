package cl.duoc.notificacionesMS.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Notificaciones")
                        .version("1.0.0")
                        .description("Microservicio de notificaciones. Permite crear, listar, buscar y actualizar el estado de notificaciones enviadas a clientes y mecánicos.")
                        .contact(new Contact()
                                .name("Soporte Taller Mecánico")
                                .email("soporte@taller.cl")
                                .url("https://www.taller.cl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
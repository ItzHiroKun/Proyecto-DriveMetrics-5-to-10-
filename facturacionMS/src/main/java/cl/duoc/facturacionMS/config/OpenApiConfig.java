package cl.duoc.facturacionMS.config;

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
                        .title("API de Facturación")
                        .version("1.0.0")
                        .description("Microservicio de facturación y pagos. Permite generar facturas desde órdenes de trabajo, listar facturas, ver detalles y registrar pagos asociados. El IVA y total final se calculan automáticamente.")
                        .contact(new Contact()
                                .name("Soporte Taller Mecánico")
                                .email("soporte@taller.cl")
                                .url("https://www.taller.cl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
        }
}
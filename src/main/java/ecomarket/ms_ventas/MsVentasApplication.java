package ecomarket.ms_ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder; // Necesario para RestTemplate Bean
import org.springframework.context.annotation.Bean; // Necesario para @Bean
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate; // Necesario para RestTemplate

// No necesitas @RestController ni @GetMapping aquí si esta es solo tu clase de aplicación principal

@SpringBootApplication
@ComponentScan("ecomarket.ms_ventas")
@EntityScan("ecomarket.ms_ventas.model")
@EnableJpaRepositories("ecomarket.ms_ventas.repository")
public class MsVentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsVentasApplication.class, args);
    }

    // Definición del Bean para RestTemplate
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Aquí puedes añadir configuraciones adicionales al RestTemplate si las necesitas,
        // como timeouts, interceptors, etc. Por ahora, una configuración básica es suficiente.
        return builder.build();
    }
}
package ecomarket.ms_ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScan("ecomarket.ms_ventas")
@EntityScan("ecomarket.ms_ventas.model")
@EnableJpaRepositories("ecomarket.ms_ventas.repository")
public class MsVentasApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsVentasApplication.class, args);
    }
}



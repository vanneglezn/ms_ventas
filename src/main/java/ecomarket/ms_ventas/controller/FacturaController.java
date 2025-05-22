package ecomarket.ms_ventas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @GetMapping("/test")
    public String testFacturaController() {
        return "🧾 FacturaController funcionando correctamente.";
    }
}

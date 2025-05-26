package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Promocion;
import ecomarket.ms_ventas.repository.PromocionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionRepository promocionRepository;

    @GetMapping
    public List<Promocion> listarPromociones() {
        return promocionRepository.findAll();
    }

    @PostMapping
    public Promocion crearPromocion(@Valid @RequestBody Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @GetMapping("/{id}")
    public Promocion obtenerPromocion(@PathVariable Long id) {
        return promocionRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarPromocion(@PathVariable Long id) {
        promocionRepository.deleteById(id);
    }

    @GetMapping("/producto/{productoId}")
    public List<Promocion> promocionesPorProducto(@PathVariable Long productoId) {
        return promocionRepository.findByProductoId(productoId);
    }

    @GetMapping("/test")
    public String test() {
        return "PromocionController funcionando correctamente.";
    }
}

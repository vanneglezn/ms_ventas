package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Factura;
import ecomarket.ms_ventas.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;

    @GetMapping
    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    @PostMapping
    public Factura crearFactura(@RequestBody Factura factura) {
        return facturaRepository.save(factura);
    }

    @GetMapping("/{id}")
    public Factura obtenerFactura(@PathVariable Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Factura actualizarFactura(@PathVariable Long id, @RequestBody Factura facturaActualizada) {
        return facturaRepository.findById(id).map(factura -> {
            factura.setFechaEmision(facturaActualizada.getFechaEmision());
            factura.setTotal(facturaActualizada.getTotal());
            factura.setEstado(facturaActualizada.getEstado());
            factura.setCompra(facturaActualizada.getCompra());
            return facturaRepository.save(factura);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarFactura(@PathVariable Long id) {
        facturaRepository.deleteById(id);
    }

    @GetMapping("/test")
    public String testFacturaController() {
        return "FacturaController funcionando correctamente.";
    }
}

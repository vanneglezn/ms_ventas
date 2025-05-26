package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.DetalleVenta;
import ecomarket.ms_ventas.repository.DetalleVentaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @GetMapping
    public List<DetalleVenta> listarDetalles() {
        return detalleVentaRepository.findAll();
    }

    @PostMapping
    public DetalleVenta crearDetalle(@Valid @RequestBody DetalleVenta detalle) {
        detalle.calcularSubtotal(); // calcula antes de guardar
        return detalleVentaRepository.save(detalle);
    }

    @GetMapping("/{id}")
    public DetalleVenta obtenerDetalle(@PathVariable Long id) {
        return detalleVentaRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public DetalleVenta actualizarDetalle(@PathVariable Long id, @Valid @RequestBody DetalleVenta detalleActualizado) {
        return detalleVentaRepository.findById(id).map(detalle -> {
            detalle.setProducto(detalleActualizado.getProducto());
            detalle.setFactura(detalleActualizado.getFactura());
            detalle.setCantidad(detalleActualizado.getCantidad());
            detalle.calcularSubtotal();
            return detalleVentaRepository.save(detalle);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarDetalle(@PathVariable Long id) {
        detalleVentaRepository.deleteById(id);
    }

    @GetMapping("/test")
    public String test() {
        return "DetalleVentaController funcionando correctamente.";
    }
}

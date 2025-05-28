package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.DetalleVenta;
import ecomarket.ms_ventas.repository.DetalleVentaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    // 🔹 Listar todos los detalles de venta
    @GetMapping
    public List<DetalleVenta> listarDetalles() {
        return detalleVentaRepository.findAll();
    }

    // 🔹 Crear un nuevo detalle
    @PostMapping
    public DetalleVenta crearDetalle(@Valid @RequestBody DetalleVenta detalle) {
        detalle.calcularSubtotal();
        return detalleVentaRepository.save(detalle);
    }

    // 🔹 Obtener un detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> obtenerDetalle(@PathVariable Long id) {
        return detalleVentaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Actualizar un detalle existente
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVenta> actualizarDetalle(@PathVariable Long id, @Valid @RequestBody DetalleVenta detalleActualizado) {
        return detalleVentaRepository.findById(id).map(detalle -> {
            detalle.setProducto(detalleActualizado.getProducto());
            detalle.setFactura(detalleActualizado.getFactura());
            detalle.setCompra(detalleActualizado.getCompra()); // ✅ Nueva relación
            detalle.setCantidad(detalleActualizado.getCantidad());
            detalle.calcularSubtotal();
            return ResponseEntity.ok(detalleVentaRepository.save(detalle));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Eliminar un detalle por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        if (detalleVentaRepository.existsById(id)) {
            detalleVentaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Endpoint de prueba
    @GetMapping("/test")
    public String test() {
        return "✅ DetalleVentaController funcionando correctamente.";
    }
}

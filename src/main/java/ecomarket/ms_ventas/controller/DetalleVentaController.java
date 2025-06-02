package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.DetalleVenta;
import ecomarket.ms_ventas.repository.DetalleVentaRepository; // Asumiendo que aún lo usas aquí
// import ecomarket.ms_ventas.service.DetalleVentaService; // Idealmente tendrías un servicio

import jakarta.validation.Valid;
import org.slf4j.Logger; // Para logging
import org.slf4j.LoggerFactory; // Para logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/detalles")
public class DetalleVentaController {

    private static final Logger log = LoggerFactory.getLogger(DetalleVentaController.class);

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    // Idealmente, inyectarías un DetalleVentaService aquí

    // 🔹 Listar todos los detalles de venta
    @GetMapping
    public List<DetalleVenta> listarDetalles() {
        log.info("Solicitud para listar todos los DetalleVenta.");
        return detalleVentaRepository.findAll();
    }

    // 🔹 Crear un nuevo detalle (ADVERTENCIA: Crear detalles aislados es inusual)
    @PostMapping
    public ResponseEntity<DetalleVenta> crearDetalle(@Valid @RequestBody DetalleVenta detalle) {
        log.warn("ADVERTENCIA: Se está creando un DetalleVenta de forma aislada. Esto es inusual. Payload: {}", detalle);
        // Para que calcularSubtotal funcione, el DetalleVenta entrante DEBE tener
        // precioUnitarioAlMomentoDeVenta y cantidad ya establecidos.
        // También necesitaría tener su 'venta' (Venta a la que pertenece) ya asignada, lo cual es complejo
        // de manejar correctamente en una solicitud aislada para un DetalleVenta.
        if (detalle.getVenta() == null || detalle.getVenta().getId() == null) {
            log.error("Error al crear DetalleVenta: La Venta asociada es nula o no tiene ID.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DetalleVenta debe estar asociado a una Venta válida.");
        }
        // Asumimos que el precioUnitarioAlMomentoDeVenta y nombreProductoAlMomentoDeVenta
        // ya vienen en el objeto 'detalle' del request body si se crea así.
        // Si no, tendrías que obtenerlos del MS_INVENTARIO aquí, lo cual añade complejidad.
        detalle.calcularSubtotal(); // Asegúrate que precioUnitarioAlMomentoDeVenta esté seteado.
        DetalleVenta detalleGuardado = detalleVentaRepository.save(detalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleGuardado);
    }

    // 🔹 Obtener un detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> obtenerDetalle(@PathVariable Long id) {
        log.info("Solicitud para obtener DetalleVenta por ID: {}", id);
        return detalleVentaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Actualizar un detalle existente (ADVERTENCIA: Actualizar detalles aislados es inusual y complejo)
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVenta> actualizarDetalle(@PathVariable Long id, @Valid @RequestBody DetalleVenta detalleActualizado) {
        log.warn("ADVERTENCIA: Actualizando DetalleVenta ID {} de forma aislada. Payload: {}", id, detalleActualizado);
        return detalleVentaRepository.findById(id).map(detalleExistente -> {
            // No se puede hacer: detalleExistente.setProducto(detalleActualizado.getProducto());
            // Porque DetalleVenta ya no tiene un objeto Producto.
            // Si necesitas cambiar el producto, debes cambiar el productoId y obtener los nuevos
            // nombreProductoAlMomentoDeVenta y precioUnitarioAlMomentoDeVenta desde MS_INVENTARIO.
            // Esto es complejo y generalmente no se hace así para un item de línea ya vendido.

            if (detalleActualizado.getProductoId() != null) {
                // ADVERTENCIA: Cambiar el productoId de un detalle existente y recalcular el precio
                // puede tener implicaciones en el total de la Venta a la que pertenece.
                // Esta lógica debería estar centralizada en VentaService si se permite.
                log.warn("Cambiando productoId de DetalleVenta {} a {}", id, detalleActualizado.getProductoId());
                detalleExistente.setProductoId(detalleActualizado.getProductoId());
                // Necesitarías obtener el nuevo nombre y precio de MS_INVENTARIO aquí
                // y actualizar detalleExistente.setNombreProductoAlMomentoDeVenta(...) y setPrecioUnitarioAlMomentoDeVenta(...)
            }

            // No se puede hacer: detalleExistente.setFactura(detalleActualizado.getFactura());
            // Porque DetalleVenta ya no tiene un campo Factura.

            // Cambiar la Venta a la que pertenece un DetalleVenta es una operación muy extraña.
            if (detalleActualizado.getVenta() != null) {
                 log.warn("Cambiando la Venta asociada para DetalleVenta ID {}", id);
                detalleExistente.setVenta(detalleActualizado.getVenta());
            }

            detalleExistente.setCantidad(detalleActualizado.getCantidad());

            // Recalcular subtotal (asegúrate que precioUnitarioAlMomentoDeVenta esté correcto si el producto cambió)
            // Si cambiaste el productoId, DEBES actualizar precioUnitarioAlMomentoDeVenta antes de esto.
            detalleExistente.calcularSubtotal();

            DetalleVenta detalleGuardado = detalleVentaRepository.save(detalleExistente);
            return ResponseEntity.ok(detalleGuardado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Eliminar un detalle por ID (ADVERTENCIA: Puede dejar una Venta inconsistente)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        log.warn("ADVERTENCIA: Eliminando DetalleVenta ID {} de forma aislada.", id);
        if (detalleVentaRepository.existsById(id)) {
            // Eliminar un DetalleVenta de forma aislada podría dejar la Venta a la que pertenece
            // con un total incorrecto y sin este ítem.
            // Esta operación debería hacerse a través de VentaService.
            detalleVentaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/test")
    public String test() {
        return "✅ DetalleVentaController funcionando correctamente.";
    }
}
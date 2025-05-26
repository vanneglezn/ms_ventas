package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Factura;
import ecomarket.ms_ventas.model.Compra;
import ecomarket.ms_ventas.model.Producto;
import ecomarket.ms_ventas.model.DetalleVenta;
import ecomarket.ms_ventas.model.ReporteVentas;
import ecomarket.ms_ventas.repository.FacturaRepository;
import ecomarket.ms_ventas.repository.CompraRepository;
import ecomarket.ms_ventas.repository.ReporteVentasRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ReporteVentasRepository reporteVentasRepository;

    // 🔹 Listar todas las facturas
    @GetMapping
    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    // 🔹 Crear factura manual
    @PostMapping
    public Factura crearFactura(@Valid @RequestBody Factura factura) {
        return facturaRepository.save(factura);
    }

    // 🔹 Obtener factura por ID
    @GetMapping("/{id}")
    public Factura obtenerFactura(@PathVariable Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

    // 🔹 Actualizar una factura existente
    @PutMapping("/{id}")
    public Factura actualizarFactura(@PathVariable Long id, @Valid @RequestBody Factura facturaActualizada) {
        return facturaRepository.findById(id).map(factura -> {
            factura.setFechaEmision(facturaActualizada.getFechaEmision());
            factura.setTotal(facturaActualizada.getTotal());
            factura.setEstado(facturaActualizada.getEstado());
            factura.setCompra(facturaActualizada.getCompra());
            return facturaRepository.save(factura);
        }).orElse(null);
    }

    // 🔹 Eliminar una factura por ID
    @DeleteMapping("/{id}")
    public void eliminarFactura(@PathVariable Long id) {
        facturaRepository.deleteById(id);
    }

    // 🔹 Test de funcionamiento
    @GetMapping("/test")
    public String testFacturaController() {
        return "✅ FacturaController funcionando correctamente.";
    }

    // 🔹 Generar factura automáticamente desde una compra existente y actualizar el reporte
    @PostMapping("/generar/{compraId}")
    public Factura generarFacturaDesdeCompra(@PathVariable Long compraId) {
        // Verificar si ya existe una factura para esta compra
        if (facturaRepository.existsByCompraId(compraId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Ya existe una factura asociada a esta compra."
            );
        }

        // Buscar compra
        Compra compra = compraRepository.findById(compraId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada con ID: " + compraId));

        // Crear factura
        Factura factura = new Factura();
        factura.setCompra(compra);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setEstado("EMITIDA");

        // Crear detalles de venta a partir de los productos de la compra
        List<DetalleVenta> detalles = new ArrayList<>();
        double totalFactura = 0.0;

        for (Producto producto : compra.getProductos()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setFactura(factura);
            detalle.setProducto(producto);
            detalle.setCantidad(1); // Puedes adaptar si manejas cantidades por producto
            detalle.calcularSubtotal();
            totalFactura += detalle.getSubtotal();
            detalles.add(detalle);
        }

        factura.setTotal(totalFactura);
        factura.setDetalles(detalles);

        // Guardar factura con detalles (CascadeType.ALL se encarga de los detalles)
        Factura facturaGuardada = facturaRepository.save(factura);

        // Crear o actualizar reporte de ventas
        LocalDate hoy = facturaGuardada.getFechaEmision().toLocalDate();

        ReporteVentas reporte = reporteVentasRepository.findByFecha(hoy)
            .orElseGet(() -> new ReporteVentas(null, hoy, 0.0, 0, "Reporte generado automáticamente desde facturas."));

        reporte.setTotalFacturas(reporte.getTotalFacturas() + 1);
        reporte.setTotalVentas(reporte.getTotalVentas() + facturaGuardada.getTotal());

        reporteVentasRepository.save(reporte);

        return facturaGuardada;
    }
}

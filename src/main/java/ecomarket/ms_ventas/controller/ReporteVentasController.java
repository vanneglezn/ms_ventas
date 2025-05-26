package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.ReporteVentas;
import ecomarket.ms_ventas.model.Factura;
import ecomarket.ms_ventas.repository.FacturaRepository;
import ecomarket.ms_ventas.repository.ReporteVentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteVentasController {

    @Autowired
    private ReporteVentasRepository reporteVentasRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    // 🔹 Ver todos los reportes de ventas generados
    @GetMapping
    public List<ReporteVentas> listarReportes() {
        return reporteVentasRepository.findAll();
    }

    // 🔹 Crear un reporte manualmente (opcional)
    @PostMapping
    public ReporteVentas crearReporte(@RequestBody ReporteVentas reporte) {
        return reporteVentasRepository.save(reporte);
    }

    // 🔹 Obtener un reporte por su ID
    @GetMapping("/{id}")
    public ReporteVentas obtenerReporte(@PathVariable Long id) {
        return reporteVentasRepository.findById(id).orElse(null);
    }

    // 🔹 Eliminar un reporte por su ID
    @DeleteMapping("/{id}")
    public void eliminarReporte(@PathVariable Long id) {
        reporteVentasRepository.deleteById(id);
    }

    @GetMapping("/generar")
public ReporteVentas generarReporte() {
    List<Factura> facturas = facturaRepository.findAll();

    double totalVentas = facturas.stream().mapToDouble(Factura::getTotal).sum();
    int totalFacturas = facturas.size();

    ReporteVentas nuevoReporte = new ReporteVentas();
    nuevoReporte.setFecha(LocalDate.now()); // ✅ NUEVO
    nuevoReporte.setTotalVentas(totalVentas);
    nuevoReporte.setTotalFacturas(totalFacturas); // ✅ NUEVO
    nuevoReporte.setObservaciones("Reporte generado automáticamente desde facturas.");

    return reporteVentasRepository.save(nuevoReporte);
}

}

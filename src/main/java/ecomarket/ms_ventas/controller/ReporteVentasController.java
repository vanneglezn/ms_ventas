package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.ReporteVentas;
import ecomarket.ms_ventas.model.Factura;
import ecomarket.ms_ventas.repository.FacturaRepository;
import ecomarket.ms_ventas.repository.ReporteVentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteVentasController {

    @Autowired
    private ReporteVentasRepository reporteVentasRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @GetMapping
    public List<ReporteVentas> listarReportes() {
        return reporteVentasRepository.findAll();
    }

    @PostMapping
    public ReporteVentas crearReporte(@RequestBody ReporteVentas reporte) {
        return reporteVentasRepository.save(reporte);
    }

    @GetMapping("/{id}")
    public ReporteVentas obtenerReporte(@PathVariable Long id) {
        return reporteVentasRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarReporte(@PathVariable Long id) {
        reporteVentasRepository.deleteById(id);
    }

    @GetMapping("/test")
    public String testReporteController() {
        return "ReporteVentasController funcionando correctamente.";
    }

    @GetMapping("/generar")
    public ReporteVentas generarReporte() {
        List<Factura> facturas = facturaRepository.findAll();

        double totalVentas = facturas.stream().mapToDouble(Factura::getTotal).sum();
        int cantidadTransacciones = facturas.size();

        ReporteVentas nuevoReporte = new ReporteVentas();
        nuevoReporte.setFechaGeneracion(LocalDateTime.now());
        nuevoReporte.setTotalVentas(totalVentas);
        nuevoReporte.setCantidadTransacciones(cantidadTransacciones);
        nuevoReporte.setObservaciones("Reporte generado automáticamente desde facturas.");

        return reporteVentasRepository.save(nuevoReporte);
    }
}

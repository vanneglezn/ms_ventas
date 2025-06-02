package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.ReporteVentas;
import ecomarket.ms_ventas.model.Factura;
import ecomarket.ms_ventas.repository.FacturaRepository; // Mantener si el servicio no se usa aquí
import ecomarket.ms_ventas.repository.ReporteVentasRepository; // Mantener si el servicio no se usa aquí
// import ecomarket.ms_ventas.service.ReporteVentasService; // Idealmente usarías un servicio

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para ResponseEntity
import org.springframework.http.ResponseEntity; // Para ResponseEntity
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.server.ResponseStatusException; // Si usas servicio y excepciones

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Para findById

@RestController
@RequestMapping("/reportes")
public class ReporteVentasController {

    @Autowired
    private ReporteVentasRepository reporteVentasRepository;

    @Autowired
    private FacturaRepository facturaRepository; 
    // Idealmente:
    // @Autowired
    // private ReporteVentasService reporteVentasService;

    @GetMapping
    public ResponseEntity<List<ReporteVentas>> listarReportes() {
        // Lógica ideal: List<ReporteVentas> reportes = reporteVentasService.listarTodosReportes();
        List<ReporteVentas> reportes = reporteVentasRepository.findAll();
        if (reportes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reportes);
    }

    @PostMapping
    public ResponseEntity<ReporteVentas> crearReporte(@RequestBody ReporteVentas reporte) {
        // Lógica ideal: ReporteVentas reporteCreado = reporteVentasService.crearReporteManual(reporte);
        ReporteVentas reporteCreado = reporteVentasRepository.save(reporte);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteCreado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteVentas> obtenerReporte(@PathVariable Long id) {
        // Lógica ideal: Optional<ReporteVentas> reporteOpt = reporteVentasService.obtenerReportePorId(id);
        Optional<ReporteVentas> reporteOpt = reporteVentasRepository.findById(id);
        return reporteOpt.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        // Lógica ideal: reporteVentasService.eliminarReporte(id);
        if (reporteVentasRepository.existsById(id)) {
            reporteVentasRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/generar") // Considera cambiar a POST si esto crea/modifica un recurso
    public ResponseEntity<ReporteVentas> generarReporte() {
        // Esta lógica debería estar en ReporteVentasService.generarReporteDiarioHoy()
        List<Factura> facturas = facturaRepository.findAll();

        // --- CORRECCIÓN AQUÍ ---
        // Antes: double totalVentas = facturas.stream().mapToDouble(Factura::getTotal).sum();
        // Ahora: Usa el método correcto que devuelve el total final con IVA
        double totalVentas = facturas.stream().mapToDouble(Factura::getTotalConIva).sum(); 
        // --- FIN CORRECCIÓN ---

        int totalFacturas = facturas.size();

        ReporteVentas nuevoReporte = new ReporteVentas();
        // Asegúrate que ReporteVentas tenga un constructor sin argumentos si usas @Data
        // o que los setters existan y Lombok esté funcionando.
        // El error en la imagen de tu log anterior también mencionaba problemas con el constructor de ReporteVentas.
        // Si ReporteVentas tiene @Data @NoArgsConstructor @AllArgsConstructor, esto debería estar bien.
        nuevoReporte.setFecha(LocalDate.now());
        nuevoReporte.setTotalVentas(totalVentas);
        nuevoReporte.setTotalFacturas(totalFacturas);
        nuevoReporte.setObservaciones("Reporte generado automáticamente desde todas las facturas.");

        ReporteVentas reporteGuardado = reporteVentasRepository.save(nuevoReporte);
        return ResponseEntity.ok(reporteGuardado);
    }
}
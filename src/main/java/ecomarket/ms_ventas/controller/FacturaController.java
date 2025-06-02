package ecomarket.ms_ventas.controller;

// import ecomarket.ms_ventas.model.Factura; // Ya no se usa directamente para el tipo de retorno
import ecomarket.ms_ventas.dto.FacturaResponseDTO; // <<< IMPORTAR EL DTO
import ecomarket.ms_ventas.service.VentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
// import java.util.stream.Collectors; // No es necesario aquí si el servicio ya devuelve DTOs

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/{facturaId}")
    public ResponseEntity<FacturaResponseDTO> obtenerFacturaPorId(@PathVariable Long facturaId) {
        try {
            FacturaResponseDTO facturaDTO = ventaService.obtenerFacturaPorSuId(facturaId);
            return ResponseEntity.ok(facturaDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error obteniendo factura: " + e.getMessage(), e);
        }
    }

    @GetMapping("/por-venta/{ventaId}")
    public ResponseEntity<FacturaResponseDTO> obtenerFacturaPorVentaId(@PathVariable Long ventaId) {
        try {
            FacturaResponseDTO facturaDTO = ventaService.obtenerFacturaPorVentaId(ventaId);
            return ResponseEntity.ok(facturaDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error obteniendo factura por ID de venta: " + e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<FacturaResponseDTO>> listarTodasLasFacturas() {
        try {
            List<FacturaResponseDTO> facturasDTO = ventaService.listarTodasLasFacturas();
            if (facturasDTO.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(facturasDTO);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error listando facturas: " + e.getMessage(), e);
        }
    }

    @GetMapping("/test")
    public String testFacturaController() {
        return "✅ FacturaController (para consultas) funcionando.";
    }
}
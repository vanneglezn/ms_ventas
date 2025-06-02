package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.dto.VentaRequestDTO;
import ecomarket.ms_ventas.dto.VentaResponseDTO; // Asegúrate de crear este DTO
import ecomarket.ms_ventas.model.Venta;
import ecomarket.ms_ventas.service.VentaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID; // Importar UUID
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Listar todas las ventas
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        List<Venta> ventas = ventaService.listarTodasLasVentas();
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<VentaResponseDTO> ventasDTO = ventas.stream()
                                             .map(VentaResponseDTO::fromEntity)
                                             .collect(Collectors.toList());
        return ResponseEntity.ok(ventasDTO);
    }

    // Crear una nueva venta
    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO ventaRequestDTO) {
        try {
            Venta nuevaVenta = ventaService.crearVenta(ventaRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(VentaResponseDTO.fromEntity(nuevaVenta));
        } catch (RuntimeException e) {
            // Considera un @ControllerAdvice para manejo global de excepciones
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // Obtener una venta por su ID (el ID de la Venta sigue siendo Long)
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.obtenerVentaPorId(id);
        if (venta != null) {
            return ResponseEntity.ok(VentaResponseDTO.fromEntity(venta));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener ventas por ID de cliente (clienteId ahora es UUID)
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorCliente(@PathVariable UUID clienteId) {
        List<Venta> ventas = ventaService.obtenerVentasPorClienteId(clienteId); // VentaService debe aceptar UUID
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<VentaResponseDTO> ventasDTO = ventas.stream()
                                             .map(VentaResponseDTO::fromEntity)
                                             .collect(Collectors.toList());
        return ResponseEntity.ok(ventasDTO);
    }

    // Actualizar el estado de una venta
    @PutMapping("/{id}/estado")
    public ResponseEntity<VentaResponseDTO> actualizarEstadoVenta(@PathVariable Long id, @RequestBody String nuevoEstado) {
        // En una aplicación real, el nuevoEstado podría venir en un DTO más estructurado
        // ej. UpdateVentaEstadoDTO con validaciones.
        try {
            Venta ventaActualizada = ventaService.actualizarEstadoVenta(id, nuevoEstado);
            return ResponseEntity.ok(VentaResponseDTO.fromEntity(ventaActualizada));
        } catch (RuntimeException e) { // Ej. VentaNoEncontradaException
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    // Eliminar una venta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // Ej. VentaNoEncontradaException
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/test")
    public String testVentaController() {
        return "✅ VentaController funcionando correctamente.";
    }
}
package ecomarket.ms_ventas.service;

import ecomarket.ms_ventas.dto.FacturaResponseDTO; // <<< IMPORTANTE: Importar el DTO
import ecomarket.ms_ventas.dto.VentaRequestDTO;
import ecomarket.ms_ventas.model.Venta; // Los métodos de Venta pueden seguir devolviendo la entidad si quieres
import java.util.List;
import java.util.UUID;

public interface VentaService {

    // Métodos principales de Venta
    Venta crearVenta(VentaRequestDTO ventaRequestDTO);
    List<Venta> listarTodasLasVentas();
    Venta obtenerVentaPorId(Long id);
    List<Venta> obtenerVentasPorClienteId(UUID clienteId);
    Venta actualizarEstadoVenta(Long id, String nuevoEstado);
    void eliminarVenta(Long id);

    // --- MÉTODOS PARA GESTIONAR/CONSULTAR FACTURAS ---
    // Deben declarar que devuelven DTOs
    FacturaResponseDTO obtenerFacturaPorSuId(Long facturaId);
    FacturaResponseDTO obtenerFacturaPorVentaId(Long ventaId);
    List<FacturaResponseDTO> listarTodasLasFacturas();
}
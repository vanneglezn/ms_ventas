package ecomarket.ms_ventas.dto;

import ecomarket.ms_ventas.model.Venta; // Necesario para el método fromEntity
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class VentaResponseDTO {
    private Long id;
    private UUID clienteId;
    private UUID empleadoId;
    private LocalDateTime fecha;
    private String estado;
    private double totalSinDescuento;
    private double montoDescuento;
    private String codigoPromocionAplicado;
    private double total;
    private List<DetalleVentaDTO> detalles; // Usar un DTO para los detalles también

    // Método de fábrica para convertir una entidad Venta a VentaResponseDTO
    public static VentaResponseDTO fromEntity(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(venta.getId());
        dto.setClienteId(venta.getClienteId());
        dto.setEmpleadoId(venta.getEmpleadoId());
        dto.setFecha(venta.getFecha());
        dto.setEstado(venta.getEstado());
        dto.setTotalSinDescuento(venta.getTotalSinDescuento());
        dto.setMontoDescuento(venta.getMontoDescuento());
        dto.setCodigoPromocionAplicado(venta.getCodigoPromocionAplicado());
        dto.setTotal(venta.getTotal());
        if (venta.getDetalles() != null) {
            dto.setDetalles(venta.getDetalles().stream()
                                .map(DetalleVentaDTO::fromEntity) // Necesitarías DetalleVentaDTO
                                .collect(Collectors.toList()));
        }
        return dto;
    }
}
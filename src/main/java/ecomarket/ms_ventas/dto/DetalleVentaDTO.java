package ecomarket.ms_ventas.dto;

import ecomarket.ms_ventas.model.DetalleVenta;
import lombok.Data;

@Data
public class DetalleVentaDTO {
    private Long id;
    private Long productoId;
    private int cantidad;
    private double precioUnitarioAlMomentoDeVenta;
    private String nombreProductoAlMomentoDeVenta;
    private double subtotal;

    public static DetalleVentaDTO fromEntity(DetalleVenta detalle) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        // ... (asignar campos desde detalle a dto) ...
        dto.setId(detalle.getId());
        dto.setProductoId(detalle.getProductoId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitarioAlMomentoDeVenta(detalle.getPrecioUnitarioAlMomentoDeVenta());
        dto.setNombreProductoAlMomentoDeVenta(detalle.getNombreProductoAlMomentoDeVenta());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }
}
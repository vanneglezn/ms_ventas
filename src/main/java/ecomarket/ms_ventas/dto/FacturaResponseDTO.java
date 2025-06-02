// En dto/FacturaResponseDTO.java
package ecomarket.ms_ventas.dto;

import ecomarket.ms_ventas.model.Factura;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponseDTO {
    private Long id;
    private Long ventaId; // O UUID si Venta.id es UUID (pero Venta.id es Long actualmente)
    private LocalDateTime fechaEmision;
    private String estado;
    private double subtotalAntesIva;
    private double montoIva;
    private double totalConIva;

    public static FacturaResponseDTO fromEntity(Factura factura) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setId(factura.getId());
        if (factura.getVenta() != null) {
            dto.setVentaId(factura.getVenta().getId()); // Obtiene el ID de la venta aquí
        }
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setEstado(factura.getEstado());
        dto.setSubtotalAntesIva(factura.getSubtotalAntesIva());
        dto.setMontoIva(factura.getMontoIva());
        dto.setTotalConIva(factura.getTotalConIva());
        return dto;
    }
}
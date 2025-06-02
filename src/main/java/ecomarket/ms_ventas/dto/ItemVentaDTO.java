package ecomarket.ms_ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVentaDTO {

    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Long productoId; // Solo el ID del producto que se quiere comprar

    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private int cantidad;    // Cuántos de ese producto
}
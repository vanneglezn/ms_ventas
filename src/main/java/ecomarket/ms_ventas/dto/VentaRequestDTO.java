package ecomarket.ms_ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo.")
    private UUID clienteId;

    private UUID empleadoId; // Opcional

    @NotEmpty(message = "La lista de ítems no puede estar vacía.")
    @Valid
    private List<ItemVentaDTO> items;

    private String codigoPromocion; // Asegúrate de que esto esté presente si tu VentaServiceImpl lo usa
}
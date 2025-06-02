package ecomarket.ms_ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemStockUpdateDTO {

    private Long productoId; // El ID del producto cuyo stock se va a actualizar

    private int cantidadVendida; // La cantidad que se vendió y que debe restarse del stock
                                 // El MS de Inventario se encargará de la lógica de resta.
}
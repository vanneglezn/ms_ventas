package ecomarket.ms_ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long id; // El ID del producto según el MS de Inventario

    private String nombre; // El nombre del producto

    private double precio; // El precio actual del producto

    private int stock; // El stock actual del producto (opcional, pero puede ser útil
                       // para que VentaService haga una validación previa si lo desea,
                       // aunque la fuente de verdad del stock es el MS Inventario)
}
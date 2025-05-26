package ecomarket.ms_ventas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private double precio;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private int cantidad;


    // Método auxiliar opcional
    public void actualizarPrecio(double nuevoPrecio) {
        this.precio = nuevoPrecio;
    }
}

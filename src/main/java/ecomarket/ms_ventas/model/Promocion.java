// En ecomarket.ms_ventas.model.Promocion.java
package ecomarket.ms_ventas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Opcional: si el código debe ser único
    @NotBlank(message = "El código de la promoción no puede estar vacío.") // Si el código es un campo nuevo
    private String codigoPromocion; // NUEVO CAMPO para el código de descuento que ingresa el usuario

    @NotBlank
    private String descripcion;

    @Min(1)
    @Max(100)
    private int porcentajeDescuento;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Column(name = "producto_id") // Sigue siendo Long como lo teníamos
    private Long productoId; // Si la promoción aplica a un producto específico
}
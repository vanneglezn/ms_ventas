package ecomarket.ms_ventas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    @JsonBackReference // 
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    private double subtotal;

    public void calcularSubtotal() {
        if (producto != null) {
            this.subtotal = producto.getPrecio() * cantidad;
        }
    }
}

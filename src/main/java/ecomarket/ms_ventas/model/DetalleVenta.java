package ecomarket.ms_ventas.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // Para la relación con Venta
import jakarta.persistence.*;
import jakarta.validation.constraints.Min; // Para validaciones si es necesario
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_venta") // Especificar nombre de tabla es buena práctica
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false) // Columna FK que apunta a la tabla 'ventas'
    @JsonBackReference // Para gestionar la serialización bidireccional con Venta
    private Venta venta; // Referencia a la Venta a la que pertenece este detalle

    @Column(name = "producto_id", nullable = false)
    private Long productoId; // Solo el ID del producto

    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario_venta", nullable = false)
    private double precioUnitarioAlMomentoDeVenta; // Precio al que se vendió

    @Column(name = "nombre_producto_venta", length = 255, nullable = false)
    private String nombreProductoAlMomentoDeVenta; // Nombre del producto al momento de la venta

    @Column(nullable = false)
    private double subtotal;

    // El campo 'factura' se eliminó porque DetalleVenta pertenece a Venta,
    // y Factura se relaciona con Venta.

    // El método calcularSubtotal debe ser llamado después de setear cantidad y precioUnitarioAlMomentoDeVenta
    public void calcularSubtotal() {
        this.subtotal = this.precioUnitarioAlMomentoDeVenta * this.cantidad;
    }

    // Lombok (@Data) ya genera los getters y setters.
    // Si no usas Lombok, necesitarías definirlos manualmente.
}
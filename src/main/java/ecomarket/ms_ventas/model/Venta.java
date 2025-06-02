package ecomarket.ms_ventas.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID; // Importar UUID

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ventas") // Nombre de la tabla en la base de datos
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // El ID de la venta sigue siendo Long

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId; // Cambiado a UUID para alinearse con ms_usuarios

    @Column(name = "empleado_id") // Puede ser nullable si no siempre hay un empleado
    private UUID empleadoId; // Cambiado a UUID (asumiendo que los empleados también usan UUID en ms_usuarios)

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 50) // Puedes ajustar la longitud según los estados que manejes
    private String estado; // Ej: PROCESANDO, COMPLETADA, ERROR_NOTIFICANDO_INVENTARIO, etc.

    @Column(name = "total_sin_descuento")
    private double totalSinDescuento; // El total antes de aplicar cualquier promoción

    @Column(name = "monto_descuento")
    private double montoDescuento; // El monto exacto que se descontó

    @Column(name = "codigo_promocion_aplicado", length = 100, nullable = true)
    private String codigoPromocionAplicado; // El código de la promoción que se usó

    @Column(nullable = false)
    private double total; // El total final después de descuentos

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference // Para gestionar la serialización bidireccional con DetalleVenta
    private List<DetalleVenta> detalles;
}

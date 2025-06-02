package ecomarket.ms_ventas.model;

// No son necesarios estos imports si simplificamos
// import com.fasterxml.jackson.annotation.JsonIdentityInfo;
// import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Genera constructor con todos los argumentos (útil para pruebas)
@Entity
@Table(name = "facturas") // Es buena práctica definir explícitamente el nombre de la tabla
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // El nombre de la columna puede ser "id" o "id_factura", ambos son válidos.
    // Usar "id" es más común y simple si no hay ambigüedad.
    @Column(name = "id_factura") 
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // Una factura es para una única venta. LAZY es bueno para el rendimiento.
    @JoinColumn(name = "venta_id", nullable = false, unique = true) // FK a la Venta. 'unique = true' asegura que una venta solo tenga una factura.
    private Venta venta; // Correcto: Referencia a la Venta

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    // Campos que discutimos para el desglose del IVA (estos deberían estar aquí)
    @Column(name = "subtotal_antes_iva", nullable = false)
    private double subtotalAntesIva; 

    @Column(name = "monto_iva", nullable = false)
    private double montoIva; 

    @Column(name = "total_con_iva", nullable = false)
    private double totalConIva; // El total final de la factura (subtotalAntesIva + montoIva)
                                // Este es el campo que se usará para el ReporteVentas.
                                // El campo 'total' original fue reemplazado por estos tres para mayor claridad.

    @Column(length = 50, nullable = false) // El estado no debería ser nulo
    private String estado; // Ej: EMITIDA, PAGADA, ANULADA

    // El campo ELIMINADO de List<DetalleVenta> detalles es correcto.
    // Los detalles se consultan a través de la Venta asociada: factura.getVenta().getDetalles()
}
package ecomarket.ms_ventas.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoVenta empleado;

    private LocalDateTime fecha;
    private String estado;
    private double total;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "compra_producto",
        joinColumns = @JoinColumn(name = "compra_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;

    // ✅ Agregado manualmente para asegurar compatibilidad en compilación
    public EmpleadoVenta getEmpleado() {
        return this.empleado;
    }

    public void setEmpleado(EmpleadoVenta empleado) {
        this.empleado = empleado;
    }
}

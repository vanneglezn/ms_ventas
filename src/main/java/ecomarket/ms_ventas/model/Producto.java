package ecomarket.ms_ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double precio;
    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    // Puedes agregar métodos adicionales si lo deseas, por ejemplo:
    public void actualizarPrecio(double nuevoPrecio) {
        this.precio = nuevoPrecio;
    }
}

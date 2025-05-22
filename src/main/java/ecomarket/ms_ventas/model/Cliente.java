package ecomarket.ms_ventas.model;

import jakarta.persistence.*;
import lombok.*;

@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los campos
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String direccion;
    private String telefono;
}

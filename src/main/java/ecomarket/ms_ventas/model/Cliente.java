package ecomarket.ms_ventas.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nombre;

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    @Size(min = 5, max = 200)
    private String direccion;

    @NotBlank
    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$")
    private String telefono;
}

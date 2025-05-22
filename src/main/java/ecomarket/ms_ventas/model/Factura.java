package ecomarket.ms_ventas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    @OneToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    private LocalDateTime fechaEmision;
    private double total;
    private String estado;
}

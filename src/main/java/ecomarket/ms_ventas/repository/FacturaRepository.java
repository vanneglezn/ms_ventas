package ecomarket.ms_ventas.repository;

import ecomarket.ms_ventas.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
}

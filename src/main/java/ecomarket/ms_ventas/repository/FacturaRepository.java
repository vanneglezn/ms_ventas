package ecomarket.ms_ventas.repository;

import ecomarket.ms_ventas.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // Asegúrate de importar Optional

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Verifica si ya existe una factura para una Venta específica (por el ID de la Venta)
    boolean existsByVentaId(Long ventaId);

    // Busca una factura opcional por el ID de la Venta asociada
    Optional<Factura> findByVentaId(Long ventaId);
}
package ecomarket.ms_ventas.repository;

import ecomarket.ms_ventas.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // ✅ Verifica si ya existe una factura para una compra específica
    boolean existsByCompraId(Long compraId);
}

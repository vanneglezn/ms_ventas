package ecomarket.ms_ventas.repository;

import ecomarket.ms_ventas.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // Importar Optional

public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    List<Promocion> findByProductoId(Long productoId);
    Optional<Promocion> findByCodigoPromocion(String codigoPromocion); // NUEVO MÉTODO
}
package ecomarket.ms_ventas.repository;

import ecomarket.ms_ventas.model.ReporteVentas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteVentasRepository extends JpaRepository<ReporteVentas, Long> {
}

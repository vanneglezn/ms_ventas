package ecomarket.ms_ventas.repository;

import ecomarket.ms_ventas.model.ReporteVentas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReporteVentasRepository extends JpaRepository<ReporteVentas, Long> {

    // 🔹 Método para buscar un reporte por fecha
    Optional<ReporteVentas> findByFecha(LocalDate fecha);
}

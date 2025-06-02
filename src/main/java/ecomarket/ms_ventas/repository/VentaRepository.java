package ecomarket.ms_ventas.repository;

import ecomarket.ms_ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> { // <-- ¡ASEGÚRATE DE ESTA LÍNEA!
    List<Venta> findByClienteId(UUID clienteId);
}
package ecomarket.ms_ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecomarket.ms_ventas.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}

package ecomarket.ms_ventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecomarket.ms_ventas.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // 🔹 Este método es necesario para buscar por cliente
    List<Compra> findByClienteId(Long clienteId);
}

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ecomarket.ms_ventas.model.Compra;
import ecomarket.ms_ventas.repository.CompraRepository;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    // Método nuevo para obtener compras por ID de cliente
    public List<Compra> obtenerComprasPorCliente(Long clienteId) {
        return compraRepository.findByClienteId(clienteId);
    }

    // (Aquí irían otros métodos como guardar compra, obtener todas, etc.)
}

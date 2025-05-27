package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Cliente;
import ecomarket.ms_ventas.model.Compra;
import ecomarket.ms_ventas.model.Producto;
import ecomarket.ms_ventas.repository.ClienteRepository;
import ecomarket.ms_ventas.repository.CompraRepository;
import ecomarket.ms_ventas.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // 🔹 Listar todas las compras
    @GetMapping
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    // 🔹 Crear una nueva compra con validaciones y promoción automática
    @PostMapping
public Compra crearCompra(@RequestBody Compra compra) {
    // Validar que el cliente exista
    Long clienteId = compra.getCliente().getId();
    Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
    if (clienteOpt.isEmpty()) {
        throw new RuntimeException("El cliente con ID " + clienteId + " no existe.");
    }

    // Reasociar productos existentes (por ID)
    List<Producto> productosExistentes = compra.getProductos().stream()
        .map(p -> productoRepository.findById(p.getId())
            .orElseThrow(() -> new RuntimeException("El producto con ID " + p.getId() + " no existe.")))
        .collect(Collectors.toList());

    compra.setCliente(clienteOpt.get());
    compra.setProductos(productosExistentes);

    // Calcular el total de la compra
    double total = productosExistentes.stream()
        .mapToDouble(Producto::getPrecio)
        .sum();

    // Aplicar promoción si el total supera $20.000
    if (total >= 20000) {
        total *= 0.90; // Descuento del 10%
        compra.setEstado("CON DESCUENTO");
    } else {
        compra.setEstado("NORMAL");
    }

    compra.setTotal(total);

    return compraRepository.save(compra);
}


    // 🔹 Obtener una compra por ID
    @GetMapping("/{id}")
    public Compra obtenerCompra(@PathVariable Long id) {
        return compraRepository.findById(id).orElse(null);
    }

    // 🔹 Actualizar una compra existente
    @PutMapping("/{id}")
    public Compra actualizarCompra(@PathVariable Long id, @Valid @RequestBody Compra compraActualizada) {
        return compraRepository.findById(id).map(compra -> {
            compra.setFecha(compraActualizada.getFecha());
            compra.setTotal(compraActualizada.getTotal());
            compra.setEstado(compraActualizada.getEstado());

            // Validar cliente
            Cliente nuevoCliente = clienteRepository.findById(compraActualizada.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            compra.setCliente(nuevoCliente);

            // Validar productos
            List<Producto> productosValidados = compraActualizada.getProductos().stream()
                .map(p -> productoRepository.findById(p.getId())
                    .orElseThrow(() -> new RuntimeException("Producto con ID " + p.getId() + " no encontrado")))
                .collect(Collectors.toList());

            compra.setProductos(productosValidados);

            return compraRepository.save(compra);
        }).orElse(null);
    }

    // 🔹 Eliminar una compra por ID
    @DeleteMapping("/{id}")
    public void eliminarCompra(@PathVariable Long id) {
        compraRepository.deleteById(id);
    }

    // 🔹 Test de funcionamiento
    @GetMapping("/test")
    public String testCompraController() {
        return "✅ CompraController funcionando correctamente.";
    }

    // 🔹 Obtener historial de compras por ID de cliente
    @GetMapping("/cliente/{clienteId}")
    public List<Compra> obtenerComprasPorCliente(@PathVariable Long clienteId) {
        return compraRepository.findByClienteId(clienteId);
    }
} 
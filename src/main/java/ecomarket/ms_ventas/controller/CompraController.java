package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Cliente;
import ecomarket.ms_ventas.model.Compra;
import ecomarket.ms_ventas.model.Producto;
import ecomarket.ms_ventas.model.EmpleadoVenta;
import ecomarket.ms_ventas.repository.ClienteRepository;
import ecomarket.ms_ventas.repository.CompraRepository;
import ecomarket.ms_ventas.repository.ProductoRepository;
import ecomarket.ms_ventas.repository.EmpleadoVentaRepository;
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

    @Autowired
    private EmpleadoVentaRepository empleadoVentaRepository;

    // 🔹 Listar todas las compras
    @GetMapping
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    // 🔹 Crear una nueva compra
    @PostMapping
    public Compra crearCompra(@RequestBody Compra compra) {
        // Validar cliente
        Long clienteId = compra.getCliente().getId();
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente con ID " + clienteId + " no existe."));

        // Validar empleado
        Long empleadoId = compra.getEmpleado().getId();
        EmpleadoVenta empleado = empleadoVentaRepository.findById(empleadoId)
            .orElseThrow(() -> new RuntimeException("Empleado con ID " + empleadoId + " no existe."));

        // Validar productos
        List<Producto> productos = compra.getProductos().stream()
            .map(p -> productoRepository.findById(p.getId())
                .orElseThrow(() -> new RuntimeException("Producto con ID " + p.getId() + " no existe.")))
            .collect(Collectors.toList());

        // Asociaciones
        compra.setCliente(cliente);
        compra.setEmpleado(empleado);
        compra.setProductos(productos);

        // Calcular total
        double total = productos.stream().mapToDouble(Producto::getPrecio).sum();

        if (total >= 20000) {
            total *= 0.9;
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

    // 🔹 Actualizar una compra
    @PutMapping("/{id}")
    public Compra actualizarCompra(@PathVariable Long id, @Valid @RequestBody Compra compraActualizada) {
        return compraRepository.findById(id).map(compra -> {
            compra.setFecha(compraActualizada.getFecha());
            compra.setTotal(compraActualizada.getTotal());
            compra.setEstado(compraActualizada.getEstado());

            Cliente nuevoCliente = clienteRepository.findById(compraActualizada.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            compra.setCliente(nuevoCliente);

            EmpleadoVenta nuevoEmpleado = empleadoVentaRepository.findById(compraActualizada.getEmpleado().getId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            compra.setEmpleado(nuevoEmpleado);

            List<Producto> productosValidados = compraActualizada.getProductos().stream()
                .map(p -> productoRepository.findById(p.getId())
                    .orElseThrow(() -> new RuntimeException("Producto con ID " + p.getId() + " no encontrado")))
                .collect(Collectors.toList());
            compra.setProductos(productosValidados);

            return compraRepository.save(compra);
        }).orElse(null);
    }

    // 🔹 Eliminar una compra
    @DeleteMapping("/{id}")
    public void eliminarCompra(@PathVariable Long id) {
        compraRepository.deleteById(id);
    }

    // 🔹 Test simple
    @GetMapping("/test")
    public String testCompraController() {
        return "✅ CompraController funcionando correctamente.";
    }

    // 🔹 Historial de compras por cliente
    @GetMapping("/cliente/{clienteId}")
    public List<Compra> obtenerComprasPorCliente(@PathVariable Long clienteId) {
        return compraRepository.findByClienteId(clienteId);
    }
}

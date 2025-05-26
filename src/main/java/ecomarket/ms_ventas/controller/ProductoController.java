package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Producto;
import ecomarket.ms_ventas.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // 🔹 Obtener todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // 🔹 Crear un nuevo producto
    @PostMapping
    public Producto crearProducto(@Valid @RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // 🔹 Obtener producto por ID
    @GetMapping("/{id}")
    public Producto obtenerProducto(@PathVariable Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // 🔹 Actualizar producto
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoActualizado.getNombre());
            producto.setCantidad(productoActualizado.getCantidad());
            producto.setPrecio(productoActualizado.getPrecio());
            
            return productoRepository.save(producto);
        }).orElse(null);
    }

    // 🔹 Eliminar producto
    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }

    // 🔹 Test de conexión
    @GetMapping("/test")
    public String testProductoController() {
        return "✅ ProductoController funcionando correctamente.";
    }
}

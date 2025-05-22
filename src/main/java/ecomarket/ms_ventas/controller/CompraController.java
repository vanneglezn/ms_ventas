package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Compra;
import ecomarket.ms_ventas.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @GetMapping
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    @PostMapping
    public Compra crearCompra(@RequestBody Compra compra) {
        return compraRepository.save(compra);
    }

    @GetMapping("/{id}")
    public Compra obtenerCompra(@PathVariable Long id) {
        return compraRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Compra actualizarCompra(@PathVariable Long id, @RequestBody Compra compraActualizada) {
        return compraRepository.findById(id).map(compra -> {
            compra.setFecha(compraActualizada.getFecha());
            compra.setTotal(compraActualizada.getTotal());
            compra.setEstado(compraActualizada.getEstado());
            compra.setCliente(compraActualizada.getCliente());
            compra.setProductos(compraActualizada.getProductos());
            return compraRepository.save(compra);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarCompra(@PathVariable Long id) {
        compraRepository.deleteById(id);
    }

    @GetMapping("/test")
    public String testCompraController() {
        return "CompraController funcionando correctamente.";
    }
}

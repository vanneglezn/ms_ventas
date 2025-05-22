package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.Cliente;
import ecomarket.ms_ventas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // 🔹 Endpoint de prueba
    @GetMapping("/test")
    public String test() {
        System.out.println("✅ Endpoint /clientes/test alcanzado.");
        return "✅ ClienteController funcionando correctamente.";
    }

    // 🔹 Obtener todos los clientes
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // 🔹 Crear un nuevo cliente
    @PostMapping
    public Cliente crearCliente(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // 🔹 Obtener un cliente por ID
    @GetMapping("/{id}")
    public Cliente obtenerCliente(@PathVariable Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // 🔹 Actualizar un cliente por ID
    @PutMapping("/{id}")
    public Cliente actualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(clienteActualizado.getNombre());
            cliente.setCorreo(clienteActualizado.getCorreo());
            cliente.setDireccion(clienteActualizado.getDireccion());
            cliente.setTelefono(clienteActualizado.getTelefono());
            return clienteRepository.save(cliente);
        }).orElse(null);
    }

    // 🔹 Eliminar un cliente por ID
    @DeleteMapping("/{id}")
    public void eliminarCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
    }
}

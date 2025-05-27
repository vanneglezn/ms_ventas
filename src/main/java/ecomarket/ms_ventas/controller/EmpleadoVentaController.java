package ecomarket.ms_ventas.controller;

import ecomarket.ms_ventas.model.EmpleadoVenta;
import ecomarket.ms_ventas.repository.EmpleadoVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleados")
public class EmpleadoVentaController {

    @Autowired
    private EmpleadoVentaRepository repository;

    @GetMapping
    public List<EmpleadoVenta> listar() {
        return repository.findAll();
    }

    @PostMapping
    public EmpleadoVenta crear(@RequestBody EmpleadoVenta empleado) {
        return repository.save(empleado);
    }
}

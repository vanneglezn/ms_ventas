package ecomarket.ms_ventas.service;

import org.springframework.stereotype.Service;

import ecomarket.ms_ventas.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final ClienteRepository clienteRepository;

    public long contarClientes() {
        return clienteRepository.count();
    }
}

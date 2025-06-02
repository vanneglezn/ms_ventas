package ecomarket.ms_ventas.service;


import ecomarket.ms_ventas.dto.FacturaResponseDTO;
import java.util.stream.Collectors; // Para convertir listas
import ecomarket.ms_ventas.dto.ItemVentaDTO;
import ecomarket.ms_ventas.dto.ProductoDTO;
import ecomarket.ms_ventas.dto.VentaRequestDTO;
import ecomarket.ms_ventas.model.*; // Venta, DetalleVenta, Factura, Promocion, ReporteVentas
import ecomarket.ms_ventas.repository.FacturaRepository;
import ecomarket.ms_ventas.repository.PromocionRepository;
import ecomarket.ms_ventas.repository.ReporteVentasRepository;
import ecomarket.ms_ventas.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate; // Necesario si no está en modo simulación
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VentaServiceImpl implements VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaServiceImpl.class);

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ReporteVentasRepository reporteVentasRepository;

    // Descomenta estas líneas cuando quieras hacer llamadas HTTP reales
    @Autowired(required = false) // Lo hacemos opcional para que no falle si está comentado para pruebas aisladas
    private RestTemplate restTemplate;

    @Value("${producto.service.url:#{null}}") // Valor por defecto null si no está en properties
    private String productoServiceUrl;


    @Override
    @Transactional
    public Venta crearVenta(VentaRequestDTO ventaRequest) {
        log.info("Iniciando creación de venta para cliente ID: {}. Código promo: {}",
                ventaRequest.getClienteId(), ventaRequest.getCodigoPromocion());

        Venta nuevaVenta = new Venta();
        nuevaVenta.setClienteId(ventaRequest.getClienteId());
        nuevaVenta.setEmpleadoId(ventaRequest.getEmpleadoId());
        nuevaVenta.setFecha(LocalDateTime.now());
        nuevaVenta.setEstado("PROCESANDO_DETALLES");

        List<DetalleVenta> detallesVenta = new ArrayList<>();
        double subtotalGeneral = 0.0;

        for (ItemVentaDTO itemDTO : ventaRequest.getItems()) {
            log.debug("Procesando item: productoId={}, cantidad={}", itemDTO.getProductoId(), itemDTO.getCantidad());
            
            ProductoDTO productoInfo = obtenerProductoDesdeMSProductos(itemDTO.getProductoId());
            
            if (productoInfo == null) {
                log.error("Producto no encontrado (real o simulado) para ID: {}", itemDTO.getProductoId());
                throw new RuntimeException("Producto no encontrado o servicio de productos no disponible para ID: " + itemDTO.getProductoId());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(nuevaVenta);
            detalle.setProductoId(itemDTO.getProductoId());
            detalle.setCantidad(itemDTO.getCantidad());
            detalle.setNombreProductoAlMomentoDeVenta(productoInfo.getNombre());
            detalle.setPrecioUnitarioAlMomentoDeVenta(productoInfo.getPrecio());
            detalle.calcularSubtotal();
            detallesVenta.add(detalle);
            subtotalGeneral += detalle.getSubtotal();
        }
        nuevaVenta.setDetalles(detallesVenta);

        // LÓGICA DE PROMOCIÓN
        double totalConDescuento = subtotalGeneral;
        double montoDescuentoAplicado = 0.0;
        String codigoPromocionAplicadoEnVenta = null;

        if (ventaRequest.getCodigoPromocion() != null && !ventaRequest.getCodigoPromocion().isEmpty()) {
            Optional<Promocion> optPromocion = promocionRepository.findByCodigoPromocion(ventaRequest.getCodigoPromocion());
            if (optPromocion.isPresent()) {
                Promocion promocionAplicable = optPromocion.get();
                LocalDate hoy = LocalDate.now();
                if (!hoy.isBefore(promocionAplicable.getFechaInicio()) && !hoy.isAfter(promocionAplicable.getFechaFin())) {
                    if (promocionAplicable.getProductoId() != null) {
                        boolean aplicaAProducto = false;
                        for(DetalleVenta detalle : detallesVenta) {
                            if(detalle.getProductoId().equals(promocionAplicable.getProductoId())) {
                                aplicaAProducto = true;
                                break;
                            }
                        }
                        if(aplicaAProducto) {
                            montoDescuentoAplicado = subtotalGeneral * (promocionAplicable.getPorcentajeDescuento() / 100.0);
                        } else {
                             log.info("Promoción {} es para producto ID {} pero no se encontró en la venta.", ventaRequest.getCodigoPromocion(), promocionAplicable.getProductoId());
                        }
                    } else { 
                        montoDescuentoAplicado = subtotalGeneral * (promocionAplicable.getPorcentajeDescuento() / 100.0);
                    }
                    if (montoDescuentoAplicado > 0) {
                        totalConDescuento = subtotalGeneral - montoDescuentoAplicado;
                        codigoPromocionAplicadoEnVenta = ventaRequest.getCodigoPromocion();
                        log.info("Promoción {} aplicada ({}%). Descuento: {}", codigoPromocionAplicadoEnVenta, promocionAplicable.getPorcentajeDescuento(), montoDescuentoAplicado);
                    }
                } else {
                     log.warn("Promoción {} encontrada pero no activa.", ventaRequest.getCodigoPromocion());
                }
            } else {
                 log.warn("Promoción {} no encontrada.", ventaRequest.getCodigoPromocion());
            }
        }
        
        nuevaVenta.setTotalSinDescuento(subtotalGeneral);
        nuevaVenta.setMontoDescuento(montoDescuentoAplicado);
        nuevaVenta.setTotal(totalConDescuento); // Este es el total de la Venta (después de promociones)
        nuevaVenta.setCodigoPromocionAplicado(codigoPromocionAplicadoEnVenta);
        
        nuevaVenta.setEstado("PENDIENTE_FACTURACION");
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);
        log.info("Venta ID: {} guardada localmente. Total (post-promoción): {}", ventaGuardada.getId(), ventaGuardada.getTotal());

        try {
            Factura facturaGenerada = generarFacturaParaVentaInternamente(ventaGuardada);
            log.info("Factura ID: {} generada para Venta ID: {}. Total Factura (con IVA): {}", 
                facturaGenerada.getId(), ventaGuardada.getId(), facturaGenerada.getTotalConIva());
            ventaGuardada.setEstado("COMPLETADA_Y_FACTURADA");
        } catch (Exception e) {
            log.error("Error al generar factura para Venta ID {}: {}. La venta se guardó pero la factura no.", ventaGuardada.getId(), e.getMessage(), e);
            ventaGuardada.setEstado("ERROR_GENERANDO_FACTURA");
        }
        
        return ventaRepository.save(ventaGuardada);
    }

    @Transactional
    private Factura generarFacturaParaVentaInternamente(Venta ventaGuardada) {
        log.info("Generación interna de factura para venta ID: {}", ventaGuardada.getId());
        if (facturaRepository.existsByVentaId(ventaGuardada.getId())) {
            log.warn("Intento de generar factura duplicada para venta ID: {}.", ventaGuardada.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una factura asociada a la venta con ID: " + ventaGuardada.getId());
        }

        Factura nuevaFactura = new Factura();
        nuevaFactura.setVenta(ventaGuardada);
        nuevaFactura.setFechaEmision(LocalDateTime.now());
        nuevaFactura.setEstado("EMITIDA");

        double subtotalVentaParaFactura = ventaGuardada.getTotal(); 
        double porcentajeIva = 0.19; 
        double montoDelIvaCalculado = subtotalVentaParaFactura * porcentajeIva;
        double totalFacturaConIvaCalculado = subtotalVentaParaFactura + montoDelIvaCalculado;

        montoDelIvaCalculado = Math.round(montoDelIvaCalculado * 100.0) / 100.0;
        totalFacturaConIvaCalculado = Math.round(totalFacturaConIvaCalculado * 100.0) / 100.0;

        nuevaFactura.setSubtotalAntesIva(subtotalVentaParaFactura);
        nuevaFactura.setMontoIva(montoDelIvaCalculado);
        nuevaFactura.setTotalConIva(totalFacturaConIvaCalculado);

        Factura facturaPersistida = facturaRepository.save(nuevaFactura);
        actualizarReporteVentas(facturaPersistida);
        return facturaPersistida;
    }

    private void actualizarReporteVentas(Factura factura) {
        LocalDate hoy = factura.getFechaEmision().toLocalDate();
        ReporteVentas reporte = reporteVentasRepository.findByFecha(hoy)
                .orElseGet(() -> new ReporteVentas(null, hoy, 0.0, 0, "Reporte generado automáticamente."));
        
        reporte.setTotalFacturas(reporte.getTotalFacturas() + 1);
        reporte.setTotalVentas(reporte.getTotalVentas() + factura.getTotalConIva()); 
        reporteVentasRepository.save(reporte);
        log.info("Reporte de ventas actualizado para fecha: {}. Total facturas: {}, Total ventas (con IVA): {}",
                hoy, reporte.getTotalFacturas(), reporte.getTotalVentas());
    }

    // Para pruebas aisladas, este método llama a simularObtencionProducto.
    // Para producción, debe hacer la llamada HTTP real.
    private ProductoDTO obtenerProductoDesdeMSProductos(Long productoId) {
        // Cambia entre simulación y llamada real según necesites para probar
        boolean MODO_PRUEBA_AISLADA = true; // CAMBIA ESTO A false PARA LLAMADAS REALES

        if (MODO_PRUEBA_AISLADA) {
            return simularObtencionProducto(productoId);
        } else {
            if (restTemplate == null || productoServiceUrl == null) {
                log.error("RestTemplate o productoServiceUrl no están configurados para llamada real a MS Productos.");
                throw new IllegalStateException("RestTemplate o productoServiceUrl no configurados.");
            }
            String url = productoServiceUrl + "/" + productoId;
            log.debug("Consultando producto ID {} en URL: {}", productoId, url);
            try {
                ResponseEntity<ProductoDTO> response = restTemplate.getForEntity(url, ProductoDTO.class);
                return response.getBody();
            } catch (HttpClientErrorException.NotFound ex) {
                log.warn("Producto ID {} no encontrado desde {}. URL: {}", productoId, url, ex.getStatusCode());
                return null;
            } catch (RestClientException ex) {
                log.error("Error al obtener producto {} desde {}. Error: {}", productoId, url, ex.getMessage(), ex);
                throw new RuntimeException("Error de comunicación con el servicio de productos al obtener detalles.", ex);
            }
        }
    }
    
    private ProductoDTO simularObtencionProducto(Long productoId) {
        log.debug("SIMULACIÓN: Obteniendo producto ID {} (sin llamada HTTP real)", productoId);
        if (productoId.equals(1L)) {
            return new ProductoDTO(1L, "Producto Simulado Alfa", 12.50, 100);
        } else if (productoId.equals(2L)) {
            return new ProductoDTO(2L, "Producto Simulado Beta", 20.00, 50);
        } else if (productoId.equals(3L)) {
            return new ProductoDTO(3L, "Producto Simulado Gamma", 7.75, 70);
        } else if (productoId.equals(999L)) {
            log.warn("SIMULACIÓN: Producto ID {} no encontrado en datos de prueba simulados.", productoId);
            return null; 
        }
        log.warn("SIMULACIÓN: ID de producto {} no predefinido para simulación, devolviendo nulo.", productoId);
        return null;
    }
    
    @Override
    public List<Venta> listarTodasLasVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<Venta> obtenerVentasPorClienteId(UUID clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public Venta actualizarEstadoVenta(Long id, String nuevoEstado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + id));
        venta.setEstado(nuevoEstado);
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void eliminarVenta(Long id) {
        log.info("Eliminando venta ID: {}", id);
        Optional<Factura> facturaAsociada = facturaRepository.findByVentaId(id);
        facturaAsociada.ifPresent(factura -> {
            log.info("Eliminando factura ID {} asociada a venta ID {}", factura.getId(), id);
            // Aquí podrías querer revertir el impacto en ReporteVentas si anulas una factura
            // antes de borrarla, o manejarlo de otra forma (ej. estado ANULADA en factura y reporte).
            // Por ahora, solo elimina la factura.
            // Si eliminas la factura, y el reporte suma el total de la factura,
            // el reporte se desactualizaría. Considera anular en lugar de borrar,
            // o ajustar el reporte aquí también.
            facturaRepository.delete(factura);
        });

        if (!ventaRepository.existsById(id)) {
            log.warn("Intento de eliminar venta no encontrada con ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada con ID: " + id);
        }
        ventaRepository.deleteById(id);
        log.info("Venta ID: {} eliminada.", id);
    }

@Override
public FacturaResponseDTO obtenerFacturaPorSuId(Long facturaId) {
    log.info("Obteniendo factura por su ID: {}", facturaId);
    Factura factura = facturaRepository.findById(facturaId)
            .orElseThrow(() -> {
                log.warn("Factura no encontrada con ID: {}", facturaId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada con ID: " + facturaId);
            });
    return FacturaResponseDTO.fromEntity(factura); // <<< CONVERTIR A DTO
}

@Override
public FacturaResponseDTO obtenerFacturaPorVentaId(Long ventaId) {
    log.info("Obteniendo factura para venta ID: {}", ventaId);
    Factura factura = facturaRepository.findByVentaId(ventaId) // Asegúrate que FacturaRepository tiene este método y devuelve Optional<Factura>
            .orElseThrow(() -> {
                log.warn("Factura no encontrada para venta ID: {}", ventaId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró factura para la venta con ID: " + ventaId);
            });
    return FacturaResponseDTO.fromEntity(factura); // <<< CONVERTIR A DTO
}

@Override
public List<FacturaResponseDTO> listarTodasLasFacturas() {
    log.info("Listando todas las facturas");
    List<Factura> facturas = facturaRepository.findAll();
    return facturas.stream()
                   .map(FacturaResponseDTO::fromEntity) // Convertir cada Factura a DTO
                   .collect(Collectors.toList());
}
}
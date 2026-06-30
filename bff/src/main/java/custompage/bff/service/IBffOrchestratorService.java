package custompage.bff.service;

import custompage.bff.dto.*;
import java.util.List;

public interface IBffOrchestratorService {
    AuthResponseDTO delegarLogin(AuthRequestDTO dto);
    DashboardTiendaDTO orquestarDashboardTienda(Long idEmpresa);
    OrdenBffDTO delegarCreacionOrden(OrdenBffDTO dto);
    OrdenBffDTO delegarPagoVenta(Long idOrden, String metodoPago);
    List<PromocionBffDTO> obtenerPromocionesMarketing();
    PromocionBffDTO validarCupon(String codigo);
    ProductoBffDTO crearOActualizarProducto(ProductoBffDTO dto);
    void eliminarProducto(Long idProducto);
}
package Controlador;

import Model.*;
import Persistencia.ProductoDAO;
import Persistencia.VentaDAO;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class VentaController {

    private Pedido pedidoActual;
    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    private final Clock clock;   // 👈 inyección de tiempo

    // Constructor principal para producción
    public VentaController(VentaDAO ventaDAO, ProductoDAO productoDAO, Clock clock) {
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
        this.clock = clock;
    }

    // Constructor por defecto (usa implementación real)
    public VentaController() {
        this(new VentaDAO(),
             new ProductoDAO("data/productos.txt"),
             Clock.systemDefaultZone());
    }

    public void iniciarPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {
        pedidoActual = new Pedido(codigoPedido, tipoPedido, numeroMesa);
    }

    public void agregarProductoAlPedido(Producto producto, int cantidad) {

        if (pedidoActual == null) {
            throw new IllegalStateException("No hay pedido activo");
        }

        producto.descontarStock(cantidad);
        pedidoActual.agregarProducto(producto, cantidad);
    }

    public Venta finalizarVenta(Cliente cliente, Mesa mesa, boolean paraLlevar) throws IOException {

        if (pedidoActual == null) {
            throw new IllegalStateException("No hay pedido para finalizar");
        }

        int numeroMesa = paraLlevar ? Venta.PARA_LLEVAR : mesa.getNumero();

        Venta venta = new Venta(
                "V-" + System.currentTimeMillis(),
                LocalDateTime.now(clock),
                numeroMesa
        );

        for (Producto p : pedidoActual.getProductos()) {
            int cantidad = pedidoActual.getCantidadDeProducto(p);
            venta.agregarLinea(p, cantidad);
        }

        ventaDAO.guardarVenta(venta);

        List<Producto> productos = productoDAO.cargar();
        productoDAO.guardar(productos);

        pedidoActual = null;

        return venta;   
    }

    public Pedido getPedidoActual() {
        return pedidoActual;
    }

    public void setPedidoActual(Pedido pedidoActual) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

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
    private final Clock clock;   

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

    if (producto == null) {
        throw new IllegalArgumentException("Producto no puede ser null");
    }

    if (cantidad <= 0) {
        throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
    }

    // Esto valida stock y lanza IllegalStateException si no alcanza
    producto.descontarStock(cantidad);

    // Esto suma cantidades si el producto ya existe
    pedidoActual.agregarProducto(producto, cantidad);
}


    public Venta finalizarVenta(Cliente cliente, Mesa mesa, boolean paraLlevar) throws IOException {

    if (pedidoActual == null) {
        throw new IllegalStateException("No hay pedido para finalizar");
    }

    if (!paraLlevar && mesa == null) {
        throw new IllegalArgumentException("Mesa requerida para finalizar venta");
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

    // Persistir productos (si tu DAO requiere guardar la lista real, luego lo ajustamos)
    List<Producto> productos = productoDAO.cargar();
    productoDAO.guardar(productos);

    pedidoActual = null;

    return venta;
}


    public Pedido getPedidoActual() {
        return pedidoActual;
    }

    public void setPedidoActual(Pedido pedidoActual) {
    this.pedidoActual = pedidoActual;
}

}

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

    public VentaController(VentaDAO ventaDAO, ProductoDAO productoDAO, Clock clock) {
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
        this.clock = clock;
    }

    public VentaController() {
        this(new VentaDAO(),
                new ProductoDAO("data/productos.txt"),
                Clock.systemDefaultZone());
    }

    public void iniciarPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {
        pedidoActual = new Pedido(codigoPedido, tipoPedido, numeroMesa);
    }

    public void agregarProductoAlPedido(Producto producto, int cantidad) {
        if (pedidoActual == null) throw new IllegalStateException("No hay pedido activo");
        if (producto == null) throw new IllegalArgumentException("Producto no puede ser null");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        producto.descontarStock(cantidad);
        pedidoActual.agregarProducto(producto, cantidad);
    }

    public Venta finalizarVenta(Cliente cliente, Mesa mesa, boolean paraLlevar) throws IOException {

    int numeroMesa = paraLlevar ? Venta.PARA_LLEVAR : mesa.getNumero();

    Venta venta = new Venta(
            "V-" + System.currentTimeMillis(),
            LocalDateTime.now(clock),
            numeroMesa,
            pedidoActual.getCodigoPedido(), // ✅ NUEVO
            Venta.DEFAULT_TAX_RATE
    );

    for (Producto p : pedidoActual.getProductos()) {
        int cantidad = pedidoActual.getCantidadDeProducto(p);
        venta.agregarLinea(p, cantidad);
    }

    ventaDAO.guardarVenta(venta);

    pedidoActual = null;
    return venta;
}
    
    public List<Venta> obtenerPendientes() throws IOException {
        return ventaDAO.listarPendientes();
    }

    public void marcarComoPagada(Venta venta, String metodoPago) throws IOException {
        venta.setEstado("PAGADA");
        venta.setMetodoPago(metodoPago);
        ventaDAO.actualizarVenta(venta);
    }

    public String generarTextoFactura(Venta venta) {
        StringBuilder sb = new StringBuilder();

        sb.append("CAFETERÍA UCR - SEDE DEL SUR\n");
        sb.append("----------------------------------\n");
        sb.append("ID: ").append(venta.getId()).append("\n");
        sb.append("FECHA: ").append(venta.getFechaHora().toLocalDate()).append("\n");
        sb.append("HORA: ").append(venta.getFechaHora().toLocalTime()).append("\n");

        if (venta.esParaLlevar()) sb.append("TIPO: PARA LLEVAR\n");
        else sb.append("MESA: ").append(venta.getMesaNumero()).append("\n");

        if (venta.getMetodoPago() != null && !venta.getMetodoPago().isBlank()) {
            sb.append("PAGO: ").append(venta.getMetodoPago()).append("\n");
        }

        sb.append("\nPRODUCTO               CANT   SUBT\n");
        sb.append("----------------------------------\n");

        try {
            List<Producto> productosReales = productoDAO.cargar();

            for (Venta.LineaVenta lv : venta.getLineas()) {
                String codigo = lv.getProducto().getCodigo();
                String nombre = codigo;

                for (Producto p : productosReales) {
                    if (p.getCodigo().equals(codigo)) {
                        nombre = p.getNombre();
                        break;
                    }
                }

                sb.append(String.format("%-20s %3d   ₡%.2f\n",
                        nombre, lv.getCantidad(), lv.getSubtotal()));
            }

        } catch (IOException e) {
            sb.append("Error cargando nombres de productos\n");
        }

        sb.append("\n----------------------------------\n");
        sb.append(String.format("SUBTOTAL: ₡%.2f\n", venta.getSubtotal()));
        sb.append(String.format("IMPUESTO: ₡%.2f\n", venta.getImpuesto()));
        sb.append(String.format("TOTAL: ₡%.2f\n", venta.getTotal()));
        sb.append("\n¡Gracias por su visita!");

        return sb.toString();
    }
    
public void eliminarPendientePorCodigoPedido(int codigoPedido) throws IOException {
    ventaDAO.eliminarPendientePorCodigoPedido(codigoPedido);
}

public void quitarProductoDelPedido(Producto producto, int cantidad) {
    if (producto == null) throw new IllegalArgumentException("Producto inválido.");
    if (cantidad <= 0) return;

    if (pedidoActual == null) {
        throw new IllegalStateException("No hay pedido activo.");
    }

    int actual = pedidoActual.getCantidadDeProducto(producto);
    if (actual <= 0) {
        throw new IllegalStateException("El producto no está en el pedido.");
    }
    if (cantidad > actual) {
        throw new IllegalStateException("No puedes quitar más de lo que hay en el pedido.");
    }

    // ✅ Quitar del pedido (resta cantidad o elimina)
    pedidoActual.quitarProducto(producto, cantidad);

}


    public Pedido getPedidoActual() { return pedidoActual; }
    public void setPedidoActual(Pedido pedidoActual) { this.pedidoActual = pedidoActual; }
}
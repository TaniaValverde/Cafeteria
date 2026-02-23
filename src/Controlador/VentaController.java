package Controlador;

import Model.*;
import Persistencia.ProductoDAO;
import Persistencia.VentaDAO;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for sales operations in the MVC architecture.
 *
 * Coordinates the sale workflow: order handling, persistence through {@link VentaDAO},
 * stock-related lookups through {@link ProductoDAO}, and invoice text generation.
 */
public class VentaController {

    /** Current active order being processed. */
    private Pedido pedidoActual;

    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    private final Clock clock;

    /**
     * Creates a controller with injected dependencies (useful for testing).
     *
     * @param ventaDAO DAO used for sales persistence
     * @param productoDAO DAO used to load products for invoice display
     * @param clock clock used to obtain the current date/time
     */
    public VentaController(VentaDAO ventaDAO, ProductoDAO productoDAO, Clock clock) {
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
        this.clock = clock;
    }

    /** Creates a controller with default DAOs and system clock. */
    public VentaController() {
        this(new VentaDAO(),
                new ProductoDAO("data/productos.txt"),
                Clock.systemDefaultZone());
    }

    /**
     * Starts a new active order.
     *
     * @param codigoPedido order code
     * @param tipoPedido order type (table or take-away)
     * @param numeroMesa table number when applicable
     */
    public void iniciarPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {
        pedidoActual = new Pedido(codigoPedido, tipoPedido, numeroMesa);
    }

    /**
     * Adds a product to the active order.
     *
     * @param producto product to add
     * @param cantidad quantity to add
     * @throws IllegalStateException if there is no active order
     * @throws IllegalArgumentException if product is null or quantity is invalid
     */
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

        producto.descontarStock(cantidad);
        pedidoActual.agregarProducto(producto, cantidad);
    }

    /**
     * Finalizes the current sale and persists it.
     *
     * @param cliente customer associated with the sale
     * @param mesa table associated with the sale (null when take-away)
     * @param paraLlevar true for take-away orders
     * @return persisted sale instance
     * @throws IOException if persistence fails
     */
    public Venta finalizarVenta(Cliente cliente, Mesa mesa, boolean paraLlevar) throws IOException {

        int numeroMesa = paraLlevar ? Venta.PARA_LLEVAR : mesa.getNumero();

        Venta venta = new Venta(
                "V-" + System.currentTimeMillis(),
                LocalDateTime.now(clock),
                numeroMesa,
                pedidoActual.getCodigoPedido(),
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

    /**
     * Loads all pending sales.
     *
     * @return list of pending sales
     * @throws IOException if loading fails
     */
    public List<Venta> obtenerPendientes() throws IOException {
        return ventaDAO.listarPendientes();
    }

    /**
     * Marks a sale as paid and persists the update.
     *
     * @param venta sale to update
     * @param metodoPago payment method
     * @throws IOException if persistence fails
     */
    public void marcarComoPagada(Venta venta, String metodoPago) throws IOException {
        venta.setEstado("PAGADA");
        venta.setMetodoPago(metodoPago);
        ventaDAO.actualizarVenta(venta);
    }

    /**
     * Builds the invoice text for a sale.
     *
     * @param venta sale to print
     * @return invoice text
     */
    public String generarTextoFactura(Venta venta) {
        StringBuilder sb = new StringBuilder();

        sb.append("CAFETERÍA UCR - SEDE DEL SUR\n");
        sb.append("----------------------------------\n");
        sb.append("ID: ").append(venta.getId()).append("\n");
        sb.append("FECHA: ").append(venta.getFechaHora().toLocalDate()).append("\n");
        sb.append("HORA: ").append(venta.getFechaHora().toLocalTime()).append("\n");

        if (venta.esParaLlevar()) {
            sb.append("TIPO: PARA LLEVAR\n");
        } else {
            sb.append("MESA: ").append(venta.getMesaNumero()).append("\n");
        }

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

    /**
     * Removes a pending sale by its order code.
     *
     * @param codigoPedido order code
     * @throws IOException if persistence fails
     */
    public void eliminarPendientePorCodigoPedido(int codigoPedido) throws IOException {
        ventaDAO.eliminarPendientePorCodigoPedido(codigoPedido);
    }

    /**
     * Removes a quantity of a product from the active order.
     *
     * @param producto product to remove
     * @param cantidad quantity to remove
     * @throws IllegalStateException if there is no active order or product is not present
     * @throws IllegalArgumentException if product is null
     */
    public void quitarProductoDelPedido(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto inválido.");
        }
        if (cantidad <= 0) {
            return;
        }

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

        pedidoActual.quitarProducto(producto, cantidad);
    }

    /**
     * Returns the current active order.
     *
     * @return active order (may be null)
     */
    public Pedido getPedidoActual() {
        return pedidoActual;
    }

    /**
     * Sets the active order manually (mainly for testing or UI integration).
     *
     * @param pedidoActual order to set
     */
    public void setPedidoActual(Pedido pedidoActual) {
        this.pedidoActual = pedidoActual;
    }

    /**
     * Builds the invoice text using a payment method preview (without modifying sale state).
     *
     * @param venta sale to print
     * @param metodoPagoPreview payment method to display
     * @return invoice text
     */
    public String generarTextoFactura(Venta venta, String metodoPagoPreview) {
        StringBuilder sb = new StringBuilder();

        sb.append("CAFETERÍA UCR - SEDE DEL SUR\n");
        sb.append("----------------------------------\n");
        sb.append("ID: ").append(venta.getId()).append("\n");
        sb.append("FECHA: ").append(venta.getFechaHora().toLocalDate()).append("\n");
        sb.append("HORA: ").append(venta.getFechaHora().toLocalTime()).append("\n");

        if (venta.esParaLlevar()) {
            sb.append("TIPO: PARA LLEVAR\n");
        } else {
            sb.append("MESA: ").append(venta.getMesaNumero()).append("\n");
        }

        if (metodoPagoPreview != null && !metodoPagoPreview.isBlank()) {
            sb.append("PAGO: ").append(metodoPagoPreview).append("\n");
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
}
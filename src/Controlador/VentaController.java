package Controlador;

import Model.*;
import Persistencia.ProductoDAO;
import Persistencia.VentaDAO;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller responsible for managing sales operations.
 *
 * This class coordinates the complete sales workflow, including
 * order creation, product management, stock updates, sale persistence
 * and invoice text generation.
 *
 * It acts as the intermediary between the model and persistence layers,
 * following the MVC architectural pattern.
 *
 * @author Project Team
 */
public class VentaController {

    /** Current active order being processed */
    private Pedido pedidoActual;

    /** Data access object for sales persistence */
    private final VentaDAO ventaDAO;

    /** Data access object for products */
    private final ProductoDAO productoDAO;

    /** Clock used to obtain the current date and time */
    private final Clock clock;

    /**
     * Creates a VentaController with explicit dependencies.
     *
     * This constructor allows dependency injection, making
     * the controller easier to test.
     *
     * @param ventaDAO DAO responsible for sales persistence
     * @param productoDAO DAO responsible for product persistence
     * @param clock Clock instance used for date and time
     */
    public VentaController(VentaDAO ventaDAO, ProductoDAO productoDAO, Clock clock) {
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
        this.clock = clock;
    }

    /**
     * Creates a VentaController with default dependencies.
     *
     * Uses the system clock and default DAO implementations.
     */
    public VentaController() {
        this(new VentaDAO(),
                new ProductoDAO("data/productos.txt"),
                Clock.systemDefaultZone());
    }

    /**
     * Starts a new order in the system.
     *
     * @param codigoPedido Unique identifier of the order
     * @param tipoPedido Type of order (table or takeaway)
     * @param numeroMesa Table number if applicable
     */
    public void iniciarPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {
        pedidoActual = new Pedido(codigoPedido, tipoPedido, numeroMesa);
    }

    /**
     * Adds a product to the current order.
     *
     * The stock of the product is reduced according to
     * the quantity added.
     *
     * @param producto Product to be added
     * @param cantidad Quantity of the product
     * @throws IllegalStateException If there is no active order
     * @throws IllegalArgumentException If product is null or quantity is invalid
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
     * Creates a {@link Venta} instance, copies all order lines,
     * stores the sale and clears the active order.
     *
     * @param cliente Customer associated with the sale
     * @param mesa Table associated with the sale
     * @param paraLlevar Indicates if the order is takeaway
     * @return The generated sale
     * @throws IOException If an error occurs while saving the sale
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
     * Retrieves the list of pending sales.
     *
     * @return List of pending sales
     * @throws IOException If an error occurs while loading data
     */
    public List<Venta> obtenerPendientes() throws IOException {
        return ventaDAO.listarPendientes();
    }

    /**
     * Marks a sale as paid and updates its payment method.
     *
     * @param venta Sale to update
     * @param metodoPago Payment method used
     * @throws IOException If an error occurs while updating the sale
     */
    public void marcarComoPagada(Venta venta, String metodoPago) throws IOException {
        venta.setEstado("PAGADA");
        venta.setMetodoPago(metodoPago);
        ventaDAO.actualizarVenta(venta);
    }

    /**
     * Generates the invoice text for a sale.
     *
     * @param venta Sale to generate the invoice for
     * @return Invoice text representation
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
     * @param codigoPedido Order code to remove
     * @throws IOException If an error occurs during deletion
     */
    public void eliminarPendientePorCodigoPedido(int codigoPedido) throws IOException {
        ventaDAO.eliminarPendientePorCodigoPedido(codigoPedido);
    }

    /**
     * Removes a product or quantity from the current order.
     *
     * @param producto Product to remove
     * @param cantidad Quantity to remove
     * @throws IllegalStateException If there is no active order
     * @throws IllegalArgumentException If product or quantity is invalid
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
     * @return Current order
     */
    public Pedido getPedidoActual() {
        return pedidoActual;
    }

    /**
     * Sets the current order manually.
     *
     * @param pedidoActual Order to set
     */
    public void setPedidoActual(Pedido pedidoActual) {
        this.pedidoActual = pedidoActual;
    }

    /**
     * Generates the invoice text without modifying the sale state.
     *
     * Useful for previewing invoices before payment confirmation.
     *
     * @param venta Sale to generate invoice for
     * @param metodoPagoPreview Payment method preview
     * @return Invoice text representation
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
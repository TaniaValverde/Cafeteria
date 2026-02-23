package Controlador;

import Model.Pedido;
import Model.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for managing orders (pedidos).
 * <p>
 * This class handles the creation, storage and retrieval of orders,
 * as well as the assignment of products to each order.
 * </p>
 *
 * It works as an intermediary between the application logic and
 * the {@link Pedido} model, enforcing business rules such as
 * avoiding duplicated orders.
 *
 * @author Project Team
 */
public class PedidoController {

    /**
     * List of orders currently managed by the system.
     */
    private final List<Pedido> pedidos;

    /**
     * Default constructor used in production.
     * <p>
     * Initializes an empty list of orders.
     * </p>
     */
    public PedidoController() {
        this.pedidos = new ArrayList<>();
    }

    /**
     * Constructor used mainly for testing purposes.
     * <p>
     * Allows dependency injection of a predefined list of orders.
     * </p>
     *
     * @param pedidos List of orders to manage
     * @throws IllegalArgumentException If the provided list is {@code null}
     */
    public PedidoController(List<Pedido> pedidos) {
        if (pedidos == null) {
            throw new IllegalArgumentException("Lista de pedidos no puede ser null");
        }
        this.pedidos = pedidos;
    }

    /**
     * Searches for an order by its code.
     *
     * @param codigoPedido Unique order code
     * @return The matching {@link Pedido} if found, or {@code null} otherwise
     */
    public Pedido buscarPedido(int codigoPedido) {
        for (Pedido p : pedidos) {
            if (p.getCodigoPedido() == codigoPedido) {
                return p;
            }
        }
        return null;
    }

    /**
     * Creates a new order and adds it to the system.
     *
     * @param codigoPedido Unique order code
     * @param tipoPedido Type of order (table or takeaway)
     * @param numeroMesa Table number associated with the order
     * @return The newly created {@link Pedido}
     * @throws IllegalArgumentException If an order with the same code already exists
     */
    public Pedido crearPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {

        if (buscarPedido(codigoPedido) != null) {
            throw new IllegalArgumentException("El pedido ya Existe");
        }

        // El modelo Pedido ya valida codigo/tipo/mesa/para-llevar
        Pedido pedido = new Pedido(codigoPedido, tipoPedido, numeroMesa);
        pedidos.add(pedido);
        return pedido;
    }

    /**
     * Adds a product to an existing order.
     *
     * @param codigoPedido Code of the order
     * @param producto Product to add
     * @param cantidad Quantity of the product
     * @throws IllegalArgumentException If the order does not exist
     */
    public void agregarProductoAPedido(int codigoPedido, Producto producto, int cantidad) {

        Pedido pedidoActual = buscarPedido(codigoPedido);
        if (pedidoActual == null) {
            throw new IllegalArgumentException("El pedido no Existe");
        }

        // El modelo Pedido valida producto null y cantidad <= 0
        pedidoActual.agregarProducto(producto, cantidad);
    }

    /**
     * Returns the total number of orders currently stored.
     *
     * @return Number of orders
     */
    public int cantidadPedidos() {
        return pedidos.size();
    }
}

package Controlador;

import Model.Order;
import Model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for managing orders (pedidos) in the MVC architecture.
 *
 * Creates and stores {@link Order} instances and delegates product assignment
 * to the order model while preventing duplicate order codes.
 */
public class OrderController {

    /** Orders currently managed by the system. */
    private final List<Order> pedidos;

    /** Creates an empty controller instance. */
    public OrderController() {
        this.pedidos = new ArrayList<>();
    }

    /**
     * Creates a controller using a provided order list (mainly for testing).
     *
     * @param pedidos list of orders to manage (non-null)
     * @throws IllegalArgumentException if {@code pedidos} is null
     */
    public OrderController(List<Order> pedidos) {
        if (pedidos == null) {
            throw new IllegalArgumentException("Lista de pedidos no puede ser null");
        }
        this.pedidos = pedidos;
    }

    /**
     * Finds an order by its code.
     *
     * @param codigoPedido order code
     * @return matching {@link Order} or null if not found
     */
    public Order buscarPedido(int codigoPedido) {
        for (Order p : pedidos) {
            if (p.getCodigoPedido() == codigoPedido) {
                return p;
            }
        }
        return null;
    }

    /**
     * Creates a new order and stores it in the controller.
     *
     * @param codigoPedido order code (must be unique)
     * @param tipoPedido order type (table or take-away)
     * @param numeroMesa table number when applicable
     * @return created {@link Order}
     * @throws IllegalArgumentException if an order with the same code already exists
     */
    public Order crearPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {

        if (buscarPedido(codigoPedido) != null) {
            throw new IllegalArgumentException("El pedido ya Existe");
        }

        Order pedido = new Order(codigoPedido, tipoPedido, numeroMesa);
        pedidos.add(pedido);
        return pedido;
    }

    /**
     * Adds a product to an existing order.
     *
     * @param codigoPedido order code
     * @param producto product to add
     * @param cantidad quantity to add
     * @throws IllegalArgumentException if the order does not exist
     */
    public void agregarProductoAPedido(int codigoPedido, Product producto, int cantidad) {

        Order pedidoActual = buscarPedido(codigoPedido);
        if (pedidoActual == null) {
            throw new IllegalArgumentException("El pedido no Existe");
        }

        pedidoActual.agregarProducto(producto, cantidad);
    }

    /**
     * Returns the number of orders currently stored.
     *
     * @return order count
     */
    public int cantidadPedidos() {
        return pedidos.size();
    }
}
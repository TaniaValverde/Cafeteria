package Controlador;

import Model.Table;
import Model.Order;

/**
 * Controller responsible for managing restaurant tables in the MVC architecture.
 *
 * Coordinates table state management and order assignment using {@link Table}
 * and {@link Order}, enforcing availability rules.
 */
public class TableController {

    /** Fixed array of tables managed by the system (tables 1–5). */
    private final Table[] mesas;

    /**
     * Creates a new controller and initializes five tables (1 to 5).
     */
    public TableController() {
        mesas = new Table[5];
        mesas[0] = new Table(1);
        mesas[1] = new Table(2);
        mesas[2] = new Table(3);
        mesas[3] = new Table(4);
        mesas[4] = new Table(5);
    }

    /**
     * Returns a table by its number.
     *
     * @param numero table number (1–5)
     * @return corresponding {@link Table}
     * @throws IllegalArgumentException if the number is out of range
     */
    public Table obtenerMesa(int numero) {

        if (numero < 1 || numero > 5) {
            throw new IllegalArgumentException("Numero Incorrecto de mesa.");
        }

        return mesas[numero - 1];
    }

    /**
     * Indicates whether a table is currently free.
     *
     * @param numeroMesa table number
     * @return true if free, false otherwise
     */
    public boolean estaLibre(int numeroMesa) {
        Table mesa = obtenerMesa(numeroMesa);
        return mesa.estaLibre();
    }

    /**
     * Releases a table and marks it as available.
     *
     * @param numeroMesa table number
     * @throws IllegalStateException if the table is already free
     */
    public void liberarMesa(int numeroMesa) {

        Table mesa = obtenerMesa(numeroMesa);

        if (mesa.estaLibre()) {
            throw new IllegalStateException("Table is already free.");
        }

        mesa.liberar();
    }

    /**
     * Assigns an order to a table.
     *
     * @param numeroMesa table number
     * @param pedido order to assign
     * @throws IllegalArgumentException if parameters are invalid or
     *                                  the order does not match the table number
     */
    public void asignarPedido(int numeroMesa, Order pedido) {

        if (numeroMesa <= 0) {
            throw new IllegalArgumentException("Numero Incorrecto de mesa.");
        }

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no existe");
        }

        if (pedido.getNumeroMesa() == null || !pedido.getNumeroMesa().equals(numeroMesa)) {
            throw new IllegalArgumentException("Mesa no coincide con el pedido");
        }

        Table mesa = obtenerMesa(numeroMesa);
        mesa.asignarPedido(pedido);
    }
}
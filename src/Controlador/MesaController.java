package Controlador;

import Model.Mesa;
import Model.Pedido;

/**
 * Controller responsible for managing restaurant tables.
 * <p>
 * This class handles the creation, access and state management of tables,
 * including assigning and releasing orders associated with each table.
 * </p>
 *
 * It acts as a mediator between {@link Mesa} and {@link Pedido},
 * enforcing business rules related to table availability.
 *
 * @author Project Team
 */
public class MesaController {

    /**
     * Fixed array of tables managed by the system.
     */
    private final Mesa[] mesas;

    /**
     * Creates a new {@code MesaController}.
     * <p>
     * Initializes a fixed set of five tables with numbers from 1 to 5.
     * </p>
     */
    public MesaController() {
        mesas = new Mesa[5];
        mesas[0] = new Mesa(1);
        mesas[1] = new Mesa(2);
        mesas[2] = new Mesa(3);
        mesas[3] = new Mesa(4);
        mesas[4] = new Mesa(5);
    }

    /**
     * Retrieves a table by its number.
     *
     * @param numero Table number to retrieve
     * @return The corresponding {@link Mesa} instance
     * @throws IllegalArgumentException If the table number is out of range
     */
    public Mesa obtenerMesa(int numero) {

        if (numero < 1 || numero > 5) {
            throw new IllegalArgumentException("Numero Incorrecto de mesa.");
        }

        return mesas[numero - 1];
    }

    /**
     * Checks whether a table is free.
     *
     * @param numeroMesa Table number to check
     * @return {@code true} if the table is free, {@code false} otherwise
     */
    public boolean estaLibre(int numeroMesa) {
        Mesa mesa = obtenerMesa(numeroMesa);
        return mesa.estaLibre();
    }

    /**
     * Releases a table, making it available again.
     *
     * @param numeroMesa Table number to release
     * @throws IllegalStateException If the table is already free
     */
    public void liberarMesa(int numeroMesa) {

        // reutiliza tu validación central
        Mesa mesa = obtenerMesa(numeroMesa);

        if (mesa.estaLibre()) {
            throw new IllegalStateException("Table is already free.");
        }

        mesa.liberar();
    }

    /**
     * Assigns an order to a table.
     * <p>
     * The table number must match the table assigned in the order.
     * </p>
     *
     * @param numeroMesa Table number
     * @param pedido Order to assign to the table
     * @throws IllegalArgumentException If the table number is invalid,
     *                                  the order is null, or the table
     *                                  does not match the order
     */
    public void asignarPedido(int numeroMesa, Pedido pedido) {

        if (numeroMesa <= 0) {
            throw new IllegalArgumentException("Numero Incorrecto de mesa.");
        }

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no existe");
        }

        if (pedido.getNumeroMesa() == null || !pedido.getNumeroMesa().equals(numeroMesa)) {
            throw new IllegalArgumentException("Mesa no coincide con el pedido");
        }

        Mesa mesa = obtenerMesa(numeroMesa);
        mesa.asignarPedido(pedido);
    }
}
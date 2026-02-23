package Model;

/**
 * Represents a table in the cafeteria system.
 *
 * This class is part of the Model layer (MVC) and manages the table number,
 * its current status (FREE or OCCUPIED), and the associated active order.
 * It enforces business rules such as valid table range and order assignment.
 */
public class Table {

    /** Table is available and has no active order. */
    public static final String LIBRE = "LIBRE";

    /** Table is occupied and has an active order. */
    public static final String OCUPADA = "OCUPADA";

    private int numero;
    private String estado;
    private Order pedidoActual;

    /**
     * Creates a table with a number between 1 and 5.
     *
     * @param numero table number (1–5)
     * @throws IllegalArgumentException if the number is outside the valid range
     */
    public Table(int numero) {
        if (numero < 1 || numero > 5) {
            throw new IllegalArgumentException(
                "Invalid table number. It must be between 1 and 5."
            );
        }
        this.numero = numero;
        this.estado = LIBRE;
        this.pedidoActual = null;
    }

    public int getNumero() {
        return numero;
    }

    public String getEstado() {
        return estado;
    }

    public Order getPedidoActual() {
        return pedidoActual;
    }

    /**
     * Indicates whether the table is currently free.
     *
     * @return true if free, false otherwise
     */
    public boolean estaLibre() {
        return estado.equals(LIBRE);
    }

    /**
     * Assigns an order to the table and changes its status to occupied.
     *
     * @param pedido order to assign
     * @throws IllegalArgumentException if the order is null or does not match the table number
     * @throws IllegalStateException if the table is already occupied
     */
    public void asignarPedido(Order pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException("Invalid order.");
        }

        if (pedido.getNumeroMesa() == null || pedido.getNumeroMesa() != this.numero) {
            throw new IllegalArgumentException("Mesa no coincide con el pedido");
        }

        if (!estaLibre()) {
            throw new IllegalStateException("Table is occupied.");
        }

        pedidoActual = pedido;
        estado = OCUPADA;
    }

    /**
     * Releases the table and marks it as free.
     *
     * @throws IllegalStateException if the table is already free
     */
    public void liberar() {

        if (estaLibre()) {
            throw new IllegalStateException("Table is already free.");
        }

        pedidoActual = null;
        estado = LIBRE;
    }
}
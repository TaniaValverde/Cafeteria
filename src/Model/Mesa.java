package Model;

/**
 * Represents a table in the coffee shop system.
 * A table has a number (1 to 5), a status (FREE or OCCUPIED),
 * and an optional current order.
 */
public class Mesa {

    /** Table is available and has no active order. */
    public static final String LIBRE = "LIBRE";

    /** Table is occupied and has an active order. */
    public static final String OCUPADA = "OCUPADA";

    private int numero;
    private String estado;
    private Pedido pedidoActual;

    /**
     * Creates a new table with the given number.
     *
     * @param numero table number (must be between 1 and 5)
     * @throws IllegalArgumentException if the number is outside the valid range
     */
    public Mesa(int numero) {
        if (numero < 1 || numero > 5) {
            throw new IllegalArgumentException(
                "Invalid table number. It must be between 1 and 5."
            );
        }
        this.numero = numero;
        this.estado = LIBRE;
        this.pedidoActual = null;
    }

    /** @return the table number */
    public int getNumero() {
        return numero;
    }

    /** @return the current table status */
    public String getEstado() {
        return estado;
    }

    /** @return the current order, or null if the table is free */
    public Pedido getPedidoActual() {
        return pedidoActual;
    }

    /**
     * Indicates whether the table is free.
     *
     * @return true if the table is free, false otherwise
     */
    public boolean estaLibre() {
        return estado.equals(LIBRE);
    }

    /**
     * Assigns an order to the table and marks it as occupied.
     *
     * @param pedido order to assign
     * @throws IllegalArgumentException if the order is null
     * @throws IllegalStateException if the table is already occupied
     */
    public void asignarPedido(Pedido pedido) {

    if (pedido == null) {
        throw new IllegalArgumentException("Invalid order.");
    }

    // 🔥 Validación de negocio faltante
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

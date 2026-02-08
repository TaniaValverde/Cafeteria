package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in the cafeteria system.
 * <p>
 * An order can be associated with a table ({@link #MESA}) or be
 * a take-away order ({@link #PARA_LLEVAR}). Orders for tables must
 * have a valid table number (1–5), while take-away orders must not
 * have a table assigned.
 * </p>
 */
public class Pedido {

    /** Order type constant for take-away orders. */
    public static final String PARA_LLEVAR = "PARA_LLEVAR";

    /** Order type constant for table orders. */
    public static final String MESA = "MESA";

    /** Unique order identifier. */
    private int codigoPedido;

    /** Order type (MESA or PARA_LLEVAR). */
    private String tipoPedido;

    /** Table number associated with the order (null for take-away). */
    private Integer numeroMesa;

    /** List of products included in the order. */
    private List<Producto> productos;

    /** Quantity corresponding to each product in the order. */
    private List<Integer> cantidades;

    /**
     * Creates a new order.
     *
     * @param codigoPedido order identifier
     * @param tipoPedido order type (MESA or PARA_LLEVAR)
     * @param numeroMesa table number if the order is for a table
     * @throws IllegalArgumentException if the order data is invalid
     */
    public Pedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {

        if (tipoPedido == null) {
            throw new IllegalArgumentException("No hay pedido");
        }

        if (!tipoPedido.equals(MESA) && !tipoPedido.equals(PARA_LLEVAR)) {
            throw new IllegalArgumentException("Tipo de pedido incorrecto");
        }

        if (tipoPedido.equals(MESA)) {
            if (numeroMesa == null || numeroMesa < 1 || numeroMesa > 5) {
                throw new IllegalArgumentException("Numero de mesa invalido");
            }
        } else {
            if (numeroMesa != null) {
                throw new IllegalArgumentException("Pedido para llevar no debe tener mesa");
            }
        }

        this.codigoPedido = codigoPedido;
        this.tipoPedido = tipoPedido;
        this.numeroMesa = numeroMesa;

        this.productos = new ArrayList<>();
        this.cantidades = new ArrayList<>();
    }

    /**
     * Adds a product to the order. If the product already exists,
     * its quantity is increased.
     *
     * @param producto product to add
     * @param cantidad quantity to add
     * @throws IllegalArgumentException if the product is null or quantity is invalid
     */
    public void agregarProducto(Producto producto, int cantidad) {

        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).equals(producto)) {
                cantidades.set(i, cantidades.get(i) + cantidad);
                return;
            }
        }

        productos.add(producto);
        cantidades.add(cantidad);
    }

    /** @return order identifier */
    public int getCodigoPedido() {
        return codigoPedido;
    }

    /** @param codigoPedido new order identifier */
    public void setCodigoPedido(int codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    /** @return order type */
    public String getTipoPedido() {
        return tipoPedido;
    }

    /** @return table number or null if take-away */
    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    /**
     * Returns the list of products in the order.
     *
     * @return list of products
     */
    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    /**
     * Returns the quantity of a specific product in the order.
     *
     * @param producto product to search
     * @return quantity of the product, or 0 if not present
     * @throws IllegalArgumentException if the product is null
     */
    public int getCantidadDeProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null");
        }

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).equals(producto)) {
                return cantidades.get(i);
            }
        }

        return 0;
    }
}

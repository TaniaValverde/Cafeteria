package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in the cafeteria system.
 *
 * This class belongs to the Model layer (MVC) and manages the order type
 * (table or take-away), associated table number when applicable, and
 * the collection of products with their respective quantities.
 *
 * It enforces business rules related to order validation and product handling.
 */
public class Pedido {

    /** Order type for take-away service. */
    public static final String PARA_LLEVAR = "PARA_LLEVAR";

    /** Order type for table service. */
    public static final String MESA = "MESA";

    private int codigoPedido;
    private String tipoPedido;
    private Integer numeroMesa;

    private List<Producto> productos;
    private List<Integer> cantidades;

    /**
     * Creates a new order validating service type and table assignment rules.
     *
     * @param codigoPedido order identifier (must be non-negative)
     * @param tipoPedido order type (MESA or PARA_LLEVAR)
     * @param numeroMesa table number (required for MESA, null for PARA_LLEVAR)
     * @throws IllegalArgumentException if validation rules are violated
     */
    public Pedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {

        if (codigoPedido < 0) {
            throw new IllegalArgumentException("Codigo de pedido invalido");
        }

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

    public int getCodigoPedido() {
        return codigoPedido;
    }

    public String getTipoPedido() {
        return tipoPedido;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    /**
     * Adds a product to the order or increases its quantity if already present.
     *
     * @param producto product to add
     * @param cantidad quantity to add (must be greater than zero)
     * @throws IllegalArgumentException if product is null or quantity is invalid
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

    /**
     * Returns a defensive copy of the product list.
     *
     * @return list of products in the order
     */
    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    /**
     * Returns the quantity of a specific product in the order.
     *
     * @param producto product to query
     * @return quantity, or 0 if not present
     * @throws IllegalArgumentException if product is null
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

    /**
     * Removes a quantity of a product from the order.
     * If the resulting quantity is zero, the product is removed completely.
     *
     * @param producto product to remove
     * @param cantidad quantity to remove (must be greater than zero)
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if removing more than existing quantity
     *                               or product is not in the order
     */
    public void quitarProducto(Producto producto, int cantidad) {

        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).equals(producto)) {

                int actual = cantidades.get(i);

                if (cantidad > actual) {
                    throw new IllegalStateException("No puedes quitar más de lo que hay en el pedido");
                }

                int nuevo = actual - cantidad;

                if (nuevo == 0) {
                    productos.remove(i);
                    cantidades.remove(i);
                } else {
                    cantidades.set(i, nuevo);
                }

                return;
            }
        }

        throw new IllegalStateException("El producto no está en el pedido");
    }
}
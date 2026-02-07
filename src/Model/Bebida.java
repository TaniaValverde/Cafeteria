package Model;

/**
 * Represents a beverage product.
 *
 * <p>This class extends {@link Producto} to satisfy the inheritance requirement
 * (Producto -> Bebida) described in the project specification.</p>
 *
 * <p>Currently, the PDF does not require extra beverage attributes (e.g., size, ml, hot/cold).
 * If your team decides to add them later, they should be added here.</p>
 */
public class Bebida extends Producto {

    /**
     * Creates a beverage product.
     *
     * @param codigo unique product code (non-empty)
     * @param nombre
     * @param categoria beverage category (non-empty)
     * @param precio product price (>= 0)
     * @param stock available units (>= 0)
     */
    public Bebida(String codigo, String nombre, String categoria, double precio, int stock) {
    super(codigo, nombre, categoria, precio, stock);
}

}

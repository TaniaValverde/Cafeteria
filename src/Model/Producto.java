package Model;

import java.util.Objects;

/**
 * Represents a product sold by the cafeteria.
 *
 * <p>Project requirement: products must have code, category, price and stock.</p>
 */
public class Producto {

    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;

    /**
     * Creates a new product.
     *
     * @param codigo unique product code (non-empty)
     * @param nombre product name (non-empty)
     * @param categoria product category (non-empty)
     * @param precio product price (>= 0)
     * @param stock available units (>= 0)
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Producto(String codigo, String nombre, String categoria, double precio, int stock) {
        setCodigo(codigo);
        setNombre(nombre);
        setCategoria(categoria);
        setPrecio(precio);
        setStock(stock);
    }

    /** @return unique product code */
    public String getCodigo() { return codigo; }

    /** @return product name */
    public String getNombre() { return nombre; }

    /** @return product category */
    public String getCategoria() { return categoria; }

    /** @return product price */
    public double getPrecio() { return precio; }

    /** @return available stock */
    public int getStock() { return stock; }

    /**
     * Updates the product code.
     * @param codigo new code (non-empty)
     */
    public final void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty.");
        }
        this.codigo = codigo.trim();
    }

    /**
     * Updates the product name.
     * @param nombre new name (non-empty)
     */
    public final void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Updates the product category.
     * @param categoria new category (non-empty)
     */
    public final void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        this.categoria = categoria.trim();
    }

    /**
     * Updates the product price.
     * @param precio new price (>= 0)
     */
    public final void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.precio = precio;
    }

    /**
     * Updates the available stock.
     * @param stock new stock (>= 0)
     */
    public final void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        this.stock = stock;
    }

    /**
     * Decreases product stock (inventory update after a sale).
     *
     * @param cantidad units to remove (> 0)
     * @throws IllegalArgumentException if cantidad <= 0
     * @throws IllegalStateException if there is not enough stock
     */
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }
        if (cantidad > stock) {
            throw new IllegalStateException("Insufficient stock for product: " + codigo);
        }
        stock -= cantidad;
    }

    /**
     * Increases product stock.
     *
     * @param cantidad units to add (> 0)
     * @throws IllegalArgumentException if cantidad <= 0
     */
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }
        stock += cantidad;
    }

    @Override
    public String toString() {
        return "Producto{codigo='" + codigo + "', nombre='" + nombre + "', categoria='" + categoria
                + "', precio=" + precio + ", stock=" + stock + "}";
    }

    /**
     * Products are considered equal if they share the same code.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto)) return false;
        Producto producto = (Producto) o;
        return codigo.equals(producto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}

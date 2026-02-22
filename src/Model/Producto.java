package Model;

import java.util.Objects;

/**
 * Represents a product sold by the cafeteria.
 *
 * This class belongs to the Model layer (MVC) and encapsulates the required
 * product data: code, name, category, price, and stock. It also provides basic
 * validation and inventory updates used during sales.
 */
public class Producto {

    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;

    /**
     * Creates a new product with the required attributes and validation.
     *
     * @param codigo unique product code (non-null, non-blank)
     * @param nombre product name (non-null, non-blank)
     * @param categoria product category (non-null, non-blank)
     * @param precio unit price (must be >= 0)
     * @param stock available units (must be >= 0)
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Producto(String codigo, String nombre, String categoria, double precio, int stock) {
        setCodigo(codigo);
        setNombre(nombre);
        setCategoria(categoria);
        setPrecio(precio);
        setStock(stock);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    public final void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty.");
        }
        this.codigo = codigo.trim();
    }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        this.nombre = nombre.trim();
    }

    public final void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        this.categoria = categoria.trim();
    }

    public final void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.precio = precio;
    }

    public final void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        this.stock = stock;
    }

    /**
     * Decreases stock after a sale.
     *
     * @param cantidad units to remove (must be > 0)
     * @throws IllegalArgumentException if {@code cantidad <= 0}
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
     * Increases stock when inventory is restocked.
     *
     * @param cantidad units to add (must be > 0)
     * @throws IllegalArgumentException if {@code cantidad <= 0}
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

    /** Equality is based only on the product code.
     * @param o */
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
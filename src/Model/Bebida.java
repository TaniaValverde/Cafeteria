package Model;

/**
 * Represents a beverage product.
 * Inherits validation and fields from Producto.
 */
public class Bebida extends Producto {

    public Bebida(String codigo, String nombre, String categoria, double precio, int stock) {
        super(codigo, nombre, categoria, precio, stock);
    }
}

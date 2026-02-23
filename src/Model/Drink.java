package Model;

/**
 * Represents a beverage product within the cafeteria ordering system.
 * 
 * This class extends {@link Product}, inheriting its core attributes
 * (code, name, category, price, and stock) and validation behavior.
 * 
 * It demonstrates the application of inheritance as part of the
 * object-oriented design implemented under the MVC architecture.
 * 
 * This specialization allows the system to differentiate product types
 * while maintaining a cohesive and scalable model structure.
 * 
 * @author 
 * Course: IF-0004 Software Development II
 */
public class Drink extends Product {

    /**
     * Constructs a new Beverage instance.
     *
     * @param codigo   unique product code
     * @param nombre   beverage name
     * @param categoria product category
     * @param precio   unit price
     * @param stock    available stock quantity
     */
    public Drink(String codigo, String nombre, String categoria, double precio, int stock) {
        super(codigo, nombre, categoria, precio, stock);
    }
}
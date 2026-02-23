package Model;

import java.io.Serializable;

/**
 * Represents a food product in the cafeteria system.
 *
 * This class extends {@link Product}, applying inheritance as part of the
 * object-oriented design required in the MVC architecture. It adds a specific
 * attribute (calories) to specialize the generic product model.
 *
 * Implements {@link Serializable} to support object persistence.
 */
public class Food extends Product implements Serializable {

    private int calorias;

    /**
     * Creates a food product with a defined calorie value.
     *
     * @param codigo product code
     * @param nombre product name
     * @param categoria product category
     * @param precio unit price
     * @param stock available stock
     * @param calorias calorie value
     */
    public Food(String codigo, String nombre, String categoria, double precio, int stock, int calorias) {
        super(codigo, nombre, categoria, precio, stock);
        this.calorias = calorias;
    }

    /**
     * Creates a food product with zero calories by default.
     *
     * @param codigo product code
     * @param nombre product name
     * @param categoria product category
     * @param precio unit price
     * @param stock available stock
     */
    public Food(String codigo, String nombre, String categoria, double precio, int stock) {
        this(codigo, nombre, categoria, precio, stock, 0);
    }

    /**
     * Returns the calorie value of the product.
     *
     * @return calories
     */
    public int getCalorias() {
        return calorias;
    }

    /**
     * Sets the calorie value of the product.
     *
     * @param calorias new calorie value
     */
    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    @Override
    public String toString() {
        return super.toString() + " - Calorías: " + calorias;
    }
}
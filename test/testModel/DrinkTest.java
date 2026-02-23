package testModel;

import Model.Drink;
import Model.Product;
import org.junit.Test;

import static org.junit.Assert.*;

public class DrinkTest {

    @Test
    public void heredaDeProducto_yConservaDatos() {
        Drink b = new Drink("B001", "Coca Cola", "Bebida", 1500.0, 20);

        assertTrue(b instanceof Product);

        assertEquals("B001", b.getCodigo());
        assertEquals("Coca Cola", b.getNombre());
        assertEquals("Bebida", b.getCategoria());
        assertEquals(1500.0, b.getPrecio(), 0.0001);
        assertEquals(20, b.getStock());
    }

    @Test
    public void validaciones_vienenDeProducto() {
        try {
            new Drink("", "X", "Bebida", 1000.0, 1);
            fail("Debe lanzar IllegalArgumentException por código vacío.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }

        try {
            new Drink("B1", "X", "", 1000.0, 1);
            fail("Debe lanzar IllegalArgumentException por categoría vacía.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }

        try {
            new Drink("B1", "X", "Bebida", -1.0, 1);
            fail("Debe lanzar IllegalArgumentException por precio negativo.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }

        try {
            new Drink("B1", "X", "Bebida", 1.0, -1);
            fail("Debe lanzar IllegalArgumentException por stock negativo.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }
    }
}

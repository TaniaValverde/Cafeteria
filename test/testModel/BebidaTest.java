package testModel;

import Model.Bebida;
import Model.Producto;
import org.junit.Test;

import static org.junit.Assert.*;

public class BebidaTest {

    @Test
    public void heredaDeProducto_yConservaDatos() {
        Bebida b = new Bebida("B001", "Coca Cola", "Bebida", 1500.0, 20);

        assertTrue(b instanceof Producto);

        assertEquals("B001", b.getCodigo());
        assertEquals("Coca Cola", b.getNombre());
        assertEquals("Bebida", b.getCategoria());
        assertEquals(1500.0, b.getPrecio(), 0.0001);
        assertEquals(20, b.getStock());
    }

    @Test
    public void validaciones_vienenDeProducto() {
        try {
            new Bebida("", "X", "Bebida", 1000.0, 1);
            fail("Debe lanzar IllegalArgumentException por código vacío.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }

        try {
            new Bebida("B1", "X", "", 1000.0, 1);
            fail("Debe lanzar IllegalArgumentException por categoría vacía.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }

        try {
            new Bebida("B1", "X", "Bebida", -1.0, 1);
            fail("Debe lanzar IllegalArgumentException por precio negativo.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }

        try {
            new Bebida("B1", "X", "Bebida", 1.0, -1);
            fail("Debe lanzar IllegalArgumentException por stock negativo.");
        } catch (IllegalArgumentException ex) {
            /* ok */ }
    }
}

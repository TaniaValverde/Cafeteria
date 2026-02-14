package testModel;

import Model.Producto;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductoTest {

    @Test
    public void constructor_yGetters_funcionan_yTrim() {
        Producto p = new Producto("  P001  ", "Cafe", "  Bebida  ", 1200.0, 10);

        assertEquals("P001", p.getCodigo());
        assertEquals("Cafe", p.getNombre());          // ojo: nombre no se trim/valida en tu clase actual
        assertEquals("Bebida", p.getCategoria());
        assertEquals(1200.0, p.getPrecio(), 0.0001);
        assertEquals(10, p.getStock());
    }

    @Test
    public void setCodigo_invalido_lanza() {
        Producto p = new Producto("P1", "N", "C", 1.0, 1);

        assertThrowsIAE(() -> p.setCodigo(null));
        assertThrowsIAE(() -> p.setCodigo(""));
        assertThrowsIAE(() -> p.setCodigo("   "));
    }

    @Test
    public void setCategoria_invalida_lanza() {
        Producto p = new Producto("P1", "N", "C", 1.0, 1);

        assertThrowsIAE(() -> p.setCategoria(null));
        assertThrowsIAE(() -> p.setCategoria(""));
        assertThrowsIAE(() -> p.setCategoria("   "));
    }

    @Test
    public void setPrecio_negativo_lanza() {
        Producto p = new Producto("P1", "N", "C", 1.0, 1);
        assertThrowsIAE(() -> p.setPrecio(-0.01));
    }

    @Test
    public void setStock_negativo_lanza() {
        Producto p = new Producto("P1", "N", "C", 1.0, 1);
        assertThrowsIAE(() -> p.setStock(-1));
    }

    @Test
    public void descontarStock_casos() {
        Producto p = new Producto("P1", "N", "C", 1.0, 10);

        assertThrowsIAE(() -> p.descontarStock(0));
        assertThrowsIAE(() -> p.descontarStock(-1));

        try {
            p.descontarStock(11);
            fail("Debe lanzar IllegalStateException por stock insuficiente.");
        } catch (IllegalStateException ex) {
            // ok
        }

        p.descontarStock(3);
        assertEquals(7, p.getStock());
    }

    @Test
    public void aumentarStock_casos() {
        Producto p = new Producto("P1", "N", "C", 1.0, 10);

        assertThrowsIAE(() -> p.aumentarStock(0));
        assertThrowsIAE(() -> p.aumentarStock(-2));

        p.aumentarStock(5);
        assertEquals(15, p.getStock());
    }

    @Test
    public void equals_y_hashCode_porCodigo() {
        Producto a = new Producto("P001", "A", "C", 1.0, 1);
        Producto b = new Producto("P001", "B", "D", 99.0, 999);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    private static void assertThrowsIAE(Runnable r) {
        try {
            r.run();
            fail("Debe lanzar IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            // ok
        }
    }
    @Test
public void setNombre_invalido_lanza() {
    Producto p = new Producto("P1", "N", "C", 1.0, 1);

    try { p.setNombre(null); fail(); } catch (IllegalArgumentException ex) {}
    try { p.setNombre(""); fail(); } catch (IllegalArgumentException ex) {}
    try { p.setNombre("   "); fail(); } catch (IllegalArgumentException ex) {}

    p.setNombre("  Te  ");
    assertEquals("Te", p.getNombre());
}

}

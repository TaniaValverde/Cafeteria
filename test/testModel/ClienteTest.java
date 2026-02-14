package testModel;

import Model.Cliente;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClienteTest {

    @Test
    public void constructor_yGetters_yTrim() {
        Cliente c = new Cliente("  C001  ", "  Ana  ", " 8888 ", Cliente.TipoCliente.FRECUENTE);

        assertEquals("C001", c.getId());
        assertEquals("Ana", c.getNombre());
        assertEquals("8888", c.getTelefono());
        assertEquals(Cliente.TipoCliente.FRECUENTE, c.getTipo());
    }

    @Test
    public void telefono_null_seConvierteVacio() {
        Cliente c = new Cliente("C1", "Ana", null, Cliente.TipoCliente.VISITANTE);
        assertEquals("", c.getTelefono());
    }

    @Test
    public void validaciones_lanzan() {
        assertThrowsIAE(() -> new Cliente("", "Ana", "1", Cliente.TipoCliente.FRECUENTE));
        assertThrowsIAE(() -> new Cliente("C1", "", "1", Cliente.TipoCliente.FRECUENTE));

        try {
            new Cliente("C1", "Ana", "1", null);
            fail("Debe lanzar IllegalArgumentException por tipo null.");
        } catch (IllegalArgumentException ex) { /* ok */ }
    }

    @Test
    public void toCsv_fromCsv_roundtrip_normal() {
        Cliente c = new Cliente("C1", "Ana", "8888", Cliente.TipoCliente.FRECUENTE);
        String csv = c.toCsv();

        Cliente parsed = Cliente.fromCsv(csv);

        assertEquals(c.getId(), parsed.getId());
        assertEquals(c.getNombre(), parsed.getNombre());
        assertEquals(c.getTelefono(), parsed.getTelefono());
        assertEquals(c.getTipo(), parsed.getTipo());
    }

    @Test
    public void toCsv_fromCsv_escapes_coma_y_backslash() {
        Cliente c = new Cliente("C,1", "An\\a, Maria", "88\\,88", Cliente.TipoCliente.VISITANTE);

        String csv = c.toCsv();
        Cliente parsed = Cliente.fromCsv(csv);

        assertEquals(c.getId(), parsed.getId());
        assertEquals(c.getNombre(), parsed.getNombre());
        assertEquals(c.getTelefono(), parsed.getTelefono());
        assertEquals(c.getTipo(), parsed.getTipo());
    }

    @Test
    public void fromCsv_invalido_lanza() {
        assertThrowsIAE(() -> Cliente.fromCsv(null));
        assertThrowsIAE(() -> Cliente.fromCsv(""));
        assertThrowsIAE(() -> Cliente.fromCsv("a,b,c")); // incompleto
    }

    @Test
    public void equals_hashCode_porId() {
        Cliente a = new Cliente("C1", "Ana", "1", Cliente.TipoCliente.FRECUENTE);
        Cliente b = new Cliente("C1", "Otra", "2", Cliente.TipoCliente.VISITANTE);

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
}

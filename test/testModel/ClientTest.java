package testModel;

import Model.Client;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientTest {

    @Test
    public void constructor_yGetters_yTrim() {
        Client c = new Client("  C001  ", "  Ana  ", " 8888 ", Client.TipoCliente.FRECUENTE);

        assertEquals("C001", c.getId());
        assertEquals("Ana", c.getNombre());
        assertEquals("8888", c.getTelefono());
        assertEquals(Client.TipoCliente.FRECUENTE, c.getTipo());
    }

    @Test
    public void telefono_null_seConvierteVacio() {
        Client c = new Client("C1", "Ana", null, Client.TipoCliente.VISITANTE);
        assertEquals("", c.getTelefono());
    }

    @Test
    public void validaciones_lanzan() {
        assertThrowsIAE(() -> new Client("", "Ana", "1", Client.TipoCliente.FRECUENTE));
        assertThrowsIAE(() -> new Client("C1", "", "1", Client.TipoCliente.FRECUENTE));

        try {
            new Client("C1", "Ana", "1", null);
            fail("Debe lanzar IllegalArgumentException por tipo null.");
        } catch (IllegalArgumentException ex) { /* ok */ }
    }

    @Test
    public void toCsv_fromCsv_roundtrip_normal() {
        Client c = new Client("C1", "Ana", "8888", Client.TipoCliente.FRECUENTE);
        String csv = c.toCsv();

        Client parsed = Client.fromCsv(csv);

        assertEquals(c.getId(), parsed.getId());
        assertEquals(c.getNombre(), parsed.getNombre());
        assertEquals(c.getTelefono(), parsed.getTelefono());
        assertEquals(c.getTipo(), parsed.getTipo());
    }

    @Test
    public void toCsv_fromCsv_escapes_coma_y_backslash() {
        Client c = new Client("C,1", "An\\a, Maria", "88\\,88", Client.TipoCliente.VISITANTE);

        String csv = c.toCsv();
        Client parsed = Client.fromCsv(csv);

        assertEquals(c.getId(), parsed.getId());
        assertEquals(c.getNombre(), parsed.getNombre());
        assertEquals(c.getTelefono(), parsed.getTelefono());
        assertEquals(c.getTipo(), parsed.getTipo());
    }

    @Test
    public void fromCsv_invalido_lanza() {
        assertThrowsIAE(() -> Client.fromCsv(null));
        assertThrowsIAE(() -> Client.fromCsv(""));
        assertThrowsIAE(() -> Client.fromCsv("a,b,c")); // incompleto
    }

    @Test
    public void equals_hashCode_porId() {
        Client a = new Client("C1", "Ana", "1", Client.TipoCliente.FRECUENTE);
        Client b = new Client("C1", "Otra", "2", Client.TipoCliente.VISITANTE);

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

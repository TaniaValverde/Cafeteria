package testModel;

import Model.Producto;
import Model.Venta;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class VentaTest {

    @Test
    public void constructor_defaultTaxRate_yGetters() {
        LocalDateTime now = LocalDateTime.now();
        Venta v = new Venta("V1", now, 1);

        assertEquals("V1", v.getId());
        assertEquals(now, v.getFechaHora());
        assertEquals(1, v.getMesaNumero());
        assertEquals(Venta.DEFAULT_TAX_RATE, v.getTaxRate(), 0.0000001);
        assertTrue(v.getLineas().isEmpty());
    }

    @Test
    public void validaciones_constructor_y_setters() {
        LocalDateTime now = LocalDateTime.now();

        assertThrowsIAE(() -> new Venta("", now, 1));
        assertThrowsIAE(() -> new Venta("V1", null, 1));

        // mesa inválida (solo 1..5 o 0 para llevar)
        assertThrowsIAE(() -> new Venta("V1", now, -1));
        assertThrowsIAE(() -> new Venta("V1", now, 6));

        // tax negativo
        assertThrowsIAE(() -> new Venta("V1", now, 1, -0.01));
    }

    @Test
    public void paraLlevar_funciona() {
        Venta v = new Venta("V1", LocalDateTime.now(), Venta.PARA_LLEVAR);
        assertTrue(v.esParaLlevar());
    }

    @Test
    public void agregarLinea_calculaSubtotalImpuestoTotal() {
        Venta v = new Venta("V1", LocalDateTime.now(), 1, 0.10); // 10%

        Producto p1 = new Producto("P1", "Prod1", "Cat", 1000.0, 10);
        Producto p2 = new Producto("P2", "Prod2", "Cat", 500.0, 10);

        v.agregarLinea(p1, 2); // 2000
        v.agregarLinea(p2, 3); // 1500

        assertEquals(3500.0, v.getSubtotal(), 0.0001);
        assertEquals(350.0, v.getImpuesto(), 0.0001);
        assertEquals(3850.0, v.getTotal(), 0.0001);

        List<Venta.LineaVenta> lineas = v.getLineas();
        assertEquals(2, lineas.size());
        assertEquals(2000.0, lineas.get(0).getSubtotal(), 0.0001);
        assertEquals(1500.0, lineas.get(1).getSubtotal(), 0.0001);
    }

    @Test
    public void agregarLinea_validaciones() {
        Venta v = new Venta("V1", LocalDateTime.now(), 1);

        try {
            v.agregarLinea(null, 1);
            fail("Debe lanzar IllegalArgumentException por producto null.");
        } catch (IllegalArgumentException ex) { /* ok */ }

        try {
            v.agregarLinea(new Producto("P1", "N", "C", 1.0, 1), 0);
            fail("Debe lanzar IllegalArgumentException por cantidad <= 0.");
        } catch (IllegalArgumentException ex) { /* ok */ }
    }

    @Test
    public void getLineas_esInmodificable() {
        Venta v = new Venta("V1", LocalDateTime.now(), 1);
        v.agregarLinea(new Producto("P1", "N", "C", 100.0, 1), 1);

        List<Venta.LineaVenta> lineas = v.getLineas();
        try {
            lineas.add(new Venta.LineaVenta(new Producto("P2", "N2", "C", 50.0, 1), 1, 50.0));
            fail("Debe lanzar UnsupportedOperationException (lista inmodificable).");
        } catch (UnsupportedOperationException ex) { /* ok */ }
    }

    @Test
    public void toCsv_fromCsv_roundtrip() {
        LocalDateTime dt = LocalDateTime.of(2026, 2, 8, 10, 30);
        Venta v = new Venta("V1", dt, 2, 0.13);

        v.agregarLinea(new Producto("P1", "N1", "C", 1000.0, 10), 2);
        v.agregarLinea(new Producto("P2", "N2", "C", 500.0, 10), 1);

        String csv = v.toCsv();
        Venta parsed = Venta.fromCsv(csv);

        assertEquals(v.getId(), parsed.getId());
        assertEquals(v.getFechaHora(), parsed.getFechaHora());
        assertEquals(v.getMesaNumero(), parsed.getMesaNumero());
        assertEquals(v.getTaxRate(), parsed.getTaxRate(), 0.0000001);

        assertEquals(v.getSubtotal(), parsed.getSubtotal(), 0.0001);
        assertEquals(v.getImpuesto(), parsed.getImpuesto(), 0.0001);
        assertEquals(v.getTotal(), parsed.getTotal(), 0.0001);
        assertEquals(v.getLineas().size(), parsed.getLineas().size());
    }

    @Test
    public void fromCsv_invalido_lanza() {
        assertThrowsIAE(() -> Venta.fromCsv(null));
        assertThrowsIAE(() -> Venta.fromCsv(""));
        assertThrowsIAE(() -> Venta.fromCsv("a,b,c")); // incompleto
    }

    @Test
    public void LineaVenta_validaciones() {
        try {
            new Venta.LineaVenta(null, 1, 10.0);
            fail("Debe lanzar IllegalArgumentException por producto null.");
        } catch (IllegalArgumentException ex) { /* ok */ }

        try {
            new Venta.LineaVenta(new Producto("P1", "N", "C", 1.0, 1), 0, 10.0);
            fail("Debe lanzar IllegalArgumentException por cantidad <= 0.");
        } catch (IllegalArgumentException ex) { /* ok */ }

        try {
            new Venta.LineaVenta(new Producto("P1", "N", "C", 1.0, 1), 1, -1.0);
            fail("Debe lanzar IllegalArgumentException por precioUnitario negativo.");
        } catch (IllegalArgumentException ex) { /* ok */ }
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

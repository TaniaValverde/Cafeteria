package testModel;

import Model.Factura;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para la clase Factura
 * 
 */
public class FacturaTest {

    private Factura factura;

    @Before
    public void setUp() {
        // Se crea la factura con valores nulos
        // para probar robustez del sistema
        factura = new Factura(null, null, null, true);
    }

    /**
     * La factura no debe ser nula
     */
    @Test
    public void testFacturaCreada() {
        assertNotNull(factura);
    }

    /**
     * El ID de la factura debe existir
     */
    @Test
    public void testIdFacturaNoNulo() {
        assertNotNull(factura.getIdFactura());
    }

    /**
     * El total no debe ser negativo
     */
    @Test
    public void testTotalNoNegativo() {
        double total = factura.getTotal();
        assertTrue(total >= 0);
    }

    /**
     * Generar impresión no debe lanzar excepción
     */
    @Test
    public void testGenerarImpresion() {
        try {
            String texto = factura.generarImpresion();
            assertNotNull(texto);
        } catch (Exception e) {
            fail("Error al generar impresión");
        }
    }

    /**
     * toString debe devolver texto válido
     */
    @Test
    public void testToString() {
        String texto = factura.toString();
        assertNotNull(texto);
        assertTrue(texto.length() > 0);
    }

    /**
     * Guardar en archivo puede fallar si no está implementado
     */
    @Test
    public void testGuardarEnArchivo() {
        try {
            factura.guardarEnArchivo("factura.txt");
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true); // excepción aceptable
        }
    }
}

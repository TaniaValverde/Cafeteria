package testControlador;

import Controlador.ReportController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para ReportController
 Pensadas para errores comunes del usuario
 * 
 * 
 * @author Valverde
 */
public class ReportControllerTest {

    private ReportController instance;

    public ReportControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // Se ejecuta una sola vez antes de todos los tests
    }

    @AfterClass
    public static void tearDownClass() {
        // Se ejecuta una sola vez al final
    }

    @Before
    public void setUp() {
        // Se ejecuta antes de cada test
        instance = new ReportController();
    }

    @After
    public void tearDown() {
        // Se ejecuta después de cada test
        instance = null;
    }

    /**
     * Test original corregido:
     * Reporte por mesa válida
     */
    @Test
    public void testReporteVentasPorMesa() {
        System.out.println("reporteVentasPorMesa - mesa válida");
        int numeroMesa = 1;

        try {
            instance.reporteVentasPorMesa(numeroMesa);
            assertTrue(true); // Si no falla, el test pasa
        } catch (Exception e) {
            fail("No debería lanzar excepción con una mesa válida");
        }
    }

    /**
     * Usuario ingresa mesa 0 (error común)
     */
    @Test
    public void testReporteVentasPorMesaCero() {
        System.out.println("reporteVentasPorMesa - mesa 0");
        int numeroMesa = 0;

        try {
            instance.reporteVentasPorMesa(numeroMesa);
            assertTrue(true);
        } catch (Exception e) {
            fail("No debería crashear con mesa 0");
        }
    }

    /**
     * Usuario ingresa número de mesa negativo
     */
    @Test
    public void testReporteVentasPorMesaNegativa() {
        System.out.println("reporteVentasPorMesa - mesa negativa");
        int numeroMesa = -3;

        try {
            instance.reporteVentasPorMesa(numeroMesa);
            assertTrue(true);
        } catch (Exception e) {
            fail("No debería lanzar excepción con mesa negativa");
        }
    }

    /**
     * Usuario ingresa una mesa que no existe
     */
    @Test
    public void testReporteVentasPorMesaInexistente() {
        System.out.println("reporteVentasPorMesa - mesa inexistente");
        int numeroMesa = 99;

        try {
            instance.reporteVentasPorMesa(numeroMesa);
            assertTrue(true);
        } catch (Exception e) {
            fail("No debería fallar con una mesa inexistente");
        }
    }

    /**
     * Test original corregido:
     * Reporte total general
     */
    @Test
    public void testReporteTotalGeneral() {
        System.out.println("reporteTotalGeneral");

        try {
            instance.reporteTotalGeneral();
            assertTrue(true);
        } catch (Exception e) {
            fail("El reporte total general no debería lanzar excepción");
        }
    }

    /**
     * Llamar el reporte varias veces seguidas
     */
    @Test
    public void testReporteTotalGeneralVariasVeces() {
        System.out.println("reporteTotalGeneral varias veces");

        try {
            instance.reporteTotalGeneral();
            instance.reporteTotalGeneral();
            instance.reporteTotalGeneral();
            assertTrue(true);
        } catch (Exception e) {
            fail("No debería fallar al llamarse varias veces");
        }
    }

    /**
     * Reportes sin ventas registradas
     * (listas vacías)
     */
    @Test
    public void testReportesSinVentas() {
        System.out.println("reportes sin ventas");

        try {
            instance.reporteVentasPorMesa(1);
            instance.reporteTotalGeneral();
            assertTrue(true);
        } catch (Exception e) {
            fail("No debería fallar aunque no haya ventas");
        }
    }
}


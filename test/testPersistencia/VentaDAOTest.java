
package testPersistencia;

import Model.Venta;
import Persistencia.ProductoDAO;
import Persistencia.VentaDAO;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Valverde
 */
public class VentaDAOTest {
    
    public VentaDAOTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of cargar method, of class VentaDAO.
     */
    @Test
    public void testCargar() throws Exception {
        System.out.println("cargar");
        VentaDAO instance = new VentaDAO();
        List<Venta> expResult = null;
        List<Venta> result = instance.cargar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guardar method, of class VentaDAO.
     */
    @Test
    public void testGuardar() throws Exception {
        System.out.println("guardar");
        List<Venta> ventas = null;
        VentaDAO instance = new VentaDAO();
        instance.guardar(ventas);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guardarVenta method, of class VentaDAO.
     */
    @Test
    public void testGuardarVenta() throws Exception {
        System.out.println("guardarVenta");
        Venta venta = null;
        VentaDAO instance = new VentaDAO();
        instance.guardarVenta(venta);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrarVentaConStock method, of class VentaDAO.
     */
    @Test
    public void testRegistrarVentaConStock() throws Exception {
        System.out.println("registrarVentaConStock");
        Venta venta = null;
        ProductoDAO productoDAO = null;
        VentaDAO instance = new VentaDAO();
        instance.registrarVentaConStock(venta, productoDAO);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVentas method, of class VentaDAO.
     */
    @Test
    public void testGetVentas() throws Exception {
        System.out.println("getVentas");
        VentaDAO instance = new VentaDAO();
        ArrayList<Venta> expResult = null;
        ArrayList<Venta> result = instance.getVentas();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

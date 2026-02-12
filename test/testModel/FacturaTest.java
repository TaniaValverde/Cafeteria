
package testModel;

import Model.Cliente;
import Model.Factura;
import Model.Mesa;
import Model.Pedido;
import java.time.LocalDateTime;
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
public class FacturaTest {
    
    public FacturaTest() {
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
     * Test of getIdFactura method, of class Factura.
     */
    @Test
    public void testGetIdFactura() {
        System.out.println("getIdFactura");
        Factura instance = null;
        String expResult = "";
        String result = instance.getIdFactura();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPedido method, of class Factura.
     */
    @Test
    public void testGetPedido() {
        System.out.println("getPedido");
        Factura instance = null;
        Pedido expResult = null;
        Pedido result = instance.getPedido();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCliente method, of class Factura.
     */
    @Test
    public void testGetCliente() {
        System.out.println("getCliente");
        Factura instance = null;
        Cliente expResult = null;
        Cliente result = instance.getCliente();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMesa method, of class Factura.
     */
    @Test
    public void testGetMesa() {
        System.out.println("getMesa");
        Factura instance = null;
        Mesa expResult = null;
        Mesa result = instance.getMesa();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isParaLlevar method, of class Factura.
     */
    @Test
    public void testIsParaLlevar() {
        System.out.println("isParaLlevar");
        Factura instance = null;
        boolean expResult = false;
        boolean result = instance.isParaLlevar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFecha method, of class Factura.
     */
    @Test
    public void testGetFecha() {
        System.out.println("getFecha");
        Factura instance = null;
        LocalDateTime expResult = null;
        LocalDateTime result = instance.getFecha();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotal method, of class Factura.
     */
    @Test
    public void testGetTotal() {
        System.out.println("getTotal");
        Factura instance = null;
        double expResult = 0.0;
        double result = instance.getTotal();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generarImpresion method, of class Factura.
     */
    @Test
    public void testGenerarImpresion() {
        System.out.println("generarImpresion");
        Factura instance = null;
        String expResult = "";
        String result = instance.generarImpresion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guardarEnArchivo method, of class Factura.
     */
    @Test
    public void testGuardarEnArchivo() throws Exception {
        System.out.println("guardarEnArchivo");
        String archivo = "";
        Factura instance = null;
        instance.guardarEnArchivo(archivo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Factura.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Factura instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

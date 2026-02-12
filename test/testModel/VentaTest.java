/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testModel;

import Model.Producto;
import Model.Venta;
import java.time.LocalDateTime;
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
public class VentaTest {
    
    public VentaTest() {
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
     * Test of getId method, of class Venta.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Venta instance = null;
        String expResult = "";
        String result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFechaHora method, of class Venta.
     */
    @Test
    public void testGetFechaHora() {
        System.out.println("getFechaHora");
        Venta instance = null;
        LocalDateTime expResult = null;
        LocalDateTime result = instance.getFechaHora();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMesaNumero method, of class Venta.
     */
    @Test
    public void testGetMesaNumero() {
        System.out.println("getMesaNumero");
        Venta instance = null;
        int expResult = 0;
        int result = instance.getMesaNumero();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTaxRate method, of class Venta.
     */
    @Test
    public void testGetTaxRate() {
        System.out.println("getTaxRate");
        Venta instance = null;
        double expResult = 0.0;
        double result = instance.getTaxRate();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLineas method, of class Venta.
     */
    @Test
    public void testGetLineas() {
        System.out.println("getLineas");
        Venta instance = null;
        List<Venta.LineaVenta> expResult = null;
        List<Venta.LineaVenta> result = instance.getLineas();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setId method, of class Venta.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        String id = "";
        Venta instance = null;
        instance.setId(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFechaHora method, of class Venta.
     */
    @Test
    public void testSetFechaHora() {
        System.out.println("setFechaHora");
        LocalDateTime fechaHora = null;
        Venta instance = null;
        instance.setFechaHora(fechaHora);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMesaNumero method, of class Venta.
     */
    @Test
    public void testSetMesaNumero() {
        System.out.println("setMesaNumero");
        int mesaNumero = 0;
        Venta instance = null;
        instance.setMesaNumero(mesaNumero);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTaxRate method, of class Venta.
     */
    @Test
    public void testSetTaxRate() {
        System.out.println("setTaxRate");
        double taxRate = 0.0;
        Venta instance = null;
        instance.setTaxRate(taxRate);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of agregarLinea method, of class Venta.
     */
    @Test
    public void testAgregarLinea() {
        System.out.println("agregarLinea");
        Producto producto = null;
        int cantidad = 0;
        Venta instance = null;
        instance.agregarLinea(producto, cantidad);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSubtotal method, of class Venta.
     */
    @Test
    public void testGetSubtotal() {
        System.out.println("getSubtotal");
        Venta instance = null;
        double expResult = 0.0;
        double result = instance.getSubtotal();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImpuesto method, of class Venta.
     */
    @Test
    public void testGetImpuesto() {
        System.out.println("getImpuesto");
        Venta instance = null;
        double expResult = 0.0;
        double result = instance.getImpuesto();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotal method, of class Venta.
     */
    @Test
    public void testGetTotal() {
        System.out.println("getTotal");
        Venta instance = null;
        double expResult = 0.0;
        double result = instance.getTotal();
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of esParaLlevar method, of class Venta.
     */
    @Test
    public void testEsParaLlevar() {
        System.out.println("esParaLlevar");
        Venta instance = null;
        boolean expResult = false;
        boolean result = instance.esParaLlevar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toCsv method, of class Venta.
     */
    @Test
    public void testToCsv() {
        System.out.println("toCsv");
        Venta instance = null;
        String expResult = "";
        String result = instance.toCsv();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fromCsv method, of class Venta.
     */
    @Test
    public void testFromCsv() {
        System.out.println("fromCsv");
        String line = "";
        Venta expResult = null;
        Venta result = Venta.fromCsv(line);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Venta.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Venta instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Venta.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        Venta instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class Venta.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Venta instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

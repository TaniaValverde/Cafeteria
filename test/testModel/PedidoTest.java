/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testModel;

import Model.Pedido;
import Model.Producto;
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
public class PedidoTest {
    
    public PedidoTest() {
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
     * Test of agregarProducto method, of class Pedido.
     */
    @Test
    public void testAgregarProducto() {
        System.out.println("agregarProducto");
        Producto producto = null;
        int cantidad = 0;
        Pedido instance = null;
        instance.agregarProducto(producto, cantidad);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCodigoPedido method, of class Pedido.
     */
    @Test
    public void testGetCodigoPedido() {
        System.out.println("getCodigoPedido");
        Pedido instance = null;
        int expResult = 0;
        int result = instance.getCodigoPedido();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCodigoPedido method, of class Pedido.
     */
    @Test
    public void testSetCodigoPedido() {
        System.out.println("setCodigoPedido");
        int codigoPedido = 0;
        Pedido instance = null;
        instance.setCodigoPedido(codigoPedido);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTipoPedido method, of class Pedido.
     */
    @Test
    public void testGetTipoPedido() {
        System.out.println("getTipoPedido");
        Pedido instance = null;
        String expResult = "";
        String result = instance.getTipoPedido();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumeroMesa method, of class Pedido.
     */
    @Test
    public void testGetNumeroMesa() {
        System.out.println("getNumeroMesa");
        Pedido instance = null;
        Integer expResult = null;
        Integer result = instance.getNumeroMesa();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProductos method, of class Pedido.
     */
    @Test
    public void testGetProductos() {
        System.out.println("getProductos");
        Pedido instance = null;
        List<Producto> expResult = null;
        List<Producto> result = instance.getProductos();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCantidadDeProducto method, of class Pedido.
     */
    @Test
    public void testGetCantidadDeProducto() {
        System.out.println("getCantidadDeProducto");
        Producto producto = null;
        Pedido instance = null;
        int expResult = 0;
        int result = instance.getCantidadDeProducto(producto);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

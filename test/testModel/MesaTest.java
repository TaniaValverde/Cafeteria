/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testModel;

import Model.Mesa;
import Model.Pedido;
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
public class MesaTest {
    
    public MesaTest() {
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
     * Test of getNumero method, of class Mesa.
     */
    @Test
    public void testGetNumero() {
        System.out.println("getNumero");
        Mesa instance = null;
        int expResult = 0;
        int result = instance.getNumero();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEstado method, of class Mesa.
     */
    @Test
    public void testGetEstado() {
        System.out.println("getEstado");
        Mesa instance = null;
        String expResult = "";
        String result = instance.getEstado();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPedidoActual method, of class Mesa.
     */
    @Test
    public void testGetPedidoActual() {
        System.out.println("getPedidoActual");
        Mesa instance = null;
        Pedido expResult = null;
        Pedido result = instance.getPedidoActual();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estaLibre method, of class Mesa.
     */
    @Test
    public void testEstaLibre() {
        System.out.println("estaLibre");
        Mesa instance = null;
        boolean expResult = false;
        boolean result = instance.estaLibre();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asignarPedido method, of class Mesa.
     */
    @Test
    public void testAsignarPedido() {
        System.out.println("asignarPedido");
        Pedido pedido = null;
        Mesa instance = null;
        instance.asignarPedido(pedido);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of liberar method, of class Mesa.
     */
    @Test
    public void testLiberar() {
        System.out.println("liberar");
        Mesa instance = null;
        instance.liberar();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

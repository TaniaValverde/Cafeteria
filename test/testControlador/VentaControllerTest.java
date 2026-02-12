/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testControlador;

import Controlador.VentaController;
import Model.Cliente;
import Model.Mesa;
import Model.Pedido;
import Model.Producto;
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
public class VentaControllerTest {
    
    public VentaControllerTest() {
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
     * Test of iniciarPedido method, of class VentaController.
     */
    @Test
    public void testIniciarPedido() {
        System.out.println("iniciarPedido");
        int codigoPedido = 0;
        String tipoPedido = "";
        Integer numeroMesa = null;
        VentaController instance = new VentaController();
        instance.iniciarPedido(codigoPedido, tipoPedido, numeroMesa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of agregarProductoAlPedido method, of class VentaController.
     */
    @Test
    public void testAgregarProductoAlPedido() {
        System.out.println("agregarProductoAlPedido");
        Producto producto = null;
        int cantidad = 0;
        VentaController instance = new VentaController();
        instance.agregarProductoAlPedido(producto, cantidad);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of finalizarVenta method, of class VentaController.
     */
    @Test
    public void testFinalizarVenta() {
        System.out.println("finalizarVenta");
        Cliente cliente = null;
        Mesa mesa = null;
        boolean paraLlevar = false;
        VentaController instance = new VentaController();
        instance.finalizarVenta(cliente, mesa, paraLlevar);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPedidoActual method, of class VentaController.
     */
    @Test
    public void testGetPedidoActual() {
        System.out.println("getPedidoActual");
        VentaController instance = new VentaController();
        Pedido expResult = null;
        Pedido result = instance.getPedidoActual();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPedidoActual method, of class VentaController.
     */
    @Test
    public void testSetPedidoActual() {
        System.out.println("setPedidoActual");
        Pedido pedidoActual = null;
        VentaController instance = new VentaController();
        instance.setPedidoActual(pedidoActual);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

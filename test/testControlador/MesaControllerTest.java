/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testControlador;

import Controlador.MesaController;
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
public class MesaControllerTest {
    
    public MesaControllerTest() {
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
     * Test of obtenerMesa method, of class MesaController.
     */
    @Test
    public void testObtenerMesa() {
        System.out.println("obtenerMesa");
        int numero = 0;
        MesaController instance = new MesaController();
        Mesa expResult = null;
        Mesa result = instance.obtenerMesa(numero);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estaLibre method, of class MesaController.
     */
    @Test
    public void testEstaLibre() {
        System.out.println("estaLibre");
        int numeroMesa = 0;
        MesaController instance = new MesaController();
        boolean expResult = false;
        boolean result = instance.estaLibre(numeroMesa);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asignarPedido method, of class MesaController.
     */
    @Test
    public void testAsignarPedido() {
        System.out.println("asignarPedido");
        int numeroMesa = 0;
        Pedido pedido = null;
        MesaController instance = new MesaController();
        instance.asignarPedido(numeroMesa, pedido);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of liberarMesa method, of class MesaController.
     */
    @Test
    public void testLiberarMesa() {
        System.out.println("liberarMesa");
        int numeroMesa = 0;
        MesaController instance = new MesaController();
        instance.liberarMesa(numeroMesa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

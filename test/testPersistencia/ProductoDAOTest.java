/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testPersistencia;

import Model.Producto;
import Persistencia.ProductoDAO;
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
public class ProductoDAOTest {
    
    public ProductoDAOTest() {
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
     * Test of cargar method, of class ProductoDAO.
     */
    @Test
    public void testCargar() throws Exception {
        System.out.println("cargar");
        ProductoDAO instance = null;
        List<Producto> expResult = null;
        List<Producto> result = instance.cargar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guardar method, of class ProductoDAO.
     */
    @Test
    public void testGuardar() throws Exception {
        System.out.println("guardar");
        List<Producto> productos = null;
        ProductoDAO instance = null;
        instance.guardar(productos);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

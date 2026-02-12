/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testPersistencia;

import Model.Cliente;
import Persistencia.ClienteDAO;
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
public class ClienteDAOTest {
    
    public ClienteDAOTest() {
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
     * Test of cargar method, of class ClienteDAO.
     */
    @Test
    public void testCargar() throws Exception {
        System.out.println("cargar");
        ClienteDAO instance = null;
        List<Cliente> expResult = null;
        List<Cliente> result = instance.cargar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guardar method, of class ClienteDAO.
     */
    @Test
    public void testGuardar() throws Exception {
        System.out.println("guardar");
        List<Cliente> clientes = null;
        ClienteDAO instance = null;
        instance.guardar(clientes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

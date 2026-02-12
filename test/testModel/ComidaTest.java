/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package testModel;

import Model.Comida;
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
public class ComidaTest {
    
    public ComidaTest() {
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
     * Test of getCalorias method, of class Comida.
     */
    @Test
    public void testGetCalorias() {
        System.out.println("getCalorias");
        Comida instance = null;
        int expResult = 0;
        int result = instance.getCalorias();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCalorias method, of class Comida.
     */
    @Test
    public void testSetCalorias() {
        System.out.println("setCalorias");
        int calorias = 0;
        Comida instance = null;
        instance.setCalorias(calorias);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Comida.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Comida instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

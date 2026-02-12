/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package Controlador;

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
public class ReporteControllerTest {
    
    public ReporteControllerTest() {
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
     * Test of reporteVentasPorMesa method, of class ReporteController.
     */
    @Test
    public void testReporteVentasPorMesa() {
        System.out.println("reporteVentasPorMesa");
        int numeroMesa = 0;
        ReporteController instance = new ReporteController();
        instance.reporteVentasPorMesa(numeroMesa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reporteTotalGeneral method, of class ReporteController.
     */
    @Test
    public void testReporteTotalGeneral() {
        System.out.println("reporteTotalGeneral");
        ReporteController instance = new ReporteController();
        instance.reporteTotalGeneral();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

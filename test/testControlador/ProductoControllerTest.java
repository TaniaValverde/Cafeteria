
package testControlador;

import Controlador.ProductoController;
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
public class ProductoControllerTest {
    
    public ProductoControllerTest() {
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
     * Test of listar method, of class ProductoController.
     */
    @Test
    public void testListar() {
        System.out.println("listar");
        ProductoController instance = null;
        List<Producto> expResult = null;
        List<Producto> result = instance.listar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buscarPorCodigo method, of class ProductoController.
     */
    @Test
    public void testBuscarPorCodigo() {
        System.out.println("buscarPorCodigo");
        String codigo = "";
        ProductoController instance = null;
        Producto expResult = null;
        Producto result = instance.buscarPorCodigo(codigo);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of agregar method, of class ProductoController.
     */
    @Test
    public void testAgregar() throws Exception {
        System.out.println("agregar");
        Producto producto = null;
        ProductoController instance = null;
        instance.agregar(producto);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of modificar method, of class ProductoController.
     */
    @Test
    public void testModificar() throws Exception {
        System.out.println("modificar");
        String codigo = "";
        String nuevaCategoria = "";
        double nuevoPrecio = 0.0;
        int nuevoStock = 0;
        ProductoController instance = null;
        instance.modificar(codigo, nuevaCategoria, nuevoPrecio, nuevoStock);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of eliminar method, of class ProductoController.
     */
    @Test
    public void testEliminar() throws Exception {
        System.out.println("eliminar");
        String codigo = "";
        ProductoController instance = null;
        instance.eliminar(codigo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ordenarPorCodigo method, of class ProductoController.
     */
    @Test
    public void testOrdenarPorCodigo() {
        System.out.println("ordenarPorCodigo");
        ProductoController instance = null;
        instance.ordenarPorCodigo();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ordenarPorPrecio method, of class ProductoController.
     */
    @Test
    public void testOrdenarPorPrecio() {
        System.out.println("ordenarPorPrecio");
        ProductoController instance = null;
        instance.ordenarPorPrecio();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guardarCambios method, of class ProductoController.
     */
    @Test
    public void testGuardarCambios() throws Exception {
        System.out.println("guardarCambios");
        ProductoController instance = null;
        instance.guardarCambios();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

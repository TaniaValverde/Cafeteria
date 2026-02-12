
package testControlador;

import Controlador.ClienteController;
import Model.Cliente;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Usuario
 */
public class ClienteControllerTest {
    
    public ClienteControllerTest() {
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
     * Test of listar method, of class ClienteController.
     */
    @Test
    public void testListar() {
        System.out.println("listar");
        ClienteController instance = null;
        List<Cliente> expResult = null;
        List<Cliente> result = instance.listar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buscarPorId method, of class ClienteController.
     */
    @Test
    public void testBuscarPorId() {
        System.out.println("buscarPorId");
        String id = "";
        ClienteController instance = null;
        Cliente expResult = null;
        Cliente result = instance.buscarPorId(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrar method, of class ClienteController.
     */
    @Test
    public void testRegistrar() throws Exception {
        System.out.println("registrar");
        Cliente cliente = null;
        ClienteController instance = null;
        instance.registrar(cliente);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of modificar method, of class ClienteController.
     */
    @Test
    public void testModificar() throws Exception {
        System.out.println("modificar");
        String id = "";
        String nuevoNombre = "";
        String nuevoTelefono = "";
        Cliente.TipoCliente nuevoTipo = null;
        ClienteController instance = null;
        instance.modificar(id, nuevoNombre, nuevoTelefono, nuevoTipo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of eliminar method, of class ClienteController.
     */
    @Test
    public void testEliminar() throws Exception {
        System.out.println("eliminar");
        String id = "";
        ClienteController instance = null;
        instance.eliminar(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ordenarPorNombre method, of class ClienteController.
     */
    @Test
    public void testOrdenarPorNombre() {
        System.out.println("ordenarPorNombre");
        ClienteController instance = null;
        instance.ordenarPorNombre();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guardarCambios method, of class ClienteController.
     */
    @Test
    public void testGuardarCambios() throws Exception {
        System.out.println("guardarCambios");
        ClienteController instance = null;
        instance.guardarCambios();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

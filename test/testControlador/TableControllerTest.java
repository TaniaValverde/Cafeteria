package testControlador;

import Controlador.TableController;
import Model.Table;
import Model.Order;
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
public class TableControllerTest {

    public TableControllerTest() {
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
     * Test of obtenerMesa method, of class TableController.
     */
    @Test
    public void testObtenerMesa() {
        System.out.println("obtenerMesa");
        int numero = 1;
        TableController instance = new TableController();
        Table expResult = null;
        Table result = instance.obtenerMesa(numero);

        assertNotNull(result);
        assertEquals(numero, result.getNumero());

    }

    /**
     * Test of estaLibre method, of class TableController.
     */
    @Test
    public void testEstaLibre() {
        System.out.println("estaLibre");
        int numeroMesa = 1;
        TableController instance = new TableController();
        boolean expResult = false;
        boolean result = instance.estaLibre(numeroMesa);
        assertTrue(result);

    }

    /**
     * Test of asignarPedido method, of class TableController.
     */
    @Test
    public void testAsignarPedido() {
        System.out.println("asignarPedido");
        int numeroMesa = 1;
        Order pedido = new Order(1, "MESA", 1);

        TableController instance = new TableController();
        instance.asignarPedido(numeroMesa, pedido);

    }

    /**
     * Test of liberarMesa method, of class TableController.
     */
    @Test
    public void testLiberarMesa() {
        System.out.println("liberarMesa");
        int numeroMesa = 1;
        TableController instance = new TableController();

        /*1) ocupar la mesa primero*/
        Order pedido = new Order(1, "MESA", numeroMesa);
        instance.asignarPedido(numeroMesa, pedido);

        /* 2) ahora sí liberarla*/
        instance.liberarMesa(numeroMesa);

        /* 3) verificar que quedó libre**/
        assertTrue(instance.estaLibre(numeroMesa));

    }

    @Test
    public void testAsignarPedidoValido() {
        TableController controller = new TableController();
        Order pedido = new Order(1, "MESA", 1);

        controller.asignarPedido(1, pedido);

        assertFalse(controller.estaLibre(1));
    }

    @Test
    public void testMesaInvalida() {
        TableController controller = new TableController();

        /* Capturamos la excepción*/
        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> {
                    controller.obtenerMesa(0);
                });

        /* Verificamos el mensaje de la excepción*/
        assertEquals("Numero Incorrecto de mesa.", exception.getMessage());
    }

    @Test
    public void testMesaExiste() {
        TableController controller = new TableController();
        assertNotNull(controller.obtenerMesa(1));
    }

    @Test
    public void testMesaNumeroNegativo() {

        TableController controller = new TableController();

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.obtenerMesa(-1));

        assertEquals("Numero Incorrecto de mesa.", ex.getMessage());
    }

    @Test
    public void testLiberarMesaNumeroSeis() {

        TableController controller = new TableController();

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.liberarMesa(6));

        assertEquals("Numero Incorrecto de mesa.", ex.getMessage());
    }

    @Test
    public void testAsignarPedidoMesaCero() {

        TableController controller = new TableController();
        Order pedido = new Order(0, "MESA", 1);

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.asignarPedido(0, pedido));

        assertEquals("Numero Incorrecto de mesa.", ex.getMessage());
    }

    @Test
    public void testLiberarMesaDosVeces() {

        TableController controller = new TableController();

        Order pedido = new Order(50, "MESA", 4);

        controller.asignarPedido(4, pedido);
        controller.liberarMesa(4);

        IllegalStateException ex
                = assertThrows(IllegalStateException.class,
                        () -> controller.liberarMesa(4));

        assertEquals("Table is already free.", ex.getMessage());
    }

    @Test
    public void testPedidoMesaIncorrecta() {

        TableController controller = new TableController();
        Order pedido = new Order(60, "MESA", 2);

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.asignarPedido(3, pedido));

        assertEquals("Mesa no coincide con el pedido", ex.getMessage());
    }

}

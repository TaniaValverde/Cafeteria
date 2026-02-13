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
        int numero = 1;
        MesaController instance = new MesaController();
        Mesa expResult = null;
        Mesa result = instance.obtenerMesa(numero);

        assertNotNull(result);
        assertEquals(numero, result.getNumero());

    }

    /**
     * Test of estaLibre method, of class MesaController.
     */
    @Test
    public void testEstaLibre() {
        System.out.println("estaLibre");
        int numeroMesa = 1;
        MesaController instance = new MesaController();
        boolean expResult = false;
        boolean result = instance.estaLibre(numeroMesa);
        assertTrue(result);

    }

    /**
     * Test of asignarPedido method, of class MesaController.
     */
    @Test
    public void testAsignarPedido() {
        System.out.println("asignarPedido");
        int numeroMesa = 1;
        Pedido pedido = new Pedido(1, "MESA", 1);

        MesaController instance = new MesaController();
        instance.asignarPedido(numeroMesa, pedido);

    }

    /**
     * Test of liberarMesa method, of class MesaController.
     */
    @Test
    public void testLiberarMesa() {
        System.out.println("liberarMesa");
        int numeroMesa = 1;
        MesaController instance = new MesaController();

        /*1) ocupar la mesa primero*/
        Pedido pedido = new Pedido(1, "MESA", numeroMesa);
        instance.asignarPedido(numeroMesa, pedido);

        /* 2) ahora sí liberarla*/
        instance.liberarMesa(numeroMesa);

        /* 3) verificar que quedó libre**/
        assertTrue(instance.estaLibre(numeroMesa));

    }

    @Test
    public void testAsignarPedidoValido() {
        MesaController controller = new MesaController();
        Pedido pedido = new Pedido(1, "MESA", 1);

        controller.asignarPedido(1, pedido);

        assertFalse(controller.estaLibre(1));
    }

    @Test
    public void testMesaInvalida() {
        MesaController controller = new MesaController();

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
        MesaController controller = new MesaController();
        assertNotNull(controller.obtenerMesa(1));
    }

    @Test
    public void testMesaNumeroNegativo() {

        MesaController controller = new MesaController();

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.obtenerMesa(-1));

        assertEquals("Numero Incorrecto de mesa.", ex.getMessage());
    }

    @Test
    public void testLiberarMesaNumeroSeis() {

        MesaController controller = new MesaController();

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.liberarMesa(6));

        assertEquals("Numero Incorrecto de mesa.", ex.getMessage());
    }

    @Test
    public void testAsignarPedidoMesaCero() {

        MesaController controller = new MesaController();
        Pedido pedido = new Pedido(0, "MESA", 1);

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.asignarPedido(0, pedido));

        assertEquals("Numero Incorrecto de mesa.", ex.getMessage());
    }

    @Test
    public void testLiberarMesaDosVeces() {

        MesaController controller = new MesaController();

        Pedido pedido = new Pedido(50, "MESA", 4);

        controller.asignarPedido(4, pedido);
        controller.liberarMesa(4);

        IllegalStateException ex
                = assertThrows(IllegalStateException.class,
                        () -> controller.liberarMesa(4));

        assertEquals("Table is already free.", ex.getMessage());
    }

    @Test
    public void testPedidoMesaIncorrecta() {

        MesaController controller = new MesaController();
        Pedido pedido = new Pedido(60, "MESA", 2);

        IllegalArgumentException ex
                = assertThrows(IllegalArgumentException.class,
                        () -> controller.asignarPedido(3, pedido));

        assertEquals("Mesa no coincide con el pedido", ex.getMessage());
    }

}

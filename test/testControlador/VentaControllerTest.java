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
        int codigoPedido = 1;
        String tipoPedido = "MESA";
        Integer numeroMesa = 1;

        VentaController instance = new VentaController();

        try {
            instance.iniciarPedido(codigoPedido, tipoPedido, numeroMesa);
            assertNotNull(instance.getPedidoActual());
        } catch (Exception e) {
            fail("Error al iniciar el pedido");
        }
    }

    /**
     * Test of agregarProductoAlPedido method, of class VentaController.
     */
    @Test
    public void testAgregarProductoAlPedido() {
        System.out.println("agregarProductoAlPedido");
        Producto producto = null;
        int cantidad = 1;

        VentaController instance = new VentaController();

        try {
            instance.agregarProductoAlPedido(producto, cantidad);
            assertTrue(true); // No debe fallar
        } catch (Exception e) {
            fail("Error al agregar producto al pedido");
        }
    }

    /**
     * Test of finalizarVenta method, of class VentaController.
     */
    @Test
    public void testFinalizarVenta() {
        System.out.println("finalizarVenta");
        Cliente cliente = null;
        Mesa mesa = null;
        boolean paraLlevar = true;

        VentaController instance = new VentaController();

        try {
            instance.finalizarVenta(cliente, mesa, paraLlevar);
            assertTrue(true);
        } catch (Exception e) {
            fail("Error al finalizar la venta");
        }
    }

    /**
     * Test of getPedidoActual method, of class VentaController.
     */
    @Test
    public void testGetPedidoActual() {
        System.out.println("getPedidoActual");
        VentaController instance = new VentaController();
        Pedido result = instance.getPedidoActual();
        assertNull(result);
    }

    /**
     * Test of setPedidoActual method, of class VentaController.
     * CORREGIDO: se elimina el fail y se valida correctamente.
     */
    @Test
    public void testSetPedidoActual() {
        System.out.println("setPedidoActual");
        Pedido pedidoActual = null;
        VentaController instance = new VentaController();

        try {
            instance.setPedidoActual(pedidoActual);
            assertNull(instance.getPedidoActual());
        } catch (Exception e) {
            fail("Error al asignar pedidoActual");
        }
    }

    /*
    TEST ADICIONALES AGREGADOS
    */
    

    /**
     * Iniciar un pedido para llevar no debe causar errores.
     */
    @Test
    public void testIniciarPedidoParaLlevar() {
        VentaController instance = new VentaController();

        try {
            instance.iniciarPedido(2, "PARA_LLEVAR", null);
            assertNotNull(instance.getPedidoActual());
        } catch (Exception e) {
            fail("Error al iniciar pedido para llevar");
        }
    }

    /**
     * Finalizar una venta sin haber iniciado pedido.
     */
    @Test
    public void testFinalizarVentaSinPedidoPrevio() {
        VentaController instance = new VentaController();

        try {
            instance.finalizarVenta(null, null, true);
            assertTrue(true);
        } catch (Exception e) {
            fail("Error al finalizar venta sin pedido previo");
        }
    }

    /**
     * Asignar pedidoActual varias veces no debe causar errores.
     */
    @Test
    public void testSetPedidoActualVariasVeces() {
        VentaController instance = new VentaController();

        try {
            instance.setPedidoActual(null);
            instance.setPedidoActual(null);
            assertNull(instance.getPedidoActual());
        } catch (Exception e) {
            fail("Error al asignar pedidoActual varias veces");
        }
    }

    /**
     * Llamar múltiples veces a getPedidoActual no debe fallar.
     */
    @Test
    public void testGetPedidoActualMultipleVeces() {
        VentaController instance = new VentaController();

        try {
            instance.getPedidoActual();
            instance.getPedidoActual();
            instance.getPedidoActual();
            assertTrue(true);
        } catch (Exception e) {
            fail("Error al llamar getPedidoActual varias veces");
        }
    }
}


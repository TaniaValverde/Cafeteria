package testControlador;

import Controlador.TableController;
import Controlador.OrderController;
import Model.Order;
import Model.Product;
import java.util.ArrayList;
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
public class OrderControllerTest {

    public OrderControllerTest() {
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

    private Product productoValido() {
        return new Product("P1", "Cafe", "Bebida", 2000, 10);
    }

    @Test
    public void testConstructorListaNullDebeFallar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderController(null));
        assertEquals("Lista de pedidos no puede ser null", ex.getMessage());
    }

    // ---------- buscarPedido ----------
    @Test
    public void testBuscarPedidoNoExiste() {
        OrderController controller = new OrderController();
        assertNull(controller.buscarPedido(1));
    }

    @Test
    public void testBuscarPedidoExiste() {
        OrderController controller = new OrderController();
        controller.crearPedido(1, Order.MESA, 1);

        Order p = controller.buscarPedido(1);
        assertNotNull(p);
        assertEquals(1, p.getCodigoPedido());
    }

    // ---------- crearPedido (happy path) ----------
    @Test
    public void testCrearPedidoMesaValido() {
        OrderController controller = new OrderController();

        Order p = controller.crearPedido(10, Order.MESA, 2);

        assertNotNull(p);
        assertEquals(10, p.getCodigoPedido());
        assertEquals(Order.MESA, p.getTipoPedido());
        assertEquals(Integer.valueOf(2), p.getNumeroMesa());
        assertEquals(1, controller.cantidadPedidos());
    }

    @Test
    public void testCrearPedidoParaLlevarValido() {
        OrderController controller = new OrderController();

        Order p = controller.crearPedido(11, Order.PARA_LLEVAR, null);

        assertNotNull(p);
        assertEquals(Order.PARA_LLEVAR, p.getTipoPedido());
        assertNull(p.getNumeroMesa());
        assertEquals(1, controller.cantidadPedidos());
    }

   
    @Test
    public void testCrearPedidoDuplicadoDebeFallar() {
        OrderController controller = new OrderController();
        controller.crearPedido(10, Order.MESA, 2);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.crearPedido(10, Order.MESA, 2));

        assertEquals("El pedido ya Existe", ex.getMessage());
        assertEquals(1, controller.cantidadPedidos());
    }

    @Test
    public void testPedidoCodigoNegativo() {
        OrderController controller = new OrderController();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.crearPedido(-1, Order.MESA, 1));

        assertEquals("Codigo de pedido invalido", ex.getMessage());
    }

    @Test
    public void testCrearPedidoNull() {
        OrderController controller = new OrderController();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.crearPedido(1, null, 1));

        assertEquals("No hay pedido", ex.getMessage());
    }

    @Test
    public void testCrearPedidoIncorrecto() {
        OrderController controller = new OrderController();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.crearPedido(1, "DOMICILIO", 1));

        assertEquals("Tipo de pedido incorrecto", ex.getMessage());
    }

    @Test
    public void testCrearPedidoMesaInvalidaCero() {
        OrderController controller = new OrderController();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.crearPedido(1, Order.MESA, 0));

        assertEquals("Numero de mesa invalido", ex.getMessage());
    }

    @Test
    public void testCrearPedidoMesaInvalidaSeis() {
        OrderController controller = new OrderController();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.crearPedido(1, Order.MESA, 6));

        assertEquals("Numero de mesa invalido", ex.getMessage());
    }

    @Test
    public void testCrearPedidoParaLlevarConMesa() {
        OrderController controller = new OrderController();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.crearPedido(1, Order.PARA_LLEVAR, 1));

        assertEquals("Pedido para llevar no debe tener mesa", ex.getMessage());
    }

  
    @Test
    public void testAgregarProductoAPedidoAgrega() {
        OrderController controller = new OrderController();
        controller.crearPedido(1, Order.MESA, 1);

        Product cafe = productoValido();
        controller.agregarProductoAPedido(1, cafe, 2);

        Order p = controller.buscarPedido(1);
        assertEquals(2, p.getCantidadDeProducto(cafe));
        assertEquals(1, p.getProductos().size());
    }

    @Test
    public void testAgregarProductoAPedidoSumaSiRepetido() {
        OrderController controller = new OrderController();
        controller.crearPedido(1, Order.MESA, 1);

        Product cafe = productoValido();
        controller.agregarProductoAPedido(1, cafe, 2);
        controller.agregarProductoAPedido(1, cafe, 3);

        Order p = controller.buscarPedido(1);
        assertEquals(5, p.getCantidadDeProducto(cafe));
        assertEquals(1, p.getProductos().size());
    }

    // ---------- agregarProductoAPedido (reviente) ----------
    @Test
    public void testAddProdoAPedidoNoExist() {
        OrderController controller = new OrderController();

        Product cafe = productoValido();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.agregarProductoAPedido(99, cafe, 1));

        assertEquals("El pedido no Existe", ex.getMessage());
    }

    @Test
    public void testAddProductoNull() {
        OrderController controller = new OrderController();
        controller.crearPedido(1, Order.MESA, 1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.agregarProductoAPedido(1, null, 1));

        assertEquals("Producto no puede ser null", ex.getMessage());
    }

    @Test
    public void testAddProductoCantCero() {
        OrderController controller = new OrderController();
        controller.crearPedido(1, Order.MESA, 1);

        Product cafe = productoValido();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.agregarProductoAPedido(1, cafe, 0));

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    public void testAddProductoCantNegativa() {
        OrderController controller = new OrderController();
        controller.crearPedido(1, Order.MESA, 1);

        Product cafe = productoValido();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.agregarProductoAPedido(1, cafe, -5));

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    // ---------- “usuario lo revienta”: comportamiento estable ----------
    @Test
    public void testAsignarPedidoMesaIncorrecta() {

        TableController controller = new TableController();

        // Order es para la mesa 2
        Order pedido = new Order(60, "MESA", 2);

        // Precondición: la mesa 3 debe estar libre antes del intento
        assertTrue(controller.estaLibre(3));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.asignarPedido(3, pedido)
        );

        assertEquals("Mesa no coincide con el pedido", ex.getMessage());

        // Postcondición: como falló, la mesa 3 NO debe haber cambiado
        assertTrue(controller.estaLibre(3));
    }
@Test
public void testCrearPedidoFallido() {
    OrderController controller = new OrderController(new ArrayList<>());

    assertEquals(0, controller.cantidadPedidos());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> controller.crearPedido(1, Order.MESA, 0)
    );

    assertEquals("Numero de mesa invalido", ex.getMessage());

    assertEquals(0, controller.cantidadPedidos()); // no debe ensuciar la lista
}

}

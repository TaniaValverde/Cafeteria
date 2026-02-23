package testControlador;

import Controlador.SaleController;
import Model.Order;
import Model.Product;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaleControllerTest {

    private Product cafeConStock(int stock) {
        return new Product("P1", "Cafe", "Bebida", 2000, stock);
    }

    // -------- starPedid -------

    @Test
    public void testIniciarPedido() {
        SaleController vc = new SaleController();

        vc.iniciarPedido(1, Order.MESA, 1);

        assertNotNull(vc.getPedidoActual());
        assertEquals(1, vc.getPedidoActual().getCodigoPedido());
        assertEquals(Order.MESA, vc.getPedidoActual().getTipoPedido());
        assertEquals(Integer.valueOf(1), vc.getPedidoActual().getNumeroMesa());
    }

    @Test
    public void testIniciarParaLlevar() {
        SaleController vc = new SaleController();

        vc.iniciarPedido(2, Order.PARA_LLEVAR, null);

        assertNotNull(vc.getPedidoActual());
        assertEquals(Order.PARA_LLEVAR, vc.getPedidoActual().getTipoPedido());
        assertNull(vc.getPedidoActual().getNumeroMesa());
    }

    // -------- getPedidoActual / setPedidoActual -------

    @Test
    public void testGetPedidoActual() {
        SaleController vc = new SaleController();
        assertNull(vc.getPedidoActual());
    }

    @Test
    public void testSetPedidoActual() {
        SaleController vc = new SaleController();
        vc.setPedidoActual(null);
        assertNull(vc.getPedidoActual());
    }

    // -------- add ProductAlPedid -------

    @Test
    public void testAgregarSinPedido() {
        SaleController vc = new SaleController();
        Product cafe = cafeConStock(10);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> vc.agregarProductoAlPedido(cafe, 1)
        );

        assertEquals("No hay pedido activo", ex.getMessage());
    }

    @Test
    public void testAgregarProductoNull() {
        SaleController vc = new SaleController();
        vc.iniciarPedido(1, Order.MESA, 1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> vc.agregarProductoAlPedido(null, 1)
        );

        assertEquals("Producto no puede ser null", ex.getMessage());
    }

    @Test
    public void testCantidadInvalida() {
        SaleController vc = new SaleController();
        vc.iniciarPedido(1, Order.MESA, 1);
        Product cafe = cafeConStock(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> vc.agregarProductoAlPedido(cafe, 0)
        );

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    public void testStockInsuficiente() {
        SaleController vc = new SaleController();
        vc.iniciarPedido(1, Order.MESA, 1);
        Product cafe = cafeConStock(1);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> vc.agregarProductoAlPedido(cafe, 5)
        );

        // message comes from Product.
        assertTrue(ex.getMessage().startsWith("Insufficient stock"));
    }

    // --------- finalVenta (resp.) -------

    @Test
    public void testFinalizarSinPedido() {
        SaleController vc = new SaleController();

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> vc.finalizarVenta(null, null, true)
        );

        assertEquals("No hay pedido para finalizar", ex.getMessage());
    }
}

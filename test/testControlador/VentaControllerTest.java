package testControlador;

import Controlador.VentaController;
import Model.Pedido;
import Model.Producto;
import org.junit.Test;

import static org.junit.Assert.*;

public class VentaControllerTest {

    private Producto cafeConStock(int stock) {
        return new Producto("P1", "Cafe", "Bebida", 2000, stock);
    }

    // -------- starPedid -------

    @Test
    public void testIniciarPedido() {
        VentaController vc = new VentaController();

        vc.iniciarPedido(1, Pedido.MESA, 1);

        assertNotNull(vc.getPedidoActual());
        assertEquals(1, vc.getPedidoActual().getCodigoPedido());
        assertEquals(Pedido.MESA, vc.getPedidoActual().getTipoPedido());
        assertEquals(Integer.valueOf(1), vc.getPedidoActual().getNumeroMesa());
    }

    @Test
    public void testIniciarParaLlevar() {
        VentaController vc = new VentaController();

        vc.iniciarPedido(2, Pedido.PARA_LLEVAR, null);

        assertNotNull(vc.getPedidoActual());
        assertEquals(Pedido.PARA_LLEVAR, vc.getPedidoActual().getTipoPedido());
        assertNull(vc.getPedidoActual().getNumeroMesa());
    }

    // -------- getPedidoActual / setPedidoActual -------

    @Test
    public void testGetPedidoActual() {
        VentaController vc = new VentaController();
        assertNull(vc.getPedidoActual());
    }

    @Test
    public void testSetPedidoActual() {
        VentaController vc = new VentaController();
        vc.setPedidoActual(null);
        assertNull(vc.getPedidoActual());
    }

    // -------- add ProductAlPedid -------

    @Test
    public void testAgregarSinPedido() {
        VentaController vc = new VentaController();
        Producto cafe = cafeConStock(10);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> vc.agregarProductoAlPedido(cafe, 1)
        );

        assertEquals("No hay pedido activo", ex.getMessage());
    }

    @Test
    public void testAgregarProductoNull() {
        VentaController vc = new VentaController();
        vc.iniciarPedido(1, Pedido.MESA, 1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> vc.agregarProductoAlPedido(null, 1)
        );

        assertEquals("Producto no puede ser null", ex.getMessage());
    }

    @Test
    public void testCantidadInvalida() {
        VentaController vc = new VentaController();
        vc.iniciarPedido(1, Pedido.MESA, 1);
        Producto cafe = cafeConStock(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> vc.agregarProductoAlPedido(cafe, 0)
        );

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    public void testStockInsuficiente() {
        VentaController vc = new VentaController();
        vc.iniciarPedido(1, Pedido.MESA, 1);
        Producto cafe = cafeConStock(1);

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
        VentaController vc = new VentaController();

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> vc.finalizarVenta(null, null, true)
        );

        assertEquals("No hay pedido para finalizar", ex.getMessage());
    }
}

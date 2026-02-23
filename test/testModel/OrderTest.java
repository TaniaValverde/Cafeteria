package testModel;

import Model.Order;
import Model.Product;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrderTest {

    private Product cafe() {
        return new Product("P1", "Cafe", "Bebida", 2000, 10);
    }

    private Product sandwich() {
        return new Product("P2", "Sandwich", "Comida", 3500, 5);
    }

    private Order mesa(int cod, int num) {
        return new Order(cod, Order.MESA, num);
    }

    private Order llevar(int cod) {
        return new Order(cod, Order.PARA_LLEVAR, null);
    }

    // -------- constructor --------

    @Test
    public void ctorMesaOk() {
        Order p = mesa(1, 1);
        assertEquals(1, p.getCodigoPedido());
        assertEquals(Order.MESA, p.getTipoPedido());
        assertEquals(Integer.valueOf(1), p.getNumeroMesa());
    }

    @Test
    public void ctorLlevarOk() {
        Order p = llevar(2);
        assertEquals(Order.PARA_LLEVAR, p.getTipoPedido());
        assertNull(p.getNumeroMesa());
    }

    @Test
    public void ctorCodNeg() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Order(-1, Order.MESA, 1)
        );
        assertEquals("Codigo de pedido invalido", ex.getMessage());
    }

    @Test
    public void ctorTipoNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Order(1, null, 1)
        );
        assertEquals("No hay pedido", ex.getMessage());
    }

    @Test
    public void ctorTipoBad() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Order(1, "DOMICILIO", 1)
        );
        assertEquals("Tipo de pedido incorrecto", ex.getMessage());
    }

    @Test
    public void ctorMesaBad() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order(1, Order.MESA, 0));

        assertThrows(IllegalArgumentException.class,
                () -> new Order(1, Order.MESA, 6));
    }

    @Test
    public void ctorLlevarConMesa() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Order(1, Order.PARA_LLEVAR, 1)
        );
        assertEquals("Pedido para llevar no debe tener mesa", ex.getMessage());
    }

    // -------- agregarProducto --------

    @Test
    public void addOk() {
        Order p = mesa(1, 1);
        Product c = cafe();

        p.agregarProducto(c, 2);

        assertEquals(1, p.getProductos().size());
        assertEquals(2, p.getCantidadDeProducto(c));
    }

    @Test
    public void addSuma() {
        Order p = mesa(1, 1);
        Product c = cafe();

        p.agregarProducto(c, 2);
        p.agregarProducto(c, 3);

        assertEquals(5, p.getCantidadDeProducto(c));
    }

    @Test
    public void addNull() {
        Order p = mesa(1, 1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> p.agregarProducto(null, 1)
        );

        assertEquals("Producto no puede ser null", ex.getMessage());
    }

    @Test
    public void addCantBad() {
        Order p = mesa(1, 1);
        Product c = cafe();

        assertThrows(IllegalArgumentException.class,
                () -> p.agregarProducto(c, 0));

        assertThrows(IllegalArgumentException.class,
                () -> p.agregarProducto(c, -5));
    }

    // -------- getters --------

    @Test
    public void getProductosCopia() {
        Order p = mesa(1, 1);
        p.agregarProducto(cafe(), 1);

        List<Product> lista = p.getProductos();
        lista.clear(); // usuario revienta

        assertEquals(1, p.getProductos().size());
    }

    @Test
    public void getCantNoExiste() {
        Order p = mesa(1, 1);
        p.agregarProducto(cafe(), 1);

        assertEquals(0, p.getCantidadDeProducto(sandwich()));
    }

    @Test
    public void getCantNull() {
        Order p = mesa(1, 1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> p.getCantidadDeProducto(null)
        );

        assertEquals("Producto no puede ser null", ex.getMessage());
    }
}

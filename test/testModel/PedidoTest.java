package testModel;

import Model.Pedido;
import Model.Producto;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class PedidoTest {

    private Producto cafe() {
        return new Producto("P1", "Cafe", "Bebida", 2000, 10);
    }

    private Producto sandwich() {
        return new Producto("P2", "Sandwich", "Comida", 3500, 5);
    }

    private Pedido mesa(int cod, int num) {
        return new Pedido(cod, Pedido.MESA, num);
    }

    private Pedido llevar(int cod) {
        return new Pedido(cod, Pedido.PARA_LLEVAR, null);
    }

    // -------- constructor --------

    @Test
    public void ctorMesaOk() {
        Pedido p = mesa(1, 1);
        assertEquals(1, p.getCodigoPedido());
        assertEquals(Pedido.MESA, p.getTipoPedido());
        assertEquals(Integer.valueOf(1), p.getNumeroMesa());
    }

    @Test
    public void ctorLlevarOk() {
        Pedido p = llevar(2);
        assertEquals(Pedido.PARA_LLEVAR, p.getTipoPedido());
        assertNull(p.getNumeroMesa());
    }

    @Test
    public void ctorCodNeg() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(-1, Pedido.MESA, 1)
        );
        assertEquals("Codigo de pedido invalido", ex.getMessage());
    }

    @Test
    public void ctorTipoNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(1, null, 1)
        );
        assertEquals("No hay pedido", ex.getMessage());
    }

    @Test
    public void ctorTipoBad() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(1, "DOMICILIO", 1)
        );
        assertEquals("Tipo de pedido incorrecto", ex.getMessage());
    }

    @Test
    public void ctorMesaBad() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(1, Pedido.MESA, 0));

        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(1, Pedido.MESA, 6));
    }

    @Test
    public void ctorLlevarConMesa() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido(1, Pedido.PARA_LLEVAR, 1)
        );
        assertEquals("Pedido para llevar no debe tener mesa", ex.getMessage());
    }

    // -------- agregarProducto --------

    @Test
    public void addOk() {
        Pedido p = mesa(1, 1);
        Producto c = cafe();

        p.agregarProducto(c, 2);

        assertEquals(1, p.getProductos().size());
        assertEquals(2, p.getCantidadDeProducto(c));
    }

    @Test
    public void addSuma() {
        Pedido p = mesa(1, 1);
        Producto c = cafe();

        p.agregarProducto(c, 2);
        p.agregarProducto(c, 3);

        assertEquals(5, p.getCantidadDeProducto(c));
    }

    @Test
    public void addNull() {
        Pedido p = mesa(1, 1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> p.agregarProducto(null, 1)
        );

        assertEquals("Producto no puede ser null", ex.getMessage());
    }

    @Test
    public void addCantBad() {
        Pedido p = mesa(1, 1);
        Producto c = cafe();

        assertThrows(IllegalArgumentException.class,
                () -> p.agregarProducto(c, 0));

        assertThrows(IllegalArgumentException.class,
                () -> p.agregarProducto(c, -5));
    }

    // -------- getters --------

    @Test
    public void getProductosCopia() {
        Pedido p = mesa(1, 1);
        p.agregarProducto(cafe(), 1);

        List<Producto> lista = p.getProductos();
        lista.clear(); // usuario revienta

        assertEquals(1, p.getProductos().size());
    }

    @Test
    public void getCantNoExiste() {
        Pedido p = mesa(1, 1);
        p.agregarProducto(cafe(), 1);

        assertEquals(0, p.getCantidadDeProducto(sandwich()));
    }

    @Test
    public void getCantNull() {
        Pedido p = mesa(1, 1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> p.getCantidadDeProducto(null)
        );

        assertEquals("Producto no puede ser null", ex.getMessage());
    }
}

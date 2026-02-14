package testModel;

import Model.Mesa;
import Model.Pedido;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link Mesa}.
 *
 * <p>Covers core business rules: valid table range, assign/release workflow,
 * and defensive behavior against invalid operations.</p>
 */
public class MesaTest {

    private Pedido pedidoMesa(int cod, int mesa) {
        return new Pedido(cod, Pedido.MESA, mesa);
    }

    /** Valid table is created as free and without an active order. */
    @Test
    public void ctorOk() {
        Mesa m = new Mesa(1);
        assertEquals(1, m.getNumero());
        assertNotNull(m.getEstado());
        assertNull(m.getPedidoActual());
        assertTrue(m.estaLibre());
    }

    /** Invalid table numbers must be rejected. */
    @Test
    public void ctorNumBad() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> new Mesa(0)
        );
        assertEquals("Numero de mesa invalido", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> new Mesa(6)
        );
        assertEquals("Numero de mesa invalido", ex2.getMessage());
    }

    /** A new table starts free. */
    @Test
    public void libreAlInicio() {
        Mesa m = new Mesa(2);
        assertTrue(m.estaLibre());
        assertNull(m.getPedidoActual());
    }

    /* -------- asignarPedido -------- */

    /** Assigning a matching table-order occupies the table. */
    @Test
    public void asignarOk() {
        Mesa m = new Mesa(2);
        Pedido p = pedidoMesa(10, 2);

        m.asignarPedido(p);

        assertFalse(m.estaLibre());
        assertNotNull(m.getPedidoActual());
        assertEquals(10, m.getPedidoActual().getCodigoPedido());
    }

    /** Null orders are not allowed. */
    @Test
    public void asignarNull() {
        Mesa m = new Mesa(1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> m.asignarPedido(null)
        );

        assertEquals("Invalid order.", ex.getMessage());
        assertTrue(m.estaLibre());
        assertNull(m.getPedidoActual());
    }

    /** Order table must match this table number; state must remain unchanged on failure. */
    @Test
    public void asignarMesaNoCoincide() {
        Mesa m = new Mesa(3);
        Pedido p = pedidoMesa(11, 2); // pedido es para mesa 2

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> m.asignarPedido(p)
        );

        assertEquals("Mesa no coincide con el pedido", ex.getMessage());
        assertTrue(m.estaLibre());
        assertNull(m.getPedidoActual());
    }

    /** Re-assigning when occupied must fail. */
    @Test
    public void asignarDoble() {
        Mesa m = new Mesa(1);
        m.asignarPedido(pedidoMesa(1, 1));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> m.asignarPedido(pedidoMesa(2, 1))
        );

        assertEquals("Table is occupied.", ex.getMessage());
    }

    /** "Para llevar" orders cannot be assigned to a table. */
    @Test
    public void asignarParaLlevar() {
        Mesa m = new Mesa(1);
        Pedido p = new Pedido(5, Pedido.PARA_LLEVAR, null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> m.asignarPedido(p)
        );

        assertEquals("Mesa no coincide con el pedido", ex.getMessage());
        assertTrue(m.estaLibre());
        assertNull(m.getPedidoActual());
    }

    /* -------- liberar -------- */

    /** Releasing an occupied table frees it and clears the order. */
    @Test
    public void liberarOk() {
        Mesa m = new Mesa(4);
        m.asignarPedido(pedidoMesa(20, 4));

        m.liberar();

        assertTrue(m.estaLibre());
        assertNull(m.getPedidoActual());
    }

    /** Releasing an already free table must fail. */
    @Test
    public void liberarLibre() {
        Mesa m = new Mesa(5);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> m.liberar()
        );

        assertEquals("Table is already free.", ex.getMessage());
        assertTrue(m.estaLibre());
    }
}

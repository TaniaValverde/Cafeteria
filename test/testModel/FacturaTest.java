package testModel;

import Model.Cliente;
import Model.Factura;
import Model.Mesa;
import Model.Pedido;
import Model.Producto;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FacturaTest {

    private static final double IVA = 0.13;
    private static final double DELTA = 0.0001;

    private Cliente cliente;
    private Producto p1;
    private Producto p2;

    @Before
    public void setUp() {
        cliente = new Cliente("1001", "Ana Pérez", "88887777", Cliente.TipoCliente.FRECUENTE);

        p1 = new Producto("2001", "Café", "BEBIDA", 1000.0, 50);
        p2 = new Producto("2002", "Galleta", "COMIDA", 500.0, 50);
    }

    private Pedido pedidoParaLlevarConProductos() {
        Pedido pedido = new Pedido(1, Pedido.PARA_LLEVAR, null);
        pedido.agregarProducto(p1, 2); // 2000
        pedido.agregarProducto(p2, 1); //  500
        return pedido;
    }

    private Pedido pedidoMesaConProductos(int numeroMesa) {
        Pedido pedido = new Pedido(2, Pedido.MESA, numeroMesa);
        pedido.agregarProducto(p1, 1); // 1000
        pedido.agregarProducto(p2, 3); // 1500
        return pedido;
    }

    @Test
    public void facturaNoDebeSerNula_yDebeTenerId() {
        Factura f = new Factura(pedidoParaLlevarConProductos(), cliente, null, true);

        assertNotNull(f);
        assertNotNull(f.getIdFactura());
        assertFalse(f.getIdFactura().trim().isEmpty());
        assertTrue("El id debería iniciar con FAC-", f.getIdFactura().startsWith("FAC-"));
    }

    @Test
    public void calcularTotal_paraLlevar_debeAplicarIVA() {
        Pedido pedido = pedidoParaLlevarConProductos();
        Factura f = new Factura(pedido, cliente, null, true);

        double subtotalEsperado = (1000.0 * 2) + (500.0 * 1); // 2500
        double totalEsperado = subtotalEsperado + (subtotalEsperado * IVA);

        assertEquals(totalEsperado, f.getTotal(), DELTA);
        assertTrue("El total no debe ser negativo", f.getTotal() >= 0);
    }

    @Test
    public void calcularTotal_conMesa_debeAplicarIVA() {
        int mesaNum = 1;
        Mesa mesa = new Mesa(mesaNum);

        Pedido pedido = pedidoMesaConProductos(mesaNum);
        Factura f = new Factura(pedido, cliente, mesa, false);

        double subtotalEsperado = (1000.0 * 1) + (500.0 * 3); // 2500
        double totalEsperado = subtotalEsperado + (subtotalEsperado * IVA);

        assertEquals(totalEsperado, f.getTotal(), DELTA);
    }

    @Test
    public void generarImpresion_debeIncluirDatosClave_paraLlevar() {
        Factura f = new Factura(pedidoParaLlevarConProductos(), cliente, null, true);

        String imp = f.generarImpresion();

        assertNotNull(imp);
        assertTrue(imp.contains("Factura:"));
        assertTrue(imp.contains("Cliente: " + cliente.getNombre()));
        assertTrue("Debe indicar 'Para llevar'", imp.toLowerCase().contains("para llevar"));
        assertTrue("Debe listar productos", imp.contains("--- Productos ---"));
        assertTrue("Debe mostrar TOTAL", imp.toUpperCase().contains("TOTAL"));
    }

    @Test
    public void generarImpresion_debeIncluirMesa_siNoEsParaLlevar() {
        int mesaNum = 2;
        Mesa mesa = new Mesa(mesaNum);

        Factura f = new Factura(pedidoMesaConProductos(mesaNum), cliente, mesa, false);

        String imp = f.generarImpresion();

        assertNotNull(imp);
        assertTrue("Debe incluir el número de mesa", imp.contains("Mesa: " + mesaNum));
    }

    @Test
    public void toString_debeSerLegible_yNoVacio() {
        Factura f = new Factura(pedidoParaLlevarConProductos(), cliente, null, true);

        String s = f.toString();
        assertNotNull(s);
        assertFalse(s.trim().isEmpty());
        assertTrue(s.contains("Factura"));
        assertTrue(s.contains("Total"));
    }

    @Test
    public void guardarEnArchivo_debeLanzarIOException_porImplementacionPendiente() {
        Factura f = new Factura(pedidoParaLlevarConProductos(), cliente, null, true);

        try {
            f.guardarEnArchivo("factura.txt");
            fail("Se esperaba IOException porque guardarEnArchivo está pendiente de implementación.");
        } catch (IOException ex) {
            assertNotNull(ex.getMessage());
        }
    }
}
